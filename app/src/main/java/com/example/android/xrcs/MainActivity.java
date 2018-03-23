package com.example.android.xrcs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set main layout
        setContentView(R.layout.activity_main);
        // Set up the bottom navigation
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // Load first fragment
        getSupportActionBar().setTitle("Work out");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new WorkOutFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        WorkOutFragment fragment1 = new WorkOutFragment();
        ManageWorkoutsFragment fragment2 = new ManageWorkoutsFragment();
        StatsFragment fragment3 = new StatsFragment();

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_work_out:
                    getSupportActionBar().setTitle("Work out");
                    fragmentTransaction.replace(R.id.fragment_container, fragment1).commit();
                    return true;
                case R.id.navigation_manage_workouts:
                    getSupportActionBar().setTitle("Manage workouts");
                    fragmentTransaction.replace(R.id.fragment_container, fragment2).commit();
                    return true;
                case R.id.navigation_stats:
                    getSupportActionBar().setTitle("Stats");
                    fragmentTransaction.replace(R.id.fragment_container, fragment3).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
