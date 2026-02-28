package com.example.timecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

public class UnitConverterActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    private Spinner spinnerCategory, spinnerFrom, spinnerTo;
    private EditText etValue;
    private TextView tvResult;

    private HashMap<String, Double> conversionMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_converter);

        initViews();
        setupToolbar();
        setupNavigation();
        setupCategorySpinner();

        drawerLayout.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        disableInputFocus();
                    }
                });

        findViewById(R.id.btnConvert).setOnClickListener(v -> {
            hideKeyboard();
            convertUnits();
        });

        setupLengthUnits();
    }

    // ================= INIT =================

    private void initViews() {

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);

        etValue = findViewById(R.id.etValue);
        tvResult = findViewById(R.id.tvResult);

        navigationView.setCheckedItem(R.id.nav_unit_converter);
    }

    // ================= TOOLBAR =================

    private void setupToolbar() {

        setSupportActionBar(toolbar);

        getWindow().setStatusBarColor(
                ContextCompat.getColor(this, R.color.teal_700));

        toolbar.setNavigationOnClickListener(v -> {

            disableInputFocus();   // ⭐ IMPORTANT

            drawerLayout.openDrawer(GravityCompat.START);
        });
    }

    // ================= NAVIGATION =================

    private void setupNavigation() {

        navigationView.setNavigationItemSelectedListener(item -> {

            disableInputFocus();   // ⭐ CLOSE INPUT STATE

            handleNavigationItemClick(item);
            return true;
        });
    }

    // ================= CATEGORY =================

    private void setupCategorySpinner() {

        String[] categories = {
                "Length","Weight","Area","Volume",
                "Temperature","Speed","Time",
                "Data","Energy","Pressure"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        categories);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(
                new SimpleItemSelectedListener() {
                    @Override
                    public void onItemSelected(int position) {

                        switch (position) {
                            case 0: setupLengthUnits(); break;
                            case 1: setupWeightUnits(); break;
                            case 2: setupAreaUnits(); break;
                            case 3: setupVolumeUnits(); break;
                            case 4: setupTemperatureUnits(); break;
                            case 5: setupSpeedUnits(); break;
                            case 6: setupTimeUnits(); break;
                            case 7: setupDataUnits(); break;
                            case 8: setupEnergyUnits(); break;
                            case 9: setupPressureUnits(); break;
                        }
                    }
                });
    }

    // ================= UNIT SETUPS =================

    private void setupLengthUnits() {

        setUnits(new String[]{
                "Millimeter",
                "Centimeter",
                "Meter",
                "Kilometer",
                "Inch",
                "Feet",
                "Yard",
                "Mile"
        });

        conversionMap = new HashMap<>();

        // Metric System
        conversionMap.put("Millimeter", 0.001);
        conversionMap.put("Centimeter", 0.01);
        conversionMap.put("Meter", 1.0);
        conversionMap.put("Kilometer", 1000.0);

        // Imperial System
        conversionMap.put("Inch", 0.0254);
        conversionMap.put("Feet", 0.3048);
        conversionMap.put("Yard", 0.9144);
        conversionMap.put("Mile", 1609.34);
    }
    private void setupWeightUnits() {

        setUnits(new String[]{
                "Kilogram","Gram","Quintal",
                "Ton","Pound","Ounce"
        });

        conversionMap = new HashMap<>();
        conversionMap.put("Kilogram",1.0);
        conversionMap.put("Gram",0.001);
        conversionMap.put("Quintal",100.0);
        conversionMap.put("Ton",1000.0);
        conversionMap.put("Pound",0.453592);
        conversionMap.put("Ounce",0.0283495);
    }

    private void setupAreaUnits() {

        setUnits(new String[]{
                "Square Millimeter",
                "Square Centimeter",
                "Square Meter",
                "Square Feet",
                "Square Yard",
                "Acre",
                "Hectare",
                "Cent",
                "Ground"
        });

        conversionMap = new HashMap<>();

        // Metric Units
        conversionMap.put("Square Millimeter", 0.000001);
        conversionMap.put("Square Centimeter", 0.0001);
        conversionMap.put("Square Meter", 1.0);

        // Building / House Units
        conversionMap.put("Square Feet", 0.092903);
        conversionMap.put("Square Yard", 0.836127);

        // Land Units
        conversionMap.put("Acre", 4046.86);
        conversionMap.put("Hectare", 10000.0);

        // Indian Local Units
        conversionMap.put("Cent", 40.4686);
        conversionMap.put("Ground", 222.967);
    }

    private void setupVolumeUnits() {

        setUnits(new String[]{
                "Litre","Millilitre","Gallon"
        });

        conversionMap = new HashMap<>();
        conversionMap.put("Litre",1.0);
        conversionMap.put("Millilitre",0.001);
        conversionMap.put("Gallon",3.78541);
    }

    private void setupTemperatureUnits() {
        setUnits(new String[]{"Celsius","Fahrenheit","Kelvin"});
        conversionMap = null;
    }

    private void setupSpeedUnits() {

        setUnits(new String[]{"m/s","km/h","mph","knot"});

        conversionMap = new HashMap<>();
        conversionMap.put("m/s",1.0);
        conversionMap.put("km/h",0.277778);
        conversionMap.put("mph",0.44704);
        conversionMap.put("knot",0.514444);
    }

    private void setupTimeUnits() {

        setUnits(new String[]{
                "Second","Minute","Hour",
                "Day","Week","Month","Year"
        });

        conversionMap = new HashMap<>();
        conversionMap.put("Second",1.0);
        conversionMap.put("Minute",60.0);
        conversionMap.put("Hour",3600.0);
        conversionMap.put("Day",86400.0);
        conversionMap.put("Week",604800.0);
        conversionMap.put("Month",2628000.0);
        conversionMap.put("Year",31536000.0);
    }

    private void setupDataUnits() {

        setUnits(new String[]{"Byte","KB","MB","GB","TB"});

        conversionMap = new HashMap<>();
        conversionMap.put("Byte",1.0);
        conversionMap.put("KB",1024.0);
        conversionMap.put("MB",Math.pow(1024,2));
        conversionMap.put("GB",Math.pow(1024,3));
        conversionMap.put("TB",Math.pow(1024,4));
    }

    private void setupEnergyUnits() {

        setUnits(new String[]{"Joule","Kilojoule","Calorie","kWh"});

        conversionMap = new HashMap<>();
        conversionMap.put("Joule",1.0);
        conversionMap.put("Kilojoule",1000.0);
        conversionMap.put("Calorie",4.184);
        conversionMap.put("kWh",3600000.0);
    }

    private void setupPressureUnits() {

        setUnits(new String[]{"Pascal","Bar","ATM","PSI"});

        conversionMap = new HashMap<>();
        conversionMap.put("Pascal",1.0);
        conversionMap.put("Bar",100000.0);
        conversionMap.put("ATM",101325.0);
        conversionMap.put("PSI",6894.76);
    }

    private void setUnits(String[] units) {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        units);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
    }

    // ================= CONVERSION =================

    private void convertUnits() {

        String input = etValue.getText().toString().trim();

        if(input.isEmpty()){
            tvResult.setText("Enter value");
            return;
        }

        double value;

        try{
            value = Double.parseDouble(input);
        }catch(Exception e){
            tvResult.setText("Invalid number");
            return;
        }

        String category =
                spinnerCategory.getSelectedItem().toString();

        String from =
                spinnerFrom.getSelectedItem().toString();

        String to =
                spinnerTo.getSelectedItem().toString();

        if(category.equals("Temperature")){
            double result =
                    convertTemperature(value,from,to);

            tvResult.setText(
                    "Result: "+formatResult(result)+" "+to);
            return;
        }

        double base = value * conversionMap.get(from);
        double result = base / conversionMap.get(to);

        tvResult.setText(
                "Result: "+formatResult(result)+" "+to);
    }

    private double convertTemperature(
            double value,String from,String to){

        double c;

        if(from.equals("Celsius")) c=value;
        else if(from.equals("Fahrenheit"))
            c=(value-32)*5/9;
        else c=value-273.15;

        if(to.equals("Celsius")) return c;
        if(to.equals("Fahrenheit")) return (c*9/5)+32;
        return c+273.15;
    }

    private String formatResult(double value){

        NumberFormat f =
                NumberFormat.getNumberInstance(
                        Locale.getDefault());

        f.setMaximumFractionDigits(6);
        return f.format(value);
    }

    abstract class SimpleItemSelectedListener
            implements AdapterView.OnItemSelectedListener {

        public abstract void onItemSelected(int position);

        @Override
        public void onItemSelected(
                AdapterView<?> parent,
                View view,int position,long id){
            onItemSelected(position);
        }

        @Override
        public void onNothingSelected(
                AdapterView<?> parent){}
    }

    private void hideKeyboard(){

        View view=getCurrentFocus();
        if(view==null) view=new View(this);

        InputMethodManager imm=
                (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(
                view.getWindowToken(),0);
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

  // -------------------- NAVIGATION --------------------

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