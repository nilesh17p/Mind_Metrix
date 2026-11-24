package com.example.mindmetrix;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.Button;
import android.app.AlertDialog;

public class SettingsActivity extends Activity {

    private TextView btnBackSettings;
    private TextView btnPrivacyPolicy;
    private TextView btnAbout;

    private TextView tvSettingsUserName;
    private TextView tvSettingsUserEmail;

    private Switch switchSound;
    private Switch switchVibration;
    private Switch switchNotifications;

    private Button btnLogout;

    private SharedPreferences preferences;
    private SharedPreferences sessionPreferences;

    private static final String PREF_NAME = "MindMetrixSettings";
    private static final String SESSION_PREF = "MindMetrixSession";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // App settings preferences
        preferences = getSharedPreferences(
                PREF_NAME,
                MODE_PRIVATE
        );

        // Login session preferences
        sessionPreferences = getSharedPreferences(
                SESSION_PREF,
                MODE_PRIVATE
        );

        // Initialize views
        btnBackSettings = findViewById(R.id.btnBackSettings);
        btnPrivacyPolicy = findViewById(R.id.btnPrivacyPolicy);
        btnAbout = findViewById(R.id.btnAbout);

        tvSettingsUserName = findViewById(R.id.tvSettingsUserName);
        tvSettingsUserEmail = findViewById(R.id.tvSettingsUserEmail);

        switchSound = findViewById(R.id.switchSound);
        switchVibration = findViewById(R.id.switchVibration);
        switchNotifications = findViewById(R.id.switchNotifications);

        btnLogout = findViewById(R.id.btnLogout);

        // Display account information
        loadUserProfile();

        // Load saved settings
        switchSound.setChecked(
                preferences.getBoolean("sound", true)
        );

        switchVibration.setChecked(
                preferences.getBoolean("vibration", true)
        );

        switchNotifications.setChecked(
                preferences.getBoolean("notifications", true)
        );

        // Save Sound preference
        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit()
                    .putBoolean("sound", isChecked)
                    .apply();
        });

        // Save Vibration preference
        switchVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit()
                    .putBoolean("vibration", isChecked)
                    .apply();
        });

        // Save Notifications preference
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit()
                    .putBoolean("notifications", isChecked)
                    .apply();
        });

        // Back button
        btnBackSettings.setOnClickListener(v -> finish());

        // Logout button
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        // About button
        btnAbout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("About Mind Metrix")
                    .setMessage(
                            "Mind Metrix\n\n" +
                                    "A cognitive assessment application designed " +
                                    "to test memory, attention, pattern recognition " +
                                    "and reaction time.\n\n" +
                                    "Version 1.0"
                    )
                    .setPositiveButton("OK", null)
                    .show();
        });

        // Privacy Policy button
        btnPrivacyPolicy.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Privacy Policy")
                    .setMessage(
                            "Mind Metrix Privacy Policy\n\n" +
                                    "Your test results are stored locally " +
                                    "on your device using SQLite.\n\n" +
                                    "A complete privacy policy will be added later."
                    )
                    .setPositiveButton("OK", null)
                    .show();
        });
    }

    // =========================================
    // LOAD USER PROFILE
    // =========================================

    private void loadUserProfile() {

        boolean isLoggedIn = sessionPreferences.getBoolean(
                "is_logged_in", false
        );

        boolean isGuest = sessionPreferences.getBoolean(
                "is_guest", false
        );

        if (isLoggedIn) {

            String name = sessionPreferences.getString(
                    "user_name", "Mind Metrix User"
            );

            String email = sessionPreferences.getString(
                    "user_email", ""
            );

            tvSettingsUserName.setText(name);
            tvSettingsUserEmail.setText(email);

        } else if (isGuest) {

            tvSettingsUserName.setText("Guest User");
            tvSettingsUserEmail.setText("Guest Mode");

        } else {

            tvSettingsUserName.setText("Mind Metrix User");
            tvSettingsUserEmail.setText("Not logged in");
        }
    }

    // =========================================
    // LOGOUT CONFIRMATION
    // =========================================

    private void showLogoutDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("Logout", (dialog, which) -> {
                    logoutUser();
                })
                .show();
    }

    // =========================================
    // LOGOUT USER
    // =========================================

    private void logoutUser() {

        // Clear login and guest session only.
        // Keep sound, vibration and notification settings.
        sessionPreferences.edit()
                .clear()
                .apply();

        Intent intent = new Intent(
                SettingsActivity.this,
                WelcomeActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}



