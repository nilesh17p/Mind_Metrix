package com.example.mindmetrix;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import android.content.Intent;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main);

        TextView btnProfile = findViewById(R.id.btnProfile);
        TextView btnSettings = findViewById(R.id.btnSettings);

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });

        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });

        // Open Visual Memory game
        findViewById(R.id.cardVisualMemory).setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    VisualMemoryActivity.class
            );
            startActivity(intent);
        });

        // Open Number Memory game
        findViewById(R.id.cardNumberMemory).setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    NumberMemoryActivity.class
            );
            startActivity(intent);
        });

        // Open Pattern Recognition game
        findViewById(R.id.cardPatternRecognition).setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    PatternRecognitionActivity.class
            );
            startActivity(intent);
        });

        // Open Attention game
        findViewById(R.id.cardAttention).setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AttentionActivity.class
            );
            startActivity(intent);
        });

        // Open Reaction Time game
        findViewById(R.id.cardReactionTime).setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ReactionTimeActivity.class
            );

            startActivity(intent);
        });
    }
}


