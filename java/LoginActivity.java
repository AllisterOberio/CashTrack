package com.example.budgetify1;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signUpPrompt;
    private TextView forgotPassword;
    private ImageView eyeIcon; // Eye icon for toggling password visibility

    private boolean isPasswordVisible = false;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Initialize DB helper
        dbHelper = new DatabaseHelper(this);

        // Initialize UI elements
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.btn_sign_up);
        signUpPrompt = findViewById(R.id.tv_sign_up_prompt);
        forgotPassword = findViewById(R.id.tv_forgot_password);
        eyeIcon = findViewById(R.id.eye_icon); // Initialize eye icon

        // Toggle password visibility on eye icon click
        eyeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide password
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eyeIcon.setImageResource(R.drawable.ic_eye_closed);
                } else {
                    // Show password
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    eyeIcon.setImageResource(R.drawable.ic_eye_open);
                }
                isPasswordVisible = !isPasswordVisible;
                passwordEditText.setSelection(passwordEditText.getText().length()); // Keep cursor position
            }
        });

        // Login Button Click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    authenticateUser(email, password);
                }
            }
        });

        // Sign-Up Prompt Click
        signUpPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        // Forgot Password Click (Redirect to Reset Password)
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Redirect to Forgot Password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void authenticateUser(String email, String password) {
        boolean isAuthenticated = dbHelper.authenticateUser(email, password);

        if (isAuthenticated) {
            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

            // Navigate to TransactionHistory
            Intent intent = new Intent(LoginActivity.this, TransactionHistory.class);
            intent.putExtra("user_email", email);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
