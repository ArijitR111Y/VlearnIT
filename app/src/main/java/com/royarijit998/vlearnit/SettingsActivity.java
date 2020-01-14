package com.royarijit998.vlearnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private Toolbar navSettingsToolbar;
    private CircleImageView settingsProfileImageView;
    private EditText settingsStatusEditText, settingsUsernameEditText, settingsFullnameEditText, settingsCountryEditText, settingsDobEditText, settingsGenderEditText, settingsLevelEditText;
    private Button settingsUpdateBtn;

    private DatabaseReference userRef;
    private FirebaseAuth fAuth;
    private FirebaseUser firebaseUser;
    private StorageReference usersProfileImgRef;

    final static int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        navSettingsToolbar = findViewById(R.id.navSettingsToolbar);
        setSupportActionBar(navSettingsToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        settingsProfileImageView = findViewById(R.id.settingsProfileImageView);
        settingsStatusEditText = findViewById(R.id.settingsStatusEditText);
        settingsUsernameEditText = findViewById(R.id.settingsUsernameEditText);
        settingsFullnameEditText = findViewById(R.id.settingsFullnameEditText);
        settingsCountryEditText = findViewById(R.id.settingsCountryEditText);
        settingsDobEditText = findViewById(R.id.settingsDobEditText);
        settingsGenderEditText = findViewById(R.id.settingsGenderEditText);
        settingsLevelEditText = findViewById(R.id.settingsLevelEditText);
        settingsUpdateBtn = findViewById(R.id.settingsUpdateBtn);

        progressDialog = new ProgressDialog(this);

        fAuth = FirebaseAuth.getInstance();
        firebaseUser = fAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        usersProfileImgRef = FirebaseStorage.getInstance().getReference().child("ProfileImg");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String profileImg = "";
                    String status = "";
                    String username = "";
                    String fullname = "";
                    String country = "";
                    String dob = "";
                    String gender = "";
                    String level = "";

                    if(dataSnapshot.child("profileImg").getValue() != null){
                        profileImg = dataSnapshot.child("profileImg").getValue().toString();
                    }
                    if(dataSnapshot.child("status").getValue() != null){
                        status = dataSnapshot.child("status").getValue().toString();
                    }
                    if(dataSnapshot.child("username").getValue() != null){
                        username = dataSnapshot.child("username").getValue().toString();
                    }
                    if(dataSnapshot.child("fullname").getValue() != null){
                        fullname = dataSnapshot.child("fullname").getValue().toString();
                    }
                    if(dataSnapshot.child("country").getValue() != null){
                        country = dataSnapshot.child("country").getValue().toString();
                    }
                    if(dataSnapshot.child("dob").getValue() != null){
                        dob = dataSnapshot.child("dob").getValue().toString();
                    }
                    if(dataSnapshot.child("level").getValue() != null){
                        level = dataSnapshot.child("level").getValue().toString();
                    }
                    if(dataSnapshot.child("gender").getValue() != null){
                        gender = dataSnapshot.child("gender").getValue().toString();
                    }

                    if(!profileImg.isEmpty())
                        Picasso.get().load(profileImg).placeholder(R.drawable.profile).into(settingsProfileImageView);
                    else
                        settingsProfileImageView.setImageResource(R.drawable.profile);
                    settingsStatusEditText.setText(status);
                    settingsUsernameEditText.setText(username);
                    settingsFullnameEditText.setText(fullname);
                    settingsCountryEditText.setText(country);
                    settingsDobEditText.setText(dob);
                    settingsGenderEditText.setText(gender);
                    settingsLevelEditText.setText(level);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        settingsUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAccountInfo();
            }
        });

        settingsProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK);
            }
        });

    }

    private void validateAccountInfo() {
        String status = settingsStatusEditText.getText().toString();
        String username = settingsUsernameEditText.getText().toString();
        String fullname = settingsFullnameEditText.getText().toString();
        String country = settingsCountryEditText.getText().toString();
        String dob = settingsDobEditText.getText().toString();
        String gender = settingsGenderEditText.getText().toString();
        String level = settingsLevelEditText.getText().toString();

        if(status.isEmpty() || username.isEmpty() || fullname.isEmpty() || country.isEmpty() || dob.isEmpty() || gender.isEmpty() || level.isEmpty()){
            Toast.makeText(this, "All fields must be provided", Toast.LENGTH_SHORT).show();
        }
        else{
            progressDialog.setTitle("Saving Account Information");
            progressDialog.setMessage("Updating user information with the servers...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);
            updateAccountInfo(status, username, fullname, country, dob, gender, level);
        }

    }

    private void updateAccountInfo(String status, String username, String fullname, String country, String dob, String gender, String level) {
        HashMap  userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("fullname", fullname);
        userMap.put("country", country);
        userMap.put("status", status);
        userMap.put("gender", gender);
        userMap.put("dob", dob);
        userMap.put("level", level);
        userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    sendUserToMainActivity();
                    Toast.makeText(SettingsActivity.this, "Successfully Updated Information", Toast.LENGTH_SHORT).show();
                }else{
                    String errorMsg = task.getException().getMessage();
                    Toast.makeText(SettingsActivity.this, "Couldn't update information: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null){
            data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1).start(this);
        }
        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            progressDialog.setTitle("Saving User Picture");
            progressDialog.setMessage("Storing User profile picture to the storage server...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);
            Uri resultUri = result.getUri();
            final StorageReference filePath = usersProfileImgRef.child(firebaseUser.getUid());
            filePath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        String errorMsg = task.getException().getMessage();
                        Toast.makeText(SettingsActivity.this, "Image could not be stored: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }

                    // Continue with the task to get the download URL
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        final String downloadUrl = task.getResult().toString();
                        userRef.child("profileImg").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(SettingsActivity.this, "Image set for the  user", Toast.LENGTH_SHORT).show();
                                }else{
                                    String errorMsg = task.getException().getMessage();
                                    Toast.makeText(SettingsActivity.this, "Image could not be set: " + errorMsg, Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }
                        });

                    }else{
                        String errorMsg = task.getException().getMessage();
                        Toast.makeText(SettingsActivity.this, "Image could not be stored: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
