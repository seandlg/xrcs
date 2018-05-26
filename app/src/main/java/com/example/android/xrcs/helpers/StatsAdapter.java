package com.example.android.xrcs.helpers;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.xrcs.R;
import com.example.android.xrcs.data.WorkoutContract;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatsViewHolder> {
    private Cursor mCursor;
    private Context mContext;
    private Gson gson;

    public StatsAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
        this.gson = new Gson();
    }

    @Override
    public StatsAdapter.StatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.stats_item, parent, false);
        return new StatsAdapter.StatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StatsAdapter.StatsViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        // Get data from database
        int database_id = mCursor.getInt(mCursor.getColumnIndex("_id"));
        String workout_name = mCursor.getString(mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_WORKOUT_NAME));
        String workout_type = mCursor.getString(mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_WORKOUT_TYPE));
        int no_sets = mCursor.getInt(mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_NO_SETS));
        int rest_time = mCursor.getInt(mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_REST_TIME));
        int no_reps = mCursor.getInt(mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_REPS));
        String timed_target_mode = mCursor.getString(mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_TIME_TARGET_MODE));
        int target_time = mCursor.getInt(mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_TARGET_TIME));
        // Set the database ID in the holder object for later reference
        holder.setDatabaseID(database_id);
        // Get the rep information
        Type type = new TypeToken<ArrayList<Long>>() {
        }.getType();
        ArrayList<Long> repTimesLongList = gson.fromJson(mCursor.getString((mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_REP_TIMES))), type);
        long startSecond = repTimesLongList.get(0) / 1000;
        long endSecond = repTimesLongList.get(repTimesLongList.size() - 1) / 1000;
        long totalDuration = endSecond - startSecond;
        long effectiveWorkoutTime;
        long effectiveSetRestTime;
        boolean timeTarget = mCursor.getString(mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_TIME_TARGET_MODE)).equals("Time Target Mode");
        Long setTarget = Long.valueOf(mCursor.getString(mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_NO_SETS)));
        Long restBetween = Long.valueOf(mCursor.getString(mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_REST_TIME)));
        Long numberOfRepsPerSet = Long.valueOf(no_reps);
        Long targetTimePerSet = Long.valueOf(target_time);
        if (timeTarget) {
            effectiveSetRestTime = (setTarget - 1) * (restBetween); // No break after last set
            effectiveWorkoutTime = totalDuration - effectiveSetRestTime;
        } else {
            // Long no_pauses = setTarget - 1;
            // TODO calculate effective Set rest time
            effectiveSetRestTime = 0;
            effectiveWorkoutTime = totalDuration;
        }
        // Update the holder insights information from the calculated data
        Long workoutTargetTime = setTarget * targetTimePerSet;
        holder.actualWorkoutTimeStats.setText(String.valueOf(effectiveWorkoutTime));
        holder.workoutTargetTimeStats.setText(String.valueOf(workoutTargetTime));
        holder.totalRestTimeStats.setText(String.valueOf(effectiveSetRestTime));
        double timeTakenRatio = ((double) (effectiveWorkoutTime)) / ((double) workoutTargetTime);
        if (timeTakenRatio<0.8){
            holder.sentimentTextStats.setText("Well below target time");
            //holder.sentimentImageStats.setImageURI();
        } else if (timeTakenRatio>1.2){
            holder.sentimentTextStats.setText("Well above target time");
        } else{
            holder.sentimentTextStats.setText("Mostly within target time");
        }
        // Update the holder workout information from database data
        holder.workoutTitleTV.setText(workout_name);
        holder.workoutTypeTV.setText(workout_type);
        holder.noSetsTV.setText(String.valueOf(no_sets));
        holder.restBetweenTV.setText(String.valueOf(rest_time) + "s");
        holder.noRepsTV.setText(String.valueOf(no_reps));
        holder.timedTargetModeTV.setText(timed_target_mode);
        holder.setTargetTimeNumberTV.setText(String.valueOf(target_time) + "s");
        if (timed_target_mode.equals("No Time Target")) {
            holder.setTargetTimeNumberTV.setText("---");
            holder.restBetweenTV.setText("---");
        }
    }

    @Override
    public int getItemCount() {
        // getItemCount returns the getCount of the cursor object
        return mCursor.getCount();
    }

    public class StatsViewHolder extends RecyclerView.ViewHolder {
        private int databaseID;
        private TextView workoutTitleTV;
        private TextView workoutTypeTV;
        private TextView timedTargetModeTV;
        private TextView noSetsTV;
        private TextView restBetweenTV;
        private TextView noRepsTV;
        private TextView setTargetTimeNumberTV;
        private TextView actualWorkoutTimeStats;
        private TextView workoutTargetTimeStats;
        private TextView totalRestTimeStats;
        private ImageView sentimentImageStats;
        private TextView sentimentTextStats;

        public StatsViewHolder(View itemView) {
            super(itemView);
            databaseID = this.databaseID;
            workoutTitleTV = itemView.findViewById(R.id.workout_title_stats_tv);
            workoutTypeTV = itemView.findViewById(R.id.workout_type_stats_tv);
            timedTargetModeTV = itemView.findViewById(R.id.time_target_mode_stats_tv);
            noSetsTV = itemView.findViewById(R.id.no_sets_number_stats_tv);
            restBetweenTV = itemView.findViewById(R.id.rest_between_stats_tv);
            noRepsTV = itemView.findViewById(R.id.no_reps_number_stats_tv);
            setTargetTimeNumberTV = itemView.findViewById(R.id.set_target_time_number_stats_tv);
            actualWorkoutTimeStats = itemView.findViewById(R.id.actual_workout_time_stats);
            workoutTargetTimeStats = itemView.findViewById(R.id.workout_target_time_stats);
            totalRestTimeStats = itemView.findViewById(R.id.total_rest_time_stats_tv);
            sentimentImageStats = itemView.findViewById(R.id.sentiment_view_stats);
            sentimentTextStats = itemView.findViewById(R.id.sentiment_text_stats);
        }

        public int getDatabaseID() {
            return this.databaseID;
        }

        private void setDatabaseID(int newDatabaseID) {
            this.databaseID = newDatabaseID;
        }
    }
}
