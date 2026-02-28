package com.example.timecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class NumberToWordsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    private EditText etNumber;
    private Button btnConvert;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_to_words);

        getWindow().setStatusBarColor(
                ContextCompat.getColor(this, R.color.teal_700)
        );

        initViews();
        setupToolbar();
        setupNavigation();
        setupDrawerListener();

        drawerLayout.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        disableInputFocus();
                    }
                });

        btnConvert.setOnClickListener(v -> {
            hideKeyboard();
            convertNumber();
        });
    }

    // ================= INIT =================
    private void initViews() {

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        etNumber = findViewById(R.id.etNumber);
        btnConvert = findViewById(R.id.btnConvert);
        tvResult = findViewById(R.id.tvResult);
    }

    // ================= TOOLBAR =================
    private void setupToolbar() {

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> {
            disableInputs();
            drawerLayout.openDrawer(GravityCompat.START);
        });
    }

    // ================= NAVIGATION =================
    private void setupNavigation() {

        navigationView.setCheckedItem(R.id.nav_number_to_words);

        navigationView.setNavigationItemSelectedListener(item -> {
            disableInputs();
            handleNavigationItemClick(item);
            return true;
        });
    }

    // ⭐ CLOSE INPUT WHEN DRAWER OPENS
    private void setupDrawerListener() {

        drawerLayout.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        disableInputs();
                    }
                });
    }

    // ================= NUMBER CONVERSION =================
    private void convertNumber() {

        String input = etNumber.getText().toString().trim();

        if (input.isEmpty()) {
            tvResult.setText("Please enter a number");
            return;
        }

        try {

            double value = Double.parseDouble(input);

            boolean negative = value < 0;
            value = Math.abs(value);

            long integerPart = (long) value;
            long decimalPart =
                    Math.round((value - integerPart) * 100);

            StringBuilder result = new StringBuilder();

            if (negative) result.append("Minus ");

            if (integerPart == 0)
                result.append("Zero");
            else
                result.append(
                        convertToIndianWords(integerPart));

            if (decimalPart > 0) {
                result.append(" Point ");
                result.append(
                        convertToIndianWords(decimalPart));
            }

            tvResult.setText(result.toString());

        } catch (Exception e) {
            tvResult.setText("Invalid Number");
        }
    }

    // ================= INDIAN NUMBER SYSTEM =================
    private String convertToIndianWords(long number) {

        String[] units = {"","One","Two","Three","Four","Five",
                "Six","Seven","Eight","Nine","Ten","Eleven",
                "Twelve","Thirteen","Fourteen","Fifteen",
                "Sixteen","Seventeen","Eighteen","Nineteen"};

        String[] tens = {"","","Twenty","Thirty","Forty",
                "Fifty","Sixty","Seventy","Eighty","Ninety"};

        if (number < 20)
            return units[(int) number];

        if (number < 100)
            return tens[(int)(number / 10)]
                    + (number % 10 != 0
                    ? " " + units[(int)(number % 10)]
                    : "");

        if (number < 1000)
            return units[(int)(number / 100)] + " Hundred"
                    + (number % 100 != 0
                    ? " " + convertToIndianWords(number % 100)
                    : "");

        if (number < 100000)
            return convertToIndianWords(number / 1000) + " Thousand"
                    + (number % 1000 != 0
                    ? " " + convertToIndianWords(number % 1000)
                    : "");

        if (number < 10000000)
            return convertToIndianWords(number / 100000) + " Lakh"
                    + (number % 100000 != 0
                    ? " " + convertToIndianWords(number % 100000)
                    : "");

        return convertToIndianWords(number / 10000000) + " Crore"
                + (number % 10000000 != 0
                ? " " + convertToIndianWords(number % 10000000)
                : "");
    }

    // ================= KEYBOARD =================
    private void hideKeyboard() {

        View view = getCurrentFocus();
        if (view == null) view = new View(this);

        InputMethodManager imm =
                (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(
                view.getWindowToken(), 0);
    }

    // ⭐ FULL INPUT DISABLE
    private void disableInputs() {

        hideKeyboard();

        View view = getCurrentFocus();
        if (view != null) view.clearFocus();

        getWindow().getDecorView().clearFocus();
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

    // ================= NAVIGATION HANDLER =================
    private void handleNavigationItemClick(
            @NonNull MenuItem item) {

        Intent intent = null;
        int id = item.getItemId();

        if(id==R.id.nav_calculator)
            intent=new Intent(this,CalculatorActivity.class);

        else if(id==R.id.nav_duration_finder)
            intent=new Intent(this,DurationFinderActivity.class);

        else if(id==R.id.nav_village_interest)
            intent=new Intent(this,VillageInterestCalculatorActivity.class);

        else if(id==R.id.nav_money_calc)
            intent=new Intent(this,MoneyCalculatorActivity.class);

        else if(id==R.id.nav_age_calculator)
            intent=new Intent(this,AgeCalculatorActivity.class);

        else if(id==R.id.nav_notes_counter)
            intent=new Intent(this,NotesCounterActivity.class);

        else if(id==R.id.nav_unit_converter)
            intent=new Intent(this,UnitConverterActivity.class);

        else if(id==R.id.nav_currency_converter)
            intent=new Intent(this,CurrencyConverterActivity.class);

        else if(id==R.id.nav_fuel_cost)
            intent=new Intent(this,FuelCostActivity.class);

        if (intent != null) {

            final Intent finalIntent = intent; // ⭐ FIX

            drawerLayout.closeDrawer(GravityCompat.START);

            drawerLayout.postDelayed(() -> {
                startActivity(finalIntent);
                finish();
            }, 200);
        }
    }
}