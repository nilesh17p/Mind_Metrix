
package com.example.mindmetrix;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

    private EditText etRegisterName;
    private EditText etRegisterEmail;
    private EditText etRegisterPassword;
    private EditText etRegisterConfirmPassword;

    private Button btnCreateAccount;

    private TextView btnRegisterBack;
    private TextView tvRegisterLogin;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        etRegisterName = findViewById(R.id.etRegisterName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterConfirmPassword =
                findViewById(R.id.etRegisterConfirmPassword);

        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnRegisterBack = findViewById(R.id.btnRegisterBack);
        tvRegisterLogin = findViewById(R.id.tvRegisterLogin);

        // Back button
        btnRegisterBack.setOnClickListener(v -> finish());

        // Create Account button
        btnCreateAccount.setOnClickListener(v -> validateRegistration());

        // Navigate to Login
        tvRegisterLogin.setOnClickListener(v -> {
            Intent intent = new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            );
            startActivity(intent);
        });
    }

    private void validateRegistration() {

        String name = etRegisterName.getText().toString().trim();
        String email = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString();
        String confirmPassword =
                etRegisterConfirmPassword.getText().toString();

        // Validate name
        if (name.isEmpty()) {
            etRegisterName.setError("Please enter your name");
            etRegisterName.requestFocus();
            return;
        }

        if (name.length() < 2) {
            etRegisterName.setError(
                    "Name must contain at least 2 characters"
            );
            etRegisterName.requestFocus();
            return;
        }

        // Validate email
        if (email.isEmpty()) {
            etRegisterEmail.setError("Please enter your email");
            etRegisterEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegisterEmail.setError("Enter a valid email address");
            etRegisterEmail.requestFocus();
            return;
        }

        // Validate password
        if (password.isEmpty()) {
            etRegisterPassword.setError("Please create a password");
            etRegisterPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            etRegisterPassword.setError(
                    "Password must contain at least 8 characters"
            );
            etRegisterPassword.requestFocus();
            return;
        }

        // Confirm password
        if (confirmPassword.isEmpty()) {
            etRegisterConfirmPassword.setError(
                    "Please confirm your password"
            );
            etRegisterConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etRegisterConfirmPassword.setError(
                    "Passwords do not match"
            );
            etRegisterConfirmPassword.requestFocus();
            return;
        }

        // All validation passed. Generate password hash.
        registerAccount(name, email, password);
    }

    // =========================================
    // REGISTER ACCOUNT IN SQLITE
    // =========================================

    private void registerAccount(
            String name,
            String email,
            String password) {

        btnCreateAccount.setEnabled(false);

        try {

            // Generate PBKDF2 password hash and random salt
            PasswordHasher.HashResult passwordData =
                    PasswordHasher.hashPassword(password);

            // Save account in SQLite
            long userId = databaseHelper.registerUser(
                    name,
                    email,
                    passwordData
            );

            if (userId != -1) {

                Toast.makeText(
                        this,
                        "Account created successfully!",
                        Toast.LENGTH_LONG
                ).show();

                // Clear password fields
                etRegisterPassword.setText("");
                etRegisterConfirmPassword.setText("");

                // Open Login screen
                Intent intent = new Intent(
                        RegisterActivity.this,
                        LoginActivity.class
                );

                intent.putExtra("registered_email", email);

                startActivity(intent);
                finish();

            } else {

                etRegisterEmail.setError(
                        "This email is already registered"
                );

                etRegisterEmail.requestFocus();

                Toast.makeText(
                        this,
                        "Registration failed. Email may already exist.",
                        Toast.LENGTH_LONG
                ).show();
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to create account. Please try again.",
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();

        } finally {

            btnCreateAccount.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
