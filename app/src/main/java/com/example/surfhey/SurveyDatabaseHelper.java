package com.example.surfhey;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SurveyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "survey.db";
    private static final int DATABASE_VERSION = 1;

    public SurveyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSurveysTable = "CREATE TABLE surveys (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL)";
        db.execSQL(createSurveysTable);

        String createQuestionsTable = "CREATE TABLE questions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "survey_id INTEGER NOT NULL, " +
                "question TEXT NOT NULL)";
        db.execSQL(createQuestionsTable);

        String createChoicesTable = "CREATE TABLE choices (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "question_id INTEGER NOT NULL, " +
                "choice TEXT NOT NULL)";
        db.execSQL(createChoicesTable);

        String createCurrentUser = "CREATE TABLE account (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL," +
                "userid TEXT NOT NULL," +
                "userpassword TEXT NOT NULL)";
        db.execSQL(createCurrentUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS surveys");
        db.execSQL("DROP TABLE IF EXISTS questions");
        db.execSQL("DROP TABLE IF EXISTS choices");
        db.execSQL("DROP TABLE IF EXISTS account");
        onCreate(db);
    }

    public void insertLoginCredentials(String username, String userId, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("userid", userId);
        contentValues.put("userpassword", password);

        // Delete any existing rows
        db.delete("account", null, null);

        // Insert the new row
        db.insert("account", null, contentValues);
        db.close();
    }

    public String getUserId() {
        SQLiteDatabase db = this.getReadableDatabase();
        String userId = null;
        Cursor cursor = db.rawQuery("SELECT userid FROM account LIMIT 1", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("userid");
                if (columnIndex != -1) {
                    userId = cursor.getString(columnIndex);
                }
            }
            cursor.close();
        }
        db.close();
        return userId;
    }
}