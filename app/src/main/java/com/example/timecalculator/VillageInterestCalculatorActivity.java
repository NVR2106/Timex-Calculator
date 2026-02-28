package com.example.timecalculator;

import android.app.DatePickerDialog;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class VillageInterestCalculatorActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    private EditText etPrincipal, etRatePerHundred, etStartDate, etEndDate;
    private TextView tvInterestResult;

    private final SimpleDateFormat sdf =
            new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village_interest_calculator);

        getWindow().setStatusBarColor(
                ContextCompat.getColor(this, R.color.teal_700)
        );


        sdf.setLenient(false);

        initViews();
        setupToolbar();
        setupDatePickers();

        Button btnCalculate = findViewById(R.id.btnCalculateInterest);

        btnCalculate.setOnClickListener(v -> {
            hideKeyboard();        // ✅ Close keyboard
            calculateInterest();   // ✅ Calculate interest
        });
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        etPrincipal = findViewById(R.id.etPrincipal);
        etRatePerHundred = findViewById(R.id.etRatePerHundred);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        tvInterestResult = findViewById(R.id.tvInterestResult);

        drawerLayout.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        disableInputFocus();
                    }
                });

    }

    private void setupToolbar() {

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Village Interest Calculator");

        toolbar.setNavigationOnClickListener(v -> {

            disableInputFocus();   // ⭐ IMPORTANT

            drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setCheckedItem(R.id.nav_village_interest);

        navigationView.setNavigationItemSelectedListener(item -> {

            disableInputFocus();   // ⭐ CLOSE INPUT STATE

            handleNavigationItemClick(item);
            return true;
        });
    }

    private void setupDatePickers() {
        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePicker(etEndDate));
    }

    private void showDatePicker(EditText target) {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    String date = String.format(Locale.getDefault(),
                            "%02d-%02d-%04d", day, month + 1, year);
                    target.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    // ===================== MAIN LOGIC =====================

    private void calculateInterest() {

        String principalStr = etPrincipal.getText().toString().trim();
        String rateStr = etRatePerHundred.getText().toString().trim();
        String startStr = etStartDate.getText().toString().trim();
        String endStr = etEndDate.getText().toString().trim();

        if (principalStr.isEmpty() || rateStr.isEmpty()
                || startStr.isEmpty() || endStr.isEmpty()) {

            tvInterestResult.setText("⚠ Please fill all fields!");
            return;
        }

        try {

            double principal = Double.parseDouble(principalStr);
            double rate = Double.parseDouble(rateStr);

            if (principal <= 0 || rate <= 0) {
                tvInterestResult.setText("⚠ Principal and rate must be positive!");
                return;
            }

            Date startDate = sdf.parse(startStr);
            Date endDate = sdf.parse(endStr);

            if (startDate == null || endDate == null) {
                tvInterestResult.setText("⚠ Invalid Date!");
                return;
            }

            if (!endDate.after(startDate)) {
                tvInterestResult.setText("⚠ End date must be after start date!");
                return;
            }

            Calendar startCal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();

            startCal.setTime(startDate);
            endCal.setTime(endDate);

            int totalMonths = 0;

            while (startCal.before(endCal)) {
                startCal.add(Calendar.MONTH, 1);
                if (!startCal.after(endCal)) {
                    totalMonths++;
                } else {
                    startCal.add(Calendar.MONTH, -1);
                    break;
                }
            }

            long diffMillis =
                    endCal.getTimeInMillis()
                            - startCal.getTimeInMillis();

            int remainingDays =
                    (int) (diffMillis / (1000 * 60 * 60 * 24));

            double monthlyInterest =
                    (principal * rate) / 100.0;

            double interestMonths =
                    monthlyInterest * totalMonths;

            double interestDays =
                    (monthlyInterest / 30.0) * remainingDays;

            double totalInterest =
                    interestMonths + interestDays;

            double totalPayable =
                    principal + totalInterest;

            tvInterestResult.setText(
                            "Total Months: " + totalMonths + "\n" +
                            "Remaining Days: " + remainingDays + "\n\n" +

                            "💰 Monthly Interest: ₹" +
                            formatCurrency(monthlyInterest) + "\n\n" +

                            "💵 Interest for "+ totalMonths +" Months: ₹" +
                            formatCurrency(interestMonths) + "\n" +

                            "💵 Interest for " + remainingDays + " Days: ₹" +
                            formatCurrency(interestDays) + "\n\n" +

                            "🧾 Total Interest: ₹" +
                            formatCurrency(totalInterest) + "\n" +

                            "🏦 Total Payable: ₹" +
                            formatCurrency(totalPayable)
            );

        } catch (NumberFormatException e) {
            tvInterestResult.setText("⚠ Invalid number entered!");
        } catch (ParseException e) {
            tvInterestResult.setText("⚠ Date format must be DD-MM-YYYY");
        }
    }

    // ===================== FORMAT =====================

    private String formatCurrency(double value) {

        double abs = Math.abs(value);

        if (abs >= 1e9 || (abs > 0 && abs < 1e-6)) {
            return String.format(Locale.getDefault(),
                    "%.6E", value);
        }

        NumberFormat formatter =
                NumberFormat.getNumberInstance(
                        new Locale("en", "IN"));

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
    // ===================== NAVIGATION =====================

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