package com.royarijit998.vlearnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private NavigationView mainNavigationView;
    private DrawerLayout mainDrawerLayout;
    private RecyclerView userPostRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainNavigationView = findViewById(R.id.mainNavigationView);
        mainDrawerLayout = findViewById(R.id.mainDrawerLayout);
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
