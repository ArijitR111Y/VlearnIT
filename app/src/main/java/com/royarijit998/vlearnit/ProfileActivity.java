package com.royarijit998.vlearnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView pprofileImageView;
    private TextView pprofileFullnameTextView, pprofileUsernameTextView, pprofileStatusTextView,
            pprofileCountryTextView, pprofileDobTextView, pprofileLevelTextView;

    private FirebaseUser currUserRef;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pprofileImageView = findViewById(R.id.pprofileImageView);
        pprofileFullnameTextView = findViewById(R.id.pprofileFullnameTextView);
        pprofileUsernameTextView = findViewById(R.id.pprofileUsernameTextView);
        pprofileStatusTextView = findViewById(R.id.pprofileStatusTextView);
        pprofileCountryTextView = findViewById(R.id.pprofileCountryTextView);
        pprofileDobTextView = findViewById(R.id.pprofileDobTextView);
        pprofileLevelTextView = findViewById(R.id.pprofileLevelTextView);

        currUserRef = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currUserRef.getUid());

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
                        Picasso.get().load(profileImg).placeholder(R.drawable.profile).into(pprofileImageView);
                    else
                        pprofileImageView.setImageResource(R.drawable.profile);
                    pprofileStatusTextView.setText(status);
                    pprofileUsernameTextView.setText("@" + username);
                    pprofileFullnameTextView.setText(fullname);
                    pprofileCountryTextView.setText("Country: " + country);
                    pprofileDobTextView.setText("Birthday: " + dob);
                    pprofileLevelTextView.setText("Programming Level: " + level);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
