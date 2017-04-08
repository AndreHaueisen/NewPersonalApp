package com.andrehaueisen.fitx.personal.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.utilities.Utils;

import java.util.ArrayList;

/**
 * Created by andre on 11/24/2016.
 */

public class WorkingTimesAdapter extends RecyclerView.Adapter<WorkingTimesAdapter.TimeViewHolder> {

    private Context mContext;
    private ArrayList<Integer> mStartTimes;
    private ArrayList<Integer> mEndTimes;

    public WorkingTimesAdapter(Context context, ArrayList<Integer> startTimes, ArrayList<Integer> endTimes) {
        mContext = context;
        mStartTimes = startTimes;
        mEndTimes = endTimes;
    }

    @Override
    public WorkingTimesAdapter.TimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_simple_time, parent, false);

        return new TimeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WorkingTimesAdapter.TimeViewHolder holder, int position) {

        holder.mTimeTextView.setText(mContext.getString(R.string.class_duration, Utils.getClockFromTimeCode(mContext, mStartTimes.get(position)),
                Utils.getClockFromTimeCode(mContext, mEndTimes.get(position))));

    }

    @Override
    public int getItemCount() {
        if(mStartTimes != null) {
            return mStartTimes.size();
        }else {
            return 0;
        }
    }

    protected class TimeViewHolder extends RecyclerView.ViewHolder{

        private TextView mTimeTextView;

        public TimeViewHolder(View itemView) {
            super(itemView);
            mTimeTextView = (TextView) itemView.findViewById(R.id.time_text_view);
            mTimeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
