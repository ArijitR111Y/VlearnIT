package com.royarijit998.vlearnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView commentsRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter commentsListAdapter;

    private DatabaseReference usersRef;
    private DatabaseReference postsRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currUser;

    private EditText addCommentEditText;
    private Button addCommentBtn;

    private String postKey;
    private String commentText;
    private String currDate, currTime, randomKey;

    private ArrayList<Comments> commentsArrayList;
    private HashSet<String> uniqueComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        postKey = getIntent().getExtras().get("postKey").toString();

        commentsArrayList = new ArrayList<>();
        uniqueComments = new HashSet<>();

        addCommentEditText = findViewById(R.id.addCommentEditText);
        addCommentBtn = findViewById(R.id.addCommentBtn);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).child("Comments");
        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setNestedScrollingEnabled(false);
        commentsRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(this);
        commentsRecyclerView.setLayoutManager(linearLayoutManager);
        // Finally, setting the Adapter for the recycler view
        commentsListAdapter = new CommentListAdapter(usersRef, commentsArrayList);
        commentsRecyclerView.setAdapter(commentsListAdapter);

        addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCommentText();
            }
        });

        getUserComments();


    }

    private void validateCommentText() {
        commentText = addCommentEditText.getText().toString();
        if(commentText.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please add some text to the comment field!!", Toast.LENGTH_SHORT).show();
        }else{
            addCommentToDatabase();
        }
    }

    private void addCommentToDatabase() {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        currDate = currDateFormat.format(calForDate.getTime());

        SimpleDateFormat currTimeFormat = new SimpleDateFormat("HH:mm");
        currTime = currTimeFormat.format(calForDate.getTime());

        randomKey = postKey + currUser.getUid() + currDate + currTime;

        HashMap map = new HashMap();
        map.put("uid", currUser.getUid());
        map.put("date", currDate);
        map.put("time", currTime);
        map.put("comment", commentText);

        postsRef.child(randomKey).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(CommentsActivity.this, "Comment added successfully", Toast.LENGTH_SHORT).show();
                }else{
                    String errorMsg = task.getException().getMessage();
                    Toast.makeText(CommentsActivity.this, "Error adding comment: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
                addCommentEditText.setText("");
            }
        });

    }

    private void getUserComments() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        String commentId = childSnapshot.getKey();

                        if(uniqueComments.contains(commentId)){
                            continue;
                        }

                        String uid = childSnapshot.child("uid").getValue().toString();
                        String date = childSnapshot.child("date").getValue().toString();
                        String time = childSnapshot.child("time").getValue().toString();
                        String comment = childSnapshot.child("comment").getValue().toString();

                        Comments comments = new Comments(uid, date, time, comment);
                        commentsArrayList.add(comments);
                        uniqueComments.add(commentId);
                        commentsListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
