package com.royarijit998.vlearnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView mainNavigationView;
    private CircleImageView profileImg;
    private TextView profileUsernameTextView;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout mainDrawerLayout;

    private RecyclerView userPostRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter postsListAdapter;

    private FirebaseUser firebaseUser;
    private Toolbar mainToolbar;

    private DatabaseReference postsRef;
    private DatabaseReference usersRef;
    private DatabaseReference likesRef;

    private ArrayList<Posts> postsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainToolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Home");

        mainNavigationView = findViewById(R.id.mainNavigationView);
        mainDrawerLayout = findViewById(R.id.mainDrawerLayout);
        //Step-1 : Create an actionBarDrawerToggle object
        actionBarDrawerToggle  = new ActionBarDrawerToggle(MainActivity.this, mainDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        //Step-2 : Add drawer listener to the drawerLayout
        mainDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        //Step -3 : Set corresponding parameter to the supportActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        postsArrayList = new ArrayList<>();

        retrieveAllUsersPosts();

        userPostRecyclerView = findViewById(R.id.userPostRecyclerView);
        userPostRecyclerView.setNestedScrollingEnabled(false);
        userPostRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        userPostRecyclerView.setLayoutManager(linearLayoutManager);
        postsListAdapter = new PostListAdapter(usersRef, likesRef, firebaseUser, postsArrayList);
        userPostRecyclerView.setAdapter(postsListAdapter);


        View navView = mainNavigationView.inflateHeaderView(R.layout.navigation_header);
        profileImg = navView.findViewById(R.id.profileImg);
        profileUsernameTextView = navView.findViewById(R.id.profileUsernameTextView);
        

        mainNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                raiseToastForSelection(menuItem);
                return false;
            }
        });

        usersRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("fullname")) {
                        String fname = dataSnapshot.child("fullname").getValue().toString();
                        profileUsernameTextView.setText(fname);
                    }
                    if (dataSnapshot.hasChild("profileImg")) {
                        String imagePath = dataSnapshot.child("profileImg").getValue().toString();
                        Picasso.get().load(imagePath).placeholder(R.drawable.profile).into(profileImg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void retrieveAllUsersPosts() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {

                        for(Posts posts : postsArrayList){
                            if(posts.getKey().equals(childSnapshot.getKey())){
                                postsArrayList.remove(posts);
                                break;
                            }
                        }

                        String uid = "", date = "", time = "", description = "", postImg = "";

                        if(childSnapshot.child("uid").getValue() != null)
                            uid = childSnapshot.child("uid").getValue().toString();
                        if(childSnapshot.child("date").getValue() != null)
                            date = childSnapshot.child("date").getValue().toString();
                        if(childSnapshot.child("time").getValue() != null)
                            time = childSnapshot.child("time").getValue().toString();
                        if(childSnapshot.child("description").getValue() != null)
                            description = childSnapshot.child("description").getValue().toString();
                        if(childSnapshot.child("postImg").getValue() != null)
                            postImg = childSnapshot.child("postImg").getValue().toString();

                        String postKey = childSnapshot.getKey();
                        Posts post = new Posts(postKey, uid, date, time, description, postImg);
                        postsArrayList.add(post);
                        postsListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            checkUserExistence();
        }
    }

    private void checkUserExistence() {
        final String currentUserId = FirebaseAuth.getInstance().getUid();
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(currentUserId)){
                    sendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToSetupActivity() {
        Intent intent = new Intent(MainActivity.this, SetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToPostActivity() {
        Intent intent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(intent);
    }

    private void sendUserToProfileActivity() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void sendUserToSettingsActivity() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void sendUserToFindFriendsActivity() {
        Intent intent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void raiseToastForSelection(MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.navPost:
                sendUserToPostActivity();
                break;
            case R.id.navHome:
                Toast.makeText(getApplicationContext(), "Open Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navProfile:
                sendUserToProfileActivity();
                break;
            case R.id.navFriends:
                Toast.makeText(getApplicationContext(), "See Friends", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navFindFriends:
                sendUserToFindFriendsActivity();
                break;
            case R.id.navSettings:
                sendUserToSettingsActivity();
                break;
            case R.id.navLogout:
                Toast.makeText(getApplicationContext(), "Logged out from App", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                sendUserToLoginActivity();
                break;
        }

    }


}
