package com.example.surfhey;

import android.content.Context;
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS surveys");
        db.execSQL("DROP TABLE IF EXISTS questions");
        db.execSQL("DROP TABLE IF EXISTS choices");
        onCreate(db);
    }

}
