package com.example.mindmetrix;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import android.os.CountDownTimer;
import android.content.Intent;

public class AttentionActivity extends AppCompatActivity {

    private TextView tvAttentionRound;
    private TextView tvAttentionScore;
    private TextView tvAttentionTarget;
    private TextView tvAttentionDifficulty;
    private TextView tvAttentionFeedback;
    private TextView tvAttentionResult;
    private GridLayout gridAttentionSymbols;
    private Button btnStartAttentionGame;
    private Button btnAttentionHistory;
    private DatabaseHelper databaseHelper;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private CountDownTimer countDownTimer;
    private TextView tvAttentionTimer;
    private long roundTime;
    private final Random random = new Random();

    private final String[] symbols = {
            "●", "○",
            "▲", "△",
            "■", "□",
            "★", "☆",
            "◆", "◇",
            "♥", "♡",
            "+", "✚",
            "☀", "☼"
    };

    private int currentRound = 0;
    private int score = 0;
    private int correctAnswers = 0;

    private int gridSize;
    private String targetSymbol;

    private boolean gameRunning = false;
    private boolean answerSelected = false;
    private boolean resultSaved = false;

    private int completedRounds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        setupButtons();

        resetScreen();
    }

    private void initializeViews() {

        tvAttentionRound = findViewById(R.id.tvAttentionRound);
        tvAttentionScore = findViewById(R.id.tvAttentionScore);
        tvAttentionTarget = findViewById(R.id.tvAttentionTarget);
        tvAttentionDifficulty = findViewById(R.id.tvAttentionDifficulty);
        tvAttentionFeedback = findViewById(R.id.tvAttentionFeedback);
        tvAttentionResult = findViewById(R.id.tvAttentionResult);

        gridAttentionSymbols = findViewById(R.id.gridAttentionSymbols);

        btnStartAttentionGame = findViewById(R.id.btnStartAttentionGame);
        btnAttentionHistory = findViewById(R.id.btnAttentionHistory);
        tvAttentionTimer = findViewById(R.id.tvAttentionTimer);
    }

    private void setupButtons() {

        btnStartAttentionGame.setOnClickListener(v -> startGame());

        btnAttentionHistory.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AttentionActivity.this,
                    AttentionHistoryActivity.class
            );

            startActivity(intent);
        });
    }

    private void resetScreen() {

        tvAttentionRound.setText("Round: 0/10");
        tvAttentionScore.setText("Score: 0");

        tvAttentionTarget.setText("?");
        tvAttentionDifficulty.setText("Difficulty: Easy");

        tvAttentionFeedback.setText("");
        tvAttentionResult.setVisibility(View.GONE);

        gridAttentionSymbols.removeAllViews();

        btnStartAttentionGame.setText("Start Game");
    }

    // =========================================
    // START GAME
    // =========================================

    private void startGame() {

        handler.removeCallbacksAndMessages(null);
        cancelRoundTimer();

        currentRound = 0;
        score = 0;
        correctAnswers = 0;
        completedRounds = 0;

        gameRunning = true;
        answerSelected = false;
        resultSaved = false;

        tvAttentionResult.setVisibility(View.GONE);
        tvAttentionFeedback.setText("");

        btnStartAttentionGame.setText("Game Running");

        loadNextRound();
    }

    // =========================================
    // LOAD NEXT ROUND
    // =========================================

    private void loadNextRound() {

        if (currentRound >= 10) {
            finishGame();
            return;
        }

        currentRound++;

        answerSelected = false;

        updateDifficulty();

        tvAttentionRound.setText(
                "Round: " + currentRound + "/10"
        );

        tvAttentionScore.setText(
                "Score: " + score
        );

        tvAttentionFeedback.setText("");

        generateSymbols();
        startRoundTimer();
    }

    // =========================================
    // DIFFICULTY
    // =========================================

    private void updateDifficulty() {

        if (currentRound <= 3) {

            gridSize = 4;
            roundTime = 5000;

            tvAttentionDifficulty.setText("Difficulty: Easy");

        } else if (currentRound <= 7) {

            gridSize = 5;
            roundTime = 3000;

            tvAttentionDifficulty.setText("Difficulty: Medium");

        } else {

            gridSize = 6;
            roundTime = 2000;

            tvAttentionDifficulty.setText("Difficulty: Hard");
        }
    }

    // =========================================
    // GENERATE SYMBOL GRID
    // =========================================

    private void generateSymbols() {

        gridAttentionSymbols.removeAllViews();

        gridAttentionSymbols.setColumnCount(gridSize);
        gridAttentionSymbols.setRowCount(gridSize);

        int totalCells = gridSize * gridSize;

        targetSymbol = symbols[random.nextInt(symbols.length)];

        tvAttentionTarget.setText(targetSymbol);

        List<String> symbolList = new ArrayList<>();

        // Add distractor symbols.
        for (int i = 0; i < totalCells - 1; i++) {

            String symbol;

            do {
                symbol = symbols[random.nextInt(symbols.length)];
            } while (symbol.equals(targetSymbol));

            symbolList.add(symbol);
        }

        // Add target symbol exactly once.
        int targetPosition = random.nextInt(totalCells);

        symbolList.add(targetPosition, targetSymbol);

        Collections.shuffle(symbolList);

        // Create grid buttons.
        for (int i = 0; i < totalCells; i++) {

            final String selectedSymbol = symbolList.get(i);

            TextView symbolView = new TextView(this);

            symbolView.setText(selectedSymbol);
            symbolView.setTextSize(gridSize == 6 ? 21 : 25);
            symbolView.setTextColor(Color.WHITE);
            symbolView.setGravity(Gravity.CENTER);

            symbolView.setBackground(createCellBackground());

            symbolView.setClickable(true);
            symbolView.setFocusable(true);

            GridLayout.LayoutParams params =
                    new GridLayout.LayoutParams(
                            GridLayout.spec(i / gridSize, 1f),
                            GridLayout.spec(i % gridSize, 1f)
                    );

            params.width = 0;
            params.height = dp(gridSize == 6 ? 48 : 58);

            params.setMargins(
                    dp(4),
                    dp(4),
                    dp(4),
                    dp(4)
            );

            symbolView.setLayoutParams(params);

            symbolView.setOnClickListener(v ->
                    checkAnswer(selectedSymbol, symbolView)
            );

            gridAttentionSymbols.addView(symbolView);
        }
    }

    // =========================================
