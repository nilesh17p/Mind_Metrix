package com.example.mindmetrix;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PatternRecognitionHistoryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private TextView tvPatternTotalAttempts;
    private TextView tvPatternBestScore;
    private TextView tvPatternEmptyHistory;

    private LinearLayout patternHistoryContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_recognition_history);

        databaseHelper = new DatabaseHelper(this);

        tvPatternTotalAttempts =
                findViewById(R.id.tvPatternTotalAttempts);

        tvPatternBestScore =
                findViewById(R.id.tvPatternBestScore);

        tvPatternEmptyHistory =
                findViewById(R.id.tvPatternEmptyHistory);

        patternHistoryContainer =
                findViewById(R.id.patternHistoryContainer);

        loadHistory();
    }

    private void loadHistory() {

        patternHistoryContainer.removeAllViews();

        Cursor cursor =
                databaseHelper.getAllPatternRecognitionResults();

        int totalAttempts = cursor.getCount();

        tvPatternTotalAttempts.setText(
                String.valueOf(totalAttempts)
        );

        int bestScore = 0;

        if (totalAttempts == 0) {

            tvPatternBestScore.setText("0/100");

            tvPatternEmptyHistory.setVisibility(TextView.VISIBLE);

            cursor.close();
            return;
        }

        tvPatternEmptyHistory.setVisibility(TextView.GONE);

        try {

            int scoreIndex = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COL_PATTERN_SCORE
            );

            int roundsIndex = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COL_PATTERN_ROUNDS
            );

            int correctIndex = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COL_CORRECT_ANSWERS
            );

            int dateIndex = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COL_PATTERN_PLAYED_AT
            );

            while (cursor.moveToNext()) {

                int score = cursor.getInt(scoreIndex);

                int completedRounds = cursor.getInt(roundsIndex);

                int correctAnswers = cursor.getInt(correctIndex);

                long playedAt = cursor.getLong(dateIndex);

                if (score > bestScore) {
                    bestScore = score;
                }

                addHistoryCard(
                        score,
                        completedRounds,
                        correctAnswers,
                        playedAt
                );
            }

            tvPatternBestScore.setText(bestScore + "/100");

        } finally {
            cursor.close();
        }
    }

    private void addHistoryCard(
            int score,
            int completedRounds,
            int correctAnswers,
            long playedAt) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 16, 18, 16);
        card.setBackgroundColor(0xFF272F49);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, 14);

        card.setLayoutParams(cardParams);

        // Score

        TextView scoreView = new TextView(this);

        scoreView.setText("Score: " + score + "/100");

        scoreView.setTextColor(0xFFFFFFFF);
        scoreView.setTextSize(20);

        scoreView.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        // Game details

        TextView detailsView = new TextView(this);

        detailsView.setText(
                "Completed Rounds: " + completedRounds + "/10" +
                        "\nCorrect Answers: " + correctAnswers + "/10"
        );

        detailsView.setTextColor(0xFFD0D5E8);
        detailsView.setTextSize(14);
        detailsView.setPadding(0, 8, 0, 8);

        // Date and time

        TextView dateView = new TextView(this);

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "dd MMM yyyy, hh:mm a",
                        Locale.getDefault()
                );

        dateView.setText(
                "Played: " + dateFormat.format(new Date(playedAt))
        );

        dateView.setTextColor(0xFFAFA8FF);
        dateView.setTextSize(12);

        card.addView(scoreView);
        card.addView(detailsView);
        card.addView(dateView);

        patternHistoryContainer.addView(card);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
