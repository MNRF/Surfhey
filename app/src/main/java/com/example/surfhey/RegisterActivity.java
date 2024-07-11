package com.example.surfhey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "FirestoreService";
    private FirestoreService FSdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView loginButton = findViewById(R.id.goSignUp);
        EditText usernameEditText = findViewById(R.id.editTextText3);
        EditText userIDEditText = findViewById(R.id.editTextText4);
        EditText userpasswordEditText = findViewById(R.id.showEditTxt);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        AppCompatButton signupButton = findViewById(R.id.button5);
        signupButton.setOnClickListener(view -> {
            FSdb = new FirestoreService();

            String username = usernameEditText.getText().toString();
            String userID = userIDEditText.getText().toString();
            String password = userpasswordEditText.getText().toString();

            try {
                boolean usernameExists = FSdb.isUsernameExist(username);
                if (!usernameExists) {
                    boolean userIDExists = FSdb.isUserIDExist(userID);
                    if (!userIDExists) {
                        FSdb.createAccount(username, userID, password);
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Log.w(TAG, "User ID already exists");
                    }
                } else {
                    Log.w(TAG, "Username already exists");
                }
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error checking if username or userID exists", e);
            }
        });
    }
}