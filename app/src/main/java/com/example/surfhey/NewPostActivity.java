package com.example.surfhey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class NewPostActivity extends AppCompatActivity {
    Firestore FSdb;
    private String judulSurvey;
    private String date;
    private String detail;
    private String imageurl;
    private String authorname;
    private String likes;
    private String dateAgo;
    private boolean update = false;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        FSdb = new Firestore();

        ImageView postImage = findViewById(R.id.imageView3);
        EditText postDescription = findViewById(R.id.text_area);
        EditText surveyGoal = findViewById(R.id.editTextNumber);
        EditText surveyTimeLimit = findViewById(R.id.loginUsername);
        Button BtnPublish = findViewById(R.id.button5);

        judulSurvey = getIntent().getStringExtra("title");
        date = getIntent().getStringExtra("date");
        detail = getIntent().getStringExtra("detail");
        imageurl = getIntent().getStringExtra("image");
        authorname = getIntent().getStringExtra("authorname");
        likes = getIntent().getStringExtra("likes");
        dateAgo = getIntent().getStringExtra("dateAgo");

        if (!imageurl.isEmpty()) {
            int drawableResourceId = getResources().getIdentifier(imageurl, "drawable", getPackageName());
            postImage.setImageResource(drawableResourceId);
            postDescription.setText(detail);

        }

        ImageView backButton = findViewById(R.id.back_btn_newpost);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPostActivity.this, MainActivity.class);
                startActivity(intent);
                update = true;
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
        BtnCreateSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the CreateSurveyActivity
                Intent intent = new Intent(NewPostActivity.this, CreateSurveyActivity.class);
                startActivity(intent);
            }
        });

        BtnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!update){
                    FSdb.createPost(LoginActivity.userID, "img1", CreateSurveyActivity.postTitle, postDescription.getText().toString());
                    Intent intent = new Intent(NewPostActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else if (update){
                    FSdb.updatePost(LoginActivity.userID, "img1", CreateSurveyActivity.postTitle, postDescription.getText().toString(), date);
                    Intent intent = new Intent(NewPostActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
