package com.example.surfhey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "FirestoreService";
    private FirestoreService FSdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        try {
            FirestoreConfig.initialize(this);
            FSdb = new FirestoreService();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Firestore", e);
        }

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

            String username = usernameEditText.getText().toString();
            String userID = userIDEditText.getText().toString();
            String password = userpasswordEditText.getText().toString();

            new Thread(() -> {
                try {
                    // Check if username exists
                    boolean usernameExists = FSdb.isUsernameExist(username);

                    // Check if userID exists
                    boolean userIDExists = FSdb.isUserIDExist(userID).get().exists();

                    runOnUiThread(() -> {
                        if (!usernameExists) {
                            if (!userIDExists) {
                                FSdb.createAccount(username, userID, password);
                                Toast.makeText(this, "Account created succefully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.w(TAG, "User ID already exists");
                                Toast.makeText(this, "User ID already exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Username already exist", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Username already exists");
                        }
                    });
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Error checking if username or userID exists", e);
                }
            }).start();
        });
    }
}