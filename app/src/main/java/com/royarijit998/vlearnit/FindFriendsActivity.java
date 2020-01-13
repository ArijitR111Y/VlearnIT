package com.royarijit998.vlearnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar findFriendsToolbar;
    private EditText searchEditText;
    private Button searchBtn;

    private RecyclerView usersRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter usersListAdapter;

    private ArrayList<Users> usersArrayList;

    private DatabaseReference userRef;

    private String searchTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        findFriendsToolbar = findViewById(R.id.findFriendsToolbar);
        setSupportActionBar(findFriendsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Users..");

        searchEditText = findViewById(R.id.searchEditText);
        searchBtn = findViewById(R.id.searchBtn);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        usersArrayList = new ArrayList<>();

        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setNestedScrollingEnabled(false);
        usersRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(this);
        usersRecyclerView.setLayoutManager(linearLayoutManager);
        usersListAdapter = new UserListAdapter(FindFriendsActivity.this, usersArrayList);
        usersRecyclerView.setAdapter(usersListAdapter);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateSearchInput();
            }
        });

    }

    private void validateSearchInput() {
         searchTerm = searchEditText.getText().toString();
         usersListAdapter.notifyDataSetChanged();
         usersArrayList.clear();
         if(searchTerm.isEmpty()){
             Toast.makeText(this, "Please provide a search query..", Toast.LENGTH_SHORT).show();
         }else{
             retrieveUsersList();
         }

    }

    private void retrieveUsersList() {
        Query usersSearchQuery = userRef.orderByChild("fullname").startAt(searchTerm).endAt(searchTerm + "\uf8ff");
        usersSearchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        String profileImg = "";
                        String fullname = "";
                        String status = "";

                        if(childSnapshot.child("profileImg").exists()){
                            profileImg = childSnapshot.child("profileImg").getValue().toString();
                        }
                        if(childSnapshot.child("fullname").exists()){
                            fullname = childSnapshot.child("fullname").getValue().toString();
                        }
                        if(childSnapshot.child("status").exists()){
                            status = childSnapshot.child("status").getValue().toString();
                        }

                        Users user = new Users(childSnapshot.getKey(),
                                profileImg,
                                fullname,
                                status);

                        usersArrayList.add(user);
                    }
                    usersListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
