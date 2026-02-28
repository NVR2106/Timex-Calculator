package com.example.timecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Locale;

public class AgeCalculatorActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    private Spinner spinnerDobMonth, spinnerDobDay, spinnerDobYear;
    private Spinner spinnerTargetMonth, spinnerTargetDay, spinnerTargetYear;

    private Button btnCalculate;
    private TextView tvResult;

    private int currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_calculator);

        getWindow().setStatusBarColor(
                ContextCompat.getColor(this, R.color.teal_700));

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigationItemClick(item);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        spinnerDobMonth = findViewById(R.id.spinnerDobMonth);
        spinnerDobDay = findViewById(R.id.spinnerDobDay);
        spinnerDobYear = findViewById(R.id.spinnerDobYear);

        spinnerTargetMonth = findViewById(R.id.spinnerTargetMonth);
        spinnerTargetDay = findViewById(R.id.spinnerTargetDay);
        spinnerTargetYear = findViewById(R.id.spinnerTargetYear);

        btnCalculate = findViewById(R.id.btn_calculate);
        tvResult = findViewById(R.id.tv_result);

        setupDateSpinners();

        btnCalculate.setOnClickListener(v -> calculateAge());
    }

    private void setupDateSpinners() {

        String[] months = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };

        currentYear = Calendar.getInstance().get(Calendar.YEAR);

        Integer[] years = new Integer[120];
        for (int i = 0; i < 120; i++) {
            years[i] = currentYear - i;
        }

        ArrayAdapter<String> monthAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        months);

        ArrayAdapter<Integer> yearAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        years);

        spinnerDobMonth.setAdapter(monthAdapter);
        spinnerDobYear.setAdapter(yearAdapter);

        spinnerTargetMonth.setAdapter(monthAdapter);
        spinnerTargetYear.setAdapter(yearAdapter);

        updateDays(spinnerDobMonth, spinnerDobYear, spinnerDobDay);
        updateDays(spinnerTargetMonth, spinnerTargetYear, spinnerTargetDay);

        // ✅ Correct listeners (no custom class)
        spinnerDobMonth.setOnItemSelectedListener(createListener(
                () -> updateDays(spinnerDobMonth, spinnerDobYear, spinnerDobDay)));

        spinnerDobYear.setOnItemSelectedListener(createListener(
                () -> updateDays(spinnerDobMonth, spinnerDobYear, spinnerDobDay)));

        spinnerTargetMonth.setOnItemSelectedListener(createListener(
                () -> updateDays(spinnerTargetMonth, spinnerTargetYear, spinnerTargetDay)));

        spinnerTargetYear.setOnItemSelectedListener(createListener(
                () -> updateDays(spinnerTargetMonth, spinnerTargetYear, spinnerTargetDay)));

        // Default target = today
        Calendar today = Calendar.getInstance();
        spinnerTargetDay.setSelection(today.get(Calendar.DAY_OF_MONTH) - 1);
        spinnerTargetMonth.setSelection(today.get(Calendar.MONTH));
        spinnerTargetYear.setSelection(0);
    }

    // Cleaner reusable listener
    private AdapterView.OnItemSelectedListener createListener(Runnable action) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                action.run();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
    }

    private void updateDays(Spinner monthSpinner, Spinner yearSpinner, Spinner daySpinner) {

        int month = monthSpinner.getSelectedItemPosition() + 1;
        int year = (int) yearSpinner.getSelectedItem();

        int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();

        Integer[] days = new Integer[daysInMonth];
        for (int i = 0; i < daysInMonth; i++) {
            days[i] = i + 1;
        }

        ArrayAdapter<Integer> dayAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        days);

        daySpinner.setAdapter(dayAdapter);
    }

    private void calculateAge() {

        try {

            LocalDate dob = LocalDate.of(
                    (int) spinnerDobYear.getSelectedItem(),
                    spinnerDobMonth.getSelectedItemPosition() + 1,
                    (int) spinnerDobDay.getSelectedItem()
            );

            LocalDate target = LocalDate.of(
                    (int) spinnerTargetYear.getSelectedItem(),
                    spinnerTargetMonth.getSelectedItemPosition() + 1,
                    (int) spinnerTargetDay.getSelectedItem()
            );

            if (dob.isAfter(target)) {
                tvResult.setText("⚠ Target date must be after Date of Birth!");
                return;
            }

            Period age = Period.between(dob, target);
            long totalDays = ChronoUnit.DAYS.between(dob, target);

            // Born day name
            DayOfWeek bornDay = dob.getDayOfWeek();
            String bornDayFormatted =
                    bornDay.getDisplayName(
                            TextStyle.FULL,
                            Locale.getDefault());

// ✅ Next Birthday Calculation
            LocalDate nextBirthday = dob.withYear(target.getYear());

            if (!nextBirthday.isAfter(target)) {
                nextBirthday = nextBirthday.plusYears(1);
            }

// Days remaining
            long daysLeft =
                    ChronoUnit.DAYS.between(target, nextBirthday);

// ✅ Next birthday day name
            DayOfWeek nextBirthDayName =
                    nextBirthday.getDayOfWeek();

            String nextBirthDayFormatted =
                    nextBirthDayName.getDisplayName(
                            TextStyle.FULL,
                            Locale.getDefault());

// ✅ Format date nicely
            String formattedNextBirthday =
                    nextBirthday.format(
                            java.time.format.DateTimeFormatter
                                    .ofPattern("dd MMM yyyy"));

// ✅ FINAL RESULT
            String result =
                    "🎯 AGE RESULT\n\n" +

                            "Age: "
                            + age.getYears() + " Years "
                            + age.getMonths() + " Months "
                            + age.getDays() + " Days\n\n" +

                            "Total Days Lived: "
                            + totalDays + "\n\n" +

                            "Born On: "
                            + bornDayFormatted + "\n\n" +

                            "🎂 Next Birthday:\n"
                            + formattedNextBirthday
                            + " (" + nextBirthDayFormatted + ")\n\n" +

                            "⏳ Days Left: "
                            + daysLeft + " Days";

            tvResult.setText(result);

        } catch (Exception e) {
            tvResult.setText("⚠ Invalid Date Selected!");
        }
    }



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