package com.example.mindmetrix;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AttentionHistoryActivity extends AppCompatActivity {

    private TextView tvTotalAttempts;
    private TextView tvBestScore;
    private TextView tvLatestScore;
    private TextView tvBestAccuracy;
    private TextView tvEmptyHistory;

    private LinearLayout layoutHistoryList;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_history);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        loadHistory();
    }

    private void initializeViews() {

        tvTotalAttempts = findViewById(R.id.tvAttentionTotalAttempts);
        tvBestScore = findViewById(R.id.tvAttentionBestScore);
        tvLatestScore = findViewById(R.id.tvAttentionLatestScore);
        tvBestAccuracy = findViewById(R.id.tvAttentionBestAccuracy);

        tvEmptyHistory = findViewById(R.id.tvAttentionEmptyHistory);

        layoutHistoryList = findViewById(R.id.layoutAttentionHistoryList);
    }

    private void loadHistory() {

        layoutHistoryList.removeAllViews();

        int totalAttempts = databaseHelper.getAttentionAttemptCount();
        int bestScore = databaseHelper.getAttentionBestScore();
        int latestScore = databaseHelper.getLatestAttentionScore();

        tvTotalAttempts.setText(String.valueOf(totalAttempts));
        tvBestScore.setText(String.valueOf(bestScore));
        tvLatestScore.setText(String.valueOf(latestScore));

        int bestAccuracy = 0;

        Cursor cursor = databaseHelper.getAllAttentionResults();

        if (cursor != null) {

            try {

                while (cursor.moveToNext()) {

                    int score = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_ATTENTION_SCORE
                            )
                    );

                    int completedRounds = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_ATTENTION_ROUNDS
                            )
                    );

                    int correctAnswers = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_ATTENTION_CORRECT
                            )
                    );

                    long playedAt = cursor.getLong(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_ATTENTION_PLAYED_AT
                            )
                    );

                    if (completedRounds > 0) {

                        int accuracy = Math.round(
                                (correctAnswers * 100f) / completedRounds
                        );

                        bestAccuracy = Math.max(bestAccuracy, accuracy);
                    }

                    addHistoryCard(
                            score,
                            completedRounds,
                            correctAnswers,
                            playedAt
                    );
                }

            } finally {
                cursor.close();
            }
        }

        tvBestAccuracy.setText(bestAccuracy + "%");

        if (totalAttempts == 0) {
            tvEmptyHistory.setVisibility(TextView.VISIBLE);
        } else {
            tvEmptyHistory.setVisibility(TextView.GONE);
        }
    }

    private void addHistoryCard(
            int score,
            int completedRounds,
            int correctAnswers,
            long playedAt
    ) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.rgb(39, 47, 73));
        background.setCornerRadius(dp(14));

        card.setBackground(background);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.bottomMargin = dp(12);

        card.setLayoutParams(cardParams);

        TextView scoreText = createTextView(
                "Score: " + score + "/100",
                19,
                "#AFA8FF",
                true
        );

        TextView correctText = createTextView(
                "Correct Answers: " + correctAnswers
                        + "/" + completedRounds,
                15,
                "#FFFFFF",
                false
        );

        int accuracy = 0;

        if (completedRounds > 0) {
            accuracy = Math.round(
                    (correctAnswers * 100f) / completedRounds
            );
        }

        TextView accuracyText = createTextView(
                "Accuracy: " + accuracy + "%",
                15,
                "#D0D5E8",
                false
        );

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "dd MMM yyyy, hh:mm a",
                        Locale.getDefault()
                );

        String date = dateFormat.format(new Date(playedAt));

        TextView dateText = createTextView(
                date,
                13,
                "#D0D5E8",
                false
        );

        card.addView(scoreText);
        card.addView(correctText);
        card.addView(accuracyText);
        card.addView(dateText);

        layoutHistoryList.addView(card);
    }

    private TextView createTextView(
            String text,
            int textSize,
            String textColor,
            boolean bold
    ) {

        TextView textView = new TextView(this);

        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setTextColor(Color.parseColor(textColor));

        if (bold) {
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        textView.setPadding(0, dp(4), 0, dp(4));

        return textView;
    }

    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && layoutHistoryList != null) {
            loadHistory();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}

