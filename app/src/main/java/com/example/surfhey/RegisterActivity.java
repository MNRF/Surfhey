package com.example.surfhey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {
    private Firestore FSdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView signUpButton = findViewById(R.id.goSignUp);
        EditText emailEditText = findViewById(R.id.editTextText3);
        EditText surferIDEditText = findViewById(R.id.editTextText4);
        EditText passwordEditText = findViewById(R.id.showEditTxt);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        AppCompatButton loginButton = findViewById(R.id.button5);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FSdb = new Firestore();
                FSdb.readAccount(emailEditText.getText().toString(), passwordEditText.getText().toString());
                FSdb.createAccount(emailEditText.getText().toString(),surferIDEditText.getText().toString(),passwordEditText.getText().toString());
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}