package com.example.android.xrcs;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.example.android.xrcs.data.WorkoutContract;
import com.example.android.xrcs.data.WorkoutDbHelper;

import org.w3c.dom.Text;

public class editWorkoutActivity extends AppCompatActivity {
    private EditText workOutName;
    private Switch timeTargetSwitch;
    private NumberPicker exerciseType;
    private NumberPicker noSets;
    private NumberPicker noReps;
    private NumberPicker setBreakTime;
    private NumberPicker setTargetTime;
    private SQLiteDatabase mDb;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workout);
        getSupportActionBar().setTitle("Manage workouts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Configure database access
        WorkoutDbHelper dbHelper = new WorkoutDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        // Setting boolean whether activity in Edit Mode or Add Mode based on whether additional data has been passed to the activity
        Bundle extras = getIntent().getExtras();
        editMode = extras != null;
        populateView(extras);
    }

    public void populateView(Bundle extras) {
        // Getting references to workout_name tv and time_target_switch
        workOutName = findViewById(R.id.workout_name_edit_activity);
        timeTargetSwitch = findViewById(R.id.time_target_switch_edit_activity);
        // Getting references to and configuring the number pickers
        exerciseType = findViewById(R.id.exercise_type_edit_activity);
        exerciseType.setMinValue(0);
        exerciseType.setMaxValue(1);
        exerciseType.setDisplayedValues(new String[]{"Pushups", "Pullups"});
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
        setBreakTime.setDisplayedValues(new String[]{"30", "60", "90", "120", "150", "180"});
        setBreakTime.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        setTargetTime = findViewById(R.id.set_target_time_edit_activity);
        setTargetTime.setMinValue(1);
        setTargetTime.setMaxValue(100);
        setTargetTime.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        timeTargetSwitch = findViewById(R.id.time_target_switch_edit_activity);
        // If the user is in edit mode, query the database to populate the fields with the respective information
        // This means that the user wants to edit/view a workout!
        if (editMode) {
            int databaseID = extras.getInt("databaseID");
            Cursor cursor = mDb.rawQuery("SELECT * FROM " + WorkoutContract.WorkoutEntry.TABLE_NAME + " WHERE _id = " + databaseID, null);
            Log.d("Database", DatabaseUtils.dumpCursorToString(cursor));
            cursor.moveToFirst();
            // Populate all fields with data from the database!
            workOutName.setText(cursor.getString(cursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_NAME)));
            switch (cursor.getString(cursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_TYPE))) {
                case "Pushups":
                    exerciseType.setValue(0);
                    break;
                case "Pullups":
                    exerciseType.setValue(1);
                    break;
                default:
                    throw new RuntimeException("Database error! Database should only contain WORKOUT_TYPES of 'Pushups' or 'Pullups'");
            }
            switch (cursor.getString(cursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_TIME_TARGET_MODE))) {
                case "Time Target Mode":
                    timeTargetSwitch.setChecked(true);
                    break;
                case "No Time Target":
                    timeTargetSwitch.setChecked(false);
                    break;
                default:
                    throw new RuntimeException("Database error! Database should only contain TIME_TARGET_MODES of 'Time Target Mode' or 'No Time Target'");
            }
            noSets.setValue(cursor.getInt(cursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_NO_SETS)));
            noReps.setValue(cursor.getInt(cursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_REPS)));
            int breakTime = cursor.getInt(cursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_REST_TIME)) / 30; // Moving in steps of 30, yet index is integer
            setBreakTime.setValue(breakTime);
            setTargetTime.setValue(cursor.getInt(cursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_TARGET_TIME)));
        }
    }
}
