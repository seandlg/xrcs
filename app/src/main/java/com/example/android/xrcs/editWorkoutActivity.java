package com.example.android.xrcs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

public class editWorkoutActivity extends AppCompatActivity {
    private EditText workOutName;
    private Switch timeTargetSwitch;
    private NumberPicker exerciseType;
    private NumberPicker noSets;
    private NumberPicker noReps;
    private NumberPicker setBreakTime;
    private NumberPicker setTargetTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workout);
        getSupportActionBar().setTitle("Manage workouts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        workOutName = findViewById(R.id.workout_name_edit_activity);
        timeTargetSwitch = findViewById(R.id.time_target_switch_edit_activity);

        // Configuring the number pickers
        exerciseType = findViewById(R.id.exercise_type_edit_activity);
        exerciseType.setMinValue(0);
        exerciseType.setMaxValue(1);
        exerciseType.setDisplayedValues(new String[] {"Pushups", "Pullups"});
        exerciseType.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        noSets = findViewById(R.id.no_sets_edit_activity);
        noSets.setMinValue(1);
        noSets.setMaxValue(10);
        noSets.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        noReps = findViewById(R.id.no_reps_edit_activity);
        noReps.setMinValue(1);
        noReps.setMaxValue(100);
        noReps.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        setBreakTime = findViewById(R.id.set_break_time_edit_activity);
        setBreakTime.setMinValue(1);
        setBreakTime.setMaxValue(6);
        setBreakTime.setDisplayedValues(new String[] {"30", "60", "90", "120", "150", "180"});
        setBreakTime.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        setTargetTime = findViewById(R.id.set_target_time_edit_activity);
        setTargetTime.setMinValue(1);
        setTargetTime.setMaxValue(100);
        setTargetTime.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }
}
