package com.example.surfhey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Firestore";
    private Firestore FSdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameEditText = findViewById(R.id.editTextText3);
        EditText userIDEditText = findViewById(R.id.editTextText4);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        TextView signUpButton = findViewById(R.id.goSignUp);
        signUpButton.setOnClickListener(view -> {
            // Create an intent to start Register Activity
            Intent intent = new Intent(LoginActivity.this
                    , RegisterActivity.class);
            startActivity(intent);
        });

        Button loginButton = findViewById(R.id.button5);
        loginButton.setOnClickListener(view -> {
            FSdb = new Firestore();

            String username = usernameEditText.getText().toString();
            String password = userIDEditText.getText().toString();

            FSdb.isLogCredValid(username, password).addOnCompleteListener
                    (new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> task) {
                            if (task.isSuccessful()) {
                                boolean logCredValid = task.getResult();
                                if (logCredValid) {
                                    FSdb.getIDbyLogCred(username,password).addOnCompleteListener
                                            (new OnCompleteListener<String>() {
                                                @Override
                                                public void onComplete
                                                        (@NonNull Task<String> task)
                                                {
                                                    if (task.isSuccessful()) {
                                                        String userID = task.getResult();
                                                        Intent intent = new Intent
                                                                (LoginActivity.this
                                                                , MainActivity.class);
                                                        startActivity(intent);
                                                    }else {
                                                        Log.w(TAG
                                                                , "Error Retrieving User ID"
                                                                , task.getException());
                                                    }
                                                }
                                            });
                                }
                            }else {
                                Log.w(TAG, "Error checking if LogCred Valid"
                                        , task.getException());
                            }
                        }
                    });
        });
    }
}





