// ROUND COUNTDOWN TIMER
// =========================================

    private void startRoundTimer() {

        cancelRoundTimer();

        countDownTimer = new CountDownTimer(roundTime, 100) {

            @Override
            public void onTick(long millisUntilFinished) {

                int seconds = (int) Math.ceil(
                        millisUntilFinished / 1000.0
                );

                tvAttentionTimer.setText(
                        "Time Left: " + seconds + "s"
                );

                if (seconds <= 2) {
                    tvAttentionTimer.setTextColor(
                            Color.rgb(220, 75, 90)
                    );
                } else {
                    tvAttentionTimer.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onFinish() {

                countDownTimer = null;

                if (!gameRunning || answerSelected) {
                    return;
                }

                answerSelected = true;
                cancelRoundTimer();
                completedRounds++;

                disableGrid();

                tvAttentionTimer.setText("Time's Up!");

                tvAttentionFeedback.setText("Time Up! No points");

                tvAttentionFeedback.setTextColor(
                        Color.rgb(220, 75, 90)
                );

                handler.postDelayed(
                        AttentionActivity.this::loadNextRound,
                        1000
                );
            }

        }.start();
    }


    // =========================================
    // CANCEL COUNTDOWN TIMER
    // =========================================

    private void cancelRoundTimer() {

        if (countDownTimer != null) {

            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    // =========================================
    // GRID CELL DESIGN
    // =========================================

    private GradientDrawable createCellBackground() {

        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(Color.rgb(39, 47, 73));
        drawable.setCornerRadius(dp(12));

        drawable.setStroke(
                dp(1),
                Color.rgb(75, 83, 115)
        );

        return drawable;
    }

    // =========================================
    // CHECK ANSWER
    // =========================================

    private void checkAnswer(
            String selectedSymbol,
            TextView selectedView) {

        if (!gameRunning || answerSelected) {
            return;
        }

        answerSelected = true;
        completedRounds++;

        disableGrid();

        if (selectedSymbol.equals(targetSymbol)) {

            score += 10;
            correctAnswers++;

            tvAttentionFeedback.setText("Correct! +10 points");

            tvAttentionFeedback.setTextColor(
                    Color.rgb(46, 180, 130)
            );

            selectedView.setBackgroundColor(
                    Color.rgb(46, 130, 100)
            );

        } else {

            tvAttentionFeedback.setText("Wrong Answer!");

            tvAttentionFeedback.setTextColor(
                    Color.rgb(220, 75, 90)
            );

            selectedView.setBackgroundColor(
                    Color.rgb(150, 55, 65)
            );
        }

        tvAttentionScore.setText("Score: " + score);

        handler.postDelayed(this::loadNextRound, 1000);
    }

    // =========================================
    // DISABLE GRID AFTER ANSWER
    // =========================================

    private void disableGrid() {

        for (int i = 0; i < gridAttentionSymbols.getChildCount(); i++) {

            View child = gridAttentionSymbols.getChildAt(i);

            child.setEnabled(false);
            child.setClickable(false);
        }
    }

    // =========================================
    // FINISH GAME
    // =========================================

    private void finishGame() {

        cancelRoundTimer();

        gameRunning = false;

        tvAttentionRound.setText("Round: 10/10");
        tvAttentionScore.setText("Score: " + score);

        tvAttentionTarget.setText("✓");

        tvAttentionDifficulty.setText("Game Completed");

        tvAttentionFeedback.setText("");

        gridAttentionSymbols.removeAllViews();

        int accuracy = (correctAnswers * 100) / 10;

        tvAttentionResult.setVisibility(View.VISIBLE);

        tvAttentionResult.setText(
                "GAME COMPLETED\n\n" +
                        "Final Score: " + score + "/100\n" +
                        "Correct Answers: " + correctAnswers + "/10\n" +
                        "Accuracy: " + accuracy + "%"
        );

        btnStartAttentionGame.setText("Play Again");

        saveResult();
    }

    // =========================================
    // SAVE RESULT IN SQLITE
    // =========================================

    private void saveResult() {

        if (resultSaved) {
            return;
        }

        resultSaved = true;

        databaseHelper.saveAttentionResult(
                score,
                completedRounds,
                correctAnswers,
                System.currentTimeMillis()
        );
    }

    // =========================================
    // DP CONVERSION
    // =========================================

    private int dp(int value) {

        return (int) (value *
                getResources().getDisplayMetrics().density + 0.5f);
    }

    // =========================================
    // DESTROY ACTIVITY
    // =========================================

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);
        cancelRoundTimer();

        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}

