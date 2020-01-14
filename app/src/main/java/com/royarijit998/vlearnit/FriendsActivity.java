package com.royarijit998.vlearnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    private Toolbar friendsListToolbar;

    private RecyclerView friendRecyclerView;
    private LinearLayoutManager friendLinearLayoutManager;
    private RecyclerView.Adapter friendsListAdapter;

    private RecyclerView friendReqRecyclerView;
    private LinearLayoutManager friendReqLinearLayoutManager;
    private RecyclerView.Adapter friendReqsListAdapter;

    private DatabaseReference usersRef, friendsRef, friendReqsRef;
    private FirebaseUser currUserRef;

    // We simply store the uids for the friends and users who've sent a friend request
    private ArrayList<String> friendReqsArrayList, friendsArrayList;
    private String currUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        friendsListToolbar = findViewById(R.id.friendsListToolbar);
        setSupportActionBar(friendsListToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create Post");
        getSupportActionBar().setTitle("Manage Friends");

        friendsArrayList = new ArrayList<>();
        friendReqsArrayList = new ArrayList<>();

        currUserRef = FirebaseAuth.getInstance().getCurrentUser();
        currUserId = currUserRef.getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        friendReqsRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        friendRecyclerView = findViewById(R.id.friendRecyclerView);
        friendRecyclerView.setNestedScrollingEnabled(false);
        friendRecyclerView.setHasFixedSize(false);
        friendLinearLayoutManager = new LinearLayoutManager(this);
        friendRecyclerView.setLayoutManager(friendLinearLayoutManager);
        friendsListAdapter = new FriendsListAdapter(usersRef, friendsArrayList);
        friendRecyclerView.setAdapter(friendsListAdapter);

        friendReqRecyclerView = findViewById(R.id.friendReqRecyclerView);
        friendReqRecyclerView.setNestedScrollingEnabled(false);
        friendReqRecyclerView.setHasFixedSize(false);
        friendReqLinearLayoutManager = new LinearLayoutManager(this);
        friendReqRecyclerView.setLayoutManager(friendReqLinearLayoutManager);
        friendReqsListAdapter = new FriendRequestsListAdapter(usersRef, friendReqsArrayList);
        friendReqRecyclerView.setAdapter(friendReqsListAdapter);

        retrieveFriendRequests();
        retrieveFriends();
    }

    private void retrieveFriendRequests() {
        friendReqsRef.child(currUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        friendReqsArrayList.add(childSnapshot.getKey());
                        friendReqsListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void retrieveFriends(){
        friendsRef.child(currUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        friendsArrayList.add(childSnapshot.getKey());
                        friendsListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
