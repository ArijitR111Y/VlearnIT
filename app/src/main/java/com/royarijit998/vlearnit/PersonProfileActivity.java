package com.royarijit998.vlearnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private CircleImageView personProfileImageView;
    private TextView personProfileFullnameTextView, personProfileUsernameTextView, personProfileStatusTextView,
            personProfileCountryTextView, personProfileDobTextView, personProfileLevelTextView;
    private Button sendFriendRequestBtn, declineFriendRequestBtn;

    private FirebaseUser currUserRef;
    private DatabaseReference friendReqsRef, usersRef, userRef, friendsRef;

    private String currUserId, currentFriendshipState;
    private String userId;
    private String currDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        userId = getIntent().getExtras().get("userId").toString();

        personProfileImageView = findViewById(R.id.personProfileImageView);
        personProfileFullnameTextView = findViewById(R.id.personProfileFullnameTextView);
        personProfileUsernameTextView = findViewById(R.id.personProfileUsernameTextView);
        personProfileStatusTextView = findViewById(R.id.personProfileStatusTextView);
        personProfileCountryTextView = findViewById(R.id.personProfileCountryTextView);
        personProfileDobTextView = findViewById(R.id.personProfileDobTextView);
        personProfileLevelTextView = findViewById(R.id.personProfileLevelTextView);

        sendFriendRequestBtn = findViewById(R.id.sendFriendRequestBtn);
        declineFriendRequestBtn = findViewById(R.id.declineFriendRequestBtn);

        currentFriendshipState = "not_friends";

        currUserRef = FirebaseAuth.getInstance().getCurrentUser();
        currUserId = currUserRef.getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef = usersRef.child(userId);
        friendReqsRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        declineFriendRequestBtn.setVisibility(View.INVISIBLE);

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

                    if(!profileImg.isEmpty())
                        Picasso.get().load(profileImg).placeholder(R.drawable.profile).into(personProfileImageView);
                    else
                        personProfileImageView.setImageResource(R.drawable.profile);
                    personProfileStatusTextView.setText(status);
                    personProfileUsernameTextView.setText("@" + username);
                    personProfileFullnameTextView.setText(fullname);
                    personProfileCountryTextView.setText("Country: " + country);
                    personProfileDobTextView.setText("Birthday: " + dob);
                    personProfileLevelTextView.setText("Programming Level: " + level);
                    
                    maintenanceOfButtons();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(currUserId.equals(userId)){
            sendFriendRequestBtn.setVisibility(View.INVISIBLE);
        }else{
            sendFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendFriendRequestBtn.setEnabled(false);
                    if(currentFriendshipState.equals("not_friends")){
                        sendFriendRequest();
                    }else if(currentFriendshipState.equals("request_sent")){
                        cancelFriendRequest();
                    }else if(currentFriendshipState.equals("request_received")){
                        acceptFriendRequest();
                    }else if(currentFriendshipState.equals("friends")){
                        unfriendExistingFriend();
                    }
                }
            });

        }
    }

    private void maintenanceOfButtons() {
        friendReqsRef.child(currUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(userId)){
                            String request_type = dataSnapshot.child(userId).child("request_type").getValue().toString();

                            if(request_type.equals("sent")){
                                currentFriendshipState = "request_sent";
                                sendFriendRequestBtn.setText("Cancel Friend Request");
                            }else if(request_type.equals("received")){
                                currentFriendshipState = "request_received";

                                sendFriendRequestBtn.setText("Accept Friend Request");
                                declineFriendRequestBtn.setVisibility(View.VISIBLE);
                                declineFriendRequestBtn.setEnabled(true);

                                declineFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancelFriendRequest();
                                    }
                                });
                            }
                        }
                        else{
                            friendsRef.child(currUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(userId)){
                                                currentFriendshipState = "friends";
                                                sendFriendRequestBtn.setText("Unfriend");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
        });
    }

    private void sendFriendRequest() {
        friendReqsRef.child(currUserId)
                .child(userId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendReqsRef.child(userId)
                                    .child(currUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendFriendRequestBtn.setEnabled(true);
                                                currentFriendshipState = "request_sent";
                                                sendFriendRequestBtn.setText("Cancel Friend Request");

                                                declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                declineFriendRequestBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void cancelFriendRequest() {
        friendReqsRef.child(currUserId)
                .child(userId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendReqsRef.child(userId)
                                    .child(currUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendFriendRequestBtn.setEnabled(true);
                                                currentFriendshipState = "not_friends";
                                                sendFriendRequestBtn.setText("Send Friend Request");

                                                declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                declineFriendRequestBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void acceptFriendRequest() {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        currDate = currDateFormat.format(calForDate.getTime());

        friendsRef.child(currUserId)
                .child(userId)
                .child("date").setValue(currDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendsRef.child(userId)
                                    .child(currUserId)
                                    .child("date").setValue(currDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {


                                            friendReqsRef.child(currUserId)
                                                    .child(userId)
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                friendReqsRef.child(userId)
                                                                        .child(currUserId)
                                                                        .removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful()){
                                                                                    sendFriendRequestBtn.setEnabled(true);
                                                                                    currentFriendshipState = "friends";
                                                                                    sendFriendRequestBtn.setText("Unfriend");

                                                                                    declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                                                    declineFriendRequestBtn.setEnabled(false);
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });



                                        }
                                    });
                        }
                    }
                });
    }

    private void unfriendExistingFriend() {
        friendsRef.child(currUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendsRef.child(userId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendFriendRequestBtn.setEnabled(true);
                                                currentFriendshipState = "not_friends";
                                                sendFriendRequestBtn.setText("Send Friend Request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
