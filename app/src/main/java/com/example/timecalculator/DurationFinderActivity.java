package com.example.timecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class DurationFinderActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    private EditText startTimeInput, endTimeInput;
    private Spinner startAmPm, endAmPm;
    private Button findDurationBtn;
    private TextView durationResult;

    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("hh:mm a", Locale.getDefault());

    // Strict time validation (hh:mm format)
    private final Pattern timePattern =
            Pattern.compile("^(0?[1-9]|1[0-2]):[0-5][0-9]$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duration_finder);



        getWindow().setStatusBarColor(
                ContextCompat.getColor(this, R.color.teal_700)
        );

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        navigationView.setCheckedItem(R.id.nav_duration_finder);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> {

            disableInputFocus();   // ⭐ IMPORTANT

            drawerLayout.openDrawer(GravityCompat.START);
        });

        drawerLayout.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        disableInputFocus();
                    }
                });

        navigationView.setNavigationItemSelectedListener(item -> {

            disableInputFocus();   // ⭐ CLOSE INPUT STATE

            handleNavigationItemClick(item);
            return true;
        });

        startTimeInput = findViewById(R.id.startTime);
        endTimeInput = findViewById(R.id.endTime);

        startAmPm = findViewById(R.id.startAmPm);
        endAmPm = findViewById(R.id.endAmPm);

        findDurationBtn = findViewById(R.id.findDuration);
        durationResult = findViewById(R.id.durationResult);

        setupSpinners();

        findDurationBtn.setOnClickListener(v -> {
            hideKeyboard();       // ✅ Close keyboard
            calculateDuration();  // ✅ Calculate duration
        });
    }

    private void setupSpinners() {
        String[] ampm = {"AM", "PM"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                ampm
        );

        startAmPm.setAdapter(adapter);
        endAmPm.setAdapter(adapter);
    }

    private void calculateDuration() {

        String startStr = startTimeInput.getText().toString().trim();
        String endStr = endTimeInput.getText().toString().trim();

        if (startStr.isEmpty() || endStr.isEmpty()) {
            Toast.makeText(this,
                    "Please enter both times",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate time format
        if (!timePattern.matcher(startStr).matches()
                || !timePattern.matcher(endStr).matches()) {

            durationResult.setText("⚠ Enter time in hh:mm format: ");
            return;
        }

        String startTime = startStr + " " + startAmPm.getSelectedItem();
        String endTime = endStr + " " + endAmPm.getSelectedItem();

        try {

            Date startDate = timeFormat.parse(startTime);
            Date endDate = timeFormat.parse(endTime);

            if (startDate == null || endDate == null) {
                durationResult.setText("⚠ Invalid Time!");
                return;
            }

            long diff = endDate.getTime() - startDate.getTime();

            // If end time is next day
            if (diff < 0) {
                diff += 24 * 60 * 60 * 1000;
            }

            long totalMinutes = diff / (1000 * 60);
            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;

            durationResult.setText(
                    "⏱ Duration: " + hours + " hours " +
                            minutes + " minutes"
            );

        } catch (ParseException e) {
            durationResult.setText("⚠ Invalid Time Format!");
        }
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