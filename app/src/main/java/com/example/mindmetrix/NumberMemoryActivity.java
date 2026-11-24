package com.example.mindmetrix;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class NumberMemoryActivity extends AppCompatActivity {

    private TextView tvNumberInstructions;
    private TextView tvNumberGameStatus;
    private TextView tvNumberRound;
    private TextView tvNumberDisplay;
    private TextView tvNumberAnswer;
    private TextView tvNumberResult;
    private TextView tvNumberTimer;

    private Button btnStartNumberGame;
    private Button btnSubmit;
    private Button btnDelete;

    private DatabaseHelper databaseHelper;

    private final StringBuilder userAnswer = new StringBuilder();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    private String correctNumber;

    private boolean gameRunning = false;
    private boolean acceptingAnswer = false;

    private int currentRound = 1;
    private int completedRounds = 0;

    private static final int TOTAL_ROUNDS = 5;
    private static final int POINTS_PER_ROUND = 20;
    private static final long NEXT_ROUND_DELAY = 1000;

    private Runnable countdownRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_number_memory);

        initializeViews();
        setupKeypad();

        databaseHelper = new DatabaseHelper(this);

        btnStartNumberGame.setOnClickListener(v -> startGame());

        setKeypadEnabled(false);

        Button btnNumberMemoryHistory =
                findViewById(R.id.btnNumberMemoryHistory);

        btnNumberMemoryHistory.setOnClickListener(v -> {
            Intent intent = new Intent(
                    NumberMemoryActivity.this,
                    NumberMemoryHistoryActivity.class
            );
            startActivity(intent);
        });
    }

    private void initializeViews() {

        tvNumberInstructions = findViewById(R.id.tvNumberInstructions);
        tvNumberGameStatus = findViewById(R.id.tvNumberGameStatus);
        tvNumberRound = findViewById(R.id.tvNumberRound);
        tvNumberDisplay = findViewById(R.id.tvNumberDisplay);
        tvNumberAnswer = findViewById(R.id.tvNumberAnswer);
        tvNumberResult = findViewById(R.id.tvNumberResult);
        tvNumberTimer = findViewById(R.id.tvNumberTimer);

        btnStartNumberGame = findViewById(R.id.btnStartNumberGame);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void setupKeypad() {

        int[] buttonIds = {
                R.id.btnKey0,
                R.id.btnKey1,
                R.id.btnKey2,
                R.id.btnKey3,
                R.id.btnKey4,
                R.id.btnKey5,
                R.id.btnKey6,
                R.id.btnKey7,
                R.id.btnKey8,
                R.id.btnKey9
        };

        for (int buttonId : buttonIds) {

            Button button = findViewById(buttonId);

            button.setOnClickListener(v -> {

                if (!acceptingAnswer) {
                    return;
                }

                if (userAnswer.length() < correctNumber.length()) {

                    userAnswer.append(button.getText().toString());

                    updateAnswerDisplay();
                }
            });
        }

        btnDelete.setOnClickListener(v -> {

            if (!acceptingAnswer) {
                return;
            }

            if (userAnswer.length() > 0) {

                userAnswer.deleteCharAt(userAnswer.length() - 1);

                updateAnswerDisplay();
            }
        });

        btnSubmit.setOnClickListener(v -> checkAnswer());
    }

    private void startGame() {

        handler.removeCallbacksAndMessages(null);
        countdownRunnable = null;

        currentRound = 1;
        completedRounds = 0;

        gameRunning = true;
        acceptingAnswer = false;

        tvNumberResult.setText("");

        btnStartNumberGame.setText("Restart Test");

        startRound();
    }

    private void startRound() {

        if (!gameRunning) {
            return;
        }

        userAnswer.setLength(0);

        acceptingAnswer = false;

        setKeypadEnabled(false);

        tvNumberAnswer.setText("Your answer: ");
        tvNumberTimer.setText("Timer: --");

        tvNumberRound.setText(
                "Round: " + currentRound + " / " + TOTAL_ROUNDS
        );

        tvNumberInstructions.setText(
                "Memorize the number carefully."
        );

        tvNumberGameStatus.setText("Memorize the number!");

        correctNumber = generateNumber(getNumberLength());

        tvNumberDisplay.setText(correctNumber);

        startCountdown(
                getDisplayTime() / 1000,
                "Memorize",
                () -> {

                    if (!gameRunning) {
                        return;
                    }

                    tvNumberDisplay.setText("????????");

                    tvNumberGameStatus.setText("Enter the number!");

                    tvNumberInstructions.setText(
                            "Use the keypad to enter the number you remember."
                    );

                    acceptingAnswer = true;

                    setKeypadEnabled(true);

                    startCountdown(
                            getAnswerTime() / 1000,
                            "Time left",
                            () -> {

                                if (gameRunning && acceptingAnswer) {

                                    acceptingAnswer = false;

                                    setKeypadEnabled(false);

                                    tvNumberDisplay.setText(correctNumber);

                                    tvNumberGameStatus.setText(
                                            "Time's up! You ran out of time."
                                    );

                                    finishGame();

                                    tvNumberGameStatus.setText(
                                            "Time's up! You ran out of time."
                                    );
                                }
                            }
                    );
                }
        );
    }

    private void startCountdown(
            long seconds,
            String label,
            Runnable onFinished) {

        stopCountdown();

        final int[] remainingSeconds = {(int) seconds};

        countdownRunnable = new Runnable() {

            @Override
            public void run() {

                if (!gameRunning) {
                    return;
                }

                if (remainingSeconds[0] > 0) {

                    tvNumberTimer.setText(
                            label + ": " + remainingSeconds[0] + "s"
                    );

                    remainingSeconds[0]--;

                    handler.postDelayed(this, 1000);

                } else {

                    tvNumberTimer.setText(label + ": 0s");

                    countdownRunnable = null;

                    onFinished.run();
                }
            }
        };

        handler.post(countdownRunnable);
    }

    private void stopCountdown() {

        if (countdownRunnable != null) {

            handler.removeCallbacks(countdownRunnable);

            countdownRunnable = null;
        }
    }

    private int getNumberLength() {

        return currentRound + 3;
    }

    private long getDisplayTime() {

        return currentRound * 1000L + 2000L;
    }

    private long getAnswerTime() {

        return currentRound * 2000L + 6000L;
    }

    private String generateNumber(int length) {

        StringBuilder number = new StringBuilder();

        // First digit cannot be zero.
        number.append(random.nextInt(9) + 1);

        for (int i = 1; i < length; i++) {

            number.append(random.nextInt(10));
        }

        return number.toString();
    }

    private void updateAnswerDisplay() {

        tvNumberAnswer.setText(
                "Your answer: " + userAnswer
        );
    }

    private void checkAnswer() {

        if (!acceptingAnswer || !gameRunning) {
            return;
        }

        if (userAnswer.length() == 0) {

            tvNumberGameStatus.setText("Please enter a number!");

            return;
        }

        if (userAnswer.length() < correctNumber.length()) {

            tvNumberGameStatus.setText(
                    "Enter all " + correctNumber.length() + " digits."
            );

            return;
        }

        acceptingAnswer = false;

        stopCountdown();

        setKeypadEnabled(false);

        if (userAnswer.toString().equals(correctNumber)) {

            completedRounds++;

            tvNumberTimer.setText("Round completed!");

            tvNumberGameStatus.setText("Correct! Excellent memory!");

            tvNumberDisplay.setText(correctNumber);

            tvNumberResult.setText(
                    "Round " + currentRound + " completed!\n" +
                            "Points earned: " + POINTS_PER_ROUND
            );

            if (currentRound == TOTAL_ROUNDS) {

                finishGame();

            } else {

                currentRound++;

                handler.postDelayed(() -> {

                    if (gameRunning) {
                        startRound();
                    }

                }, NEXT_ROUND_DELAY);
            }

        } else {

            tvNumberGameStatus.setText("Incorrect answer!");

            tvNumberDisplay.setText(correctNumber);

            finishGame();
        }
    }

    private void finishGame() {

        gameRunning = false;
        acceptingAnswer = false;

        stopCountdown();

        handler.removeCallbacksAndMessages(null);

        setKeypadEnabled(false);

        int finalScore = completedRounds * POINTS_PER_ROUND;

        if (completedRounds == TOTAL_ROUNDS) {

            tvNumberGameStatus.setText("Outstanding! Perfect memory!");

        } else {

            tvNumberGameStatus.setText("Game Over!");
        }

        tvNumberInstructions.setText(
                "You completed " + completedRounds +
                        " out of " + TOTAL_ROUNDS + " rounds."
        );

        tvNumberResult.setText(
                "GAME COMPLETED\n\n" +
                        "Score: " + finalScore + "/100\n" +
                        "Rounds completed: " + completedRounds +
                        "/" + TOTAL_ROUNDS + "\n" +
                        "Best round reached: " +
                        Math.max(1, currentRound) + "\n\n" +
                        (completedRounds == TOTAL_ROUNDS
                                ? "You completed every round successfully!"
                                : "Keep practicing to improve your memory.")
        );

        long rowId = databaseHelper.saveNumberMemoryResult(
                finalScore,
                completedRounds,
                currentRound,
                System.currentTimeMillis()
        );

        if (rowId != -1) {

            tvNumberResult.append("\n\nResult saved successfully!");

        } else {

            tvNumberResult.append("\n\nCould not save result.");
        }

        btnStartNumberGame.setText("Play Again");
    }

    private void setKeypadEnabled(boolean enabled) {

        int[] buttonIds = {
                R.id.btnKey0,
                R.id.btnKey1,
                R.id.btnKey2,
                R.id.btnKey3,
                R.id.btnKey4,
                R.id.btnKey5,
                R.id.btnKey6,
                R.id.btnKey7,
                R.id.btnKey8,
                R.id.btnKey9,
                R.id.btnDelete,
                R.id.btnSubmit
        };

        for (int buttonId : buttonIds) {

            View button = findViewById(buttonId);

            button.setEnabled(enabled);

            button.setAlpha(enabled ? 1.0f : 0.5f);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacksAndMessages(null);

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
