package com.example.android.xrcs.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WorkoutDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "workouts.db";
    private static final int DATABASE_VERSION = 1;

    public WorkoutDbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WORKOUTS_TABLE = "CREATE TABLE " +
                WorkoutContract.WorkoutEntry.TABLE_NAME + " (" +
                WorkoutContract.WorkoutEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WorkoutContract.WorkoutEntry.COLUMN_EXERCISE_NAME + " TEXT NOT NULL, " +
                WorkoutContract.WorkoutEntry.COLUMN_EXERCISE_TYPE + " TEXT NOT NULL, " +
                WorkoutContract.WorkoutEntry.COLUMN_NO_SETS + " INTEGER NOT NULL, " +
                WorkoutContract.WorkoutEntry.COLUMN_BREAK_TIME + " INTEGER NOT NULL, " +
                WorkoutContract.WorkoutEntry.COLUMN_REPS + " INTEGER NOT NULL, " +
                WorkoutContract.WorkoutEntry.COLUMN_TIMED_TARGET_MODE + " TEXT NOT NULL, " +
                WorkoutContract.WorkoutEntry.COLUMN_TARGET_TIME + " INTEGER NOT NULL, " +
                WorkoutContract.WorkoutEntry.COLUMN_TIMESTAMP +" TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
        sqLiteDatabase.execSQL(SQL_CREATE_WORKOUTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WorkoutContract.WorkoutEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
