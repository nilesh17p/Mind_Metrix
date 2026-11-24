package com.example.mindmetrix;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ReactionTimeActivity extends AppCompatActivity {

    private TextView tvReactionRound;
    private TextView tvReactionBest;
    private TextView tvReactionStatus;
    private TextView tvReactionResult;
    private TextView tvReactionInstructions;

    private Button btnStartReaction;
    private Button btnReactionHistory;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    private static final int TOTAL_ATTEMPTS = 10;

    private int currentAttempt = 0;
    private int bestReactionTime = Integer.MAX_VALUE;
    private int waitingColor;
    private int signalColor;
    private DatabaseHelper databaseHelper;
    private boolean resultSaved = false;

    private final int[] gameColors = {
            Color.rgb(220, 75, 90),   // Red
            Color.rgb(46, 180, 130),  // Green
            Color.rgb(33, 150, 243),  // Blue
            Color.rgb(130, 115, 255), // Purple
            Color.rgb(230, 149, 50),  // Orange
            Color.rgb(214, 184, 50),  // Gold
            Color.rgb(214, 76, 145),  // Pink
            Color.rgb(0, 166, 166),   // Teal
            Color.rgb(184, 134, 11),  // Amber
            Color.rgb(166, 77, 140)   // Magenta
    };

    private long greenSignalTime;

    private boolean gameRunning = false;
    private boolean waitingForSignal = false;
    private boolean signalShown = false;

    private int totalReactionTime = 0;
    private int validAttempts = 0;
    private int falseStarts = 0;

    private final int RED_COLOR = Color.rgb(220, 75, 90);
    private final int GREEN_COLOR = Color.rgb(46, 180, 130);
    private final int PURPLE_COLOR = Color.rgb(130, 115, 255);

    private final Runnable showGreenSignal = () -> {

        if (!gameRunning || !waitingForSignal) {
            return;
        }

        waitingForSignal = false;
        signalShown = true;

        tvReactionStatus.setText("TAP NOW!");
        tvReactionStatus.setBackgroundColor(signalColor);

        greenSignalTime = SystemClock.elapsedRealtime();

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reaction_time);
        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        setupButtons();
        resetScreen();
    }

    private void initializeViews() {

        tvReactionRound = findViewById(R.id.tvReactionRound);
        tvReactionBest = findViewById(R.id.tvReactionBest);
        tvReactionStatus = findViewById(R.id.tvReactionStatus);
        tvReactionResult = findViewById(R.id.tvReactionResult);
        tvReactionInstructions = findViewById(R.id.tvReactionInstructions);

        btnStartReaction = findViewById(R.id.btnStartReaction);
        btnReactionHistory = findViewById(R.id.btnReactionHistory);
    }

    private void setupButtons() {

        btnStartReaction.setOnClickListener(v -> startGame());

        tvReactionStatus.setOnClickListener(v -> handleScreenTap());

        btnReactionHistory.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ReactionTimeActivity.this,
                    ReactionTimeHistoryActivity.class
            );

            startActivity(intent);
        });
    }

    private void resetScreen() {

        tvReactionRound.setText("Attempt: 0/5");
        tvReactionBest.setText("Best: -- ms");

        tvReactionStatus.setText("READY?");
        tvReactionStatus.setBackgroundColor(RED_COLOR);

        tvReactionResult.setText("Your reaction time will appear here.");

        tvReactionInstructions.setText(
                "Wait for the green signal and tap as quickly as possible."
        );

        btnStartReaction.setText("START GAME");
        btnStartReaction.setEnabled(true);
    }

    private void startGame() {

        handler.removeCallbacksAndMessages(null);

        currentAttempt = 0;
        bestReactionTime = Integer.MAX_VALUE;

        totalReactionTime = 0;
        validAttempts = 0;
        falseStarts = 0;

        gameRunning = true;
        waitingForSignal = false;
        signalShown = false;

        tvReactionResult.setText("Game started! Get ready.");

        btnStartReaction.setEnabled(false);
        btnStartReaction.setText("GAME RUNNING");

        startNextAttempt();
    }

    private void startNextAttempt() {

        if (!gameRunning) {
            return;
        }

        if (currentAttempt >= TOTAL_ATTEMPTS) {
            finishGame();
            return;
        }

        waitingForSignal = true;
        signalShown = false;

        tvReactionRound.setText(
                "Attempt: " + (currentAttempt + 1) + "/" + TOTAL_ATTEMPTS
        );

        // Select a random waiting color.
        int waitingIndex = random.nextInt(gameColors.length);

        // Select a different random signal color.
        int signalIndex;

        do {
            signalIndex = random.nextInt(gameColors.length);
        } while (signalIndex == waitingIndex);

        waitingColor = gameColors[waitingIndex];
        signalColor = gameColors[signalIndex];

        tvReactionStatus.setText("WAIT FOR COLOR CHANGE!");
        tvReactionStatus.setBackgroundColor(waitingColor);

        tvReactionResult.setText("Do not tap yet!");

        // Random waiting time between 2 and 5 seconds.
        int delay = 2000 + random.nextInt(3001);

        handler.postDelayed(showGreenSignal, delay);
    }

    private void handleScreenTap() {

        if (!gameRunning) {
            return;
        }

        // User tapped before the green signal.
        if (waitingForSignal) {

            handler.removeCallbacks(showGreenSignal);

            waitingForSignal = false;
            signalShown = false;

            falseStarts++;
            currentAttempt++;

            tvReactionStatus.setText("TOO EARLY!");
            tvReactionStatus.setBackgroundColor(waitingColor);

            tvReactionResult.setText(
                    "False start! No reaction time recorded."
            );

            handler.postDelayed(() -> {

                if (gameRunning) {
                    startNextAttempt();
                }

            }, 1200);

            return;
        }

        // User tapped after the green signal.
        if (signalShown) {

            signalShown = false;

            long reactionTime =
                    SystemClock.elapsedRealtime() - greenSignalTime;

            int reaction = (int) reactionTime;

            totalReactionTime += reaction;
            validAttempts++;

            if (reaction < bestReactionTime) {
                bestReactionTime = reaction;
            }

            currentAttempt++;

            tvReactionBest.setText(
                    "Best: " + bestReactionTime + " ms"
            );

            tvReactionStatus.setText(reaction + " ms");
            tvReactionStatus.setBackgroundColor(GREEN_COLOR);

            tvReactionResult.setText(
                    "Good reaction! " + reaction + " milliseconds."
            );

            handler.postDelayed(() -> {

                if (gameRunning) {
                    startNextAttempt();
                }

            }, 1200);
        }
    }

    private void finishGame() {

        gameRunning = false;
        waitingForSignal = false;
        signalShown = false;

        handler.removeCallbacksAndMessages(null);

        btnStartReaction.setEnabled(true);
        btnStartReaction.setText("PLAY AGAIN");

        tvReactionStatus.setText("GAME COMPLETED");
        tvReactionStatus.setBackgroundColor(PURPLE_COLOR);

        tvReactionRound.setText("Attempt: 5/5");

        if (validAttempts > 0) {

            int averageReactionTime =
                    totalReactionTime / validAttempts;

            tvReactionResult.setText(
                    "Average: " + averageReactionTime + " ms"
                            + "\nBest: " + bestReactionTime + " ms"
                            + "\nValid attempts: " + validAttempts
                            + "\nFalse starts: " + falseStarts
            );

        } else {

            tvReactionBest.setText("Best: -- ms");

            tvReactionResult.setText(
                    "No valid reaction times recorded."
                            + "\nFalse starts: " + falseStarts
            );
        }

        tvReactionInstructions.setText(
                "Congratulations! You completed all five attempts."
        );

        // Save the completed game only once
        if (!resultSaved && validAttempts > 0) {

            int averageReaction = (int) (totalReactionTime / validAttempts);

            databaseHelper.saveReactionTimeResult(
                    averageReaction,
                    bestReactionTime,
                    validAttempts,
                    falseStarts,
                    System.currentTimeMillis()
            );

            resultSaved = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        gameRunning = false;

        handler.removeCallbacksAndMessages(null);
    }
}

