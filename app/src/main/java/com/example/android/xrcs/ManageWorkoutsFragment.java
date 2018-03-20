package com.example.android.xrcs;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.xrcs.data.WorkoutContract;
import com.example.android.xrcs.data.WorkoutDbHelper;

public class ManageWorkoutsFragment extends Fragment {
    private SQLiteDatabase mDb;
    private ManageWorkoutsAdapter mAdapter;

    public ManageWorkoutsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_manage_workouts_layout, container, false);
        RecyclerView workoutsRecyclerView = (RecyclerView) rootView.findViewById(R.id.manage_workouts_recycler_view);
        workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Create a DB helper (this will create the DB if run for the first time)
        WorkoutDbHelper dbHelper = new WorkoutDbHelper(getActivity());
        mDb = dbHelper.getWritableDatabase();
        Cursor cursor = getAllWorkouts();
        // Pass the entire cursor to the adapter rather than just the count & create an adapter for that cursor to display the data
        mAdapter = new ManageWorkoutsAdapter(getActivity(), cursor);
        // Link the adapter to the RecyclerView
        workoutsRecyclerView.setAdapter(mAdapter);
        return rootView;
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
