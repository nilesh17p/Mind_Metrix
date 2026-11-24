package com.example.mindmetrix;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NumberMemoryHistoryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private TextView tvNumberTotalAttempts;
    private TextView tvNumberBestScore;
    private TextView tvNumberEmptyHistory;
    private LinearLayout numberHistoryContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_memory_history);

        databaseHelper = new DatabaseHelper(this);

        tvNumberTotalAttempts = findViewById(R.id.tvNumberTotalAttempts);
        tvNumberBestScore = findViewById(R.id.tvNumberBestScore);
        tvNumberEmptyHistory = findViewById(R.id.tvNumberEmptyHistory);
        numberHistoryContainer = findViewById(R.id.numberHistoryContainer);

        loadHistory();
    }

    private void loadHistory() {

        numberHistoryContainer.removeAllViews();

        Cursor cursor = databaseHelper.getAllNumberMemoryResults();

        int totalAttempts = cursor.getCount();
        int bestScore = 0;

        tvNumberTotalAttempts.setText(String.valueOf(totalAttempts));

        if (totalAttempts == 0) {
            tvNumberBestScore.setText("0/100");
            tvNumberEmptyHistory.setVisibility(TextView.VISIBLE);
            cursor.close();
            return;
        }

        tvNumberEmptyHistory.setVisibility(TextView.GONE);

        try {
            int scoreIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NUMBER_SCORE);
            int roundsIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NUMBER_ROUNDS);
            int bestRoundIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BEST_ROUND);
            int dateIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NUMBER_PLAYED_AT);

            while (cursor.moveToNext()) {

                int score = cursor.getInt(scoreIndex);
                int completedRounds = cursor.getInt(roundsIndex);
                int bestRound = cursor.getInt(bestRoundIndex);
                long playedAt = cursor.getLong(dateIndex);

                if (score > bestScore) {
                    bestScore = score;
                }

                addHistoryCard(score, completedRounds, bestRound, playedAt);
            }

            tvNumberBestScore.setText(bestScore + "/100");

        } finally {
            cursor.close();
        }
    }

    private void addHistoryCard(int score, int completedRounds,
                                int bestRound, long playedAt) {

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

        TextView scoreView = new TextView(this);
        scoreView.setText("Score: " + score + "/100");
        scoreView.setTextColor(0xFFFFFFFF);
        scoreView.setTextSize(20);
        scoreView.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView roundsView = new TextView(this);
        roundsView.setText(
                "Completed Rounds: " + completedRounds + "/5"
                        + "\nBest Round Reached: " + bestRound + "/5"
        );
        roundsView.setTextColor(0xFFD0D5E8);
        roundsView.setTextSize(14);
        roundsView.setPadding(0, 8, 0, 8);

        TextView dateView = new TextView(this);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd MMM yyyy, hh:mm a", Locale.getDefault()
        );

        dateView.setText("Played: " + dateFormat.format(new Date(playedAt)));
        dateView.setTextColor(0xFFAFA8FF);
        dateView.setTextSize(12);

        card.addView(scoreView);
        card.addView(roundsView);
        card.addView(dateView);

        numberHistoryContainer.addView(card);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
