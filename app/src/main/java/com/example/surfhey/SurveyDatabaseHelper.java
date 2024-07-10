package com.example.surfhey;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

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

    public void removeLoginCredentials() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", "");
        contentValues.put("userid", "");
        contentValues.put("userpassword", "");

        // Delete any existing rows
        db.delete("account", null, null);

        // Insert the new row
        db.insert("account", null, contentValues);
        db.close();
    }

    public List<Question> getStatementsAndChoices() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT q.id as question_id, q.question, q.survey_id, c.id as choice_id, c.choice " +
                "FROM questions q " +
                "LEFT JOIN choices c ON q.id = c.question_id";
        Cursor cursor = db.rawQuery(query, null);

        List<Question> result = new ArrayList<>();
        if (cursor != null) {
            int questionIdIndex = cursor.getColumnIndex("question_id");
            int questionIndex = cursor.getColumnIndex("question");
            int surveyIdIndex = cursor.getColumnIndex("survey_id");
            int choiceIdIndex = cursor.getColumnIndex("choice_id");
            int choiceIndex = cursor.getColumnIndex("choice");

            while (cursor.moveToNext()) {
                if (questionIdIndex == -1 || questionIndex == -1 || surveyIdIndex == -1 || choiceIdIndex == -1 || choiceIndex == -1) {
                    continue;  // Skip this iteration if any column is missing
                }

                int questionId = cursor.getInt(questionIdIndex);
                String question = cursor.getString(questionIndex);
                String surveyId = cursor.getString(surveyIdIndex);
                int choiceId = cursor.getInt(choiceIdIndex);
                String choice = cursor.getString(choiceIndex);

                Question questionObj = findQuestionById(result, questionId);
                if (questionObj == null) {
                    questionObj = new Question(questionId, question);
                    questionObj.setSurveyID(surveyId); // Set survey ID for the question
                    result.add(questionObj);
                }
                questionObj.addChoice(new Choice(choiceId, choice));
            }
            cursor.close();
        }
        db.close();

        return result;
    }

    private Question findQuestionById(List<Question> questions, int questionId) {
        for (Question question : questions) {
            if (question.getId() == questionId) {
                return question;
            }
        }
        return null;
    }

    public static class Question {
        private int id;
        private String question;
        private String surveyID; // Survey ID associated with the question
        private List<Choice> choices;

        public Question(int id, String question) {
            this.id = id;
            this.question = question;
            this.choices = new ArrayList<>();
        }

        public int getId() {
            return id;
        }

        public String getQuestion() {
            return question;
        }

        public String getSurveyID() {
            return surveyID;
        }

        public void setSurveyID(String surveyID) {
            this.surveyID = surveyID;
        }

        public List<Choice> getChoices() {
            return choices;
        }

        public void addChoice(Choice choice) {
            choices.add(choice);
        }
    }

    public static class Choice {
        private int id;
        private String choice;

        public Choice(int id, String choice) {
            this.id = id;
            this.choice = choice;
        }

        public int getId() {
            return id;
        }

        public String getChoice() {
            return choice;
        }
    }
}