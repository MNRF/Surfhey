package com.example.surfhey;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        ImageView imageView = findViewById(R.id.back_btn_editProfile);
        imageView.setOnClickListener(view -> {
            finish();
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

                dismissBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Objects.requireNonNull(editText.getText()).toString().isEmpty()) {

                        } else {
                            Toast.makeText(EditProfileActivity.this, editText.getText().toString(), Toast.LENGTH_SHORT).show();

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
    }
}
