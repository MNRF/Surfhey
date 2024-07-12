package com.example.surfhey;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "FirestoreService";
    private FirestoreService FSdb;
    private static final int PICK_IMAGE_REQUEST = 1;
    SurveyDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        FSdb = new FirestoreService();
        db = new SurveyDatabaseHelper(this);

        ImageView imageView = findViewById(R.id.back_btn_editProfile);
        imageView.setOnClickListener(view -> finish());

        TextView userName = findViewById(R.id.editTextText3);
        TextView userID = findViewById(R.id.editTextText4);
        TextView saveButton = findViewById(R.id.button);

        try {
            String username = FSdb.getUsernamebyUserID(LoginActivity.userID).get().getString("username");
            userName.setText(username);
            userID.setText(LoginActivity.userID);
        } catch (ExecutionException | InterruptedException e) {
            Log.w(TAG, "Error retrieving username", e);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String currentPassword = FSdb.getUserPasswordbyUserID(LoginActivity.userID);
                    FSdb.updateUsername(LoginActivity.userID, currentPassword, userName.getText().toString());
                    Toast.makeText(EditProfileActivity.this, "Username successfully updated", Toast.LENGTH_SHORT).show();
                } catch (ExecutionException | InterruptedException e) {
                    Log.w(TAG, "Error updating username", e);
                }
            }
        });

        TextView tvEditPhoto = findViewById(R.id.tvEditPicture);
        tvEditPhoto.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        Button BtnBottomBar = findViewById(R.id.btnBottomBar);
        BtnBottomBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(EditProfileActivity.this);
                View view1 = LayoutInflater.from(EditProfileActivity.this).inflate(R.layout.bottom_sheet_change, null);
                bottomSheetDialog.setContentView(view1);
                bottomSheetDialog.show();

                TextInputEditText editText = view1.findViewById(R.id.editText);
                MaterialButton dismissBtn = view1.findViewById(R.id.dismiss);

                try {
                    String currentPassword = FSdb.getUserPasswordbyUserID(LoginActivity.userID);
                    editText.setText(currentPassword);
                } catch (ExecutionException | InterruptedException e) {
                    Log.w(TAG, "Error retrieving user password", e);
                }

                dismissBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Objects.requireNonNull(editText.getText()).toString().isEmpty()) {
                            bottomSheetDialog.dismiss();
                        } else {
                            try {
                                String username = FSdb.getUsernamebyUserID(LoginActivity.userID).get().getString("username");
                                FSdb.updatePassword(username, LoginActivity.userID, editText.getText().toString());
                            } catch (ExecutionException | InterruptedException e) {
                                Log.w(TAG, "Error updating password", e);
                            }

                            bottomSheetDialog.dismiss();
                        }
                    }
                });

                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Toast.makeText(EditProfileActivity.this, "Bottom sheet dismissed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Button BtnBottomBar2 = findViewById(R.id.btnBottomBar2);
        BtnBottomBar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(EditProfileActivity.this);
                View view1 = LayoutInflater.from(EditProfileActivity.this).inflate(R.layout.bottom_sheet_acc_delete, null);
                bottomSheetDialog.setContentView(view1);
                bottomSheetDialog.show();

                Button ButtonNo = view1.findViewById(R.id.btn_no_delete_acc);
                ButtonNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                Button ButtonYes = view1.findViewById(R.id.btn_yes_delete_acc);
                ButtonYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String username = FSdb.getUsernamebyUserID(LoginActivity.userID).get().getString("username");
                            FSdb.deleteAccount(username, LoginActivity.userID);
                            LoginActivity.userID = "";
                            db.removeLoginCredentials();
                            Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(EditProfileActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (ExecutionException | InterruptedException e) {
                            Log.w(TAG, "Error deleting user account", e);
                        }

                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });
    }
}