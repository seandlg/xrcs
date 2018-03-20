package com.example.android.xrcs;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.example.android.xrcs.data.WorkoutContract;
import com.example.android.xrcs.data.WorkoutDbHelper;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private SQLiteDatabase mDb;
    private ManageWorkoutsAdapter mAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    WorkOutFragment fragment1 = new WorkOutFragment();
                    fragmentTransaction.replace(R.id.fragment_container, fragment1).commit();
                    return true;
                case R.id.navigation_dashboard:
                    ManageWorkoutsFragment fragment2 = new ManageWorkoutsFragment();
                    fragmentTransaction.replace(R.id.fragment_container, fragment2).commit();
                    return true;
                case R.id.navigation_notifications:
                    StatsFragment fragment3 = new StatsFragment();
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

        RecyclerView workoutsRecyclerView;

        // Set local attributes to corresponding views
        workoutsRecyclerView = (RecyclerView) this.findViewById(R.id.manage_workouts_recycler_view);
        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Create a DB helper (this will create the DB if run for the first time)
        WorkoutDbHelper dbHelper = new WorkoutDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        Cursor cursor = getAllWorkouts();
        // Pass the entire cursor to the adapter rather than just the count & create an adapter for that cursor to display the data
        mAdapter = new ManageWorkoutsAdapter(this, cursor);
        // Link the adapter to the RecyclerView
        workoutsRecyclerView.setAdapter(mAdapter);
        Log.d("point","made it to here");

        // Set up the bottom navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private Cursor getAllWorkouts() {
        return mDb.query(
                WorkoutContract.WorkoutEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WorkoutContract.WorkoutEntry.COLUMN_TIMESTAMP);
    }

}
