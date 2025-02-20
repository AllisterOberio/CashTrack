package com.example.budgetify1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class CategoriesActivity extends AppCompatActivity {

    private TextView totalBalanceView, totalExpenseView;
    private static final int REQUEST_CODE_FOOD = 100;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton menuButton;
    private SharedPreferences sharedPreferences;

    @SuppressLint({"DefaultLocale", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories);

        // Initialize UI components
        totalBalanceView = findViewById(R.id.totalBalance);
        totalExpenseView = findViewById(R.id.totalExpense);
        menuButton = findViewById(R.id.hamburgerMenu);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        updateBalanceAndExpense();

        // Set up category click listeners
        setupCategoryClick(R.id.category_transport, TransportRedirectActivity.class);
        setupCategoryClick(R.id.category_food, FoodRedirectActivity.class);
        setupCategoryClick(R.id.category_medicine, MedicineRedirectActivity.class);
        setupCategoryClick(R.id.category_groceries, GroceriesRedirectActivity.class);
        setupCategoryClick(R.id.category_rent, RentRedirectActivity.class);
        setupCategoryClick(R.id.category_gifts, GiftsRedirectActivity.class);
        setupCategoryClick(R.id.category_savings, SavingsRedirectActivity.class);
        setupCategoryClick(R.id.category_entertainment, EntertainmentRedirectActivity.class);
        setupCategoryClick(R.id.category_balance, AddBalanceActivity.class);

        ImageButton editBalanceButton = findViewById(R.id.editBalanceButton);
        ImageButton editExpenseButton = findViewById(R.id.editExpenseButton);

        editBalanceButton.setOnClickListener(v -> showEditBalanceDialog(totalBalanceView));
        editExpenseButton.setOnClickListener(v -> showEditExpenseDialog(totalExpenseView));

        // Hamburger Menu Click Listener
        menuButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Add Navigation Functionality for Profile, Transaction History, and Logout
        View headerView = navigationView.getHeaderView(0);

        // Profile Button
        TextView profileButton = headerView.findViewById(R.id.profile);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(CategoriesActivity.this, ProfileActivity.class);
            startActivity(intent);
            updateRoundedMenuItem("profile");
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        // Transaction History Button
        TextView transactionHistoryButton = headerView.findViewById(R.id.transaction_history);
        transactionHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(CategoriesActivity.this, TransactionHistory.class);
            startActivity(intent);
            updateRoundedMenuItem("transaction_history");
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        // Logout Button
        TextView logoutButton = headerView.findViewById(R.id.logout);
        logoutButton.setOnClickListener(v -> showLogoutDialog());

        // Highlight the active menu item
        highlightActiveMenuItem();
    }

    private void updateRoundedMenuItem(String selectedItem) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("active_menu_item", selectedItem);
        editor.apply();
    }

    private void highlightActiveMenuItem() {
        String activeMenuItem = sharedPreferences.getString("active_menu_item", "category");

        View headerView = navigationView.getHeaderView(0);
        TextView transactionHistoryButton = headerView.findViewById(R.id.transaction_history);
        TextView categoriesButton = headerView.findViewById(R.id.category);
        TextView profileButton = headerView.findViewById(R.id.profile);

        // Reset all items to default background
        transactionHistoryButton.setBackgroundResource(0);
        categoriesButton.setBackgroundResource(0);
        profileButton.setBackgroundResource(0);

        // Highlight the selected item
        if ("transaction_history".equals(activeMenuItem)) {
            transactionHistoryButton.setBackgroundResource(R.drawable.rounded_menu_item);
        } else if ("category".equals(activeMenuItem)) {
            categoriesButton.setBackgroundResource(R.drawable.rounded_menu_item);
        } else if ("profile".equals(activeMenuItem)) {
            profileButton.setBackgroundResource(R.drawable.rounded_menu_item);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setupCategoryClick(int categoryId, Class<?> targetActivity) {
        findViewById(categoryId).setOnClickListener(v -> {
            Intent intent = new Intent(CategoriesActivity.this, targetActivity);
            intent.putExtra("totalBalance", getUpdatedTotalBalance());
            intent.putExtra("totalExpense", getUpdatedTotalExpense());
            startActivityForResult(intent, REQUEST_CODE_FOOD);
        });
    }

    private double getUpdatedTotalBalance() {
        return getSharedPreferences("FinanceData", MODE_PRIVATE).getFloat("totalBalance", 0);
    }

    private double getUpdatedTotalExpense() {
        return getSharedPreferences("FinanceData", MODE_PRIVATE).getFloat("totalExpense", 0);
    }

    @SuppressLint("DefaultLocale")
    private void updateBalanceAndExpense() {
        SharedPreferences prefs = getSharedPreferences("FinanceData", MODE_PRIVATE);
        double totalBalance = prefs.getFloat("totalBalance", 0);
        double totalExpense = prefs.getFloat("totalExpense", 0);

        totalBalanceView.setText(String.format("₱%,.2f", totalBalance));
        totalExpenseView.setText(String.format("-₱%,.2f", totalExpense));
    }

    @SuppressLint("DefaultLocale")
    private void showEditBalanceDialog(TextView totalBalanceView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Balance");
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String newBalance = input.getText().toString();
            if (!newBalance.isEmpty()) {
                double newBalanceValue = Double.parseDouble(newBalance);
                totalBalanceView.setText(String.format("₱%,.2f", newBalanceValue));
                SharedPreferences prefs = getSharedPreferences("FinanceData", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putFloat("totalBalance", (float) newBalanceValue);
                editor.apply();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @SuppressLint("DefaultLocale")
    private void showEditExpenseDialog(TextView totalExpenseView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Expense");
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String newExpense = input.getText().toString();
            if (!newExpense.isEmpty()) {
                double newExpenseValue = Double.parseDouble(newExpense);
                totalExpenseView.setText(String.format("-₱%,.2f", newExpenseValue));
                SharedPreferences prefs = getSharedPreferences("FinanceData", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putFloat("totalExpense", (float) newExpenseValue);
                editor.apply();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showLogoutDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.logout, null);
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false);

        android.app.AlertDialog alertDialog = dialogBuilder.create();

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
        Intent intent = new Intent(CategoriesActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
