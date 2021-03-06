package com.example.android.xrcs.helpers;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.xrcs.R;
import com.example.android.xrcs.data.WorkoutContract;

public class ManageWorkoutsAdapter extends RecyclerView.Adapter<ManageWorkoutsAdapter.WorkoutViewHolder> {
    private Cursor mCursor;
    private Context mContext;

    /**
     * Constructor using the context and the db cursor
     *
     * @param context the calling context/activity
     * @param cursor  the db cursor with workout data to display
     */
    public ManageWorkoutsAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        // Set the local mCursor to be equal to cursor
        this.mCursor = cursor;
    }

    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.workout_item, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WorkoutViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        // Get data from database
        int database_id = mCursor.getInt(mCursor.getColumnIndex("_id"));
        String workout_name = mCursor.getString(mCursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_NAME));
        String workout_type = mCursor.getString(mCursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_TYPE));
        int no_sets = mCursor.getInt(mCursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_NO_SETS));
        int rest_time = mCursor.getInt(mCursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_REST_TIME));
        int no_reps = mCursor.getInt(mCursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_REPS));
        String timed_target_mode = mCursor.getString(mCursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_TIME_TARGET_MODE));
        int target_time = mCursor.getInt(mCursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_TARGET_TIME));
        // Set the database ID in the holder object for later reference
        holder.setDatabaseID(database_id);
        // Update the holder information from database data
        holder.workoutTitleTV.setText(workout_name);
        holder.workoutTypeTV.setText(workout_type);
        holder.noSetsTV.setText(String.valueOf(no_sets));
        holder.restBetweenTV.setText(String.valueOf(rest_time)+"s");
        holder.noRepsTV.setText(String.valueOf(no_reps));
        holder.timedTargetModeTV.setText(timed_target_mode);
        holder.setTargetTimeNumberTV.setText(String.valueOf(target_time)+"s");
        if (timed_target_mode.equals("No Time Target")){
            holder.setTargetTimeNumberTV.setText("---");
            holder.restBetweenTV.setText("---");
        }
    }

    @Override
    public int getItemCount() {
        // getItemCount returns the getCount of the cursor object
        return mCursor.getCount();
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private int databaseID;
        private TextView workoutTitleTV;
        private TextView workoutTypeTV;
        private TextView timedTargetModeTV;
        private TextView noSetsTV;
        private TextView restBetweenTV;
        private TextView noRepsTV;
        private TextView setTargetTimeNumberTV;

        public WorkoutViewHolder(View itemView) {
            super(itemView);
            databaseID = this.databaseID;
            workoutTitleTV = itemView.findViewById(R.id.workout_title_stats_tv);
            workoutTypeTV = itemView.findViewById(R.id.workout_type_stats_tv);
            timedTargetModeTV = itemView.findViewById(R.id.time_target_mode_tv);
            noSetsTV = itemView.findViewById(R.id.no_sets_number_stats_tv);
            restBetweenTV = itemView.findViewById(R.id.rest_between_number_tv);
            noRepsTV = itemView.findViewById(R.id.no_reps_number_tv);
            setTargetTimeNumberTV = itemView.findViewById(R.id.set_target_time_number_tv);
        }
        public int getDatabaseID(){
            return this.databaseID;
        }
        private void setDatabaseID(int newDatabaseID){
            this.databaseID = newDatabaseID;
        }
    }
}
