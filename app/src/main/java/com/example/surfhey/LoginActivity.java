package com.example.surfhey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Firestore";
    private Firestore FSdb;
    public static String userID;
    private SurveyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameEditText = findViewById(R.id.editTextText3);
        EditText userIDEditText = findViewById(R.id.editTextText4);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        dbHelper = new SurveyDatabaseHelper(this);

        // Check if a session exists
        userID = dbHelper.getUserId();
        if (!Objects.equals(userID, "")) {
            // If a session exists, navigate to MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish LoginActivity
        }

        TextView signUpButton = findViewById(R.id.goSignUp);
        signUpButton.setOnClickListener(view -> {
            // Create an intent to start Register Activity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        Button loginButton = findViewById(R.id.button5);
        loginButton.setOnClickListener(view -> {
            FSdb = new Firestore();

            String username = usernameEditText.getText().toString();
            String password = userIDEditText.getText().toString();

            if (!username.isEmpty() && !password.isEmpty()) {
                FSdb.isLogCredValid(username, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean logCredValid = task.getResult();
                        if (logCredValid) {
                            FSdb.getIDbyLogCred(username, password).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    userID = task1.getResult();
                                    dbHelper.insertLoginCredentials(username, userID, password);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish(); // Finish LoginActivity
                                } else {
                                    Log.w(TAG, "Error Retrieving User ID", task1.getException());
                                }
                            });
                        }
                    } else {
                        Log.w(TAG, "Error checking if LogCred Valid", task.getException());
                    }
                });
            }
        });

        TextView forgotPassword = findViewById(R.id.textView9);
        forgotPassword.setOnClickListener(view -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(LoginActivity.this);
            View view1 = LayoutInflater.from(LoginActivity.this).inflate(R.layout.bottom_sheet_reset_pass, null);
            bottomSheetDialog.setContentView(view1);
            bottomSheetDialog.show();

            TextInputEditText editText1 = view1.findViewById(R.id.editText1);
            TextInputEditText editText2 = view1.findViewById(R.id.editText2);
            TextInputEditText editText3 = view1.findViewById(R.id.editText3);
            MaterialButton changePass = view1.findViewById(R.id.changePass);


            changePass.setOnClickListener(view2 -> {
                String text1 = Objects.requireNonNull(editText1.getText()).toString();
                String text2 = Objects.requireNonNull(editText2.getText()).toString();
                String text3 = Objects.requireNonNull(editText3.getText()).toString();

                if (text1.isEmpty() && text2.isEmpty() && text3.isEmpty()) {

                } else if (!text2.equals(text3)) {

                }else {
                    FSdb.isUserIDExist(text1).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult()) {
                                bottomSheetDialog.dismiss();

                                BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(LoginActivity.this);
                                View view11 = LayoutInflater.from(LoginActivity.this).inflate(R.layout.bottom_sheet_pass_confirm, null);
                                bottomSheetDialog1.setContentView(view11);
                                bottomSheetDialog1.show();

                                Button ButtonNo = view11.findViewById(R.id.btn_no_reset_pass);
                                ButtonNo.setOnClickListener(v -> bottomSheetDialog1.dismiss());

                                Button ButtonYes = view11.findViewById(R.id.btn_yes_reset_pass);
                                ButtonYes.setOnClickListener(v -> {
                                    FSdb.updatePassword()
                                    bottomSheetDialog1.setOnDismissListener(dialogInterface -> {
                                        Toast.makeText(LoginActivity.this, "Password Change Successfull", Toast.LENGTH_SHORT).show();
                                        // Toast.makeText(LoginActivity.this, text1, Toast.LENGTH_SHORT).show();
                                    });
                                    bottomSheetDialog1.dismiss();
                                });
                            } else {

                            }
                        } else {
                            Log.w(TAG, "Error checking if User ID Exist", task.getException());
                        }
                    });
                }
            });
        });
    }
}