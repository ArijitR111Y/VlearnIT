package com.royarijit998.vlearnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private Toolbar postToolbar;
    private ImageButton addImageButton;
    private Button submitPostBtn;
    private EditText postDescEditText;

    private ProgressDialog progressDialog;

    private Uri imageUri;
    private String currDate, currTime, randomKey;
    private String downloadUrl;
    private String postDescription;

    private FirebaseUser currUserRef;

    private StorageReference storageRef;
    private DatabaseReference userRef, postRef;

    final static int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        storageRef = FirebaseStorage.getInstance().getReference();
        currUserRef = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        postToolbar = findViewById(R.id.postToolbar);
        setSupportActionBar(postToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create Post");


        addImageButton = findViewById(R.id.addImageButton);
        submitPostBtn = findViewById(R.id.submitPostBtn);
        postDescEditText = findViewById(R.id.postDescEditText);

        progressDialog = new ProgressDialog(this);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK);
            }
        });

        submitPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePostInfo();
            }
        });

    }

    private void validatePostInfo() {
        postDescription = postDescEditText.getText().toString();

        if(postDescription.isEmpty()){
            Toast.makeText(this, "Please add some text to be posted..", Toast.LENGTH_SHORT).show();
        }
        else if(imageUri == null){
            Toast.makeText(this, "Add an image to make your post appeal more students..", Toast.LENGTH_SHORT).show();
        }
        else{
            progressDialog.setTitle("Adding New Post");
            progressDialog.setMessage("Please wait while we update your new post...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);
            storeImageToFirebaseStorage();
        }

    }

    private void storeImageToFirebaseStorage() {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        currDate = currDateFormat.format(calForDate.getTime());

        SimpleDateFormat currTimeFormat = new SimpleDateFormat("HH:mm");
        currTime = currTimeFormat.format(calForDate.getTime());

        randomKey = currDate + currTime;

        final StorageReference filePath = storageRef.child("PostImg").child(imageUri.getLastPathSegment() + currUserRef.getUid() + randomKey);
        filePath.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    progressDialog.dismiss();
                    String errorMsg = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Image could not be uploaded: " + errorMsg, Toast.LENGTH_SHORT).show();
                }

                // Continue with the task to get the download URL
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    downloadUrl = task.getResult().toString();
                    savePostInformationToDatabase();
                }else{
                    progressDialog.dismiss();
                    String errorMsg = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Image could not be uploaded: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void savePostInformationToDatabase() {
        userRef.child(currUserRef.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    HashMap postMap = new HashMap();
                    postMap.put("uid", currUserRef.getUid());
                    postMap.put("date", currDate);
                    postMap.put("time", currTime);
                    postMap.put("description", postDescription);
                    postMap.put("postImg", downloadUrl);

                    postRef.child(currUserRef.getUid() + randomKey).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                sendUserToMainActivity();
                                Toast.makeText(PostActivity.this, "Post created successfully..", Toast.LENGTH_SHORT).show();
                            }else{
                                String errorMsg  = task.getException().getMessage();
                                Toast.makeText(PostActivity.this, "Post could not be created: " + errorMsg, Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
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
            imageUri = data.getData();
            addImageButton.setImageURI(imageUri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home){
            sendUserToMainActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
