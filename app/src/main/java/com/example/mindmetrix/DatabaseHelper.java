package com.example.mindmetrix;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mind_metrix.db";
    private static final int DATABASE_VERSION = 6;

    // =========================================
    // VISUAL MEMORY TABLE
    // =========================================

    public static final String TABLE_RESULTS = "visual_memory_results";

    public static final String COL_ID = "id";
    public static final String COL_SCORE = "score";
    public static final String COL_ROUNDS = "completed_rounds";
    public static final String COL_MAX_PATTERN = "max_pattern_length";
    public static final String COL_PLAYED_AT = "played_at";

    // =========================================
    // USER ACCOUNT TABLE
    // =========================================

    public static final String TABLE_USERS = "users";

    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD_HASH = "password_hash";
    public static final String COL_USER_PASSWORD_SALT = "password_salt";
    public static final String COL_USER_PASSWORD_ALGORITHM = "password_algorithm";
    public static final String COL_USER_CREATED_AT = "created_at";

    // =========================================
    // NUMBER MEMORY TABLE
    // =========================================

    public static final String TABLE_NUMBER_RESULTS =
            "number_memory_results";

    public static final String COL_NUMBER_ID = "id";
    public static final String COL_NUMBER_SCORE = "score";
    public static final String COL_NUMBER_ROUNDS = "completed_rounds";
    public static final String COL_BEST_ROUND = "best_round";
    public static final String COL_NUMBER_PLAYED_AT = "played_at";

    // =========================================
    // PATTERN RECOGNITION TABLE
    // =========================================

    public static final String TABLE_PATTERN_RESULTS =
            "pattern_recognition_results";

    public static final String COL_PATTERN_ID = "id";
    public static final String COL_PATTERN_SCORE = "score";
    public static final String COL_PATTERN_ROUNDS = "completed_rounds";
    public static final String COL_CORRECT_ANSWERS = "correct_answers";
    public static final String COL_PATTERN_PLAYED_AT = "played_at";

    // =========================================
    // ATTENTION TABLE
    // =========================================

    public static final String TABLE_ATTENTION_RESULTS =
            "attention_results";

    public static final String COL_ATTENTION_ID = "id";
    public static final String COL_ATTENTION_SCORE = "score";
    public static final String COL_ATTENTION_ROUNDS = "completed_rounds";
    public static final String COL_ATTENTION_CORRECT = "correct_answers";
    public static final String COL_ATTENTION_PLAYED_AT = "played_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // =========================================
    // REACTION TIME TABLE
    // =========================================

    public static final String TABLE_REACTION_RESULTS =
            "reaction_time_results";

    public static final String COL_REACTION_ID = "id";
    public static final String COL_AVERAGE_REACTION = "average_reaction";
    public static final String COL_BEST_REACTION = "best_reaction";
    public static final String COL_VALID_ATTEMPTS = "valid_attempts";
    public static final String COL_FALSE_STARTS = "false_starts";
    public static final String COL_REACTION_PLAYED_AT = "played_at";

    // =========================================
    // CREATE ALL TABLES
    // =========================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        createVisualMemoryTable(db);
        createNumberMemoryTable(db);
        createPatternRecognitionTable(db);
        createAttentionTable(db);
        createReactionTimeTable(db);

        // User Account Table
        createUserTable(db);
    }

    private void createVisualMemoryTable(SQLiteDatabase db) {

        String query = "CREATE TABLE IF NOT EXISTS " +
                TABLE_RESULTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SCORE + " INTEGER NOT NULL, " +
                COL_ROUNDS + " INTEGER NOT NULL, " +
                COL_MAX_PATTERN + " INTEGER NOT NULL, " +
                COL_PLAYED_AT + " INTEGER NOT NULL" +
                ")";

        db.execSQL(query);
    }

    private void createNumberMemoryTable(SQLiteDatabase db) {

        String query = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NUMBER_RESULTS + " (" +
                COL_NUMBER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NUMBER_SCORE + " INTEGER NOT NULL, " +
                COL_NUMBER_ROUNDS + " INTEGER NOT NULL, " +
                COL_BEST_ROUND + " INTEGER NOT NULL, " +
                COL_NUMBER_PLAYED_AT + " INTEGER NOT NULL" +
                ")";

        db.execSQL(query);
    }

    private void createPatternRecognitionTable(SQLiteDatabase db) {

        String query = "CREATE TABLE IF NOT EXISTS " +
                TABLE_PATTERN_RESULTS + " (" +
                COL_PATTERN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PATTERN_SCORE + " INTEGER NOT NULL, " +
                COL_PATTERN_ROUNDS + " INTEGER NOT NULL, " +
                COL_CORRECT_ANSWERS + " INTEGER NOT NULL, " +
                COL_PATTERN_PLAYED_AT + " INTEGER NOT NULL" +
                ")";

        db.execSQL(query);
    }

    // =========================================
    // CREATE ATTENTION TABLE
    // =========================================

    private void createAttentionTable(SQLiteDatabase db) {

        String query = "CREATE TABLE IF NOT EXISTS " +
                TABLE_ATTENTION_RESULTS + " (" +
                COL_ATTENTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ATTENTION_SCORE + " INTEGER NOT NULL, " +
                COL_ATTENTION_ROUNDS + " INTEGER NOT NULL, " +
                COL_ATTENTION_CORRECT + " INTEGER NOT NULL, " +
                COL_ATTENTION_PLAYED_AT + " INTEGER NOT NULL" +
                ")";

        db.execSQL(query);


    }

    // =========================================
    // DATABASE UPGRADE
    // =========================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        if (oldVersion < 2) {
            createNumberMemoryTable(db);
        }

        if (oldVersion < 3) {
            createPatternRecognitionTable(db);
        }

        if (oldVersion < 4) {
            createAttentionTable(db);
        }

        if (oldVersion < 5) {
            createReactionTimeTable(db);
        }

        if (oldVersion < 6) {
            createUserTable(db);
        }
    }

    // =========================================
    // VISUAL MEMORY DATABASE METHODS
    // =========================================

    public long saveVisualMemoryResult(
            int score,
            int completedRounds,
            int maxPatternLength,
            long playedAt) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_SCORE, score);
        values.put(COL_ROUNDS, completedRounds);
        values.put(COL_MAX_PATTERN, maxPatternLength);
        values.put(COL_PLAYED_AT, playedAt);

        return db.insert(TABLE_RESULTS, null, values);
    }

    public Cursor getAllVisualMemoryResults() {

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_RESULTS,
                new String[]{
                        COL_ID,
                        COL_SCORE,
                        COL_ROUNDS,
                        COL_MAX_PATTERN,
                        COL_PLAYED_AT
                },
                null,
                null,
                null,
                null,
                COL_PLAYED_AT + " DESC"
        );
    }

    public int getAttemptCount() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_RESULTS,
                null)) {

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }

    public int getBestScore() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT MAX(" + COL_SCORE + ") FROM " + TABLE_RESULTS,
                null)) {

            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }

    public int getLatestScore() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_RESULTS,
                new String[]{COL_SCORE},
                null,
                null,
                null,
                null,
                COL_ID + " DESC",
                "1")) {

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }

    // =========================================
    // CREATE REACTION TIME TABLE
    // =========================================

    private void createReactionTimeTable(SQLiteDatabase db) {

        String query = "CREATE TABLE IF NOT EXISTS " +
                TABLE_REACTION_RESULTS + " (" +
                COL_REACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_AVERAGE_REACTION + " INTEGER NOT NULL, " +
                COL_BEST_REACTION + " INTEGER NOT NULL, " +
                COL_VALID_ATTEMPTS + " INTEGER NOT NULL, " +
                COL_FALSE_STARTS + " INTEGER NOT NULL, " +
                COL_REACTION_PLAYED_AT + " INTEGER NOT NULL" +
                ")";

        db.execSQL(query);
    }

    // =========================================
    // CREATE USER ACCOUNT TABLE
    // =========================================

    private void createUserTable(SQLiteDatabase db) {

        String query = "CREATE TABLE IF NOT EXISTS " +
                TABLE_USERS + " (" +

                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                COL_USER_NAME + " TEXT NOT NULL, " +

                COL_USER_EMAIL + " TEXT NOT NULL COLLATE NOCASE UNIQUE, " +

                COL_USER_PASSWORD_HASH + " TEXT NOT NULL, " +

                COL_USER_PASSWORD_SALT + " TEXT NOT NULL, " +

                COL_USER_PASSWORD_ALGORITHM + " TEXT NOT NULL, " +

                COL_USER_CREATED_AT + " INTEGER NOT NULL" +

                ")";

        db.execSQL(query);
    }

    // =========================================
    // NUMBER MEMORY DATABASE METHODS
    // =========================================

    public long saveNumberMemoryResult(
            int score,
            int completedRounds,
            int bestRound,
            long playedAt) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NUMBER_SCORE, score);
        values.put(COL_NUMBER_ROUNDS, completedRounds);
        values.put(COL_BEST_ROUND, bestRound);
        values.put(COL_NUMBER_PLAYED_AT, playedAt);

        return db.insert(TABLE_NUMBER_RESULTS, null, values);
    }

    public Cursor getAllNumberMemoryResults() {

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_NUMBER_RESULTS,
                new String[]{
                        COL_NUMBER_ID,
                        COL_NUMBER_SCORE,
                        COL_NUMBER_ROUNDS,
                        COL_BEST_ROUND,
                        COL_NUMBER_PLAYED_AT
                },
                null,
                null,
                null,
                null,
                COL_NUMBER_ID + " DESC"
        );
    }

    public int getNumberMemoryAttemptCount() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_NUMBER_RESULTS,
                null)) {

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }

    public int getNumberMemoryBestScore() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT MAX(" + COL_NUMBER_SCORE + ") FROM " +
                        TABLE_NUMBER_RESULTS,
                null)) {

            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }

    public int getLatestNumberMemoryScore() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_NUMBER_RESULTS,
                new String[]{COL_NUMBER_SCORE},
                null,
                null,
                null,
                null,
                COL_NUMBER_ID + " DESC",
                "1")) {

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }

    // =========================================
    // PATTERN RECOGNITION DATABASE METHODS
    // =========================================

    public long savePatternRecognitionResult(
            int score,
            int completedRounds,
            int correctAnswers,
            long playedAt) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_PATTERN_SCORE, score);
        values.put(COL_PATTERN_ROUNDS, completedRounds);
        values.put(COL_CORRECT_ANSWERS, correctAnswers);
        values.put(COL_PATTERN_PLAYED_AT, playedAt);

        return db.insert(TABLE_PATTERN_RESULTS, null, values);
    }

    public Cursor getAllPatternRecognitionResults() {

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_PATTERN_RESULTS,
                new String[]{
                        COL_PATTERN_ID,
                        COL_PATTERN_SCORE,
                        COL_PATTERN_ROUNDS,
                        COL_CORRECT_ANSWERS,
                        COL_PATTERN_PLAYED_AT
                },
                null,
                null,
                null,
                null,
                COL_PATTERN_ID + " DESC"
        );
    }

    public int getPatternRecognitionAttemptCount() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_PATTERN_RESULTS,
                null)) {

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }

    public int getPatternRecognitionBestScore() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT MAX(" + COL_PATTERN_SCORE + ") FROM " +
                        TABLE_PATTERN_RESULTS,
                null)) {

            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }

    public int getLatestPatternRecognitionScore() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_PATTERN_RESULTS,
                new String[]{COL_PATTERN_SCORE},
                null,
                null,
                null,
                null,
                COL_PATTERN_ID + " DESC",
                "1")) {

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }
    // =========================================
    // ATTENTION DATABASE METHODS
    // =========================================

    public long saveAttentionResult(
            int score,
            int completedRounds,
            int correctAnswers,
            long playedAt) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_ATTENTION_SCORE, score);
        values.put(COL_ATTENTION_ROUNDS, completedRounds);
        values.put(COL_ATTENTION_CORRECT, correctAnswers);
        values.put(COL_ATTENTION_PLAYED_AT, playedAt);

        return db.insert(TABLE_ATTENTION_RESULTS, null, values);
    }


    public Cursor getAllAttentionResults() {

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_ATTENTION_RESULTS,
                new String[]{
                        COL_ATTENTION_ID,
                        COL_ATTENTION_SCORE,
                        COL_ATTENTION_ROUNDS,
                        COL_ATTENTION_CORRECT,
                        COL_ATTENTION_PLAYED_AT
                },
                null,
                null,
                null,
                null,
                COL_ATTENTION_ID + " DESC"
        );
    }


    public int getAttentionAttemptCount() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_ATTENTION_RESULTS,
                null)) {

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }


    public int getAttentionBestScore() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT MAX(" + COL_ATTENTION_SCORE + ") FROM " +
                        TABLE_ATTENTION_RESULTS,
                null)) {

            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }


    public int getLatestAttentionScore() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_ATTENTION_RESULTS,
                new String[]{COL_ATTENTION_SCORE},
                null,
                null,
                null,
                null,
                COL_ATTENTION_ID + " DESC",
                "1")) {

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }

    // =========================================
