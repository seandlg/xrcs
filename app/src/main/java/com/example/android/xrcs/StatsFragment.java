package com.example.android.xrcs;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.xrcs.data.WorkoutContract;
import com.example.android.xrcs.data.WorkoutDbHelper;
import com.example.android.xrcs.helpers.ItemClickSupport;
import com.example.android.xrcs.helpers.StatsAdapter;
import com.google.gson.Gson;

public class StatsFragment extends Fragment {
    private SQLiteDatabase mDb;
    private StatsAdapter mAdapter;
    private RecyclerView statsRecyclerView;
    private Gson gson;

    public StatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Create a DB helper (this will create the DB if run for the first time)
        WorkoutDbHelper dbHelper = new WorkoutDbHelper(getActivity());
        mDb = dbHelper.getWritableDatabase();
        gson = new Gson();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_stats_layout, container, false);
        statsRecyclerView = rootView.findViewById(R.id.stats_recycler_view);
        statsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Cursor cursor = getAllWorkoutStats();
        // Pass the entire cursor to the adapter rather than just the count & create an adapter for that cursor to display the data
        mAdapter = new StatsAdapter(getActivity(), cursor);
        // Link the adapter to the RecyclerView
        statsRecyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true);
        return rootView;
    }

    private Cursor getAllWorkoutStats() {
        return mDb.query(
                WorkoutContract.WorkoutLog.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WorkoutContract.WorkoutLog.COLUMN_TIMESTAMP + " DESC");
    }
}
