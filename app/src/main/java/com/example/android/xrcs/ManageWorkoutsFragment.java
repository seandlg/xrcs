package com.example.android.xrcs;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.xrcs.data.WorkoutContract;
import com.example.android.xrcs.data.WorkoutDbHelper;
import com.example.android.xrcs.helpers.ItemClickSupport;
import com.example.android.xrcs.helpers.ManageWorkoutsAdapter;

public class ManageWorkoutsFragment extends Fragment {
    private SQLiteDatabase mDb;
    private ManageWorkoutsAdapter mAdapter;
    private RecyclerView workoutsRecyclerView;

    public ManageWorkoutsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Create a DB helper (this will create the DB if run for the first time)
        WorkoutDbHelper dbHelper = new WorkoutDbHelper(getActivity());
        mDb = dbHelper.getWritableDatabase();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_manage_workouts_layout, container, false);
        workoutsRecyclerView = rootView.findViewById(R.id.manage_workouts_recycler_view);
        workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Cursor cursor = getAllWorkouts();
        // Pass the entire cursor to the adapter rather than just the count & create an adapter for that cursor to display the data
        mAdapter = new ManageWorkoutsAdapter(getActivity(), cursor);
        // Link the adapter to the RecyclerView
        workoutsRecyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true);

        ItemClickSupport.addTo(workoutsRecyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // Handle click by getting database ID and passing it to new createWorkoutIntent
                        Intent createWorkoutIntent = new Intent(getActivity(), editWorkoutActivity.class);
                        final ManageWorkoutsAdapter.WorkoutViewHolder holder = (ManageWorkoutsAdapter.WorkoutViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(position));
                        createWorkoutIntent.putExtra("databaseID", holder.getDatabaseID());
                        startActivityForResult(createWorkoutIntent,1);
                    }
                }
        );
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.manage_workouts_extra_menu_items, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_workout:
                Intent createWorkoutIntent = new Intent(getActivity(), editWorkoutActivity.class);
                startActivityForResult(createWorkoutIntent,1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Create new adapter and pass it to the RecyclerView. Not performant, but works for now.
        // TODO: Upgrade this behavior!
        workoutsRecyclerView.setAdapter(new ManageWorkoutsAdapter(getActivity(), getAllWorkouts()));
    }
}