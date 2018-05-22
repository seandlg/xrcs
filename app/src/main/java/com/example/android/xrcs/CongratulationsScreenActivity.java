package com.example.android.xrcs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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

        Intent workoutData = getIntent();
    }
}
