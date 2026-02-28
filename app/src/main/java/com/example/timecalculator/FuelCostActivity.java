package com.example.timecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class FuelCostActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    private EditText etDistance, etMileage, etFuelPrice;
    private TextView tvFuelNeeded, tvTotalCost;
    private MaterialButton btnCalculate, btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_cost);
        // ✅ Make Status Bar Teal
        getWindow().setStatusBarColor(
                getResources().getColor(R.color.teal_700));




        initViews();
        setupToolbar();
        setupNavigation();
        setupListeners();


    }

    // ================= INIT =================

    private void initViews() {

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        etDistance = findViewById(R.id.etDistance);
        etMileage = findViewById(R.id.etMileage);
        etFuelPrice = findViewById(R.id.etFuelPrice);

        tvFuelNeeded = findViewById(R.id.tvFuelNeeded);
        tvTotalCost = findViewById(R.id.tvTotalCost);

        btnCalculate = findViewById(R.id.btnCalculate);
        btnClear = findViewById(R.id.btnClear);

        drawerLayout.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        disableInputFocus();
                    }
                });
    }

    // ================= TOOLBAR =================

    private void setupToolbar() {

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(
                    getString(R.string.fuel_title)); // ✅ FIXED
        }

        toolbar.setNavigationOnClickListener(v -> {

            disableInputFocus();   // ⭐ IMPORTANT

            drawerLayout.openDrawer(GravityCompat.START);
        });
    }

    private void clearFocusFromScreen() {

        View view = getCurrentFocus();

        if (view != null) {
            view.clearFocus();
        }

        hideKeyboard();
    }
    // ================= NAVIGATION =================

    private void setupNavigation() {

        navigationView.setCheckedItem(R.id.nav_fuel_cost);

        navigationView.setNavigationItemSelectedListener(item -> {

            disableInputFocus();   // ⭐ CLOSE INPUT STATE

            handleNavigationItemClick(item);
            return true;
        });
        // ✅ FIX PURPLE DOT ISSUE
        drawerLayout.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerOpened(@NonNull View drawerView) {
                        clearFocusFromScreen();
                    }
                });
    }

    // ================= BUTTON LISTENERS =================

    private void setupListeners() {

        btnCalculate.setOnClickListener(v -> {
            hideKeyboard();   // ✅ Close keyboard
            calculateFuel();  // ✅ Perform calculation
        });
        btnClear.setOnClickListener(v -> clearAll());
    }

    // ================= CALCULATION =================

    private void calculateFuel() {

        String distanceStr = etDistance.getText().toString().trim();
        String mileageStr = etMileage.getText().toString().trim();
        String priceStr = etFuelPrice.getText().toString().trim();

        if (TextUtils.isEmpty(distanceStr)
                || TextUtils.isEmpty(mileageStr)
                || TextUtils.isEmpty(priceStr)) {

            Toast.makeText(this,
                    "Enter all values",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double distance = Double.parseDouble(distanceStr);
        double mileage = Double.parseDouble(mileageStr);
        double price = Double.parseDouble(priceStr);

        if (mileage <= 0) {
            Toast.makeText(this,
                    "Mileage must be greater than 0",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double fuelNeeded = distance / mileage;
        double totalCost = fuelNeeded * price;

        // ✅ Professional formatting
        tvFuelNeeded.setText(
                String.format(Locale.US,
                        "%.2f L", fuelNeeded));

        tvTotalCost.setText(
                String.format(Locale.US,
                        "₹ %.2f", totalCost));
    }

    // ================= CLEAR =================

    private void clearAll() {

        etDistance.setText("");
        etMileage.setText("");
        etFuelPrice.setText("");

        tvFuelNeeded.setText(
                getString(R.string.fuel_default));

        tvTotalCost.setText(
                getString(R.string.cost_default));

        etDistance.requestFocus();
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
    // ================= NAVIGATION HANDLER =================

    private void handleNavigationItemClick(@NonNull MenuItem item) {
        hideKeyboard();
        int id = item.getItemId();
        item.setChecked(true);

        Intent intent = null;

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

        else if (id == R.id.nav_fuel_cost)
            return; // already here ✅

        if (intent != null) {

            final Intent finalIntent = intent; // ✅ make final copy

            drawerLayout.closeDrawer(GravityCompat.START);

            drawerLayout.postDelayed(() -> {
                startActivity(finalIntent);
                finish();
            }, 100);
        }
    }
}