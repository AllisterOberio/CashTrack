package com.example.budgetify1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton hamburgerButton;
    private View roundedMenuItem;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        hamburgerButton = findViewById(R.id.hamburgerMenu);
        roundedMenuItem = findViewById(R.id.dashboard); // Rounded item in nav header

        // Set up click listener for the hamburger button
        hamburgerButton.setOnClickListener(v -> toggleDrawer());

        // Handle click on navigation items
        setupNavigationItems();
    }

    // Function to toggle the drawer (open/close)
    private void toggleDrawer() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            drawerLayout.openDrawer(navigationView);
        }
    }

    // Function to handle clicks on the navigation items
    private void setupNavigationItems() {
        View headerView = navigationView.getHeaderView(0);
        TextView dashboard = headerView.findViewById(R.id.dashboard);
        TextView category = headerView.findViewById(R.id.category);
        TextView profile = headerView.findViewById(R.id.profile);
        TextView logout = headerView.findViewById(R.id.logout);

        dashboard.setOnClickListener(v -> {
            moveRoundedMenuItemTo(dashboard);
            Toast.makeText(this, "Dashboard clicked", Toast.LENGTH_SHORT).show();
        });

        category.setOnClickListener(v -> {
            moveRoundedMenuItemTo(category);
            Toast.makeText(this, "Categories clicked", Toast.LENGTH_SHORT).show();
        });

        profile.setOnClickListener(v -> {
            moveRoundedMenuItemTo(profile);
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
        });

        logout.setOnClickListener(v -> {
            moveRoundedMenuItemTo(logout);
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
        });
    }

    // Function to move the rounded menu item based on the clicked view
    private void moveRoundedMenuItemTo(View targetView) {
        // Get the position of the clicked item in the layout
        int[] location = new int[2];
        targetView.getLocationOnScreen(location);

        // Adjust the position within the navigation drawer
        int yPosition = location[1] - drawerLayout.getTop(); // Adjust for drawer top offset

        // Get the current layout parameters of the rounded menu item
        ViewGroup.LayoutParams params = roundedMenuItem.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) params;
            layoutParams.topMargin = yPosition;

            // Apply the updated layout params
            roundedMenuItem.setLayoutParams(layoutParams);
            roundedMenuItem.requestLayout(); // Ensure the layout updates immediately
        } else {
            Log.e("NavHeader", "Error: layoutParams is not of type MarginLayoutParams");
        }
    }
}
