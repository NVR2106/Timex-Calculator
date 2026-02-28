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

public class MoneyCalculatorActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    private EditText timeWorkedInput, rateInput;
    private Button calculateBtn;
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_calculator);

        getWindow().setStatusBarColor(
                ContextCompat.getColor(this, R.color.teal_700)
        );



        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        drawerLayout.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        disableInputFocus();
                    }
                });

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Money Calculator");
        }

        toolbar.setNavigationOnClickListener(v -> {

            disableInputFocus();   // ⭐ IMPORTANT

            drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setCheckedItem(R.id.nav_money_calc);

        navigationView.setNavigationItemSelectedListener(item -> {

            disableInputFocus();   // ⭐ CLOSE INPUT STATE

            handleNavigationItemClick(item);
            return true;
        });

        timeWorkedInput = findViewById(R.id.etTimeWorked);
        rateInput = findViewById(R.id.etRatePerHour);
        calculateBtn = findViewById(R.id.btnCalculateEarnings);
        resultView = findViewById(R.id.tvResult);

        calculateBtn.setOnClickListener(v -> {
            hideKeyboard();        // ✅ Close keyboard
            calculateEarnings();   // ✅ Calculate earnings
        });
    }

    // ================= CALCULATION =================

    private void calculateEarnings() {

        String timeStr = timeWorkedInput.getText().toString().trim();
        String rateStr = rateInput.getText().toString().trim();

        if (timeStr.isEmpty() || rateStr.isEmpty()) {
            resultView.setText("⚠ Please enter Time (HH:MM) and Rate per Hour.");
            return;
        }

        // Strict HH:MM validation
        if (!timeStr.matches("^\\d{1,3}:[0-5]\\d$")) {
            resultView.setText("⚠ Invalid format! Use HH:MM (Example: 07:30)");
            return;
        }

        try {

            String[] parts = timeStr.split(":");

            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            if (hours < 0) {
                resultView.setText("⚠ Hours cannot be negative.");
                return;
            }

            double ratePerHour = Double.parseDouble(rateStr); // supports 1E6

            if (ratePerHour < 0) {
                resultView.setText("⚠ Rate cannot be negative.");
                return;
            }

            double ratePerMinute = ratePerHour / 60.0;
            double earningsHours = hours * ratePerHour;
            double earningsMinutes = minutes * ratePerMinute;
            double totalEarnings = earningsHours + earningsMinutes;

            resultView.setText(
                            "📌 Rate per Minute: ₹" +
                            formatCurrency(ratePerMinute) + "\n\n" +

                            "🕑 Rate per " + hours + " Hours : ₹" +
                            formatCurrency(earningsHours) + "\n" +

                            "⏳ Rate per " + minutes + " Minutes: ₹" +
                            formatCurrency(earningsMinutes) + "\n\n" +



                            "💵 Total Earnings: ₹" +
                            formatCurrency(totalEarnings)
            );

        } catch (NumberFormatException e) {
            resultView.setText("⚠ Invalid numeric input! Example: 07:30 and 500");
        }
    }

    // ================= FORMAT =================

    private String formatCurrency(double value) {

        NumberFormat formatter =
                NumberFormat.getNumberInstance(new Locale("en", "IN"));

        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(value);
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