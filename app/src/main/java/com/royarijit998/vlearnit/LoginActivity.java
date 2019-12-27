package com.royarijit998.vlearnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText emailLoginEditText, passLoginEditText;
    private Button loginBtn;
    private TextView noAccountTextView;
    private ProgressDialog progressDialog;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLoginEditText = findViewById(R.id.emailLoginEditText);
        passLoginEditText = findViewById(R.id.passLoginEditText);
        loginBtn = findViewById(R.id.loginBtn);
        noAccountTextView = findViewById(R.id.noAccountTextView);
        progressDialog = new ProgressDialog(this);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        noAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegisterActivity();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            checkUserExistence();
        }
    }

    private void checkUserExistence() {
        final String currentUserId = FirebaseAuth.getInstance().getUid();
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(currentUserId)){
                    sendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToSetupActivity() {
        Intent intent = new Intent(LoginActivity.this, SetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loginUser() {
        String emailLogin = emailLoginEditText.getText().toString();
        String passLogin = passLoginEditText.getText().toString();
        if(emailLogin.isEmpty() || passLogin.isEmpty()){
            Toast.makeText(LoginActivity.this, "All fields must be provided", Toast.LENGTH_SHORT).show();
        }
        else{
            progressDialog.setTitle("Logging you in...");
            progressDialog.setMessage("Please wait while we log you in..");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailLogin, passLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        sendUserToHomeActivity();
                        Toast.makeText(LoginActivity.this, "Successful Login", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String errorMsg = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Couldn't login user: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void sendUserToHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
