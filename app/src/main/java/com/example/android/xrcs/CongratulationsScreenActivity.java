package com.example.android.xrcs;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.xrcs.data.WorkoutContract;
import com.example.android.xrcs.data.WorkoutDbHelper;
import com.google.gson.Gson;

import java.util.ArrayList;

public class CongratulationsScreenActivity extends AppCompatActivity {
    private TextView workoutName;
    private TextView noSetsTV;
    private TextView noReps;
    private TextView duration;
    private TextView restTime;
    private SQLiteDatabase mDb;
    private boolean timeTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulations_screen);
        getSupportActionBar().setTitle("Congratulations");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        workoutName = findViewById(R.id.workout_name_congratulations);
        noSetsTV = findViewById(R.id.no_sets_congratulations);
        noReps = findViewById(R.id.no_reps_congratulations);
        duration = findViewById(R.id.duration_congratulations);
        restTime = findViewById(R.id.rest_time_congratulations);

        Intent AllWorkoutDataIntent = getIntent();
        Bundle workoutDataBundle = AllWorkoutDataIntent.getBundleExtra("workoutDataBundle");
        timeTarget = workoutDataBundle.getString("timeTargetMode").equals("Time Target Mode");
        ArrayList<String> repTimes = AllWorkoutDataIntent.getStringArrayListExtra("repTimes");
        workoutName.setText(workoutDataBundle.getString("workoutName"));
        noReps.setText(workoutDataBundle.getString("repTarget"));
        int numberOfSets = Integer.parseInt(workoutDataBundle.getString("setTarget"));
        noSetsTV.setText(numberOfSets);

        // Calculate the effective Workout / rest time
        ArrayList<Long> repTimesLongList = new ArrayList<Long>();
        for (String l : repTimes) {
            repTimesLongList.add(Long.valueOf(l));
        }
        long startSecond = repTimesLongList.get(0) / 1000;
        long endSecond = repTimesLongList.get(repTimes.size() - 1) / 1000;
        long totalDuration = endSecond - startSecond;
        long effectiveWorkoutTime;
        long effectiveSetRestTime;
        if (timeTarget){
            effectiveSetRestTime = (Long.valueOf(workoutDataBundle.getString("setTarget")) - 1) * (Long.valueOf(workoutDataBundle.getString("restBetween"))); // No break after last set
            effectiveWorkoutTime = totalDuration - effectiveSetRestTime;
        } else {
            int no_pauses = numberOfSets - 1;
            // TODO calculate effective Set rest time
            effectiveSetRestTime = 0;
            effectiveWorkoutTime = totalDuration;
        }

        duration.setText(String.valueOf(effectiveWorkoutTime) + " s");
        restTime.setText(String.valueOf(effectiveSetRestTime) + " s");

        // TODO: Save the data to the database
        WorkoutDbHelper dbHelper = new WorkoutDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        Gson gson = new Gson();
        cv.put(WorkoutContract.WorkoutLog.COLUMN_WORKOUT_NAME, workoutDataBundle.getString("workoutName"));
        cv.put(WorkoutContract.WorkoutLog.COLUMN_WORKOUT_TYPE, workoutDataBundle.getString("workoutType"));
        cv.put(WorkoutContract.WorkoutLog.COLUMN_NO_SETS, workoutDataBundle.getString("setTarget"));
        cv.put(WorkoutContract.WorkoutLog.COLUMN_REST_TIME, workoutDataBundle.getString("restBetween"));
        cv.put(WorkoutContract.WorkoutLog.COLUMN_REPS, workoutDataBundle.getString("repTarget"));
        cv.put(WorkoutContract.WorkoutLog.COLUMN_TIME_TARGET_MODE, workoutDataBundle.getString("timeTargetMode"));
        cv.put(WorkoutContract.WorkoutLog.COLUMN_TARGET_TIME, workoutDataBundle.getString("targetTime"));
        cv.put(WorkoutContract.WorkoutLog.COLUMN_REP_TIMES, gson.toJson(repTimesLongList));

        try {
            mDb.beginTransaction();
            mDb.insert(WorkoutContract.WorkoutLog.TABLE_NAME, null, cv);
            mDb.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            mDb.endTransaction();
            Toast.makeText(this, "Workout successfully saved!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
