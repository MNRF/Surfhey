package com.example.surfhey;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

public class CreateSurveyActivity extends AppCompatActivity {

    private SurveyDatabaseHelper dbHelper;
    private long surveyId;
    private LinearLayout questionListLayout;
    public static String postTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_survey);

        dbHelper = new SurveyDatabaseHelper(this);
        questionListLayout = findViewById(R.id.question_list_layout);
        Button addQuestionButton = findViewById(R.id.add_choice_button);

        // Load surveyId from intent
        surveyId = getIntent().getLongExtra("surveyId", -1);

        // Load existing questions
        loadQuestions();

        addQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuestion();
            }
        });

        EditText postTitleField = findViewById(R.id.editText3);
        ImageView backButton = findViewById(R.id.back_btn_createSurvey);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postTitle = postTitleField.getText().toString();
                Intent intent = new Intent(CreateSurveyActivity.this, NewPostActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadQuestions() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("questions", null, "survey_id = ?", new String[]{String.valueOf(surveyId)}, null, null, null);

        while (cursor.moveToNext()) {
            long questionId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String questionText = cursor.getString(cursor.getColumnIndexOrThrow("question"));
            addQuestionCard(questionId, questionText);
        }

        cursor.close();
    }

    private void addQuestion() {
        saveSurvey();

        // Insert a new question into the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("survey_id", surveyId);
        values.put("question", "");

        long questionId = db.insert("questions", null, values);
        addQuestionCard(questionId, "New Question");
    }

    private void addQuestionCard(long questionId, String questionText) {
        CardView questionCardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(40, 0, 40, 14);
        questionCardView.setLayoutParams(cardParams);
        questionCardView.setRadius(8);
        questionCardView.setCardElevation(1);
        questionCardView.setBackgroundResource(R.drawable.bg_white);

        TextView questionTitle = new TextView(this);
        questionTitle.setPadding(33, 13, 33, 13);
        questionTitle.setText(questionText);
        questionTitle.setTextSize(18);

        questionCardView.addView(questionTitle);

        questionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateSurveyActivity.this, QuestionActivity.class);
                intent.putExtra("surveyId", surveyId);
                intent.putExtra("questionId", questionId); // Pass questionId
                startActivity(intent);
            }
        });

        questionListLayout.addView(questionCardView);
    }

    private void saveSurvey() {
        // Save survey to database
        // ...
    }
}
