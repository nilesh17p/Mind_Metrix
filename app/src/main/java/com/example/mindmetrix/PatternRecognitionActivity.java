package com.example.mindmetrix;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PatternRecognitionActivity extends AppCompatActivity {

    private TextView tvPatternStatus;
    private TextView tvPatternRound;
    private TextView tvPatternScore;
    private TextView tvPatternQuestion;
    private TextView tvPatternHint;
    private TextView tvPatternResult;

    private Button[] optionButtons;
    private Button btnStartPatternGame;
    private Button btnPatternHistory;

    private DatabaseHelper databaseHelper;

    private int currentRound = 0;
    private int score = 0;
    private int correctAnswerIndex = -1;

    private boolean gameRunning = false;
    private boolean answerLocked = false;
    private boolean resultSaved = false;

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private final List<PatternQuestion> questions =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_recognition);

        databaseHelper = new DatabaseHelper(this);

        tvPatternStatus = findViewById(R.id.tvPatternStatus);
        tvPatternRound = findViewById(R.id.tvPatternRound);
        tvPatternScore = findViewById(R.id.tvPatternScore);
        tvPatternQuestion = findViewById(R.id.tvPatternQuestion);
        tvPatternHint = findViewById(R.id.tvPatternHint);
        tvPatternResult = findViewById(R.id.tvPatternResult);

        btnStartPatternGame = findViewById(R.id.btnStartPatternGame);
        btnPatternHistory = findViewById(R.id.btnPatternHistory);

        optionButtons = new Button[]{
                findViewById(R.id.btnPatternOption1),
                findViewById(R.id.btnPatternOption2),
                findViewById(R.id.btnPatternOption3),
                findViewById(R.id.btnPatternOption4)
        };

        for (int i = 0; i < optionButtons.length; i++) {
            final int selectedIndex = i;

            optionButtons[i].setOnClickListener(v ->
                    checkAnswer(selectedIndex)
            );
        }

        btnStartPatternGame.setOnClickListener(v -> startGame());

        btnPatternHistory.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PatternRecognitionActivity.this,
                    PatternRecognitionHistoryActivity.class
            );
            startActivity(intent);
        });

        showInitialScreen();
    }

    private void showInitialScreen() {

        tvPatternStatus.setText("Ready to challenge your brain?");
        tvPatternRound.setText("Round: 0/10");
        tvPatternScore.setText("Score: 0");

        tvPatternQuestion.setText("🔴  🔵  🔴  🔵  ❓");
        tvPatternHint.setText("Press Start to begin");

        tvPatternResult.setText("");

        btnStartPatternGame.setText("Start Pattern Recognition");

        setOptionsEnabled(false);
    }

    private void startGame() {

        handler.removeCallbacksAndMessages(null);

        currentRound = 0;
        score = 0;

        gameRunning = true;
        answerLocked = false;
        resultSaved = false;

        createQuestions();

        btnStartPatternGame.setText("Restart Game");

        tvPatternScore.setText("Score: 0");
        tvPatternResult.setText("");

        loadNextQuestion();
    }

    // =========================================
    // RANDOM QUESTION GENERATOR
    // =========================================

    private void createQuestions() {

        questions.clear();

        List<PatternQuestion> easyQuestions = new ArrayList<>();
        List<PatternQuestion> mediumQuestions = new ArrayList<>();
        List<PatternQuestion> hardQuestions = new ArrayList<>();

        // =====================================
        // EASY QUESTION BANK
        // =====================================

        addQuestion(easyQuestions,
                "🔴  🔵  🔴  🔵  ❓",
                "Choose the next shape",
                new String[]{
                        "🔴 Red Circle", "🔵 Blue Circle",
                        "🟢 Green Circle", "🟡 Yellow Circle"
                }, 0);

        addQuestion(easyQuestions,
                "🔺  🔺  🔵  🔺  🔺  ❓",
                "Observe the repeating sequence",
                new String[]{
                        "🔴 Red Circle", "🔵 Blue Circle",
                        "🔺 Red Triangle", "🟢 Green Circle"
                }, 1);

        addQuestion(easyQuestions,
                "⭐  🌙  ⭐  🌙  ❓",
                "What comes next?",
                new String[]{
                        "🌙 Moon", "⭐ Star",
                        "☀️ Sun", "❤️ Heart"
                }, 1);

        addQuestion(easyQuestions,
                "🟦  🔴  🟦  🔴  ❓",
                "Find the repeating shapes",
                new String[]{
                        "🟦 Blue Square", "🔴 Red Circle",
                        "🟢 Green Circle", "⭐ Star"
                }, 0);

        addQuestion(easyQuestions,
                "🟢  🟡  🟢  🟡  ❓",
                "Choose the next colour",
                new String[]{
                        "🟢 Green", "🔴 Red",
                        "🟡 Yellow", "🔵 Blue"
                }, 0);

        addQuestion(easyQuestions,
                "❤️  ⭐  ❤️  ⭐  ❓",
                "What comes next?",
                new String[]{
                        "⭐ Star", "🌙 Moon",
                        "❤️ Heart", "☀️ Sun"
                }, 2);

        addQuestion(easyQuestions,
                "🔴  🔺  🟦  🔴  🔺  ❓",
                "Complete the repeating sequence",
                new String[]{
                        "🔴 Red Circle", "🔺 Triangle",
                        "🟦 Blue Square", "⭐ Star"
                }, 2);

        addQuestion(easyQuestions,
                "🔴  🔴  🔵  🔴  🔴  ❓",
                "Observe the pattern carefully",
                new String[]{
                        "🔴 Red Circle", "🔵 Blue Circle",
                        "🟡 Yellow Circle", "🟢 Green Circle"
                }, 1);


        // =====================================
        // MEDIUM QUESTION BANK
        // =====================================

        addQuestion(mediumQuestions,
                "1  2  4  8  ❓",
                "Find the number pattern",
                new String[]{"10", "12", "16", "14"}, 2);

        addQuestion(mediumQuestions,
                "2  4  6  8  ❓",
                "Find the next number",
                new String[]{"9", "10", "12", "14"}, 1);

        addQuestion(mediumQuestions,
                "🟢  🔵  🔵  🟢  🔵  🔵  ❓",
                "Find the repeating sequence",
                new String[]{
                        "🔵 Blue Circle", "🔴 Red Circle",
                        "🟢 Green Circle", "🟡 Yellow Circle"
                }, 2);

        addQuestion(mediumQuestions,
                "3  6  9  12  ❓",
                "Find the next number",
                new String[]{"14", "15", "16", "18"}, 1);

        addQuestion(mediumQuestions,
                "5  10  15  20  ❓",
                "Identify the number pattern",
                new String[]{"22", "25", "30", "35"}, 1);

        addQuestion(mediumQuestions,
                "10  9  8  7  ❓",
                "Find the missing number",
                new String[]{"6", "5", "4", "8"}, 0);

        addQuestion(mediumQuestions,
                "2  5  8  11  ❓",
                "Find the next number",
                new String[]{"12", "13", "14", "15"}, 2);

        addQuestion(mediumQuestions,
                "🔴  🟦  🟦  🔴  🟦  🟦  ❓",
                "Complete the shape sequence",
                new String[]{
                        "🔴 Red Circle", "🟦 Blue Square",
                        "🟢 Green Circle", "⭐ Star"
                }, 0);


        // =====================================
        // HARD QUESTION BANK
        // =====================================

        addQuestion(hardQuestions,
                "1  4  9  16  ❓",
                "Find the next number",
                new String[]{"20", "24", "25", "36"}, 2);

        addQuestion(hardQuestions,
                "2  6  12  20  ❓",
                "Find the next number",
                new String[]{"28", "30", "32", "36"}, 1);

        addQuestion(hardQuestions,
                "1  1  2  3  5  8  ❓",
                "Find the next number",
                new String[]{"11", "12", "13", "15"}, 2);

        addQuestion(hardQuestions,
                "1  8  27  64  ❓",
                "Find the next number",
                new String[]{"100", "125", "144", "216"}, 1);

        addQuestion(hardQuestions,
                "2  3  5  8  12  ❓",
                "Find the next number",
                new String[]{"15", "16", "17", "18"}, 2);

        addQuestion(hardQuestions,
                "81  27  9  3  ❓",
                "Find the next number",
                new String[]{"1", "2", "0", "6"}, 0);

        addQuestion(hardQuestions,
                "3  7  15  31  ❓",
                "Identify the number pattern",
                new String[]{"47", "62", "63", "64"}, 2);

        addQuestion(hardQuestions,
                "4  9  19  39  ❓",
                "Find the next number",
                new String[]{"59", "69", "79", "89"}, 2);


        // Shuffle each difficulty bank independently.
        Collections.shuffle(easyQuestions);
        Collections.shuffle(mediumQuestions);
        Collections.shuffle(hardQuestions);

        // Select 3 Easy questions.
        questions.addAll(easyQuestions.subList(0, 3));

        // Select 4 Medium questions.
        questions.addAll(mediumQuestions.subList(0, 4));

        // Select 3 Hard questions.
        questions.addAll(hardQuestions.subList(0, 3));
    }

    // Helper method to add questions.
    private void addQuestion(
            List<PatternQuestion> questionBank,
            String sequence,
            String hint,
            String[] options,
            int correctAnswer
    ) {

        questionBank.add(new PatternQuestion(
                sequence,
                hint,
                options,
                correctAnswer
        ));
    }

    // =========================================
    // LOAD QUESTIONS
    // =========================================

    private void loadNextQuestion() {

        if (currentRound >= questions.size()) {
            finishGame();
            return;
        }

        answerLocked = false;

        PatternQuestion question = questions.get(currentRound);

        tvPatternStatus.setText(getDifficulty());

        tvPatternRound.setText(
                "Round: " + (currentRound + 1) + "/10"
        );

        tvPatternScore.setText("Score: " + score);

        tvPatternQuestion.setText(question.sequence);
        tvPatternHint.setText(question.hint);
        tvPatternResult.setText("");

        correctAnswerIndex = question.correctAnswer;

        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i].setText(question.options[i]);
            optionButtons[i].setEnabled(true);
        }
    }

    private String getDifficulty() {

        if (currentRound < 3) {
            return "EASY LEVEL";

        } else if (currentRound < 7) {
            return "MEDIUM LEVEL";

        } else {
            return "HARD LEVEL";
        }
    }

    // =========================================
    // CHECK ANSWER
    // =========================================

    private void checkAnswer(int selectedIndex) {

        if (!gameRunning || answerLocked) {
            return;
        }

        answerLocked = true;
        setOptionsEnabled(false);

        if (selectedIndex == correctAnswerIndex) {

            score += 10;

            tvPatternResult.setText("Correct answer! +10 points");

            tvPatternResult.setTextColor(
                    Color.parseColor("#2EB482")
            );

        } else {

            tvPatternResult.setText(
                    "Incorrect! Correct answer: " +
                            questions.get(currentRound)
                                    .options[correctAnswerIndex]
            );

            tvPatternResult.setTextColor(
                    Color.parseColor("#DC4B5A")
            );
        }

        tvPatternScore.setText("Score: " + score);

        currentRound++;

        handler.postDelayed(this::loadNextQuestion, 1200);
    }

    // =========================================
    // FINISH GAME AND SAVE RESULT
    // =========================================

    private void finishGame() {

        gameRunning = false;
        answerLocked = true;

        setOptionsEnabled(false);

        tvPatternStatus.setText("GAME COMPLETED");
        tvPatternRound.setText("Round: 10/10");
        tvPatternQuestion.setText("🏆");
        tvPatternHint.setText("You completed all 10 questions!");

        tvPatternResult.setText(
                "Your Final Score: " + score + "/100"
        );

        tvPatternResult.setTextColor(
                Color.parseColor("#2EB482")
        );

        btnStartPatternGame.setText("Play Again");

        if (!resultSaved) {

            int completedRounds = currentRound;
            int correctAnswers = score / 10;

            long playedAt = System.currentTimeMillis();

            long resultId = databaseHelper.savePatternRecognitionResult(
                    score,
                    completedRounds,
                    correctAnswers,
                    playedAt
            );

            if (resultId != -1) {

                resultSaved = true;

                tvPatternResult.setText(
                        "Your Final Score: " + score + "/100\n" +
                                "Result saved successfully!"
                );

            } else {

                tvPatternResult.setText(
                        "Your Final Score: " + score + "/100\n" +
                                "Could not save result. Please try again."
                );
            }
        }
    }

    private void setOptionsEnabled(boolean enabled) {

        for (Button button : optionButtons) {
            button.setEnabled(enabled);
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

    // =========================================
    // QUESTION DATA MODEL
    // =========================================

    private static class PatternQuestion {

        String sequence;
        String hint;
        String[] options;
        int correctAnswer;

        PatternQuestion(
                String sequence,
                String hint,
                String[] options,
                int correctAnswer
        ) {

            this.sequence = sequence;
            this.hint = hint;
            this.options = options;
            this.correctAnswer = correctAnswer;
        }
    }
}
