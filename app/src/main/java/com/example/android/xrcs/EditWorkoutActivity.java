package com.example.android.xrcs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

import com.example.android.xrcs.data.WorkoutContract;
import com.example.android.xrcs.data.WorkoutDbHelper;

public class EditWorkoutActivity extends AppCompatActivity {
    private EditText workOutName;
    private Switch timeTargetSwitch;
    private NumberPicker exerciseType;
    private NumberPicker noSets;
    private NumberPicker noReps;
    private NumberPicker setRestTime;
    private NumberPicker setTargetTime;
    private SQLiteDatabase mDb;
    private boolean editMode;
    private int databaseID;
    private MenuItem removeWorkoutMenuButton;
    private AlertDialog dialog;

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
        if (extras != null) {
            databaseID = extras.getInt("databaseID");
        }
        editMode = extras != null;

        timeTargetSwitch = findViewById(R.id.time_target_switch_edit_activity);
        setRestTime = findViewById(R.id.set_break_time_edit_activity);
        setTargetTime = findViewById(R.id.set_target_time_edit_activity);
        populateViewWithData(extras);

        // Deactivate / activate time number pickers if time target switch toggled off / on
        timeTargetSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setTargetTime.setEnabled(true);
                    setRestTime.setEnabled(true);
                } else {
                    setTargetTime.setEnabled(false);
                    setRestTime.setEnabled(false);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_workout_menu, menu);
        // Only show the menu "delete workout" button if we're in Edit Mode
        removeWorkoutMenuButton = menu.findItem(R.id.action_remove_workout);
        if (!editMode) {
            removeWorkoutMenuButton.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ContentValues cv = new ContentValues();
        switch (item.getItemId()) {
            case R.id.action_confirm_workout_changes:
                // In case of empty workout name show Toast and return
                if (workOutName.getText().toString().trim().length() == 0) {
                    Toast.makeText(this, "Workout name cannot be empty!",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                cv.put(WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_NAME, workOutName.getText().toString());
                switch (exerciseType.getDisplayedValues()[exerciseType.getValue()]) {
                    case "Pushups":
                        cv.put(WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_TYPE, "Pushups");
                        break;
                    case "Pullups":
                        cv.put(WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_TYPE, "Pullups");
                        break;
                }
                cv.put(WorkoutContract.WorkoutEntry.COLUMN_NO_SETS, noSets.getValue());
                cv.put(WorkoutContract.WorkoutEntry.COLUMN_REST_TIME, setRestTime.getValue());
                cv.put(WorkoutContract.WorkoutEntry.COLUMN_REPS, noReps.getValue());
                if (timeTargetSwitch.isChecked()) {
                    cv.put(WorkoutContract.WorkoutEntry.COLUMN_TIME_TARGET_MODE, "Time Target Mode");
                } else {
                    cv.put(WorkoutContract.WorkoutEntry.COLUMN_TIME_TARGET_MODE, "No Time Target");
                }
                cv.put(WorkoutContract.WorkoutEntry.COLUMN_TARGET_TIME, setTargetTime.getValue());
                if (editMode) {
                    // We're in Edit mode!
                    try {
                        mDb.beginTransaction();
                        mDb.update(WorkoutContract.WorkoutEntry.TABLE_NAME, cv, "_id = " + String.valueOf(databaseID), null);
                        mDb.setTransactionSuccessful();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        mDb.endTransaction();
                        Toast.makeText(this, "Workout updated!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // We're in Add mode!
                    try {
                        mDb.beginTransaction();
                        mDb.insert(WorkoutContract.WorkoutEntry.TABLE_NAME, null, cv);
                        mDb.setTransactionSuccessful();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        mDb.endTransaction();
                        Toast.makeText(this, "New workout added!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                setResult(Activity.RESULT_OK, null);
                finish();
                return true; // Will not be called, but we don't mind
            case R.id.action_remove_workout:
                // Create an alert dialog to show when the remove workout button is clicked
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // Add title and message
                builder.setMessage(R.string.delete_dialog_message).setTitle(R.string.delete_dialog_title);
                // Add the buttons
                builder.setPositiveButton(R.string.confirm_delete_workout_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Delete button
                        String whereClause = "_id=?";
                        String[] whereArgs = new String[]{String.valueOf(databaseID)};
                        mDb.delete(WorkoutContract.WorkoutEntry.TABLE_NAME, whereClause, whereArgs);
                        Toast.makeText(getApplicationContext(), "Workout deleted",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(R.string.cancel_delete_workout_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Toast.makeText(getApplicationContext(), "Delete discarded",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                // Create the AlertDialog
                dialog = builder.create();
                dialog.show();
                return true;
            default:
                Toast.makeText(this, "Changes discarded!",
                        Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }

    }

    public void populateViewWithData(Bundle extras) {
        // Getting references to workout_name tv and time_target_switch
        workOutName = findViewById(R.id.workout_name_edit_activity);
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
        setRestTime.setMinValue(1);
        setRestTime.setMaxValue(6);
        setRestTime.setDisplayedValues(new String[]{"30", "60", "90", "120", "150", "180"});
        setRestTime.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        setTargetTime.setMinValue(1);
        setTargetTime.setMaxValue(100);
        setTargetTime.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        // If the user is in edit mode, query the database to populate the fields with the respective information
        // This means that the user wants to edit/view a workout!
        if (editMode) {
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
            setRestTime.setValue(breakTime);
            setTargetTime.setValue(cursor.getInt(cursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_TARGET_TIME)));
        }
        // Check after checking and potentially updating data in edit mode
        if (timeTargetSwitch.isChecked()) {
            setTargetTime.setActivated(true);
            setRestTime.setActivated(true);
        } else {
            setTargetTime.setActivated(false);
            setRestTime.setActivated(false);
        }
    }
}
