package com.example.budgetify1;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ProfileActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton menuButton;
    private TextView profileNameTextView;
    private ImageView profileImage;
    private SharedPreferences sharedPreferences;

    private ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String updatedName = result.getData().getStringExtra("updated_name");
                        if (updatedName != null) {
                            profileNameTextView.setText(updatedName);
                            sharedPreferences.edit().putString("full_name", updatedName).apply();
                        }

                        String updatedImagePath = result.getData().getStringExtra("updated_image_path");
                        if (updatedImagePath != null) {
                            Bitmap bitmap = BitmapFactory.decodeFile(updatedImagePath);
                            profileImage.setImageBitmap(bitmap);
                            sharedPreferences.edit().putString("profile_image_path", updatedImagePath).apply();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuButton = findViewById(R.id.hamburgerMenu);
        profileNameTextView = findViewById(R.id.profileNameTextView);
        profileImage = findViewById(R.id.profileImage);
        LinearLayout editProfileButton = findViewById(R.id.editProfileButton);
        TextView logoutButton = navigationView.getHeaderView(0).findViewById(R.id.logout);

        profileNameTextView.setText(sharedPreferences.getString("full_name", "User"));
        String savedImagePath = sharedPreferences.getString("profile_image_path", null);
        if (savedImagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(savedImagePath);
            profileImage.setImageBitmap(bitmap);
        }

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("current_name", profileNameTextView.getText().toString());
                intent.putExtra("current_image_path", savedImagePath);
                editProfileLauncher.launch(intent);
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        // Initialize Navigation Header View
        View headerView = navigationView.getHeaderView(0);

        // Set Click Listener for Transaction History Button
        TextView transactionHistoryButton = headerView.findViewById(R.id.transaction_history);
        transactionHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, TransactionHistory.class);
                startActivity(intent);
                updateRoundedMenuItem("transaction_history");
                drawerLayout.closeDrawer(navigationView);
            }
        });

        // Set Click Listener for Categories Button
        TextView categoriesButton = headerView.findViewById(R.id.category);
        categoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, CategoriesActivity.class);
                startActivity(intent);
                updateRoundedMenuItem("category");
                drawerLayout.closeDrawer(navigationView);
            }
        });

        // Highlight the active menu item
        highlightActiveMenuItem();
    }

    private void updateRoundedMenuItem(String selectedItem) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("active_menu_item", selectedItem);
        editor.apply();
    }

    private void highlightActiveMenuItem() {
        String activeMenuItem = sharedPreferences.getString("active_menu_item", "profile");

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

        confirmLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                logoutUser();
            }
        });

        cancelLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void logoutUser() {
        sharedPreferences.edit().clear().apply();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
