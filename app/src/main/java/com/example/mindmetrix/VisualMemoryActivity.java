package com.example.mindmetrix;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.Intent;

public class VisualMemoryActivity extends AppCompatActivity {

    private GridLayout memoryGrid;
    private TextView tvGameStatus;
    private TextView tvInstructions;
    private TextView tvResult;
    private Button btnStartGame;
    private DatabaseHelper databaseHelper;
    private View btnHistory;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    private final List<Integer> pattern = new ArrayList<>();
    private final List<Integer> userPattern = new ArrayList<>();
    private final List<Button> tileButtons = new ArrayList<>();

    private static final int GRID_SIZE = 9;
    private static final int PATTERN_LENGTH = 3;

    private static final int COLOR_BACKGROUND = Color.rgb(39, 47, 73);
    private static final int COLOR_HIGHLIGHT = Color.rgb(130, 115, 255);
    private static final int COLOR_CORRECT = Color.rgb(46, 180, 130);
    private static final int COLOR_WRONG = Color.rgb(220, 75, 90);

    private boolean gameRunning = false;
    private boolean showingPattern = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_memory);

        memoryGrid = findViewById(R.id.memoryGrid);
        tvGameStatus = findViewById(R.id.tvGameStatus);
        tvInstructions = findViewById(R.id.tvInstructions);
        tvResult = findViewById(R.id.tvResult);
        btnStartGame = findViewById(R.id.btnStartGame);
        btnHistory = findViewById(R.id.btnHistory);

        databaseHelper = new DatabaseHelper(this);
        
        createGrid();

        btnStartGame.setOnClickListener(v -> startGame());

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(
                    VisualMemoryActivity.this,
                    VisualMemoryHistoryActivity.class
            );
            startActivity(intent);
        });

    }

    private void createGrid() {
        memoryGrid.removeAllViews();
        tileButtons.clear();

        for (int i = 0; i < GRID_SIZE; i++) {
            final int tileIndex = i;

            Button tile = new Button(this);
            tile.setText("");
            tile.setContentDescription("Tile " + (i + 1));
            tile.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(COLOR_BACKGROUND));
            tile.setMinWidth(0);
            tile.setMinimumWidth(0);
            tile.setMinHeight(0);
            tile.setMinimumHeight(0);
            tile.setPadding(0, 0, 0, 0);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = dpToPx(82);
            params.columnSpec = GridLayout.spec(i % 3, 1f);
            params.rowSpec = GridLayout.spec(i / 3);
            params.setMargins(dpToPx(4), dpToPx(4),
                    dpToPx(4), dpToPx(4));

            tile.setLayoutParams(params);
            tile.setOnClickListener(v -> onTileTapped(tileIndex));

            memoryGrid.addView(tile);
            tileButtons.add(tile);
        }

        setTilesEnabled(false);
    }

    private int dpToPx(int dp) {
        return Math.round(
                dp * getResources().getDisplayMetrics().density);
    }

    private void startGame() {
        handler.removeCallbacksAndMessages(null);

        pattern.clear();
        userPattern.clear();

        tvResult.setText("");
        tvInstructions.setText(
                "Watch the highlighted tiles carefully. Then tap them in the same order.");
        tvGameStatus.setText("Memorize the pattern...");
        btnStartGame.setText("Restart Test");

        resetTileColors();

        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < GRID_SIZE; i++) {
            positions.add(i);
        }
        Collections.shuffle(positions, random);

        for (int i = 0; i < PATTERN_LENGTH; i++) {
            pattern.add(positions.get(i));
        }

        gameRunning = true;
        showingPattern = true;
        setTilesEnabled(false);

        showPatternAt(0);
    }

    private void showPatternAt(int position) {
        if (position >= pattern.size()) {
            showingPattern = false;
            resetTileColors();
            setTilesEnabled(true);
            tvGameStatus.setText("Your turn — tap the 3 tiles");
            return;
        }

        resetTileColors();

        int tileIndex = pattern.get(position);
        tileButtons.get(tileIndex).setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(COLOR_HIGHLIGHT));

        handler.postDelayed(() -> {
            if (!gameRunning) {
                return;
            }

            tileButtons.get(tileIndex).setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(COLOR_BACKGROUND));

            handler.postDelayed(
                    () -> showPatternAt(position + 1), 300);
        }, 650);
    }

    private void onTileTapped(int tileIndex) {
        if (!gameRunning || showingPattern) {
            return;
        }

        // Prevent selecting the same tile more than once.
        if (userPattern.contains(tileIndex)) {
            return;
        }

        userPattern.add(tileIndex);

        int currentPosition = userPattern.size() - 1;

        if (pattern.get(currentPosition) == tileIndex) {
            tileButtons.get(tileIndex).setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(COLOR_CORRECT));

            tvGameStatus.setText(
                    "Correct: " + userPattern.size() + " of " + PATTERN_LENGTH);

            if (userPattern.size() == PATTERN_LENGTH) {
                finishGame(true);
            }
        } else {
            tileButtons.get(tileIndex).setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(COLOR_WRONG));
            finishGame(false);
        }
    }

    private void finishGame(boolean success) {
        gameRunning = false;
        showingPattern = false;
        setTilesEnabled(false);

        int correct = 0;
        for (int i = 0; i < userPattern.size(); i++) {
            if (pattern.get(i).equals(userPattern.get(i))) {
                correct++;
            } else {
                break;
            }
        }

        int score = Math.round(
                (correct * 100f) / PATTERN_LENGTH);

        if (success) {
            tvGameStatus.setText("Excellent memory!");
            tvResult.setText(
                    "Test complete!\nScore: " + score + "/100\n" +
                            "You remembered all 3 tiles in order.");
        } else {
            tvGameStatus.setText("Pattern not matched");
            tvResult.setText(
                    "Test complete\nScore: " + score + "/100\n" +
                            "Correct tiles in order: " + correct +
                            " of " + PATTERN_LENGTH +
                            "\nTry again to improve your score.");
        }

        // Show the original pattern so the user can review the answer.
        for (int index : pattern) {
            tileButtons.get(index).setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(COLOR_HIGHLIGHT));
        }

        btnStartGame.setText("Try Again");
        saveResult(score);
    }

    private void resetTileColors() {
        for (Button tile : tileButtons) {
            tile.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(COLOR_BACKGROUND));
        }
    }

    private void setTilesEnabled(boolean enabled) {
        for (Button tile : tileButtons) {
            tile.setEnabled(enabled);
            tile.setAlpha(enabled ? 1.0f : 0.85f);
        }
    }

    private void saveResult(int score) {
        long rowId = databaseHelper.saveVisualMemoryResult(
                score,
                1,
                PATTERN_LENGTH,
                System.currentTimeMillis()
        );

        if (rowId == -1) {
            tvResult.append("\nWarning: Could not save this result.");
        } else {
            tvResult.append("\nResult saved on this device.");
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);

        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}

