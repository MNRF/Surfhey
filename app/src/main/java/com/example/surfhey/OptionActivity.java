package com.example.surfhey;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.AppCompatButton;

public class OptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        // Find the ImageView and set up the click listener for back button
        ImageView backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the activity to go back to the previous activity
                finish();
            }
        });

        // Find the AppCompatButton and set up the click listener for Edit Profile button
        AppCompatButton editProfileButton = findViewById(R.id.appCompatButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the EditProfileActivity
                Intent intent = new Intent(OptionActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        // Find the AppCompatButton and set up the click listener for Edit Profile button
        AppCompatButton logOutButton = findViewById(R.id.appCompatButton4);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the EditProfileActivity
                Intent intent = new Intent(OptionActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
