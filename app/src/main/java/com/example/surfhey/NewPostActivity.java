// File: NewPostActivity.java
package com.example.surfhey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class NewPostActivity extends AppCompatActivity {
    FirestoreService FSdb;
    SurveyDatabaseHelper SQdb;
    CloudStorage cloudStorage;
    private String judulSurvey;
    private String date;
    private String detail;
    private String imageurl;
    private String authorname;
    private String likes;
    private String dateAgo;
    private String postID;
    private Calendar calendar;
    private EditText surveyTimeLimit;
    public static String deskripsi;
    public static String goal;
    public static String time;
    public static boolean update;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView postImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        FSdb = new FirestoreService();
        SQdb = new SurveyDatabaseHelper(this);
        cloudStorage = new CloudStorage(this);

        postImage = findViewById(R.id.imageView3);
        EditText postDescription = findViewById(R.id.text_area);
        EditText surveyGoal = findViewById(R.id.editTextNumber);
        surveyTimeLimit = findViewById(R.id.loginUsername);
        calendar = Calendar.getInstance();
        surveyTimeLimit.setOnClickListener(v -> showDateTimePicker());
        Button BtnPublish = findViewById(R.id.button5);

        judulSurvey = getIntent().getStringExtra("title");
        date = getIntent().getStringExtra("date");
        detail = getIntent().getStringExtra("detail");
        imageurl = getIntent().getStringExtra("image");
        authorname = getIntent().getStringExtra("authorname");
        likes = getIntent().getStringExtra("likes");
        dateAgo = getIntent().getStringExtra("dateAgo");
        postID = getIntent().getStringExtra("postID");

        try {
            if (!imageurl.isEmpty()) {
                int drawableResourceId = getResources().getIdentifier(imageurl, "drawable", getPackageName());
                postImage.setImageResource(drawableResourceId);
                postDescription.setText(detail);
                update = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!judulSurvey.isEmpty()) {
                CreateSurveyActivity.title = judulSurvey;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (!time.isEmpty()) {
                surveyTimeLimit.setText(time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!deskripsi.isEmpty()) {
                postDescription.setText(deskripsi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!goal.isEmpty()) {
                surveyGoal.setText(goal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageView backButton = findViewById(R.id.back_btn_newpost);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateSurveyActivity.title = "";
                goal = "";
                deskripsi = "";
                time = "";
                Intent intent = new Intent(NewPostActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        ConstraintLayout addNewPost = findViewById(R.id.constraintNewPost);
        addNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        // Find the AppCompatButton and set up the click listener for Create Survey button
        Button BtnCreateSurvey = findViewById(R.id.btnCreateSurvey);
        try {
            if (!CreateSurveyActivity.title.isEmpty()) {
                BtnCreateSurvey.setText(CreateSurveyActivity.title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        BtnCreateSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    time = surveyTimeLimit.getText().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    deskripsi = postDescription.getText().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    goal = surveyGoal.getText().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(NewPostActivity.this, CreateSurveyActivity.class);
                startActivity(intent);
            }
        });

        BtnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    cloudStorage.uploadImage(imageUri).addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (task.isSuccessful()) {
                                String imageUrl = task.getResult();
                                publishPost(imageUrl);
                            } else {
                                Log.w("CloudStorage", "Image upload failed", task.getException());
                            }
                        }
                    });
                } else {
                    publishPost(null);
                }
            }
        });
    }

    private void publishPost(String imageUrl) {
        Timestamp timestamp = new Timestamp(calendar.getTime());
        EditText postDescription = findViewById(R.id.text_area);
        EditText surveyGoal = findViewById(R.id.editTextNumber);

        if (update) {
            FSdb.updatePost(imageUrl, CreateSurveyActivity.title, postDescription.getText().toString(), postID);

            List<SurveyDatabaseHelper.Question> statementAndChoices = SQdb.getStatementsAndChoices();

            for (SurveyDatabaseHelper.Question question : statementAndChoices) {
                List<String> choices = new ArrayList<>();
                for (SurveyDatabaseHelper.Choice choice : question.getChoices()) {
                    choices.add(choice.getChoice());
                }

                String surveyID = question.getSurveyID();
                if (surveyID != null && !surveyID.isEmpty()) {
                    FSdb.updateSurvey(LoginActivity.userID, timestamp,
                            Long.parseLong(surveyGoal.getText().toString()), surveyID, question.getQuestion(), choices);
                } else {
                    FSdb.createSurvey(LoginActivity.userID, timestamp,
                            Long.parseLong(surveyGoal.getText().toString()), postID, question.getQuestion(), choices);
                }
            }

            Intent intent = new Intent(NewPostActivity.this, MainActivity.class);
            startActivity(intent);
            goal = "";
            deskripsi = "";
            time = "";
            imageurl = "";
            CreateSurveyActivity.title = "";
            update = false;
            finish();
        } else if (!update) {
            FSdb.createPost(LoginActivity.userID, imageUrl, CreateSurveyActivity.title, postDescription.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (task.isSuccessful()) {
                                postID = task.getResult();

                                List<SurveyDatabaseHelper.Question> statementAndChoices = SQdb.getStatementsAndChoices();

                                for (SurveyDatabaseHelper.Question question : statementAndChoices) {
                                    List<String> choices = new ArrayList<>();
                                    for (SurveyDatabaseHelper.Choice choice : question.getChoices()) {
                                        choices.add(choice.getChoice());
                                    }

                                    FSdb.createSurvey(LoginActivity.userID, timestamp,
                                            Long.parseLong(surveyGoal.getText().toString()), postID, question.getQuestion(), choices);
                                }

                                Intent intent = new Intent(NewPostActivity.this, MainActivity.class);
                                startActivity(intent);
                                CreateSurveyActivity.title = "";
                                goal = "";
                                deskripsi = "";
                                time = "";
                                update = false;
                                finish();
                            } else {
                                Log.w("FirestoreService", "Error creating post", task.getException());
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            postImage.setImageURI(imageUri); // Display the selected image in the ImageView
        }
    }

    private void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        calendar = Calendar.getInstance();
        new DatePickerDialog(NewPostActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(year, monthOfYear, dayOfMonth);
            new TimePickerDialog(NewPostActivity.this, (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                surveyTimeLimit.setText(formatDateTime(calendar));
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private String formatDateTime(Calendar calendar) {
        return android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", calendar.getTime()).toString();
    }
}