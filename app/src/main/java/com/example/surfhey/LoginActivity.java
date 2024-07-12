package com.example.surfhey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import com.example.surfhey.FirestoreConfig;
import com.example.surfhey.FirestoreService;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "FirestoreService";
    private FirestoreService FSdb;
    public static String userID;
    private SurveyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            FirestoreConfig.initialize(this);
            FSdb = new FirestoreService();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Firestore", e);
        }

        EditText usernameEditText = findViewById(R.id.editTextText3);
        EditText userIDEditText = findViewById(R.id.editTextText4);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //FSdb = new FirestoreService();
        dbHelper = new SurveyDatabaseHelper(this);

        userID = dbHelper.getUserId();
        if (userID != null && !userID.isEmpty()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(LoginActivity.this, "Login Credential is not Valid", Toast.LENGTH_SHORT).show();
        }

        TextView signUpButton = findViewById(R.id.goSignUp);
        signUpButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        Button loginButton = findViewById(R.id.button5);
        loginButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString();
            String password = userIDEditText.getText().toString();

            if (!username.isEmpty() && !password.isEmpty()) {
                new CheckLoginTask().execute(username, password);
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

                if ((!text1.isEmpty() && !text2.isEmpty() && !text3.isEmpty()) && text2.equals(text3)) {
                    new CheckUserIDTask().execute(text1, text2);
                }
            });
        });
    }

    private class CheckLoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            try {
                QuerySnapshot querySnapshot = FSdb.isLogCredValid(username, password).get();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    QuerySnapshot querySnapshot1 = FSdb.getIDbyLogCred(username, password).get();
                    if (querySnapshot1 != null && !querySnapshot1.isEmpty()) {
                        return querySnapshot1.getDocuments().get(0).getId();
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                Log.w(TAG, "Error checking login credentials", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String userId) {
            if (userId != null) {
                userID = userId;
                dbHelper.insertLoginCredentials(userID, userID, userID);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CheckUserIDTask extends AsyncTask<String, Void, Boolean> {
        private String userId;
        private String newPassword;

        @Override
        protected Boolean doInBackground(String... params) {
            userId = params[0];
            newPassword = params[1];
            try {
                DocumentSnapshot documentSnapshot = FSdb.isUserIDExist(userId).get();
                return documentSnapshot != null && documentSnapshot.exists();
            } catch (InterruptedException | ExecutionException e) {
                Log.w(TAG, "Error checking user ID", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean userExists) {
            if (userExists) {
                showConfirmPasswordResetDialog(userId, newPassword);
            } else {
                Toast.makeText(LoginActivity.this, "User ID does not exist", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showConfirmPasswordResetDialog(String userId, String newPassword) {
        BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(LoginActivity.this);
        View view11 = LayoutInflater.from(LoginActivity.this).inflate(R.layout.bottom_sheet_pass_confirm, null);
        bottomSheetDialog1.setContentView(view11);
        bottomSheetDialog1.show();

        Button ButtonNo = view11.findViewById(R.id.btn_no_reset_pass);
        ButtonNo.setOnClickListener(v -> bottomSheetDialog1.dismiss());
        Button ButtonYes = view11.findViewById(R.id.btn_yes_reset_pass);
        ButtonYes.setOnClickListener(v -> {
            new ResetPasswordTask().execute(userId, newPassword);
            bottomSheetDialog1.dismiss();
        });
    }

    private class ResetPasswordTask extends AsyncTask<String, Void, Boolean> {
        private String userId;
        private String username;

        @Override
        protected Boolean doInBackground(String... params) {
            userId = params[0];
            String newPassword = params[1];
            try {
                DocumentSnapshot documentSnapshot = FSdb.getUsernamebyUserID(userId).get();
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    username = documentSnapshot.getString("username");
                    FSdb.updatePassword(username, userId, newPassword).get();
                    return true;
                }
            } catch (InterruptedException | ExecutionException e) {
                Log.w(TAG, "Error resetting password", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(LoginActivity.this, "Password Change Successful", Toast.LENGTH_SHORT).show();
                Toast.makeText(LoginActivity.this, userId, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Error updating user password", Toast.LENGTH_SHORT).show();
            }
        }
    }
}