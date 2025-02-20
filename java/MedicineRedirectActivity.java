package com.example.budgetify1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MedicineRedirectActivity extends AppCompatActivity {

    private TextView totalBalanceView, totalExpenseView;
    private LinearLayout transactionList;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicine_add);

        // Initialize UI components
        totalBalanceView = findViewById(R.id.totalBalance);
        totalExpenseView = findViewById(R.id.totalExpense);
        transactionList = findViewById(R.id.transaction_list);
        Button addMedicineButton = findViewById(R.id.btn_save);
        ImageView backArrow = findViewById(R.id.backArrow);

        TextView clearTransactions = findViewById(R.id.clear_transactions);
        clearTransactions.setOnClickListener(v -> clearAllTransactions());

        // Back button functionality
        backArrow.setOnClickListener(v -> onBackPressed());

        // Retrieve and display data
        displayMedicineData();
        updateBalanceAndExpense();

        // Add Medicine button click
        addMedicineButton.setOnClickListener(v -> {
            Intent intent = new Intent(MedicineRedirectActivity.this, AddMedicineActivity.class);
            startActivity(intent);
        });
    }

    @SuppressLint("DefaultLocale")
    private void updateBalanceAndExpense() {
        SharedPreferences prefs = getSharedPreferences("FinanceData", MODE_PRIVATE);

        // Retrieve the current balance and expense
        float totalBalance = prefs.getFloat("totalBalance", 0.0f);
        float totalExpense = prefs.getFloat("totalExpense", 0.0f);

        // Update the UI with the new values
        totalBalanceView.setText(String.format("₱%,.2f", totalBalance));
        totalExpenseView.setText(String.format("-₱%,.2f", totalExpense));
    }

    private void displayMedicineData() {
        SharedPreferences prefs = getSharedPreferences("FinanceData", MODE_PRIVATE);
        String medicineData = prefs.getString("medicineRecords", "[]");

        try {
            JSONArray medicineArray = new JSONArray(medicineData);

            // Clear previous views
            transactionList.removeAllViews();

            for (int i = 0; i < medicineArray.length(); i++) {
                JSONObject transaction = medicineArray.getJSONObject(i);
                addTransactionItem(transaction.getString("details"),
                        transaction.getString("date"),
                        transaction.getString("price"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addTransactionItem(String details, String date, String price) {
        // Create main LinearLayout
        LinearLayout transactionItem = new LinearLayout(this);
        transactionItem.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        transactionItem.setOrientation(LinearLayout.HORIZONTAL);
        transactionItem.setPadding(16, 16, 16, 16);

        // Create CardView (for icon background)
        LinearLayout cardView = new LinearLayout(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(120, 120);
        cardParams.setMargins(0, 0, 16, 0);
        cardView.setLayoutParams(cardParams);
        cardView.setBackgroundColor(Color.parseColor("#DEEFF5")); // Light green background
        cardView.setGravity(Gravity.CENTER);

        // Create ImageView (icon)
        ImageView medicineIcon = new ImageView(this);
        medicineIcon.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
        medicineIcon.setImageResource(R.drawable.ic_medicine); // Replace with actual medicine icon

        // Add ImageView to CardView
        cardView.addView(medicineIcon);

        // Create Vertical LinearLayout for Text Details
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        textContainer.setOrientation(LinearLayout.VERTICAL);

        // Create Medicine Details TextView
        TextView medicineDetails = new TextView(this);
        medicineDetails.setText(details);
        medicineDetails.setTextSize(16);
        medicineDetails.setTextColor(Color.BLACK);
        medicineDetails.setTypeface(null, android.graphics.Typeface.BOLD);

        // Create Medicine Date TextView
        TextView medicineDate = new TextView(this);
        medicineDate.setText(date);
        medicineDate.setTextSize(13);
        medicineDate.setTextColor(Color.GRAY);

        // Add text views to textContainer
        textContainer.addView(medicineDetails);
        textContainer.addView(medicineDate);

        // Create Medicine Price TextView
        TextView medicinePrice = new TextView(this);
        medicinePrice.setText(price);
        medicinePrice.setTextSize(16);
        medicinePrice.setTextColor(Color.BLACK);
        medicinePrice.setTypeface(null, android.graphics.Typeface.BOLD);
        medicinePrice.setGravity(Gravity.END);
        medicinePrice.setPadding(8, 0, 0, 0);

        // Add views to transactionItem
        transactionItem.addView(cardView);
        transactionItem.addView(textContainer);
        transactionItem.addView(medicinePrice);

        // Add transaction item to the main transaction list
        transactionList.addView(transactionItem);
    }

    private void clearAllTransactions() {
        SharedPreferences prefs = getSharedPreferences("FinanceData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Clear stored transactions
        editor.putString("medicineRecords", "[]");
        editor.apply();

        // Clear transaction list UI
        transactionList.removeAllViews();

        // Show feedback message
        Toast.makeText(this, "All transactions cleared", Toast.LENGTH_SHORT).show();
    }
}