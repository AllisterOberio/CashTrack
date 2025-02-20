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

public class TransportRedirectActivity extends AppCompatActivity {

    private TextView totalBalanceView, totalExpenseView;
    private LinearLayout transactionList;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transport_add);

        // Initialize UI components
        totalBalanceView = findViewById(R.id.totalBalance);
        totalExpenseView = findViewById(R.id.totalExpense);
        transactionList = findViewById(R.id.transaction_list);
        Button addTransportButton = findViewById(R.id.btn_save);
        ImageView backArrow = findViewById(R.id.backArrow);

        TextView clearTransactions = findViewById(R.id.clear_transactions);
        clearTransactions.setOnClickListener(v -> clearAllTransactions());

        // Back button functionality
        backArrow.setOnClickListener(v -> onBackPressed());

        // Retrieve and display data
        displayTransportData();
        updateBalanceAndExpense();

        // Add Transport button click
        addTransportButton.setOnClickListener(v -> {
            Intent intent = new Intent(TransportRedirectActivity.this, AddTransportActivity.class);
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

    private void displayTransportData() {
        SharedPreferences prefs = getSharedPreferences("FinanceData", MODE_PRIVATE);
        String transportData = prefs.getString("transportRecords", "[]");

        try {
            JSONArray transportArray = new JSONArray(transportData);

            // Clear previous views
            transactionList.removeAllViews();

            for (int i = 0; i < transportArray.length(); i++) {
                JSONObject transaction = transportArray.getJSONObject(i);
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
        ImageView transportIcon = new ImageView(this);
        transportIcon.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
        transportIcon.setImageResource(R.drawable.ic_transport);

        // Add ImageView to CardView
        cardView.addView(transportIcon);

        // Create Vertical LinearLayout for Text Details
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        textContainer.setOrientation(LinearLayout.VERTICAL);

        // Create Transport Details TextView
        TextView transportDetails = new TextView(this);
        transportDetails.setText(details);
        transportDetails.setTextSize(16);
        transportDetails.setTextColor(Color.BLACK);
        transportDetails.setTypeface(null, android.graphics.Typeface.BOLD);

        // Create Transport Date TextView
        TextView transportDate = new TextView(this);
        transportDate.setText(date);
        transportDate.setTextSize(13);
        transportDate.setTextColor(Color.GRAY);

        // Add text views to textContainer
        textContainer.addView(transportDetails);
        textContainer.addView(transportDate);

        // Create Transport Price TextView
        TextView transportPrice = new TextView(this);
        transportPrice.setText(price);
        transportPrice.setTextSize(16);
        transportPrice.setTextColor(Color.BLACK);
        transportPrice.setTypeface(null, android.graphics.Typeface.BOLD);
        transportPrice.setGravity(Gravity.END);
        transportPrice.setPadding(8, 0, 0, 0);

        // Add views to transactionItem
        transactionItem.addView(cardView);
        transactionItem.addView(textContainer);
        transactionItem.addView(transportPrice);

        // Add transaction item to the main transaction list
        transactionList.addView(transactionItem);
    }

    private void clearAllTransactions() {
        SharedPreferences prefs = getSharedPreferences("FinanceData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Clear stored transactions
        editor.putString("transportRecords", "[]");
        editor.apply();

        // Clear transaction list UI
        transactionList.removeAllViews();

        // Show feedback message
        Toast.makeText(this, "All transactions cleared", Toast.LENGTH_SHORT).show();
    }
}
