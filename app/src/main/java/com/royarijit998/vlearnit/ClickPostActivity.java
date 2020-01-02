package com.royarijit998.vlearnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {

    private ImageView postClickImageView;
    private TextView postDescTextView;
    private Button deletePostBtn, editPostBtn;

    private String postKey, currUserID, databaseUserID, postDescription;

    private DatabaseReference postRef;
    private FirebaseAuth currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        currUser = FirebaseAuth.getInstance();
        currUserID = currUser.getCurrentUser().getUid();

        postClickImageView = findViewById(R.id.postClickImageView);
        postDescTextView = findViewById(R.id.postClickDescTextView);
        deletePostBtn = findViewById(R.id.deletePostBtn);
        editPostBtn = findViewById(R.id.editPostBtn);

        // Initially, the buttons mustn't be visible
        deletePostBtn.setVisibility(View.INVISIBLE);
        editPostBtn.setVisibility(View.INVISIBLE);

        postKey = getIntent().getExtras().get("postKey").toString();
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey);


        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    databaseUserID = dataSnapshot.child("uid").getValue().toString();

                    postDescription = dataSnapshot.child("description").getValue().toString();
                    postDescTextView.setText(postDescription);

                    String imagePath = dataSnapshot.child("postImg").getValue().toString();
                    //Image display and caching library for displaying images stored on servers
                    Picasso.get().load(imagePath).placeholder(R.drawable.select_image).into(postClickImageView);

                    if(currUserID.equals(databaseUserID)){
                        deletePostBtn.setVisibility(View.VISIBLE);
                        editPostBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        deletePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCurrentPost();
            }
        });

        editPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrentPost();
            }
        });

    }

    private void updateCurrentPost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post Description");

        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(postDescription);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this, "Post Description Updated", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);

    }

    private void deleteCurrentPost() {
        postRef.removeValue();
        sendUserToHomeActivity();
        Toast.makeText(this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
    }

    private void sendUserToHomeActivity() {
        Intent intent = new Intent(ClickPostActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
