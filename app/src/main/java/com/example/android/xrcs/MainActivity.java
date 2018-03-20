package com.example.android.xrcs;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private ActionBar customActionBar;
    private TextView ActionBarHeadingTV;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_work_out:
                    WorkOutFragment fragment1 = new WorkOutFragment();
                    ActionBarHeadingTV.setText("Work out");
                    fragmentTransaction.replace(R.id.fragment_container, fragment1).commit();
                    return true;
                case R.id.navigation_manage_workouts:
                    ManageWorkoutsFragment fragment2 = new ManageWorkoutsFragment();
                    ActionBarHeadingTV.setText("Manage workouts");
                    fragmentTransaction.replace(R.id.fragment_container, fragment2).commit();
                    return true;
                case R.id.navigation_stats:
                    StatsFragment fragment3 = new StatsFragment();
                    ActionBarHeadingTV.setText("Stats");
                    fragmentTransaction.replace(R.id.fragment_container, fragment3).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use a custom action bar
        customActionBar = getSupportActionBar();
        customActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        customActionBar.setCustomView(R.layout.action_bar_layout);
        ActionBarHeadingTV = (TextView) findViewById(R.id.action_bar_title);
        // Set main layout
        setContentView(R.layout.activity_main);
        // Set up the bottom navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // Load first fragment
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new WorkOutFragment()).commit();
    }
}
