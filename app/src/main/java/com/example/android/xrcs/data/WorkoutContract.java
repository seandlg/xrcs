package com.example.android.xrcs.data;

import android.provider.BaseColumns;

public class WorkoutContract {
    public static final class WorkoutEntry implements BaseColumns{
        public static final String TABLE_NAME = "workouts";
        public static final String COLUMN_EXERCISE_NAME =  "exerciseName";
        public static final String COLUMN_EXERCISE_TYPE =  "exerciseType";
        public static final String COLUMN_NO_SETS = "noOfSets";
        public static final String COLUMN_REST_TIME = "breakTimeBetweenSets";
        public static final String COLUMN_REPS = "repsPerSet";
        public static final String COLUMN_TIMED_TARGET_MODE = "timedTargetModeBool";
        public static final String COLUMN_TARGET_TIME = "targetTime";
        public static final String COLUMN_TIMESTAMP = "timeStamp";
    }
}
