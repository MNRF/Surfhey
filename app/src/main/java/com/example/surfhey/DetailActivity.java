package com.example.surfhey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DetailActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView questionText,questionTitle,questionNumber;
    private RadioGroup radioGroup;
    private ImageView ivMore;
    private Button nextButton;
    private Button backButton;
    private Button buttonEditSurvey,buttonDeleteSurvey;
    private int progressStatus = 20; // initial progress, change as needed
    private int currentQuestion = 0;
    private String judulSurvey;

    private String[] questionTitles = {
            "Mengenal Cara Kamu Manage Waktu!",
            "Apakah kamu sering kesusahan dalam mengerjakan kegiatan antara satu dengan yang lainnya?",
            "Bagaimana kamu mengatur prioritas tugas-tugas harian?",
            "Apakah kamu merasa waktu yang kamu miliki cukup untuk menyelesaikan semua tugas?",
            "Seberapa sering kamu membuat jadwal atau to-do list?"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        judulSurvey = getIntent().getStringExtra("title");
        String date = getIntent().getStringExtra("date");
        String detail = getIntent().getStringExtra("detail");
        String imageurl = getIntent().getStringExtra("image");
        String authorname = getIntent().getStringExtra("authorname");
        String likes = getIntent().getStringExtra("likes");
        String dateAgo = getIntent().getStringExtra("dateAgo");

        TextView detailTextView = findViewById(R.id.tvDetail);
        detailTextView.setText(detail);
        TextView dateTextView = findViewById(R.id.tvDate);
        dateTextView.setText(date);
        TextView authorName = findViewById(R.id.textView4);
        authorName.setText(authorname);
        TextView like = findViewById(R.id.textView10);
        like.setText(likes);
        TextView dateago = findViewById(R.id.textView5);
        dateago.setText(dateAgo);


        ImageView posterImageView = findViewById(R.id.ivPosterD);
        int drawableResourceId = getResources().getIdentifier(imageurl, "drawable", getPackageName());
        posterImageView.setImageResource(drawableResourceId);

        ImageView btnBack = findViewById(R.id.back_btn);
        btnBack.setOnClickListener(v -> {
            finish();
        });

        Button btnTakeSurvey = findViewById(R.id.btnTakeSurvey);
        btnTakeSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        ivMore = findViewById(R.id.ivMore);
        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetMore();
            }
        });
    }

    private void showBottomSheetMore() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetailActivity.this);
        View view1 = LayoutInflater.from(DetailActivity.this).inflate(R.layout.bottom_sheet_post, null);
        buttonEditSurvey = view1.findViewById(R.id.button4);

        buttonEditSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(DetailActivity.this, NewPostActivity.class);
                startActivity(intent);
            }
        });

        buttonDeleteSurvey = view1.findViewById(R.id.button5);
        buttonDeleteSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetailActivity.this);
                View view1 = LayoutInflater.from(DetailActivity.this).inflate(R.layout.bottom_sheet_delete, null);
                bottomSheetDialog.setContentView(view1);
                bottomSheetDialog.show();
                Button ButtonNo = view1.findViewById(R.id.btn_cancel_delete_post);
                ButtonNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
                Button ButtonYes = view1.findViewById(R.id.btn_yes_delete_post);
                ButtonYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(DetailActivity.this);
                        View view1 = LayoutInflater.from(DetailActivity.this).inflate(R.layout.bottom_sheet_success, null);
                        bottomSheetDialog1.setContentView(view1);
                        bottomSheetDialog1.show();
                    }
                });
            }
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
        questionTitle= view1.findViewById(R.id.tvQuestionTitle);
        questionTitle.setText(judulSurvey);
        progressBar =view1.findViewById(R.id.progressBar);
        questionNumber = view1.findViewById(R.id.tvQuestionNumber);
        questionText = view1.findViewById(R.id.tvQuestion);
        radioGroup = view1.findViewById(R.id.radioGroup);
        nextButton = view1.findViewById(R.id.btn_next_question);
        backButton = view1.findViewById(R.id.btn_back_question);
        progressBar.setProgress(progressStatus);
        updateQuestion();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestion < questionTitles.length - 1) {
                    currentQuestion++;
                    progressStatus += 20;

                    progressBar.setProgress(progressStatus);
                    updateQuestion();
                }else{
                    bottomSheetDialog.dismiss();
                    showBottomSheetThank();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestion > 0) {
                    currentQuestion--;
                    progressStatus -= 20;
                    progressBar.setProgress(progressStatus);
                    updateQuestion();
                }else {
                    bottomSheetDialog.dismiss();
                }
            }
        });
        bottomSheetDialog.show();
    }

    private void showBottomSheetThank() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetailActivity.this);
        View view1 = LayoutInflater.from(DetailActivity.this).inflate(R.layout.bottom_sheet_thank, null);
        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();
    }

    private void updateQuestion() {
        questionNumber.setText("Question"+(currentQuestion+1));
        questionText.setText(questionTitles[currentQuestion]);
    }


}