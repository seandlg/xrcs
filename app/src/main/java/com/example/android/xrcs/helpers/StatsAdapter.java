package com.example.android.xrcs.helpers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.xrcs.GraphActivity;
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
    private int workoutNameIndex;
    private int workoutTypeIndex;
    private int noSetsIndex;
    private int restTimeIndex;
    private int noRepsIndex;
    private int timeTargetModeIndex;
    private int targetTimeIndex;
    private int repTimesIndex;
    private int timeIndex;

    public StatsAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
        this.gson = new Gson();
        this.workoutNameIndex = mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_WORKOUT_NAME);
        this.workoutTypeIndex = mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_WORKOUT_TYPE);
        this.noSetsIndex = mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_NO_SETS);
        this.restTimeIndex = mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_REST_TIME);
        this.noRepsIndex = mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_REPS);
        this.timeTargetModeIndex = mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_TIME_TARGET_MODE);
        this.targetTimeIndex = mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_TARGET_TIME);
        this.repTimesIndex = mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_REP_TIMES);
        this.timeIndex = mCursor.getColumnIndex(WorkoutContract.WorkoutLog.COLUMN_TIMESTAMP);
    }

    @Override
    public StatsAdapter.StatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.stats_item, parent, false);
        return new StatsAdapter.StatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StatsAdapter.StatsViewHolder holder, final int position) {
        mCursor.moveToPosition(position);
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null
        // Get data from database
        int databaseID = mCursor.getInt(mCursor.getColumnIndex("_id"));
        String workoutName = mCursor.getString(workoutNameIndex);
        String workoutType = mCursor.getString(workoutTypeIndex);
        int setTargetInt = mCursor.getInt(noSetsIndex);
        int restBetweenInt = mCursor.getInt(restTimeIndex);
        int noRepsInt = mCursor.getInt(noRepsIndex);
        String timedTargetMode = mCursor.getString(timeTargetModeIndex);
        boolean timeTarget = timedTargetMode.equals("Time Target Mode");
        int targetTimeInt = mCursor.getInt(targetTimeIndex);
        String timeStamp = mCursor.getString(timeIndex);
        holder.timeOfWorkoutCompletionStats.setText(timeStamp);
        Log.d("TIME",timeStamp);
        Type type = new TypeToken<ArrayList<Long>>() {
        }.getType();
        ArrayList<Long> repTimesLongList = this.gson.fromJson(mCursor.getString(repTimesIndex), type);
        holder.setRepTimesLongList(repTimesLongList); // Set the repTimesLongList in the holder object for later reference
        long startSecond = repTimesLongList.get(0) / 1000;
        long endSecond = repTimesLongList.get(repTimesLongList.size() - 1) / 1000;
        long totalDuration = endSecond - startSecond;
        long effectiveWorkoutTime;
        long effectiveSetRestTime;
        Long setTarget = Long.valueOf(setTargetInt);
        Long restBetween = Long.valueOf(restBetweenInt);
        Long targetTimePerSet = Long.valueOf(targetTimeInt);
        Long workoutTargetTime = setTarget * targetTimePerSet;
        if (timeTarget) {
            effectiveSetRestTime = (setTarget - 1) * (restBetween); // No break after last set
            effectiveWorkoutTime = totalDuration - effectiveSetRestTime;
        } else {
            // TODO calculate effective Set rest time
            // Long no_pauses = setTarget - 1;
            effectiveSetRestTime = 0;
            effectiveWorkoutTime = totalDuration;
        }
        // Update the holder insights information from the calculated data
        holder.actualWorkoutTimeStats.setText(String.valueOf(effectiveWorkoutTime) + "s");
        holder.workoutTargetTimeStats.setText(String.valueOf(workoutTargetTime) + "s");
        holder.totalRestTimeStats.setText(String.valueOf(effectiveSetRestTime) + "s");
        double timeTakenRatio = ((double) (effectiveWorkoutTime)) / ((double) workoutTargetTime);
        if (timeTakenRatio < 0.8) {
            holder.sentimentTextStats.setText("Well below target time!");
            holder.sentimentImageStats.setImageResource(R.drawable.ic_sentiment_satisfied_black_24dp);
        } else if (timeTakenRatio > 1.2) {
            holder.sentimentTextStats.setText("Well above target time!");
            holder.sentimentImageStats.setImageResource(R.drawable.ic_sentiment_neutral_black_24dp);
        } else {
            holder.sentimentTextStats.setText("Great! You're within target time!");
            holder.sentimentImageStats.setImageResource(R.drawable.ic_sentiment_very_satisfied_black_24dp);
        }
        // Update the holder workout information from database data
        holder.workoutTitleTV.setText(workoutName);
        holder.workoutTypeTV.setText(workoutType);
        holder.noSetsTV.setText(String.valueOf(setTargetInt));
        holder.restBetweenTV.setText(String.valueOf(restBetweenInt) + "s");
        holder.noRepsTV.setText(String.valueOf(noRepsInt));
        holder.timedTargetModeTV.setText(timedTargetMode);
        holder.setTargetTimeNumberTV.setText(String.valueOf(targetTimeInt) + "s");
        if (timedTargetMode.equals("No Time Target")) {
            holder.setTargetTimeNumberTV.setText("---");
            holder.restBetweenTV.setText("---");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent graphIntent = new Intent(mContext, GraphActivity.class);
                graphIntent.putExtra("repTimes", gson.toJson(holder.getRepsList()));
                mContext.startActivity(graphIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // getItemCount returns the getCount of the cursor object
        return mCursor.getCount();
    }

    public class StatsViewHolder extends RecyclerView.ViewHolder {
        private ArrayList<Long> repTimesLongList;
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
        private TextView timeOfWorkoutCompletionStats;

        public StatsViewHolder(View itemView) {
            super(itemView);
            workoutTitleTV = itemView.findViewById(R.id.workout_title_stats_tv);
            workoutTypeTV = itemView.findViewById(R.id.workout_type_stats_tv);
            timedTargetModeTV = itemView.findViewById(R.id.time_target_mode_stats_tv);
            noSetsTV = itemView.findViewById(R.id.no_sets_number_stats_tv);
            restBetweenTV = itemView.findViewById(R.id.rest_between_number_stats_tv);
            noRepsTV = itemView.findViewById(R.id.no_reps_number_stats_tv);
            setTargetTimeNumberTV = itemView.findViewById(R.id.set_target_time_number_stats_tv);
            actualWorkoutTimeStats = itemView.findViewById(R.id.actual_workout_time_stats);
            workoutTargetTimeStats = itemView.findViewById(R.id.workout_target_time_stats);
            totalRestTimeStats = itemView.findViewById(R.id.total_rest_time_stats_tv);
            sentimentImageStats = itemView.findViewById(R.id.sentiment_view_stats);
            sentimentTextStats = itemView.findViewById(R.id.sentiment_text_stats);
            timeOfWorkoutCompletionStats = itemView.findViewById(R.id.date_stats_tv);
        }

        public ArrayList<Long> getRepsList() {
            return this.repTimesLongList;
        }

        private void setRepTimesLongList(ArrayList<Long> newRepTimesLongList) {
            this.repTimesLongList = newRepTimesLongList;
        }
    }
}
