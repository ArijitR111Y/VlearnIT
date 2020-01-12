package com.royarijit998.vlearnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private CircleImageView personProfileImageView;
    private TextView personProfileFullnameTextView, personProfileUsernameTextView, personProfileStatusTextView,
            personProfileCountryTextView, personProfileDobTextView, personProfileLevelTextView;
    private Button sendFriendRequestBtn, declineFriendRequestBtn;

    private FirebaseUser currUserRef;
    private DatabaseReference usersRef, userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        String userId = getIntent().getExtras().get("userId").toString();

        personProfileImageView = findViewById(R.id.personProfileImageView);
        personProfileFullnameTextView = findViewById(R.id.personProfileFullnameTextView);
        personProfileUsernameTextView = findViewById(R.id.personProfileUsernameTextView);
        personProfileStatusTextView = findViewById(R.id.personProfileStatusTextView);
        personProfileCountryTextView = findViewById(R.id.personProfileCountryTextView);
        personProfileDobTextView = findViewById(R.id.personProfileDobTextView);
        personProfileLevelTextView = findViewById(R.id.personProfileLevelTextView);

        sendFriendRequestBtn = findViewById(R.id.sendFriendRequestBtn);
        declineFriendRequestBtn = findViewById(R.id.declineFriendRequestBtn);

        currUserRef = FirebaseAuth.getInstance().getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef = usersRef.child(userId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String profileImg = dataSnapshot.child("profileImg").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String fullname = dataSnapshot.child("fullname").getValue().toString();
                    String country = dataSnapshot.child("country").getValue().toString();
                    String dob = dataSnapshot.child("dob").getValue().toString();
                    String level = dataSnapshot.child("level").getValue().toString();

                    Picasso.get().load(profileImg).placeholder(R.drawable.profile).into(personProfileImageView);
                    personProfileStatusTextView.setText(status);
                    personProfileUsernameTextView.setText("@" + username);
                    personProfileFullnameTextView.setText(fullname);
                    personProfileCountryTextView.setText("Country: " + country);
                    personProfileDobTextView.setText("Birthday: " + dob);
                    personProfileLevelTextView.setText("Programming Level: " + level);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        declineFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
