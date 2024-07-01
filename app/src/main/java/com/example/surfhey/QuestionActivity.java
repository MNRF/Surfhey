package com.example.surfhey;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class QuestionActivity extends AppCompatActivity {

    String[] item = {"Multiple Choice", "One Choice"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    LinearLayout choiceContainer;
    Button addChoiceButton;
    int choiceCount = 0;
    int selectedIconResource;
    private SurveyDatabaseHelper dbHelper;
    private long surveyId;
    private long questionId;
    private EditText questionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        dbHelper = new SurveyDatabaseHelper(this);
        surveyId = getIntent().getLongExtra("surveyId", -1);
        questionId = getIntent().getLongExtra("questionId", -1);
        questionEditText = findViewById(R.id.questionEditText);

        // Set up the AutoCompleteTextView
        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_question_choice, item);
        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Reset the choice container
                choiceContainer.removeAllViews();
                choiceCount = 0;

                // Set the selected icon based on the choice type
                if (adapterView.getItemAtPosition(i).toString().equals("Multiple Choice")) {
                    selectedIconResource = R.drawable.multiplechoiceicon;
                } else if (adapterView.getItemAtPosition(i).toString().equals("One Choice")) {
                    selectedIconResource = R.drawable.onechoiceicon;
                }

                // Add initial choices
                addChoice();
                addChoice();
            }
        });

        // Set up the choice container and add button
        choiceContainer = findViewById(R.id.choice_container);
        addChoiceButton = findViewById(R.id.add_choice_button);

        addChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChoice();
            }
        });

        // Set up the back button
        ImageView backButton = findViewById(R.id.back_btn_createSurvey);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionEditText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(QuestionActivity.this, "Data harus diisi", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(QuestionActivity.this, CreateSurveyActivity.class);
                    startActivity(intent);
                    saveQuestionAndChoices();
                }
            }
        });

        // Set up the delete button
        Button deleteButton = findViewById(R.id.delete_question_btn);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteQuestion();
                Intent intent = new Intent(QuestionActivity.this, CreateSurveyActivity.class);
                intent.putExtra("surveyId", surveyId);
                startActivity(intent);
                finish();
            }
        });

        // Load existing question and choices if questionId is passed
        if (questionId != -1) {
            loadQuestionAndChoices();
        }
    }

    private void addChoice() {
        // Create a new LinearLayout for the choice
        LinearLayout choiceLayout = new LinearLayout(this);
        choiceLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Create an ImageView for the checkmark icon
        ImageView checkmarkIcon = new ImageView(this);
        checkmarkIcon.setImageResource(selectedIconResource); // Use the selected icon resource
        // Set padding for the checkmark icon (left, top, right, bottom)
        checkmarkIcon.setPadding(0, 40, 0, 0);

        // Create EditText for the choice
        EditText choiceEditText = new EditText(this);
        choiceEditText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        choiceEditText.setHint("Choice " + (++choiceCount));

        // Remove the underline from the EditText
        choiceEditText.setBackground(null);

        // Create a delete button for the choice
        ImageView deleteButton = new ImageView(this);
        deleteButton.setImageResource(R.drawable.deletechoiceiconx15);

        // Set padding for the delete button (left, top, right, bottom)
        deleteButton.setPadding(0, 35, 0, 0);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceContainer.removeView(choiceLayout);
                choiceCount--;
            }
        });

        // Add the checkmark icon and choice EditText to the choice layout
        choiceLayout.addView(checkmarkIcon);
        choiceLayout.addView(choiceEditText);
        choiceLayout.addView(deleteButton);

        // Add the choice layout to the choice container
        choiceContainer.addView(choiceLayout);
    }

    private void saveQuestionAndChoices() {
        String questionText = questionEditText.getText().toString();
        if (questionText.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues questionValues = new ContentValues();
        questionValues.put("survey_id", surveyId);
        questionValues.put("question", questionText);

        if (questionId == -1) {
            questionId = db.insert("questions", null, questionValues);
        } else {
            db.update("questions", questionValues, "id = ?", new String[]{String.valueOf(questionId)});
            db.delete("choices", "question_id = ?", new String[]{String.valueOf(questionId)});
        }

        // Save choices
        for (int i = 0; i < choiceContainer.getChildCount(); i++) {
            LinearLayout choiceLayout = (LinearLayout) choiceContainer.getChildAt(i);
            EditText choiceEditText = (EditText) choiceLayout.getChildAt(1);
            String choiceText = choiceEditText.getText().toString();

            if (!choiceText.isEmpty()) {
                ContentValues choiceValues = new ContentValues();
                choiceValues.put("question_id", questionId);
                choiceValues.put("choice", choiceText);
                db.insert("choices", null, choiceValues);
            }
        }
    }

    private void loadQuestionAndChoices() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor questionCursor = db.query("questions", null, "id = ?", new String[]{String.valueOf(questionId)}, null, null, null);

        if (questionCursor.moveToFirst()) {
            String questionText = questionCursor.getString(questionCursor.getColumnIndexOrThrow("question"));
            questionEditText.setText(questionText);
        }
        questionCursor.close();

        Cursor choiceCursor = db.query("choices", null, "question_id = ?", new String[]{String.valueOf(questionId)}, null, null, null);
        while (choiceCursor.moveToNext()) {
            String choiceText = choiceCursor.getString(choiceCursor.getColumnIndexOrThrow("choice"));
            addChoice(choiceText);
        }
        choiceCursor.close();
    }

    private void addChoice(String choiceText) {
        // Create a new LinearLayout for the choice
        LinearLayout choiceLayout = new LinearLayout(this);
        choiceLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Create an ImageView for the checkmark icon
        ImageView checkmarkIcon = new ImageView(this);
        checkmarkIcon.setImageResource(selectedIconResource); // Use the selected icon resource
        // Set padding for the checkmark icon (left, top, right, bottom)
        checkmarkIcon.setPadding(0, 40, 0, 0);

        // Create EditText for the choice
        EditText choiceEditText = new EditText(this);
        choiceEditText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        choiceEditText.setText(choiceText);
        choiceEditText.setHint("Choice " + (++choiceCount));

        // Remove the underline from the EditText
        choiceEditText.setBackground(null);

        // Create a delete button for the choice
        ImageView deleteButton = new ImageView(this);
        deleteButton.setImageResource(R.drawable.deletechoiceiconx15);

        // Set padding for the delete button (left, top, right, bottom)
        deleteButton.setPadding(0, 35, 0, 0);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceContainer.removeView(choiceLayout);
                choiceCount--;
            }
        });

        // Add the checkmark icon and choice EditText to the choice layout
        choiceLayout.addView(checkmarkIcon);
        choiceLayout.addView(choiceEditText);
        choiceLayout.addView(deleteButton);

        // Add the choice layout to the choice container
        choiceContainer.addView(choiceLayout);
    }

    private void deleteQuestion() {
        if (questionId != -1) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("choices", "question_id = ?", new String[]{String.valueOf(questionId)});
            db.delete("questions", "id = ?", new String[]{String.valueOf(questionId)});
        }
    }

}
