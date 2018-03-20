package com.example.android.xrcs.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "workouts.db";
    private static final int DATABASE_VERSION = 1;

    public WorkoutDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WORKOUTS_TABLE = "CREATE TABLE " +
                WorkoutContract.WorkoutEntry.TABLE_NAME + " (" +
                WorkoutContract.WorkoutEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_NAME + " TEXT NOT NULL, " +
                WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_TYPE + " TEXT NOT NULL, " +
                WorkoutContract.WorkoutEntry.COLUMN_NO_SETS + " INTEGER NOT NULL, " +
                WorkoutContract.WorkoutEntry.COLUMN_REST_TIME + " INTEGER NOT NULL, " +
                WorkoutContract.WorkoutEntry.COLUMN_REPS + " INTEGER NOT NULL, " +
                WorkoutContract.WorkoutEntry.COLUMN_TIMED_TARGET_MODE + " TEXT NOT NULL, " +
                WorkoutContract.WorkoutEntry.COLUMN_TARGET_TIME + " INTEGER NOT NULL, " +
                WorkoutContract.WorkoutEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
        sqLiteDatabase.execSQL(SQL_CREATE_WORKOUTS_TABLE);

        // Populate the database with two exemplary workouts
        List<ContentValues> list = new ArrayList<ContentValues>();
        ContentValues cv = new ContentValues();

        cv.put(WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_NAME, "Pushup Sample Exercise");
        cv.put(WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_TYPE, "Pushups");
        cv.put(WorkoutContract.WorkoutEntry.COLUMN_NO_SETS, 3);
        cv.put(WorkoutContract.WorkoutEntry.COLUMN_REST_TIME, 30);
        cv.put(WorkoutContract.WorkoutEntry.COLUMN_REPS, 10);
        cv.put(WorkoutContract.WorkoutEntry.COLUMN_TIMED_TARGET_MODE, "Timed Target Mode");
        cv.put(WorkoutContract.WorkoutEntry.COLUMN_TARGET_TIME, 10);
        list.add(cv);

        cv = new ContentValues();
        cv.put(WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_NAME, "Pullup Sample Exercise");
        cv.put(WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_TYPE, "Pullups");
        cv.put(WorkoutContract.WorkoutEntry.COLUMN_NO_SETS, 3);
        cv.put(WorkoutContract.WorkoutEntry.COLUMN_REST_TIME, 60);
        cv.put(WorkoutContract.WorkoutEntry.COLUMN_REPS, 7);
        cv.put(WorkoutContract.WorkoutEntry.COLUMN_TIMED_TARGET_MODE, "No Timed Target");
        cv.put(WorkoutContract.WorkoutEntry.COLUMN_TARGET_TIME, 0);
        list.add(cv);

        try {
            sqLiteDatabase.beginTransaction();
            //clear the table first
            sqLiteDatabase.delete(WorkoutContract.WorkoutEntry.TABLE_NAME, null, null);
            //go through the list and add one by one
            for (ContentValues c : list) {
                Log.d("Database Init", c.toString());
                sqLiteDatabase.insert(WorkoutContract.WorkoutEntry.TABLE_NAME, null, c);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("Database Error", "Error printed above!");
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // THIS MEANS THAT DATABASE UPGRADES WILL DELETE THE USERS WORKOUTS!!!
        // TODO: Improve database behavior on onUpgrade
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WorkoutContract.WorkoutEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
