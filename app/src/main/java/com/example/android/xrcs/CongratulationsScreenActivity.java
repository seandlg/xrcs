package com.example.android.xrcs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CongratulationsScreenActivity extends AppCompatActivity {
    public TextView workoutName;
    public TextView noSets;
    public TextView noReps;
    public TextView duration;
    public TextView restTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulations_screen);
        getSupportActionBar().setTitle("Congratulations");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        workoutName = findViewById(R.id.workout_name_congratulations);
        noSets = findViewById(R.id.no_sets_congratulations);
        noReps = findViewById(R.id.no_reps_congratulations);
        duration = findViewById(R.id.duration_congratulations);
        restTime = findViewById(R.id.rest_time_congratulations);

        Intent AllWorkoutDataIntent = getIntent();
        Bundle workoutDataBundle = AllWorkoutDataIntent.getBundleExtra("workoutDataBundle");
        ArrayList<String> repTimes = AllWorkoutDataIntent.getStringArrayListExtra("repTimes");
        workoutName.setText(workoutDataBundle.getString("workoutName"));
        noReps.setText(workoutDataBundle.getString("repTarget"));
        noSets.setText(workoutDataBundle.getString("setTarget"));

        // Calculate the effective Workout / rest time
        ArrayList<Long> repTimesLongList = new ArrayList<Long>();
        for (String l : repTimes) {
            repTimesLongList.add(Long.valueOf(l));
        }
        long startSecond = repTimesLongList.get(0)/1000;
        long endSecond = repTimesLongList.get(repTimes.size()-1)/1000;
        long totalDuration = endSecond-startSecond;
        long effectiveSetRestTime = (Long.valueOf(workoutDataBundle.getString("setTarget"))-1)*(Long.valueOf(workoutDataBundle.getString("restBetween"))); // No break after last set
        long effectiveWorkoutTime = totalDuration-effectiveSetRestTime;
        Log.d("SECONDEND", String.valueOf(endSecond));
        Log.d("SECONDSTART", String.valueOf(startSecond));
        Log.d("SECONDSETTOTAL", String.valueOf(effectiveSetRestTime));
        Log.d("SECONDWORKOUTTOTAL", String.valueOf(effectiveWorkoutTime));

        duration.setText(String.valueOf(effectiveWorkoutTime) + " s");
        restTime.setText(String.valueOf(effectiveSetRestTime) + " s");

        // TODO: Save the data to the database
    }
}
