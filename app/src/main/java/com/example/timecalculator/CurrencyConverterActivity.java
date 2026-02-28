package com.example.timecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
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

public class CurrencyConverterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText etAmount;
    private Spinner spFromCurrency, spToCurrency;
    private Button btnConvert;
    private TextView tvResult;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    private final String[] currencies = {"INR", "USD", "EUR", "GBP"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        initViews();
        setupToolbar();
        setupSpinner();
        setupNavigation();

        drawerLayout.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        disableInputFocus();
                    }
                });

        btnConvert.setOnClickListener(v -> {
            hideKeyboard();   // ✅ Close keyboard
            convert();        // ✅ Perform conversion
        });
    }

    // ------------------ Initialize Views ------------------
    private void initViews() {
        etAmount = findViewById(R.id.etAmount);
        spFromCurrency = findViewById(R.id.spFromCurrency);
        spToCurrency = findViewById(R.id.spToCurrency);
        btnConvert = findViewById(R.id.btnConvert);
        tvResult = findViewById(R.id.tvResult);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
    }

    // ------------------ Toolbar Setup ------------------
    private void setupToolbar() {
        setSupportActionBar(toolbar);

        getWindow().setStatusBarColor(
                ContextCompat.getColor(this, R.color.teal_700)
        );

        toolbar.setNavigationOnClickListener(v -> {

            disableInputFocus();   // ⭐ IMPORTANT

            drawerLayout.openDrawer(GravityCompat.START);
        });
    }

    // ------------------ Spinner Setup ------------------
    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                currencies
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spFromCurrency.setAdapter(adapter);
        spToCurrency.setAdapter(adapter);
    }

    // ------------------ Navigation Setup ------------------
    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_currency_converter);
    }

    // ------------------ Convert Logic ------------------
    private void convert() {

        String amountStr = etAmount.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountStr); // supports scientific notation
        } catch (Exception e) {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount < 0) {
            Toast.makeText(this, "Amount cannot be negative", Toast.LENGTH_SHORT).show();
            return;
        }

        String from = spFromCurrency.getSelectedItem().toString();
        String to = spToCurrency.getSelectedItem().toString();

        // Same currency
        if (from.equals(to)) {
            tvResult.setText(formatCurrency(amount, to));
            return;
        }

        double result = convertCurrency(amount, from, to);

        tvResult.setText(formatCurrency(result, to));
    }

    // ------------------ Currency Conversion ------------------
    private double convertCurrency(double amount, String from, String to) {

        double inr;

        switch (from) {
            case "USD": inr = amount * 83; break;
            case "EUR": inr = amount * 90; break;
            case "GBP": inr = amount * 105; break;
            default: inr = amount; break;
        }

        switch (to) {
            case "USD": return inr / 83;
            case "EUR": return inr / 90;
            case "GBP": return inr / 105;
            default: return inr;
        }
    }

    // ------------------ Currency Formatting ------------------
    private String formatCurrency(double value, String currencyCode) {

        Locale locale;

        switch (currencyCode) {
            case "INR":
                locale = new Locale("en", "IN");
                break;
            case "USD":
                locale = Locale.US;
                break;
            case "EUR":
                locale = Locale.GERMANY;
                break;
            case "GBP":
                locale = Locale.UK;
                break;
            default:
                locale = Locale.getDefault();
        }

        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        return "Converted Amount: " + formatter.format(value);
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
    // ------------------ Navigation Drawer ------------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        hideKeyboard();
        item.setChecked(true);

        Intent intent = null;
        int id = item.getItemId();

        if (id == R.id.nav_calculator)
            intent = new Intent(this, CalculatorActivity.class);

        else if (id == R.id.nav_duration_finder)
            intent = new Intent(this, DurationFinderActivity.class);

        else if (id == R.id.nav_village_interest)
            intent = new Intent(this, VillageInterestCalculatorActivity.class);

        else if (id == R.id.nav_money_calc)
            intent = new Intent(this, MoneyCalculatorActivity.class);

        else if (id == R.id.nav_age_calculator)
            intent = new Intent(this, AgeCalculatorActivity.class);

        else if (id == R.id.nav_number_to_words)
            intent = new Intent(this, NumberToWordsActivity.class);

        else if (id == R.id.nav_notes_counter)
            intent = new Intent(this, NotesCounterActivity.class);

        else if (id == R.id.nav_unit_converter)
            intent = new Intent(this, UnitConverterActivity.class);

        else if (id == R.id.nav_currency_converter)
            intent = new Intent(this, CurrencyConverterActivity.class);

            // ✅ ADDED FUEL COST
        else if (id == R.id.nav_fuel_cost)
            intent = new Intent(this, FuelCostActivity.class);

        if (intent != null) {

            final Intent finalIntent = intent;

            drawerLayout.closeDrawer(GravityCompat.START);

            drawerLayout.postDelayed(() -> {
                startActivity(finalIntent);
                finish();
            }, 200);
        }return true;
    }
}