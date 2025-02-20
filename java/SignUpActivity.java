package com.example.budgetify1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {
    private EditText firstName, middleName, lastName, email, mobile, dob, password, confirmPassword;
    private Button signUpButton;
    private TextView loginPrompt;
    private ImageView eyeIcon, eyeIconConfirm;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Initialize UI elements
        firstName = findViewById(R.id.firstname);
        middleName = findViewById(R.id.middlename);
        lastName = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile_number);
        dob = findViewById(R.id.date_of_birth);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        signUpButton = findViewById(R.id.btn_sign_up);
        loginPrompt = findViewById(R.id.login_prompt);
        eyeIcon = findViewById(R.id.eye_icon);
        eyeIconConfirm = findViewById(R.id.eye_icon_confirm);

        // Ensure password fields display bullets by default
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // Toggle password visibility
        eyeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide password
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eyeIcon.setImageResource(R.drawable.ic_eye_closed);
                } else {
                    // Show password
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    eyeIcon.setImageResource(R.drawable.ic_eye_open);
                }
                isPasswordVisible = !isPasswordVisible;
                password.setSelection(password.getText().length()); // Keep cursor position
            }
        });

        // Toggle confirm password visibility
        eyeIconConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConfirmPasswordVisible) {
                    // Hide confirm password
                    confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eyeIconConfirm.setImageResource(R.drawable.ic_eye_closed);
                } else {
                    // Show confirm password
                    confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    eyeIconConfirm.setImageResource(R.drawable.ic_eye_open);
                }
                isConfirmPasswordVisible = !isConfirmPasswordVisible;
                confirmPassword.setSelection(confirmPassword.getText().length()); // Keep cursor position
            }
        });

        // Disable manual input for DOB and open DatePicker on click
        dob.setFocusable(false);
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstNameInput = firstName.getText().toString().trim();
                String middleNameInput = middleName.getText().toString().trim();
                String lastNameInput = lastName.getText().toString().trim();
                String emailInput = email.getText().toString().trim();
                String mobileInput = mobile.getText().toString().trim();
                String dobInput = dob.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();
                String confirmPasswordInput = confirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(firstNameInput) || TextUtils.isEmpty(lastNameInput) ||
                        TextUtils.isEmpty(emailInput) || TextUtils.isEmpty(mobileInput) ||
                        TextUtils.isEmpty(dobInput) || TextUtils.isEmpty(passwordInput) ||
                        TextUtils.isEmpty(confirmPasswordInput)) {
                    Toast.makeText(SignUpActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (passwordInput.length() < 6) {
                    Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!passwordInput.equals(confirmPasswordInput)) {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isInserted = dbHelper.insertUser(
                        firstNameInput, middleNameInput, lastNameInput,
                        emailInput, mobileInput, dobInput, passwordInput
                );

                if (isInserted) {
                    // Save the full name in SharedPreferences
                    String fullName = firstNameInput + " " + middleNameInput + " " + lastNameInput;
                    sharedPreferences.edit().putString("full_name", fullName).apply();

                    Toast.makeText(SignUpActivity.this, "Sign-up successful! Please log in.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Sign-up failed! Email may already be used.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }

    private void showDatePicker() {
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Set selected date to the EditText field
                        dob.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}
