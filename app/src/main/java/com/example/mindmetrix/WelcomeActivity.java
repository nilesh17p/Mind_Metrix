package com.example.mindmetrix;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

public class WelcomeActivity extends Activity {

    private Button btnContinueGuest;
    private Button btnWelcomeLogin;
    private Button btnWelcomeSignup;

    private static final String SESSION_PREF = "MindMetrixSession";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check whether a user or guest session already exists
        SharedPreferences session = getSharedPreferences(
                SESSION_PREF,
                MODE_PRIVATE
        );

        boolean isLoggedIn = session.getBoolean("is_logged_in", false);
        boolean isGuest = session.getBoolean("is_guest", false);

        if (isLoggedIn || isGuest) {
            openDashboard();
            return;
        }

        setContentView(R.layout.activity_welcome);

        // Initialize buttons
        btnContinueGuest = findViewById(R.id.btnContinueGuest);
        btnWelcomeLogin = findViewById(R.id.btnWelcomeLogin);
        btnWelcomeSignup = findViewById(R.id.btnWelcomeSignup);

        // Continue as Guest
        btnContinueGuest.setOnClickListener(v -> {

            session.edit()
                    .clear()
                    .putBoolean("is_logged_in", false)
                    .putBoolean("is_guest", true)
                    .apply();

            openDashboard();
        });

        // Login Page
        btnWelcomeLogin.setOnClickListener(v -> {
            Intent intent = new Intent(
                    WelcomeActivity.this,
                    LoginActivity.class
            );
            startActivity(intent);
        });

        // Registration Page
        btnWelcomeSignup.setOnClickListener(v -> {
            Intent intent = new Intent(
                    WelcomeActivity.this,
                    RegisterActivity.class
            );
            startActivity(intent);
        });
    }

    private void openDashboard() {
        Intent intent = new Intent(
                WelcomeActivity.this,
                MainActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}


