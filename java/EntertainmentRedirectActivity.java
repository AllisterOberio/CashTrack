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

public class EntertainmentRedirectActivity extends AppCompatActivity {

    private TextView totalBalanceView, totalExpenseView;
    private LinearLayout transactionList;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entertainment_add);

        // Initialize UI components
        totalBalanceView = findViewById(R.id.totalBalance);
        totalExpenseView = findViewById(R.id.totalExpense);
        transactionList = findViewById(R.id.transaction_list);
        Button addEntertainmentButton = findViewById(R.id.btn_save);
        ImageView backArrow = findViewById(R.id.backArrow);

        TextView clearTransactions = findViewById(R.id.clear_transactions);
        clearTransactions.setOnClickListener(v -> clearAllTransactions());

        // Back button functionality
        backArrow.setOnClickListener(v -> onBackPressed());

        // Retrieve and display data
        displayEntertainmentData();
        updateBalanceAndExpense();

        // Add Entertainment button click
        addEntertainmentButton.setOnClickListener(v -> {
            Intent intent = new Intent(EntertainmentRedirectActivity.this, AddEntertainmentActivity.class);
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

    private void displayEntertainmentData() {
        SharedPreferences prefs = getSharedPreferences("FinanceData", MODE_PRIVATE);
        String entertainmentData = prefs.getString("entertainmentRecords", "[]");

        try {
            JSONArray entertainmentArray = new JSONArray(entertainmentData);

            // Clear previous views
            transactionList.removeAllViews();

            for (int i = 0; i < entertainmentArray.length(); i++) {
                JSONObject transaction = entertainmentArray.getJSONObject(i);
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
        ImageView entertainmentIcon = new ImageView(this);
        entertainmentIcon.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
        entertainmentIcon.setImageResource(R.drawable.ic_entertainment); // Replace with actual entertainment icon

        // Add ImageView to CardView
        cardView.addView(entertainmentIcon);

        // Create Vertical LinearLayout for Text Details
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        textContainer.setOrientation(LinearLayout.VERTICAL);

        // Create Entertainment Details TextView
        TextView entertainmentDetails = new TextView(this);
        entertainmentDetails.setText(details);
        entertainmentDetails.setTextSize(16);
        entertainmentDetails.setTextColor(Color.BLACK);
        entertainmentDetails.setTypeface(null, android.graphics.Typeface.BOLD);

        // Create Entertainment Date TextView
        TextView entertainmentDate = new TextView(this);
        entertainmentDate.setText(date);
        entertainmentDate.setTextSize(13);
        entertainmentDate.setTextColor(Color.GRAY);

        // Add text views to textContainer
        textContainer.addView(entertainmentDetails);
        textContainer.addView(entertainmentDate);

        // Create Entertainment Price TextView
        TextView entertainmentPrice = new TextView(this);
        entertainmentPrice.setText(price);
        entertainmentPrice.setTextSize(16);
        entertainmentPrice.setTextColor(Color.BLACK);
        entertainmentPrice.setTypeface(null, android.graphics.Typeface.BOLD);
        entertainmentPrice.setGravity(Gravity.END);
        entertainmentPrice.setPadding(8, 0, 0, 0);

        // Add views to transactionItem
        transactionItem.addView(cardView);
        transactionItem.addView(textContainer);
        transactionItem.addView(entertainmentPrice);

        // Add transaction item to the main transaction list
        transactionList.addView(transactionItem);
    }

    private void clearAllTransactions() {
        SharedPreferences prefs = getSharedPreferences("FinanceData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Clear stored transactions
        editor.putString("entertainmentRecords", "[]");
        editor.apply();

        // Clear transaction list UI
        transactionList.removeAllViews();

        // Show feedback message
        Toast.makeText(this, "All transactions cleared", Toast.LENGTH_SHORT).show();
    }
}
