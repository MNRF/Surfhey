package com.example.surfhey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.net.ParseException;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.surfhey.adapter.surfListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;

import java.sql.Time;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView questionText, questionTitle, questionNumber;
    private RadioGroup radioGroup;
    private ImageView ivMore;
    private Button nextButton, backButton, buttonEditSurvey, buttonDeleteSurvey;
    private int progressStatus = 20; // initial progress, change as needed
    private int currentQuestion = 0;
    private String judulSurvey, date, detail, imageUrl, authorname, likes, dateAgo, postID;
    private Firestore FSdb;
    private TextView tvSurveyGoal;

    private String[] questionTitles = {
            "Mengenal Cara Kamu Manage Waktu!",
            "Apakah kamu sering kesusahan dalam mengerjakan kegiatan antara satu dengan yang lainnya?",
            "Bagaimana kamu mengatur prioritas tugas- tugas harian?",
            "Apakah kamu merasa waktu yang kamu miliki cukup untuk menyelesaikan semua tugas?",
            "Seberapa sering kamu membuat jadwal atau to-do list?"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FSdb = new Firestore();
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

        FSdb.getSurveyGoalbyPostID(postID).addOnCompleteListener(new OnCompleteListener<Integer>() {
            @Override
            public void onComplete(@NonNull Task<Integer> task) {
                if (task.isSuccessful()) {
                    int goal = task.getResult();

                    // Fetch current sample
                    FSdb.getCurrentSurveyTotalbyPostID(postID).addOnCompleteListener(new OnCompleteListener<Integer>() {
                        @Override
                        public void onComplete(@NonNull Task<Integer> task) {
                            if (task.isSuccessful()) {
                                int currentSample = task.getResult();
                                // Update UI
                                tvSurveyGoal.setText(currentSample + "/" + goal);
                            } else {
                                Log.w("Firestore", "Error fetching current sample: ", task.getException());
                            }
                        }
                    });

                } else {
                    Log.w("Firestore", "Error fetching goal: ", task.getException());
                }
            }
        });

        TextView detailTextView = findViewById(R.id.tvDetail);
        detailTextView.setText(detail);

        TextView dateTextView = findViewById(R.id.tvDate);
        FSdb.getDateEndbyPostID(postID).addOnCompleteListener(new OnCompleteListener<Timestamp>() {
            @Override
            public void onComplete(@NonNull Task<Timestamp> task) {
                if (task.isSuccessful()) {
                    Timestamp dateEndTS = task.getResult();
                    if (dateEndTS != null) {
                        String dateEndString = dateEndTS.toDate().toString();
                        dateTextView.setText("Until: " + dateEndString);
                    } else {
                        Log.w("Firestore", "dateEndTS is null");
                    }
                } else {
                    Log.w("Firestore", "Error fetching dateEnd: ", task.getException());
                }
            }
        });

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
        FSdb.getUsernamebyUserID(LoginActivity.userID).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().equals(authorname)) {
                        ivMore.setVisibility(View.GONE);
                    } else {
                        ivMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showBottomSheetMore();
                            }
                        });
                    }
                } else {
                    Log.w("Firestore", task.getException());
                }
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
                intent.putExtra("authorname", authorname);
                intent.putExtra("title", judulSurvey);
                intent.putExtra("date", date);
                intent.putExtra("dateAgo", dateAgo);
                intent.putExtra("image", imageUrl);
                intent.putExtra("detail", detail);
                intent.putExtra("likes", likes);
                intent.putExtra("postID", postID);
                startActivity(intent);
            }
        });

        buttonDeleteSurvey = view1.findViewById(R.id.button5);
        buttonDeleteSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

                BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(DetailActivity.this);
                View view2 = LayoutInflater.from(DetailActivity.this).inflate(R.layout.bottom_sheet_delete, null);
                bottomSheetDialog1.setContentView(view2);
                bottomSheetDialog1.show();
                Button ButtonNo = view2.findViewById(R.id.btn_cancel_delete_post);
                ButtonNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog1.dismiss();
                    }
                });
                Button ButtonYes = view2.findViewById(R.id.btn_yes_delete_post);
                ButtonYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FSdb.deletePost(postID).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(DetailActivity.this, "Post successfully deleted", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.w("FSdb Post Deletion", task.getException());
                                }
                            }
                        });
                        bottomSheetDialog1.dismiss();
                        BottomSheetDialog bottomSheetDialog2 = new BottomSheetDialog(DetailActivity.this);
                        View view3 = LayoutInflater.from(DetailActivity.this).inflate(R.layout.bottom_sheet_success, null);
                        bottomSheetDialog2.setContentView(view3);
                        bottomSheetDialog2.show();
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
        questionTitle = view1.findViewById(R.id.tvQuestionTitle);
        questionTitle.setText(judulSurvey);
        progressBar = view1.findViewById(R.id.progressBar);
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
                } else {
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
                } else {
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
        questionNumber.setText("Question" + (currentQuestion + 1));
        questionText.setText(questionTitles[currentQuestion]);
    }
}