package com.example.surfhey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "Firestore";
    private Firestore FSdb;

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
                Intent intent = new Intent(RegisterActivity.this,
                        LoginActivity.class);
                startActivity(intent);
            }
        });

        AppCompatButton signupButton = findViewById(R.id.button5);
        signupButton.setOnClickListener(view -> {
            FSdb = new Firestore();

            String username = usernameEditText.getText().toString();
            String userID = userIDEditText.getText().toString();
            String password = userpasswordEditText.getText().toString();

            FSdb.isUsernameExist(username).addOnCompleteListener
                    (new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {
                    if (task.isSuccessful()) {
                        boolean usernameExists = task.getResult();
                        if (!usernameExists) {
                            FSdb.isUserIDExist(userID).addOnCompleteListener
                                    (new OnCompleteListener<Boolean>() {
                                @Override
                                public void onComplete(@NonNull Task<Boolean> task) {
                                    if (task.isSuccessful()) {
                                        boolean userIDExists = task.getResult();
                                        if (!userIDExists) {
                                            FSdb.createAccount(username, userID,
                                                    password);
                                            Intent intent = new Intent
                                                    (RegisterActivity
                                                            .this, MainActivity
                                                            .class);
                                            startActivity(intent);
                                        }
                                    } else {
                                        Log.w(TAG, "Error checking if userID exists"
                                                , task.getException());
                                    }
                                }
                            });
                        }
                    } else {
                        Log.w(TAG, "Error checking if username exists"
                                , task.getException());
                    }
                }
            });
        });
    }
}