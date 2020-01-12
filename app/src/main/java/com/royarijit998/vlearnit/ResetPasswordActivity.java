package com.royarijit998.vlearnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private Toolbar resetPasswordToolbar;
    private EditText recoverEmailEditText;
    private Button recoverAccountBtn;

    private FirebaseAuth mAuth;

    private String recoverEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetPasswordToolbar = findViewById(R.id.resetPasswordToolbar);
        setSupportActionBar(resetPasswordToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset Password Activity");

        recoverEmailEditText = findViewById(R.id.recoverEmailEditText);
        recoverAccountBtn = findViewById(R.id.recoverAccountBtn);

        mAuth = FirebaseAuth.getInstance();

        recoverAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUserInput();
            }
        });

    }

    private void validateUserInput() {
        recoverEmail = recoverEmailEditText.getText().toString();
        if(recoverEmail.isEmpty()){
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        }else{
            sendPasswordResetLink();
        }
    }

    private void sendPasswordResetLink() {
        mAuth.sendPasswordResetEmail(recoverEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ResetPasswordActivity.this, "Password reset link has been sent. Please check your mail.", Toast.LENGTH_SHORT).show();
                    sendUserToLoginActivity();
                }else{
                    String errorMsg = task.getException().getMessage();
                    Toast.makeText(ResetPasswordActivity.this, "Failed to send recovery email: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
