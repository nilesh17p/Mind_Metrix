package com.example.mindmetrix;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Patterns;
import android.database.Cursor;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private EditText etLoginEmail, etLoginPassword;

    private Button btnLogin;

    private TextView btnLoginBack;
    private TextView tvForgotPassword;
    private TextView tvGoToSignup;

    private DatabaseHelper databaseHelper;

    private static final String SESSION_PREF =
            "MindMetrixSession";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);

        btnLogin = findViewById(R.id.btnLogin);

        btnLoginBack = findViewById(R.id.btnLoginBack);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvGoToSignup = findViewById(R.id.tvGoToSignup);

        // Fill email after registration, if available
        String registeredEmail =
                getIntent().getStringExtra("registered_email");

        if (registeredEmail != null) {
            etLoginEmail.setText(registeredEmail);
        }

        // Back button
        btnLoginBack.setOnClickListener(v -> finish());

        // Login button
        btnLogin.setOnClickListener(v -> validateLogin());

        // Forgot password
        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(
                    this,
                    "Password recovery is not available for offline accounts.",
                    Toast.LENGTH_LONG
            ).show();
        });

        // Navigate to Registration
        tvGoToSignup.setOnClickListener(v -> {
            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );
            startActivity(intent);
        });
    }

    // =========================================
    // LOGIN VALIDATION
    // =========================================

    private void validateLogin() {

        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString();

        if (email.isEmpty()) {
            etLoginEmail.setError("Please enter your email");
            etLoginEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etLoginEmail.setError("Enter a valid email address");
            etLoginEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etLoginPassword.setError("Please enter your password");
            etLoginPassword.requestFocus();
            return;
        }

        authenticateUser(email, password);
    }

    // =========================================
    // SQLITE USER AUTHENTICATION
    // =========================================

    private void authenticateUser(String email, String password) {

        Cursor cursor = null;

        btnLogin.setEnabled(false);

        try {

            cursor = databaseHelper.getUserByEmail(email);

            if (cursor == null || !cursor.moveToFirst()) {

                Toast.makeText(
                        this,
                        "Account not found. Please register first.",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            int idIndex = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COL_USER_ID
            );

            int nameIndex = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COL_USER_NAME
            );

            int hashIndex = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COL_USER_PASSWORD_HASH
            );

            int saltIndex = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COL_USER_PASSWORD_SALT
            );

            int algorithmIndex = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COL_USER_PASSWORD_ALGORITHM
            );

            int userId = cursor.getInt(idIndex);

            String userName = cursor.getString(nameIndex);
            String storedHash = cursor.getString(hashIndex);
            String storedSalt = cursor.getString(saltIndex);
            String storedAlgorithm = cursor.getString(algorithmIndex);

            // Verify entered password against stored hash
            boolean passwordCorrect =
                    PasswordHasher.verifyPassword(
                            password,
                            storedHash,
                            storedSalt,
                            storedAlgorithm
                    );

            if (passwordCorrect) {

                // Save login session
                SharedPreferences preferences =
                        getSharedPreferences(
                                SESSION_PREF,
                                MODE_PRIVATE
                        );

                preferences.edit()
                        .putBoolean("is_logged_in", true)
                        .putBoolean("is_guest", false)
                        .putInt("user_id", userId)
                        .putString("user_name", userName)
                        .putString("user_email", email.trim())
                        .apply();

                Toast.makeText(
                        this,
                        "Welcome back, " + userName + "!",
                        Toast.LENGTH_LONG
                ).show();

                // Open dashboard
                Intent intent = new Intent(
                        LoginActivity.this,
                        MainActivity.class
                );

                intent.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                );

                startActivity(intent);
                finish();

            } else {

                etLoginPassword.setError("Incorrect password");
                etLoginPassword.requestFocus();

                Toast.makeText(
                        this,
                        "Invalid email or password.",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Login failed. Please try again.",
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();

        } finally {

            if (cursor != null) {
                cursor.close();
            }

            btnLogin.setEnabled(true);
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
