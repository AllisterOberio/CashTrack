package com.example.budgetify1;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class AddEntertainmentActivity extends AppCompatActivity {

    private EditText dateEditText, amountEditText, expenseTitleEditText;
    private Button addButton;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_entertainment);

        // Set up the back arrow button
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> onBackPressed());

        // Initialize UI components
        dateEditText = findViewById(R.id.entertainment_date_input);
        amountEditText = findViewById(R.id.entertainment_price_input);
        expenseTitleEditText = findViewById(R.id.entertainment_details_input);
        addButton = findViewById(R.id.btn_save_entertainment);

        // Set up the date picker
        dateEditText.setOnClickListener(v -> showDatePicker());

        // Set up the "Add Entertainment" button click listener
        addButton.setOnClickListener(v -> {
            try {
                saveEntertainmentExpense();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddEntertainmentActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    @SuppressLint("DefaultLocale") String formattedDate = String.format("%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear);
                    dateEditText.setText(formattedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void saveEntertainmentExpense() throws JSONException {
        String date = dateEditText.getText().toString().trim();
        String amountText = amountEditText.getText().toString().trim();
        String expenseTitle = expenseTitleEditText.getText().toString().trim();

        if (date.isEmpty() || amountText.isEmpty() || expenseTitle.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            updateTotalExpenseAndBalance(amount);  // Update both the total expense and balance

            // Retrieve stored entertainment records
            SharedPreferences prefs = getSharedPreferences("FinanceData", MODE_PRIVATE);
            String existingData = prefs.getString("entertainmentRecords", "[]");

            // Convert to JSONArray and add new transaction
            JSONArray entertainmentArray = new JSONArray(existingData);
            JSONObject newTransaction = new JSONObject();
            newTransaction.put("details", expenseTitle);
            newTransaction.put("date", date);
            newTransaction.put("price", String.format("₱%,.2f", amount));

            entertainmentArray.put(newTransaction);

            // Save updated array back to SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("entertainmentRecords", entertainmentArray.toString());
            editor.apply();

            // Pass data and navigate
            Intent intent = new Intent(AddEntertainmentActivity.this, EntertainmentRedirectActivity.class);
            intent.putExtra("entertainmentRecords", entertainmentArray.toString());
            startActivity(intent);
            finish();

        } catch (NumberFormatException | JSONException e) {
            Toast.makeText(this, "Error saving entertainment data", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to update total expenses and total balance in SharedPreferences
    private void updateTotalExpenseAndBalance(double amount) {
        SharedPreferences prefs = getSharedPreferences("FinanceData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Update the total expense
        float currentTotalExpense = prefs.getFloat("totalExpense", 0);
        float newTotalExpense = currentTotalExpense + (float) amount;
        editor.putFloat("totalExpense", newTotalExpense);

        // Update the total balance (subtract the expense from balance)
        float currentTotalBalance = prefs.getFloat("totalBalance", 0);
        float newTotalBalance = currentTotalBalance - (float) amount;
        editor.putFloat("totalBalance", newTotalBalance);

        editor.apply();
    }
}
