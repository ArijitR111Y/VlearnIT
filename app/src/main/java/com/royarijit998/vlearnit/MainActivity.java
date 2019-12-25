package com.royarijit998.vlearnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private NavigationView mainNavigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout mainDrawerLayout;
    private RecyclerView userPostRecyclerView;
    private Toolbar mainToolbar;

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
        userPostRecyclerView = findViewById(R.id.userPostRecyclerView);
        mainNavigationView.inflateHeaderView(R.layout.navigation_header);


        mainNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                raiseToastForSelection(menuItem);
                return false;
            }
        });
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
                Toast.makeText(getApplicationContext(), "Create new post", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navHome:
                Toast.makeText(getApplicationContext(), "Open Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navProfile:
                Toast.makeText(getApplicationContext(), "Open Profile", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navFriends:
                Toast.makeText(getApplicationContext(), "See Friends", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navFindFriends:
                Toast.makeText(getApplicationContext(), "Find Friends", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navMessage:
                Toast.makeText(getApplicationContext(), "Create new msg", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navSettings:
                Toast.makeText(getApplicationContext(), "Open Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navLogout:
                Toast.makeText(getApplicationContext(), "LogOut from App", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