// REACTION TIME DATABASE METHODS
// =========================================

    public long saveReactionTimeResult(
            int averageReaction,
            int bestReaction,
            int validAttempts,
            int falseStarts,
            long playedAt) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_AVERAGE_REACTION, averageReaction);
        values.put(COL_BEST_REACTION, bestReaction);
        values.put(COL_VALID_ATTEMPTS, validAttempts);
        values.put(COL_FALSE_STARTS, falseStarts);
        values.put(COL_REACTION_PLAYED_AT, playedAt);

        return db.insert(TABLE_REACTION_RESULTS, null, values);
    }


    public Cursor getAllReactionTimeResults() {

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_REACTION_RESULTS,
                new String[]{
                        COL_REACTION_ID,
                        COL_AVERAGE_REACTION,
                        COL_BEST_REACTION,
                        COL_VALID_ATTEMPTS,
                        COL_FALSE_STARTS,
                        COL_REACTION_PLAYED_AT
                },
                null,
                null,
                null,
                null,
                COL_REACTION_ID + " DESC"
        );
    }


    public int getReactionTimeAttemptCount() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_REACTION_RESULTS,
                null)) {

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }


    public int getReactionTimeBestResult() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT MIN(" + COL_BEST_REACTION + ") FROM " +
                        TABLE_REACTION_RESULTS,
                null)) {

            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }


    public int getLatestReactionTimeAverage() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_REACTION_RESULTS,
                new String[]{COL_AVERAGE_REACTION},
                null,
                null,
                null,
                null,
                COL_REACTION_ID + " DESC",
                "1")) {

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }

    // =========================================
    // USER REGISTRATION
    // =========================================

    public long registerUser(
            String name,
            String email,
            PasswordHasher.HashResult passwordData) {

        if (name == null || email == null || passwordData == null) {
            return -1;
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (name.trim().isEmpty() || normalizedEmail.isEmpty()) {
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_USER_NAME, name.trim());
        values.put(COL_USER_EMAIL, normalizedEmail);

        // Store only the password hash, never the original password.
        values.put(COL_USER_PASSWORD_HASH, passwordData.hash);
        values.put(COL_USER_PASSWORD_SALT, passwordData.salt);
        values.put(COL_USER_PASSWORD_ALGORITHM, passwordData.algorithm);

        values.put(COL_USER_CREATED_AT, System.currentTimeMillis());

        return db.insertWithOnConflict(
                TABLE_USERS,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );
    }

    // =========================================
    // GET USER BY EMAIL
    // =========================================

    public Cursor getUserByEmail(String email) {

        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        SQLiteDatabase db = getReadableDatabase();

        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);

        return db.query(
                TABLE_USERS,
                new String[]{
                        COL_USER_ID,
                        COL_USER_NAME,
                        COL_USER_EMAIL,
                        COL_USER_PASSWORD_HASH,
                        COL_USER_PASSWORD_SALT,
                        COL_USER_PASSWORD_ALGORITHM,
                        COL_USER_CREATED_AT
                },
                COL_USER_EMAIL + " = ?",
                new String[]{normalizedEmail},
                null,
                null,
                null
        );
    }

    // =========================================
    // CLOSE DATABASE
    // =========================================

    @Override
    public synchronized void close() {
        super.close();
    }
}
