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

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "Firestore";
    private Firestore FSdb;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        ImageView imageView = findViewById(R.id.back_btn_editProfile);
        imageView.setOnClickListener(view -> {
            finish();
        });

        TextView userName = findViewById(R.id.loginUsername);
        TextView userID = findViewById(R.id.editTextText4);
        TextView saveButton = findViewById(R.id.button);
        FSdb = new Firestore();
        FSdb.getUsernamebyUserID(LoginActivity.userID).addOnCompleteListener
                (new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            userName.setText(task.getResult());
                            userID.setText(LoginActivity.userID);
                        }else {
                            Log.w(TAG, "Error retrieving username"
                                    , task.getException());
                        }
                    }
                });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FSdb.getUserPasswordbyUserID(LoginActivity.userID).addOnCompleteListener
                        (new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (task.isSuccessful()) {
                                    FSdb.updateUsername(LoginActivity.userID, task.getResult()
                                            , userName.getText().toString());
                                }else {
                                    Log.w(TAG, "Error updating username"
                                            , task.getException());
                                }
                            }
                        });
            }
        });

        TextView tvEditPhoto = findViewById(R.id.tvEditPicture);
        tvEditPhoto.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK
                    , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
        Button BtnBottomBar = findViewById(R.id.btnBottomBar);
        BtnBottomBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog
                        (EditProfileActivity.this);
                View view1 = LayoutInflater.from(EditProfileActivity.this)
                        .inflate(R.layout.bottom_sheet_change, null);
                bottomSheetDialog.setContentView(view1);
                bottomSheetDialog.show();


                TextInputEditText editText = view1.findViewById(R.id.editText);
                MaterialButton dismissBtn = view1.findViewById(R.id.dismiss);

                FSdb.getUserPasswordbyUserID(LoginActivity.userID).addOnCompleteListener
                        (new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (task.isSuccessful()) {
                                    editText.setText(task.getResult());
                                }else {
                                    Log.w(TAG, "Error retrieving user password"
                                            , task.getException());
                                }
                            }
                        });

                dismissBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Objects.requireNonNull(editText.getText()).toString().isEmpty()) {
                            bottomSheetDialog.dismiss();
                        } else {
                            /*Toast.makeText(EditProfileActivity.this
                                    , editText.getText().toString()
                                    , Toast.LENGTH_SHORT).show();*/

                            FSdb.getUsernamebyUserID(LoginActivity.userID)
                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            if (task.isSuccessful()) {
                                                FSdb.updatePassword(task.getResult(),LoginActivity.userID
                                                        ,editText.getText().toString());
                                            }else {
                                                Log.w(TAG, "Error retrieving user password"
                                                        , task.getException());
                                            }
                                        }
                                    });

                            bottomSheetDialog.dismiss();
                        }
                    }
                });

                bottomSheetDialog.setOnDismissListener
                        (new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Toast.makeText(EditProfileActivity.this
                                , "Bottom sheet dismissed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
