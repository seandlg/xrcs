package com.example.android.xrcs;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import com.example.android.xrcs.data.WorkoutContract;
import com.example.android.xrcs.data.WorkoutDbHelper;

import java.util.ArrayList;
import java.util.List;

public class WorkOutFragment extends Fragment {
    private NumberPicker workOutNumberPicker;
    private Button startWorkoutButton;
    private SQLiteDatabase mDb;
    private TensorFlowInferenceInterface inferenceInterface;
    // Tensorflow Constants
    private static final String MODEL_FILE = "file:///android_asset/frozen_model_EXAMPLE.pb";
    private static final String INPUT_NODE = "input";
    private static final String[] OUTPUT_NODE = new String[] {"output"};
    private static final int INPUT_SIZE = 5;
    private static int[] myans;

    public WorkOutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create a DB helper (this will create the DB if run for the first time)
        WorkoutDbHelper dbHelper = new WorkoutDbHelper(getActivity());
        mDb = dbHelper.getWritableDatabase();
        inferenceInterface = new TensorFlowInferenceInterface(getActivity().getAssets(), MODEL_FILE);
        System.out.println("Model loaded successfully!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_work_out_layout, container, false);
        workOutNumberPicker = rootView.findViewById(R.id.work_out_number_picker);
        // Populate the NumberPicker from the database
        Cursor cursor = mDb.query(WorkoutContract.WorkoutEntry.TABLE_NAME, new String[]{WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_NAME},null,null,null,null,WorkoutContract.WorkoutEntry.COLUMN_TIMESTAMP);
        int noWorkouts = cursor.getCount();
        List<String> displayedElementsList = new ArrayList<>();
        while(cursor.moveToNext()){
            displayedElementsList.add(cursor.getString(cursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_NAME)));
        }
        String[] displayedElements = new String[noWorkouts];
        displayedElements = displayedElementsList.toArray(displayedElements);
        workOutNumberPicker.setMinValue(0);
        workOutNumberPicker.setMaxValue(noWorkouts-1);
        workOutNumberPicker.setDisplayedValues(displayedElements);
        workOutNumberPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        startWorkoutButton = rootView.findViewById(R.id.start_workout_button);

        startWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Initializing workout (or not)!",
                        Toast.LENGTH_SHORT).show();
                //inferenceInterface.feed(INPUT_NODE, INPUT_SIZ   E, 5);
                //inferenceInterface.run(OUTPUT_NODE);
                //inferenceInterface.fetch("output",myans);
                //Log.d("COMPUTEDINTF",myans.toString());
            }
        });
        return rootView;
    }
}
