package com.royarijit998.vlearnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailRegEditText, passRegEditText, confirmPassRegEditText;
    private ProgressDialog progressDialog;
    private Button regBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailRegEditText = findViewById(R.id.emailRegEditText);
        passRegEditText = findViewById(R.id.passRegEditText);
        confirmPassRegEditText = findViewById(R.id.confirmPassRegEditText);
        progressDialog = new ProgressDialog(this);
        regBtn = findViewById(R.id.regBtn);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser(){
        String emailReg = emailRegEditText.getText().toString();
        String passReg = passRegEditText.getText().toString();
        String confirmPassReg = confirmPassRegEditText.getText().toString();

        if (emailReg.isEmpty() || passReg.isEmpty() || confirmPassReg.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "All fields must be provided", Toast.LENGTH_SHORT).show();
        } else {
            if(passReg.equals(confirmPassReg)){
                progressDialog.setTitle("Creating New Account");
                progressDialog.setMessage("Please wait while we register you..");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(true);
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailReg, passReg).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            sendUserToSetupActivity();
                        }
                        else{
                            String errorMsg = task.getException().getMessage();
                            Toast.makeText(RegisterActivity.this, "Couldn't register user: " + errorMsg, Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
            }
            else{
                Toast.makeText(RegisterActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendUserToSetupActivity(){
        Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
