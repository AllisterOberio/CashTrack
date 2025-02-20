package com.example.budgetify1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransactionHistory extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton hamburgerMenu;
    private SharedPreferences sharedPreferences;
    private TextView totalBalanceView, totalExpenseView;
    private LinearLayout transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_history);

        // Initialize UI components
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        hamburgerMenu = findViewById(R.id.hamburgerMenu);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        totalBalanceView = findViewById(R.id.totalBalance);
        totalExpenseView = findViewById(R.id.totalExpense);
        transactionList = findViewById(R.id.transaction_list);

        // Load and display transaction data
        updateBalanceAndExpense();
        displayTransactionData();

        // Hamburger Menu Click Listener
        hamburgerMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Find Navigation Header View
        View headerView = navigationView.getHeaderView(0);

        // Profile Button
        TextView profileButton = headerView.findViewById(R.id.profile);
        profileButton.setOnClickListener(v -> {
            updateRoundedMenuItem("profile");
            startActivity(new Intent(TransactionHistory.this, ProfileActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        // Categories Button
        TextView categoriesButton = headerView.findViewById(R.id.category);
        categoriesButton.setOnClickListener(v -> {
            updateRoundedMenuItem("category");
            startActivity(new Intent(TransactionHistory.this, CategoriesActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        // Logout Button
        TextView logoutButton = headerView.findViewById(R.id.logout);
        logoutButton.setOnClickListener(v -> showLogoutDialog());

        // Highlight the active menu item
        highlightActiveMenuItem();

        // Navigation Drawer Item Clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                updateRoundedMenuItem("profile");
                startActivity(new Intent(TransactionHistory.this, ProfileActivity.class));
            } else if (item.getItemId() == R.id.nav_categories) {
                updateRoundedMenuItem("category");
                startActivity(new Intent(TransactionHistory.this, CategoriesActivity.class));
            } else if (item.getItemId() == R.id.nav_logout) {
                showLogoutDialog();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh transactions when returning from another activity
        updateBalanceAndExpense();
        displayTransactionData();
    }

    @SuppressLint("DefaultLocale")
    private void updateBalanceAndExpense() {
        SharedPreferences prefs = getSharedPreferences("FinanceData", MODE_PRIVATE);
        double totalBalance = prefs.getFloat("totalBalance", 0);
        double totalExpense = prefs.getFloat("totalExpense", 0);

        totalBalanceView.setText(String.format("₱%,.2f", totalBalance));
        totalExpenseView.setText(String.format("-₱%,.2f", totalExpense));
    }


    private void updateRoundedMenuItem(String selectedItem) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("active_menu_item", selectedItem);
        editor.apply();
    }

    private void highlightActiveMenuItem() {
        String activeMenuItem = sharedPreferences.getString("active_menu_item", "transaction_history");

        View headerView = navigationView.getHeaderView(0);
        TextView profileButton = headerView.findViewById(R.id.profile);
        TextView categoriesButton = headerView.findViewById(R.id.category);
        TextView transactionHistoryButton = headerView.findViewById(R.id.transaction_history);

        profileButton.setBackgroundResource(0);
        categoriesButton.setBackgroundResource(0);
        transactionHistoryButton.setBackgroundResource(0);

        if ("profile".equals(activeMenuItem)) {
            profileButton.setBackgroundResource(R.drawable.rounded_menu_item);
        } else if ("category".equals(activeMenuItem)) {
            categoriesButton.setBackgroundResource(R.drawable.rounded_menu_item);
        } else if ("transaction_history".equals(activeMenuItem)) {
            transactionHistoryButton.setBackgroundResource(R.drawable.rounded_menu_item);
        }
    }

    private void showLogoutDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.logout, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false);

        AlertDialog alertDialog = dialogBuilder.create();

        // Remove default dialog background
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        alertDialog.show();

        Button confirmLogout = dialogView.findViewById(R.id.confirmLogout);
        Button cancelLogout = dialogView.findViewById(R.id.cancelLogout);

        confirmLogout.setOnClickListener(v -> {
            alertDialog.dismiss();
            logoutUser();
        });

        cancelLogout.setOnClickListener(v -> alertDialog.dismiss());
    }

    private void logoutUser() {
        sharedPreferences.edit().clear().apply();
        Intent intent = new Intent(TransactionHistory.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void displayTransactionData() {
        SharedPreferences prefs = getSharedPreferences("FinanceData", MODE_PRIVATE);
        String[] categories = {"transport", "food", "medicine", "groceries", "rent", "gifts", "savings", "entertainment"};
        int[] icons = {R.drawable.ic_transport, R.drawable.ic_food, R.drawable.ic_medicine, R.drawable.ic_groceries,
                R.drawable.ic_rent, R.drawable.ic_gifts, R.drawable.ic_travel, R.drawable.ic_entertainment};
        transactionList.removeAllViews();

        for (int i = 0; i < categories.length; i++) {
            try {
                JSONArray jsonArray = new JSONArray(prefs.getString(categories[i] + "Records", "[]"));
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject transaction = jsonArray.getJSONObject(j);
                    addTransactionItem(transaction.getString("details"),
                            transaction.getString("date"),
                            transaction.getString("price"),
                            icons[i]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addTransactionItem(String details, String date, String price, int iconRes) {
        LinearLayout transactionItem = new LinearLayout(this);
        transactionItem.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        transactionItem.setOrientation(LinearLayout.HORIZONTAL);
        transactionItem.setPadding(16, 16, 16, 16);

        // Create an icon container (Card-like)
        LinearLayout iconContainer = new LinearLayout(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(120, 120); // Adjusted size
        iconParams.setMargins(0, 0, 32, 0);
        iconContainer.setLayoutParams(iconParams);
        iconContainer.setBackgroundColor(Color.parseColor("#DEEFF5"));
        iconContainer.setGravity(Gravity.CENTER);
        iconContainer.setPadding(16, 16, 16, 16);

        ImageView icon = new ImageView(this);
        icon.setLayoutParams(new LinearLayout.LayoutParams(120, 120)); // Adjusted icon size
        icon.setImageResource(iconRes);
        iconContainer.addView(icon);

        // Create a vertical layout for text details
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setPadding(16, 0, 16, 0);

        TextView detailsView = new TextView(this);
        detailsView.setText(details);
        detailsView.setTextSize(16);
        detailsView.setTextColor(Color.BLACK);
        detailsView.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView dateView = new TextView(this);
        dateView.setText(date);
        dateView.setTextSize(13);
        dateView.setTextColor(Color.GRAY);

        textContainer.addView(detailsView);
        textContainer.addView(dateView);

        // Price view aligned to the right
        TextView priceView = new TextView(this);
        priceView.setText(price);
        priceView.setTextSize(16);
        priceView.setTextColor(Color.BLACK);
        priceView.setTypeface(null, android.graphics.Typeface.BOLD);
        priceView.setGravity(Gravity.END);
        LinearLayout.LayoutParams priceParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        priceParams.setMargins(16, 0, 0, 0);
        priceView.setLayoutParams(priceParams);

        transactionItem.addView(iconContainer);
        transactionItem.addView(textContainer);
        transactionItem.addView(priceView);
        transactionList.addView(transactionItem);
    }
}
