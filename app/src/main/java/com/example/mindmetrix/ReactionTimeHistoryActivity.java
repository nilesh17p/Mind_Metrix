package com.example.mindmetrix;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReactionTimeHistoryActivity extends AppCompatActivity {

    private TextView tvTotalGames;
    private TextView tvFastest;
    private TextView tvLatestAverage;
    private TextView tvFalseStarts;
    private TextView tvEmptyHistory;

    private LinearLayout layoutHistoryList;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction_time_history);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        loadHistory();
    }

    private void initializeViews() {

        tvTotalGames = findViewById(R.id.tvReactionTotalGames);
        tvFastest = findViewById(R.id.tvReactionFastest);
        tvLatestAverage = findViewById(R.id.tvReactionLatestAverage);
        tvFalseStarts = findViewById(R.id.tvReactionFalseStarts);
        tvEmptyHistory = findViewById(R.id.tvReactionEmptyHistory);

        layoutHistoryList = findViewById(R.id.layoutReactionHistoryList);
    }

    private void loadHistory() {

        layoutHistoryList.removeAllViews();

        int totalGames = databaseHelper.getReactionTimeAttemptCount();
        int fastest = databaseHelper.getReactionTimeBestResult();
        int latestAverage = databaseHelper.getLatestReactionTimeAverage();

        tvTotalGames.setText(String.valueOf(totalGames));

        tvFastest.setText(
                fastest > 0 ? fastest + " ms" : "-- ms"
        );

        tvLatestAverage.setText(
                latestAverage > 0 ? latestAverage + " ms" : "-- ms"
        );

        int totalFalseStarts = 0;

        Cursor cursor = databaseHelper.getAllReactionTimeResults();

        if (cursor != null) {

            try {

                while (cursor.moveToNext()) {

                    int averageReaction = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_AVERAGE_REACTION
                            )
                    );

                    int bestReaction = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_BEST_REACTION
                            )
                    );

                    int validAttempts = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_VALID_ATTEMPTS
                            )
                    );

                    int falseStarts = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_FALSE_STARTS
                            )
                    );

                    long playedAt = cursor.getLong(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_REACTION_PLAYED_AT
                            )
                    );

                    totalFalseStarts += falseStarts;

                    addHistoryCard(
                            averageReaction,
                            bestReaction,
                            validAttempts,
                            falseStarts,
                            playedAt
                    );
                }

            } finally {
                cursor.close();
            }
        }

        tvFalseStarts.setText(String.valueOf(totalFalseStarts));

        if (totalGames == 0) {
            tvEmptyHistory.setVisibility(TextView.VISIBLE);
        } else {
            tvEmptyHistory.setVisibility(TextView.GONE);
        }
    }

    private void addHistoryCard(
            int averageReaction,
            int bestReaction,
            int validAttempts,
            int falseStarts,
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

        TextView averageText = createTextView(
                "Average Reaction: " + averageReaction + " ms",
                19,
                "#AFA8FF",
                true
        );

        TextView bestText = createTextView(
                "Best Reaction: " + bestReaction + " ms",
                15,
                "#FFFFFF",
                false
        );

        TextView validText = createTextView(
                "Valid Attempts: " + validAttempts + "/10",
                15,
                "#D0D5E8",
                false
        );

        TextView falseStartText = createTextView(
                "False Starts: " + falseStarts,
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

        card.addView(averageText);
        card.addView(bestText);
        card.addView(validText);
        card.addView(falseStartText);
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
            textView.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );
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
