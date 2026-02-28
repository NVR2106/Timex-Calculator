package com.example.timecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;

public class CalculatorActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private EditText display;

    private String currentInput = "";
    private boolean lastWasResult = false;

    private static final int MAX_HISTORY = 30;
    private final ArrayList<String> historyList = new ArrayList<>();

    // ================= ACTIVITY =================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);



        getWindow().setStatusBarColor(
                ContextCompat.getColor(this, R.color.teal_700));

        initViews();
        setupToolbar();
        setupCursor();
        setupButtons();

        drawerLayout.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        disableInputFocus();
                    }
                });
    }



    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        display = findViewById(R.id.display);

        navigationView.setCheckedItem(R.id.nav_calculator);
    }



    // ================= TOOLBAR =================

    private void setupToolbar() {

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Time Calculator");

        toolbar.setNavigationOnClickListener(v -> {

            disableInputFocus();   // ⭐ IMPORTANT

            drawerLayout.openDrawer(GravityCompat.START);
        });

        if (navigationView != null) {
            navigationView.setCheckedItem(R.id.nav_calculator);
            navigationView.setNavigationItemSelectedListener(item -> {

                disableInputFocus();   // ⭐ CLOSE INPUT STATE

                handleNavigationItemClick(item);
                return true;
            });
        }
    }

    // ================= CURSOR =================

    private void setupCursor() {

        display.setShowSoftInputOnFocus(false);
        display.setCursorVisible(true);
        display.requestFocus();

        display.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                display.setSelection(display.getText().length());
                adjustFontSize();
            }
            @Override public void beforeTextChanged(CharSequence s,int start,int count,int after){}
            @Override public void onTextChanged(CharSequence s,int start,int before,int count){}
        });
    }

    private void hideKeyboard() {

        View view = getCurrentFocus();

        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(
                    view.getWindowToken(), 0);
        }
    }

    // ================= BUTTONS =================

    private void setupButtons() {

        int[] buttonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot, R.id.btnPlus, R.id.btnMinus,
                R.id.btnMultiply, R.id.btnDivide,
                R.id.btnEqual, R.id.btnC,
                R.id.btnBackspace, R.id.btnHour, R.id.btnMin,
                R.id.btnSec, R.id.btnHistory, R.id.btnCount
        };

        View.OnClickListener listener = v -> {

            int id = v.getId();

            if (id == R.id.btnC) clearDisplay();
            else if (id == R.id.btnBackspace) smartBackspace();
            else if (id == R.id.btnEqual) {
                hideKeyboard();      // ✅ CLOSE KEYBOARD
                calculateResult();   // ✅ CALCULATE
            }
            else if (id == R.id.btnHistory) showHistoryBottomSheet();
            else if (id == R.id.btnCount) showCountToast();
            else appendInput(((MaterialButton) v).getText().toString());
        };

        for (int id : buttonIds) {
            View btn = findViewById(id);
            if (btn != null) btn.setOnClickListener(listener);
        }
    }

    // ================= INPUT =================

    private void appendInput(String text) {

        // ===== FIX START =====
        if (lastWasResult) {

            // Number pressed → start new calculation
            if (text.matches("[0-9.]")) {
                currentInput = "";
            }

            // Operator pressed → continue with result
            lastWasResult = false;
        }
        // ===== FIX END =====


        // Prevent double operators
        if (text.matches("[+\\-*/×÷]")) {
            if (currentInput.isEmpty()) return;

            char lastChar =
                    currentInput.charAt(currentInput.length() - 1);

            if ("+-*/×÷".indexOf(lastChar) >= 0)
                return;
        }

        currentInput += text;
        display.setText(currentInput);
    }

    private void clearDisplay() {
        currentInput = "";
        display.setText("");
        lastWasResult = false;
    }

    // ================= SMART BACKSPACE =================

    private void smartBackspace() {

        if (currentInput == null || currentInput.isEmpty())
            return;

        /*if (lastWasResult) {
            clearDisplay();
            return;
        }*/

        String lower = currentInput.toLowerCase();

        if (lower.equals("error")) {
            currentInput = "";
        }
        else if (lower.endsWith("hr")) {
            currentInput = currentInput.substring(0, currentInput.length() - 2);
        }
        else if (lower.endsWith("min")) {
            currentInput = currentInput.substring(0, currentInput.length() - 3);
        }
        else if (lower.endsWith("sec")) {
            currentInput = currentInput.substring(0, currentInput.length() - 3);
        }
        else {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        }

        display.setText(currentInput);
    }

    // ================= CALCULATION =================

    private void calculateResult() {

        if (currentInput.trim().isEmpty())
            return;

        String result;

        boolean hasTime = containsTimeUnit(currentInput);

        // Prevent mixing time and normal numbers
        if (hasTime &&
                currentInput.matches(".*(hr|min|sec)[+\\-*/×÷]\\d+$")) {
            result = "Error";
        }
        else if (hasTime) {
            result = evaluateTimeExpression(currentInput);
        }
        else {
            result = evaluateMathExpression(currentInput);
        }

        if (!result.equals("Error")) {
            historyList.add(currentInput + " = " + result);
            if (historyList.size() > MAX_HISTORY)
                historyList.remove(0);
        }

        display.setText(result);
        currentInput = result;
        lastWasResult = true;
    }

    // ================= TIME =================

    private boolean containsTimeUnit(String input) {
        String lower = input.toLowerCase();
        return lower.contains("hr") ||
                lower.contains("min") ||
                lower.contains("sec");
    }

    private String evaluateTimeExpression(String input) {

        try {

            String expr = input.toLowerCase().replaceAll("\\s+", "");

            expr = expr.replaceAll("(?<=hr)(?=\\d)", "+");
            expr = expr.replaceAll("(?<=min)(?=\\d)", "+");
            expr = expr.replaceAll("(?<=sec)(?=\\d)", "+");

            expr = expr.replaceAll("(\\d+(\\.\\d+)?)hr", "$1*3600");
            expr = expr.replaceAll("(\\d+(\\.\\d+)?)min", "$1*60");
            expr = expr.replaceAll("(\\d+(\\.\\d+)?)sec", "$1");

            expr = expr.replace("×", "*")
                    .replace("÷", "/")
                    .replace("−", "-");

            Expression e = new ExpressionBuilder(expr).build();
            double totalSeconds = e.evaluate();

            return formatTime(totalSeconds);

        } catch (Exception e) {
            return "Error";
        }
    }

    private String formatTime(double totalSeconds) {

        if (Double.isNaN(totalSeconds) ||
                Double.isInfinite(totalSeconds) ||
                totalSeconds < 0)
            return "Error";

        long seconds = Math.round(totalSeconds);

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        StringBuilder sb = new StringBuilder();

        if (hours > 0) sb.append(hours).append("hr");
        if (minutes > 0) sb.append(minutes).append("min");
        if (secs > 0) sb.append(secs).append("sec");

        if (sb.length() == 0) return "0 sec";

        return sb.toString();
    }

    // ================= MATH =================

    private String evaluateMathExpression(String input) {

        try {

            Expression e = new ExpressionBuilder(
                    input.replace("×", "*")
                            .replace("÷", "/")
                            .replace("−", "-")
            ).build();

            double result = e.evaluate();
            return formatScientific(result);

        } catch (Exception e) {
            return "Error";
        }
    }

    private String formatScientific(double value) {

        if (Double.isNaN(value) || Double.isInfinite(value))
            return "Error";

        double abs = Math.abs(value);

        if (abs >= 1e9 || (abs > 0 && abs < 1e-6))
            return String.format(java.util.Locale.US, "%.6E", value);

        if (Math.abs(value - Math.rint(value)) < 1e-12)
            return String.valueOf((long) Math.rint(value));

        return String.format(java.util.Locale.US, "%.10f", value)
                .replaceAll("\\.?0+$", "");
    }

    // ================= COUNT =================

    private void showCountToast() {
        Toast.makeText(this,
                "Total Values: " + countValues(currentInput),
                Toast.LENGTH_SHORT).show();
    }

    private int countValues(String input) {

        if (input == null || input.trim().isEmpty())
            return 0;

        String[] parts = input.replaceAll("\\s+", "")
                .split("[+\\-*/×÷]+");

        int count = 0;
        for (String part : parts)
            if (!part.isEmpty())
                count++;

        return count;
    }

    // ================= HISTORY =================

    private void showHistoryBottomSheet() {

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater()
                .inflate(R.layout.history_bottom_sheet, null);

        TextView historyContent =
                view.findViewById(R.id.historyContent);

        if (historyList.isEmpty()) {
            historyContent.setText("No History Available");
        } else {

            StringBuilder sb = new StringBuilder();

            for (int i = historyList.size() - 1; i >= 0; i--) {
                sb.append(historyList.get(i)).append("\n\n");
            }

            historyContent.setText(sb.toString());
        }

        dialog.setContentView(view);
        dialog.show();
    }

    // ================= UI =================

    private void adjustFontSize() {
        int length = display.getText().length();
        if (length < 10) display.setTextSize(30);
        else if (length < 25) display.setTextSize(22);
        else display.setTextSize(18);
    }

    private void disableInputFocus() {

        View view = getCurrentFocus();

        if (view != null) {

            // Hide keyboard
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            // Remove cursor + selection
            view.clearFocus();
        }

        // Remove focus from entire layout
        getWindow().getDecorView().clearFocus();
    }

    // ================= NAVIGATION =================
    private void handleNavigationItemClick(@NonNull MenuItem item) {

        int id = item.getItemId();
        item.setChecked(true);

        Intent intent = null;

        if (id == R.id.nav_calculator) {
            intent = new Intent(this, CalculatorActivity.class);
        }
        else if (id == R.id.nav_duration_finder) {
            intent = new Intent(this, DurationFinderActivity.class);
        }
        else if(id == R.id.nav_village_interest) {
            intent = new Intent(this, VillageInterestCalculatorActivity.class);
        }
        else if (id == R.id.nav_money_calc) {
            intent = new Intent(this, MoneyCalculatorActivity.class);
        }
        else if (id == R.id.nav_age_calculator) {
            intent = new Intent(this, AgeCalculatorActivity.class);
        }
        else if (id == R.id.nav_number_to_words) {
            intent = new Intent(this, NumberToWordsActivity.class);
        }
        else if (id == R.id.nav_notes_counter) {
            intent = new Intent(this, NotesCounterActivity.class);
        }
        else if (id == R.id.nav_unit_converter) {
            intent = new Intent(this, UnitConverterActivity.class);
        }
        else if (id == R.id.nav_currency_converter) {
            intent = new Intent(this, CurrencyConverterActivity.class);
        }
        else if (id == R.id.nav_fuel_cost) {
            intent = new Intent(this, FuelCostActivity.class);
        }

        // ✅ ADD THIS BLOCK


        if (intent != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(intent);
            finish();
        }
    }

}