package com.example.android.xrcs;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.xrcs.data.WorkoutDbHelper;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private SQLiteDatabase mDb;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    work_out_fragment fragment1 = new work_out_fragment();
                    fragmentTransaction.replace(R.id.fragment_container, fragment1).commit();
                    return true;
                case R.id.navigation_dashboard:
                    manage_workouts_fragment fragment2 = new manage_workouts_fragment();
                    fragmentTransaction.replace(R.id.fragment_container, fragment2).commit();
                    return true;
                case R.id.navigation_notifications:
                    stats_fragment fragment3 = new stats_fragment();
                    fragmentTransaction.replace(R.id.fragment_container, fragment3).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WorkoutDbHelper dbHelper = new WorkoutDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
