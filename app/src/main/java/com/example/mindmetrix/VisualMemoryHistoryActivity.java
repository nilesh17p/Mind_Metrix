package com.example.mindmetrix;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Date;

public class VisualMemoryHistoryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private LinearLayout historyContainer;
    private TextView tvHistorySummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_memory_history);

        historyContainer = findViewById(R.id.historyContainer);
        tvHistorySummary = findViewById(R.id.tvHistorySummary);

        databaseHelper = new DatabaseHelper(this);

        TextView title = findViewById(R.id.tvHistoryTitle);
        title.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
    }

    private void loadHistory() {
        historyContainer.removeAllViews();

        int attempts = databaseHelper.getAttemptCount();
        int bestScore = databaseHelper.getBestScore();

        tvHistorySummary.setText(
                "Attempts: " + attempts + "     Best score: "
                        + bestScore + "/100");

        Cursor cursor = databaseHelper.getAllVisualMemoryResults();

        try {
            if (cursor.getCount() == 0) {
                TextView emptyMessage = createTextView(
                        "No saved results yet.\n\n"
                                + "Complete a Visual Memory test "
                                + "to see your history here.",
                        17,
                        Color.LTGRAY);

                emptyMessage.setGravity(Gravity.CENTER);
                emptyMessage.setPadding(12, 32, 12, 32);
                historyContainer.addView(emptyMessage);
                return;
            }

            DateFormat dateFormat =
                    DateFormat.getDateTimeInstance(
                            DateFormat.MEDIUM,
                            DateFormat.SHORT);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_ID));

                int score = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_SCORE));

                int rounds = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_ROUNDS));

                int maxPattern = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_MAX_PATTERN));

                long playedAt = cursor.getLong(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_PLAYED_AT));

                String dateText = dateFormat.format(
                        new Date(playedAt));

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(16, 16, 16, 16);
                card.setBackgroundColor(Color.rgb(32, 42, 68));

                LinearLayout.LayoutParams cardParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);

                cardParams.setMargins(0, 0, 0, dpToPx(12));
                card.setLayoutParams(cardParams);

                TextView heading = createTextView(
                        "Attempt #" + id + "     Score: "
                                + score + "/100",
                        19,
                        Color.WHITE);

                heading.setTypeface(null, android.graphics.Typeface.BOLD);

                TextView details = createTextView(
                        "Rounds completed: " + rounds + "/5\n"
                                + "Longest pattern reached: "
                                + maxPattern + " tiles\n"
                                + "Played: " + dateText,
                        15,
                        Color.LTGRAY);

                details.setPadding(0, dpToPx(8), 0, 0);

                card.addView(heading);
                card.addView(details);
                historyContainer.addView(card);
            }

        } finally {
            cursor.close();
        }
    }

    private TextView createTextView(
            String text, int sizeSp, int color) {

        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(sizeSp);
        view.setTextColor(color);
        return view;
    }

    private int dpToPx(int dp) {
        return Math.round(
                dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}

