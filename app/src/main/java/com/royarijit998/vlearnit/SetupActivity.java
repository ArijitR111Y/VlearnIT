package com.royarijit998.vlearnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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

public class SetupActivity extends AppCompatActivity {

    private CircleImageView profileImg;
    private EditText unameEditText, fnameEditText, countryEditText;
    private Button saveInfoBtn;
    private DatabaseReference usersRef;
    private StorageReference usersProfileImgRef;
    private ProgressDialog progressDialog;
    private FirebaseUser firebaseUser;
    final static int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        profileImg = findViewById(R.id.profileImg);
        unameEditText = findViewById(R.id.unameEditText);
        fnameEditText = findViewById(R.id.fnameEditText);
        countryEditText = findViewById(R.id.countryEditText);
        saveInfoBtn = findViewById(R.id.saveInfoBtn);

        progressDialog = new ProgressDialog(this);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersProfileImgRef = FirebaseStorage.getInstance().getReference().child("ProfileImg");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        saveInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSetupInformation();
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK);
            }
        });

        usersRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("profileImg")) {
                        String imagePath = dataSnapshot.child("profileImg").getValue().toString();
                        //Image display and caching library for displaying images stored on servers
                        Picasso.get().load(imagePath).placeholder(R.drawable.profile).into(profileImg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        Toast.makeText(SetupActivity.this, "Image could not be stored: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }

                    // Continue with the task to get the download URL
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        final String downloadUrl = task.getResult().toString();
                        usersRef.child(firebaseUser.getUid()).child("profileImg").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(SetupActivity.this, "Image set for the  user", Toast.LENGTH_SHORT).show();
                                }else{
                                    String errorMsg = task.getException().getMessage();
                                    Toast.makeText(SetupActivity.this, "Image could not be set: " + errorMsg, Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }
                        });

                    }else{
                        String errorMsg = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Image could not be stored: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void saveSetupInformation() {
        String uname = unameEditText.getText().toString();
        String fname = fnameEditText.getText().toString();
        String country = countryEditText.getText().toString();

        if(uname.isEmpty() || fname.isEmpty() || country.isEmpty()){
            Toast.makeText(SetupActivity.this, "All fields must be provided", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.setTitle("Saving User Information");
            progressDialog.setMessage("Posting user details..");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);
            HashMap  userMap = new HashMap<>();
            userMap.put("username", uname);
            userMap.put("fullname", fname);
            userMap.put("country", country);
            userMap.put("status", "Hey there! I would like to learn something new!");
            userMap.put("gender", "");
            userMap.put("dob", "");
            userMap.put("level", "beginner");
            usersRef.child(firebaseUser.getUid()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SetupActivity.this, "Successfully updated User Information", Toast.LENGTH_SHORT).show();
                        sendUserToMainActivity();
                    }else{
                        String errorMsg = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Couldn't update User Information: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(SetupActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}
