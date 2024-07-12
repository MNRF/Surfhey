package com.example.surfhey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.cloud.Timestamp;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class DetailActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView questionText, questionTitle, questionNumber;
    private RadioGroup radioGroup;
    private ImageView ivMore;
    private Button nextButton, backButton, buttonEditSurvey, buttonDeleteSurvey;
    private int progressStatus = 0; // initial progress, change as needed
    private int currentQuestion = 0;
    private String judulSurvey, date, detail, imageUrl, authorname, likes, dateAgo, postID;
    private FirestoreService FSdb;
    private TextView tvSurveyGoal;
    private String[] questionTexts;
    private String[][] questionAndChoices;
    private String selectedAnswer;
    private RadioButton selectedRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FSdb = new FirestoreService();
        setContentView(R.layout.activity_detail);

        judulSurvey = getIntent().getStringExtra("title");
        date = getIntent().getStringExtra("date");
        detail = getIntent().getStringExtra("detail");
        imageUrl = getIntent().getStringExtra("image");
        authorname = getIntent().getStringExtra("authorname");
        likes = getIntent().getStringExtra("likes");
        dateAgo = getIntent().getStringExtra("dateAgo");
        postID = getIntent().getStringExtra("postID");

        tvSurveyGoal = findViewById(R.id.textView22);

        Button btnTakeSurvey = findViewById(R.id.btnTakeSurvey);

        try {
            int goal = FSdb.getSurveyGoalbyPostID(postID);
            int currentSample = FSdb.getCurrentSurveyTotalbyPostID(postID);
            String tmp1 = String.valueOf(currentSample);
            String tmp2 = String.valueOf(goal);
            tvSurveyGoal.setText(tmp1 + "/" + tmp2);
            if (currentSample >= goal) {
                btnTakeSurvey.setClickable(false);
                btnTakeSurvey.setText("The goal has been reached!");
                btnTakeSurvey.setBackgroundColor(Color.DKGRAY);
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.w("FirestoreService", "Error fetching survey goal or current sample", e);
        }

        TextView detailTextView = findViewById(R.id.tvDetail);
        detailTextView.setText(detail);

        TextView dateTextView = findViewById(R.id.tvDate);
        try {
            Timestamp dateEndTS = FSdb.getDateEndbyPostID(postID);
            if (dateEndTS != null) {
                String dateEndString = dateEndTS.toDate().toString();
                dateTextView.setText("Until: " + dateEndString);
                if (Timestamp.now().compareTo(dateEndTS) >= 1) {
                    btnTakeSurvey.setClickable(false);
                    btnTakeSurvey.setText("The time has ended!");
                    btnTakeSurvey.setBackgroundColor(Color.DKGRAY);
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.w("FirestoreService", "Error fetching date end", e);
        }

        TextView authorName = findViewById(R.id.textView4);
        authorName.setText(authorname);
        TextView like = findViewById(R.id.textView10);
        like.setText(likes);
        TextView dateago = findViewById(R.id.textView5);
        dateago.setText(dateAgo);

        ImageView posterImageView = findViewById(R.id.ivPosterD);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.img1) // Placeholder image while loading
                    .error(R.drawable.img1) // Error image if the URL fails to load
                    .into(posterImageView);
        } else {
            Log.e("surfListAdapter", "Null or empty image URL");
            posterImageView.setImageResource(R.drawable.img1); // Fallback to local image if URL is null or empty
        }

        ImageView btnBack = findViewById(R.id.back_btn);
        btnBack.setOnClickListener(v -> finish());

        btnTakeSurvey.setOnClickListener(v -> showBottomSheetDialog());

        ivMore = findViewById(R.id.ivMore);
        try {
            DocumentSnapshot documentSnapshot = FSdb.getUsernamebyUserID(LoginActivity.userID).get();
            if (documentSnapshot != null && documentSnapshot.exists() && !Objects.equals(documentSnapshot.getString("username"), authorname)) {
                ivMore.setVisibility(View.GONE);
            } else {
                ivMore.setOnClickListener(v -> showBottomSheetMore());
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        /*FSdb.getUsernamebyUserID(LoginActivity.userID).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists() && !document.getString("username").equals(authorname)) {
                        ivMore.setVisibility(View.GONE);
                    } else {
                        ivMore.setOnClickListener(v -> showBottomSheetMore());
                    }
                } else {
                    Log.w("FirestoreService", task.getException());
                }
            }
        });*/
    }

    private void showBottomSheetMore() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetailActivity.this);
        View view1 = LayoutInflater.from(DetailActivity.this).inflate(R.layout.bottom_sheet_post, null);
        buttonEditSurvey = view1.findViewById(R.id.button4);

        buttonEditSurvey.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(DetailActivity.this, NewPostActivity.class);
            intent.putExtra("authorname", authorname);
            intent.putExtra("title", judulSurvey);
            intent.putExtra("date", date);
            intent.putExtra("dateAgo", dateAgo);
            intent.putExtra("image", imageUrl);
            intent.putExtra("detail", detail);
            intent.putExtra("likes", likes);
            intent.putExtra("postID", postID);
            startActivity(intent);
        });

        buttonDeleteSurvey = view1.findViewById(R.id.button5);
        buttonDeleteSurvey.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();

            BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(DetailActivity.this);
            View view2 = LayoutInflater.from(DetailActivity.this).inflate(R.layout.bottom_sheet_delete, null);
            bottomSheetDialog1.setContentView(view2);
            bottomSheetDialog1.show();
            Button ButtonNo = view2.findViewById(R.id.btn_cancel_delete_post);
            ButtonNo.setOnClickListener(v1 -> bottomSheetDialog1.dismiss());
            Button ButtonYes = view2.findViewById(R.id.btn_yes_delete_post);
            ButtonYes.setOnClickListener(v12 -> {
                try {
                    FSdb.deletePost(postID);
                    Toast.makeText(DetailActivity.this, "Post successfully deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (ExecutionException | InterruptedException e) {
                    Log.w("FSdb Post Deletion", e);
                }
                bottomSheetDialog1.dismiss();
                BottomSheetDialog bottomSheetDialog2 = new BottomSheetDialog(DetailActivity.this);
                View view3 = LayoutInflater.from(DetailActivity.this).inflate(R.layout.bottom_sheet_success, null);
                bottomSheetDialog2.setContentView(view3);
                bottomSheetDialog2.show();
            });
        });
        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();
    }

    public void openWebsite(View view) {
        String url = "https://surfhey.mnrf.site"; // Replace with the desired URL
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetailActivity.this);
        View view1 = LayoutInflater.from(DetailActivity.this).inflate(R.layout.bottom_sheet_question, null);
        bottomSheetDialog.setContentView(view1);

        try {
            questionAndChoices = FSdb.getStatementAndAnswerByPostID(postID);
            questionTexts = new String[questionAndChoices.length];
            for (int i = 0; i < questionAndChoices.length; i++) {
                questionTexts[i] = questionAndChoices[i][0];
            }
            progressBar = view1.findViewById(R.id.progressBar);
            questionNumber = view1.findViewById(R.id.tvQuestionNumber);
            questionText = view1.findViewById(R.id.tvQuestion);
            questionTitle = view1.findViewById(R.id.tvQuestionTitle);
            questionTitle.setText(judulSurvey);
            radioGroup = view1.findViewById(R.id.radioGroup);
            nextButton = view1.findViewById(R.id.btn_next_question);
            backButton = view1.findViewById(R.id.btn_back_question);
            progressBar.setProgress(progressStatus);
            updateQuestion();

            nextButton.setOnClickListener(v -> {
                updateAnswerTotal();
                if (currentQuestion < questionTexts.length - 1) {
                    currentQuestion++;
                    progressStatus += 20;
                    progressBar.setProgress(progressStatus);
                    updateQuestion();
                } else {
                    bottomSheetDialog.dismiss();
                    showBottomSheetThank();
                }
            });

            backButton.setOnClickListener(v -> {
                if (currentQuestion > 0) {
                    currentQuestion--;
                    progressStatus -= 20;
                    progressBar.setProgress(progressStatus);
                    updateQuestion();
                } else {
                    bottomSheetDialog.dismiss();
                }
            });
        } catch (ExecutionException | InterruptedException e) {
            Log.w("FirestoreService", "Error getting documents.", e);
        }

        bottomSheetDialog.show();
    }

    private void showBottomSheetThank() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetailActivity.this);
        View view1 = LayoutInflater.from(DetailActivity.this).inflate(R.layout.bottom_sheet_thank, null);
        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();
    }

    private void updateQuestion() {
        questionNumber.setText("Question " + (currentQuestion + 1));
        questionText.setText(questionTexts[currentQuestion]);

        radioGroup.removeAllViews();
        for (int i = 1; i < questionAndChoices[currentQuestion].length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(questionAndChoices[currentQuestion][i]);
            radioGroup.addView(radioButton);
        }
    }

    private void updateAnswerTotal() {
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = radioGroup.findViewById(selectedRadioButtonId);
            if (selectedRadioButton != null) {
                String selectedAnswer = selectedRadioButton.getText().toString();
                try {
                    FSdb.incrementAnswerTotal(postID, questionTexts[currentQuestion], selectedAnswer);
                } catch (ExecutionException | InterruptedException e) {
                    Log.w("DetailActivity", "Error updating answer total", e);
                }
            }
        } else {
            Log.w("DetailActivity", "No answer selected");
        }
    }
}