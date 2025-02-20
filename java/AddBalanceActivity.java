package com.example.budgetify1;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddBalanceActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_balance);

        // Set up the back arrow button click listener
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            // Finish the current activity and return to the previous activity
            onBackPressed();
        });

        // Set up the date picker for the date field
        EditText dateInput = findViewById(R.id.date);
        dateInput.setOnClickListener(v -> {
            // Get the current date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create and show the DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddBalanceActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Format the date and set it in the EditText
                        String formattedDate = String.format("%02d/%02d/%04d", monthOfYear + 1, dayOfMonth, year1);
                        dateInput.setText(formattedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Set up the Add Balance button click listener
        findViewById(R.id.balanceInput).setOnClickListener(v -> onAddBalanceClicked(v));
    }

    // Method to update the total balance in SharedPreferences
    public void updateBalance(double amount) {
        SharedPreferences prefs = getSharedPreferences("FinanceData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        double currentBalance = prefs.getFloat("totalBalance", 0);
        editor.putFloat("totalBalance", (float) (currentBalance + amount));
        editor.apply();
    }

    // Method to handle the add balance button click
    public void onAddBalanceClicked(View view) {
        @SuppressLint("WrongViewCast")
        EditText dateInput = findViewById(R.id.date);
        @SuppressLint("WrongViewCast")
        EditText amountInput = findViewById(R.id.amount);

        String date = dateInput.getText().toString();
        String amountText = amountInput.getText().toString();

        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(amountText)) {
            // Show error if fields are empty
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountText);

        // Save the balance information
        saveBalance(date, amount);

        // Update the total balance
        updateBalance(amount);

        // Show success message
        Toast.makeText(this, "Balance Updated!", Toast.LENGTH_SHORT).show();

        // Send updated data back to CategoriesActivity
        Intent intent = new Intent(this, CategoriesActivity.class);
        intent.putExtra("updatedBalance", amount);  // Pass updated balance
        startActivity(intent);  // Launch CategoriesActivity with updated data

        // Finish the current activity
        finish();
    }

    // Method to save the balance (e.g., date and amount) in SharedPreferences
    public void saveBalance(String date, double amount) {
        SharedPreferences prefs = getSharedPreferences("FinanceData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Save the balance details (date and amount)
        editor.putString("balance_date", date);
        editor.putFloat("balance_amount", (float) amount);  // Store as float
        editor.apply();
    }
}
