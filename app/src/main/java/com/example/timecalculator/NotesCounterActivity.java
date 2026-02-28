package com.example.timecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.text.NumberFormat;
import java.util.Locale;

public class NotesCounterActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    private EditText et500, et200, et100, et50, et20, et10;
    private TextView tvTotal, tvTotalWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_counter);

        getWindow().setStatusBarColor(
                ContextCompat.getColor(this, R.color.teal_700)
        );



        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        drawerLayout.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        disableInputFocus();
                    }
                });

        toolbar.setNavigationOnClickListener(v -> {

            disableInputFocus();   // ⭐ IMPORTANT

            drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setCheckedItem(R.id.nav_notes_counter);

        navigationView.setNavigationItemSelectedListener(item -> {

            disableInputFocus();   // ⭐ CLOSE INPUT STATE

            handleNavigationItemClick(item);
            return true;
        });


        et500 = findViewById(R.id.et500);
        et200 = findViewById(R.id.et200);
        et100 = findViewById(R.id.et100);
        et50 = findViewById(R.id.et50);
        et20 = findViewById(R.id.et20);
        et10 = findViewById(R.id.et10);

        Button btnCalculate = findViewById(R.id.btnCalculate);
        Button btnClear = findViewById(R.id.btnClear);

        tvTotal = findViewById(R.id.tvTotal);
        tvTotalWords = findViewById(R.id.tvTotalWords);

        btnCalculate.setOnClickListener(v -> {
            hideKeyboard();      // ✅ Close keyboard
            calculateTotal();
        });

        btnClear.setOnClickListener(v -> {
            hideKeyboard();      // ✅ Close keyboard
            clearFields();
        });
    }

    // ================= CALCULATE TOTAL =================
    private void calculateTotal() {

        long total =
                getValue(et500) * 500L +
                        getValue(et200) * 200L +
                        getValue(et100) * 100L +
                        getValue(et50) * 50L +
                        getValue(et20) * 20L +
                        getValue(et10) * 10L;

        tvTotal.setText("Total Amount: ₹" + formatIndian(total));

        if (total == 0) {
            tvTotalWords.setText("Zero Rupees");
        } else {
            tvTotalWords.setText(
                    convertToIndianWords(total) + " Rupees"
            );
        }
    }

    // ================= SAFE VALUE GET =================
    private long getValue(EditText editText) {

        String text = editText.getText().toString().trim();

        if (text.isEmpty()) return 0;

        try {
            long value = Long.parseLong(text);
            return Math.max(value, 0); // Prevent negative values
        } catch (Exception e) {
            return 0;
        }
    }

    // ================= CLEAR =================
    private void clearFields() {
        et500.setText("");
        et200.setText("");
        et100.setText("");
        et50.setText("");
        et20.setText("");
        et10.setText("");

        tvTotal.setText("Total Amount: ₹0");
        tvTotalWords.setText("");
    }



    // ================= INDIAN FORMAT =================
    private String formatIndian(long value) {
        NumberFormat formatter =
                NumberFormat.getNumberInstance(new Locale("en", "IN"));
        return formatter.format(value);
    }

    // ================= INDIAN WORDS =================
    private String convertToIndianWords(long number) {

        String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six",
                "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
                "Thirteen", "Fourteen", "Fifteen", "Sixteen",
                "Seventeen", "Eighteen", "Nineteen"};

        String[] tens = {"", "", "Twenty", "Thirty", "Forty",
                "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

        if (number < 20)
            return units[(int) number];

        if (number < 100)
            return tens[(int) (number / 10)] +
                    ((number % 10 != 0)
                            ? " " + units[(int) (number % 10)] : "");

        if (number < 1000)
            return units[(int) (number / 100)] + " Hundred" +
                    ((number % 100 != 0)
                            ? " " + convertToIndianWords(number % 100) : "");

        if (number < 100000)
            return convertToIndianWords(number / 1000) + " Thousand" +
                    ((number % 1000 != 0)
                            ? " " + convertToIndianWords(number % 1000) : "");

        if (number < 10000000)
            return convertToIndianWords(number / 100000) + " Lakh" +
                    ((number % 100000 != 0)
                            ? " " + convertToIndianWords(number % 100000) : "");

        return convertToIndianWords(number / 10000000) + " Crore" +
                ((number % 10000000 != 0)
                        ? " " + convertToIndianWords(number % 10000000) : "");
    }
    private void hideKeyboard() {

        View view = getCurrentFocus();

        if (view == null) {
            view = new View(this);
        }

        InputMethodManager imm =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(
                view.getWindowToken(), 0);
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
        hideKeyboard();
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