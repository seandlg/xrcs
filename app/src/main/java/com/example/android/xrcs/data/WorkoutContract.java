package com.example.android.xrcs.data;

import android.provider.BaseColumns;

public class WorkoutContract {
    public static final class WorkoutEntry implements BaseColumns{
        public static final String TABLE_NAME = "workouts";
        public static final String COLUMN_WORKOUT_NAME =  "exerciseName";
        public static final String COLUMN_WORKOUT_TYPE =  "exerciseType";
        public static final String COLUMN_NO_SETS = "noOfSets";
        public static final String COLUMN_REST_TIME = "restTimeBetweenSets";
        public static final String COLUMN_REPS = "repsPerSet";
        public static final String COLUMN_TIME_TARGET_MODE = "timeTargetModeBool";
        public static final String COLUMN_TARGET_TIME = "targetTime";
        public static final String COLUMN_TIMESTAMP = "timeStamp";
    }
    public static final class WorkoutLog implements BaseColumns{
        public static final String TABLE_NAME = "workoutLog";
        public static final String COLUMN_WORKOUT_NAME =  "exerciseName";
        public static final String COLUMN_WORKOUT_TYPE =  "exerciseType";
        public static final String COLUMN_NO_SETS = "noOfSets";
        public static final String COLUMN_REST_TIME = "restTimeBetweenSets";
        public static final String COLUMN_REPS = "repsPerSet";
        public static final String COLUMN_TIME_TARGET_MODE = "timeTargetModeBool";
        public static final String COLUMN_TARGET_TIME = "targetTime";
        public static final String COLUMN_REP_TIMES =  "repTimes";
        public static final String COLUMN_TIMESTAMP = "timeStamp";
    }
}
