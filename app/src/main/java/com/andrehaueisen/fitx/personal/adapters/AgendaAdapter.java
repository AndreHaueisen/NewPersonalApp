package com.andrehaueisen.fitx.personal.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.utilities.Utils;
import com.appyvet.rangebar.RangeBar;

import java.util.ArrayList;

/**
 * Created by andre on 9/19/2016.
 */

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.AgendaViewHolder>{

    private Context mContext;

    private ArrayList<Integer> mTimeCodeListStart;
    private ArrayList<Integer> mTimeCodeListEnd;
    private ArrayList<RangeBar> mRangeBarList = new ArrayList<>();
    private ArrayList<ImageButton> mRemoveBarButtonList = new ArrayList<>();

    private int mBarCount;

    public interface BarCounter{
        int getBarCount();
    }

    public AgendaAdapter(Context context, Fragment scheduleFragment, ArrayList<Integer> timeCodeListStart, ArrayList<Integer> timeCodeListEnd) {
        mContext = context;

        if(timeCodeListStart != null && timeCodeListEnd != null){
            mTimeCodeListStart = timeCodeListStart;
            mTimeCodeListEnd = timeCodeListEnd;
            mBarCount = mTimeCodeListStart.size();
        } else {
            mTimeCodeListStart = new ArrayList<>();
            mTimeCodeListEnd = new ArrayList<>();

            BarCounter barCounter = (BarCounter) scheduleFragment;
            mBarCount = barCounter.getBarCount();
        }
    }

    @Override
    public AgendaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_agenda, parent, false);

        return new AgendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AgendaViewHolder holder, int position) {
        holder.bindAgendaItem(position);
    }

    @Override
    public int getItemCount() {
        return mBarCount;
    }

    public void addBarCount(){
        mBarCount++;
    }

    public ArrayList<Integer> getTimeCodeListStart(){
        return mTimeCodeListStart;
    }

    public ArrayList<Integer> getTimeCodeListEnd(){
        return mTimeCodeListEnd;
    }

    public int hasTimeConflicts(){

        int index = mTimeCodeListStart.size()-1;

        boolean hasConflict;
        int conflictCounter = 0;

        for(int j = index; j >= 0; j--) {
            hasConflict = false;
            for (int i = 1; i <= j; i++) {

                if (mTimeCodeListStart.get(j) >= mTimeCodeListStart.get(j - i) && mTimeCodeListStart.get(j) <= mTimeCodeListEnd.get(j - i)) {
                    hasConflict = true;
                    conflictCounter++;
                }
                if (mTimeCodeListEnd.get(j) >= mTimeCodeListStart.get(j - i) && mTimeCodeListEnd.get(j) <= mTimeCodeListEnd.get(j - i)) {
                    hasConflict = true;
                    conflictCounter++;
                }
                if((mTimeCodeListStart.get(j) <= mTimeCodeListStart.get(j - i) && mTimeCodeListStart.get(j) <= mTimeCodeListEnd.get(j - i))
                        && (mTimeCodeListEnd.get(j) >= mTimeCodeListStart.get(j - i) && mTimeCodeListEnd.get(j) >= mTimeCodeListEnd.get(j - i))){
                    hasConflict = true;
                    conflictCounter++;
                }

            }
            configureBarColors(hasConflict, mRangeBarList.get(j), mRemoveBarButtonList.get(j));
        }

        return conflictCounter;
    }

    private void configureBarColors(boolean hasConflict, RangeBar rangeBar, ImageView removeBarButton){

        //TODO check if there is solution for pin disappearing
        Resources colorResources = mContext.getResources();
        if(hasConflict){
         //   rangeBar.setPinColor(colorResources.getColor(R.color.red));
            rangeBar.setBarColor(colorResources.getColor(R.color.dark_red));
           // rangeBar.setSelectorColor(colorResources.getColor(R.color.dark_red));
            rangeBar.setTickColor(colorResources.getColor(R.color.red));

        }else{
         //   rangeBar.setPinColor(colorResources.getColor(R.color.colorAccent));
            rangeBar.setBarColor(colorResources.getColor(R.color.colorPrimaryDark));
         //   rangeBar.setSelectorColor(colorResources.getColor(R.color.colorPrimary));
            rangeBar.setTickColor(colorResources.getColor(R.color.colorPrimary));
        }
    }

    class AgendaViewHolder extends RecyclerView.ViewHolder implements RangeBar.OnRangeBarChangeListener, View.OnClickListener {

        int mTimeCodeStart = 0;
        int mTimeCodeEnd = 96;

        private RangeBar mRangeBar;
        private ImageButton mRemoveBarButton;
        private TextView mStartTimeTextView;
        private TextView mEndTimeTextView;

        AgendaViewHolder(View itemView) {
            super(itemView);

            mRangeBar = (RangeBar) itemView.findViewById(R.id.agenda_range_bar);
            mRemoveBarButton = (ImageButton) itemView.findViewById(R.id.remove_range_bar_button);
            mStartTimeTextView = (TextView) itemView.findViewById(R.id.start_time_text_view);
            mEndTimeTextView = (TextView) itemView.findViewById(R.id.end_time_text_view);
        }

        private void bindAgendaItem(int position) {

            initializeBars(position);

            mRemoveBarButton.setOnClickListener(this);

            if(mTimeCodeListStart.size() > mRangeBarList.size()){
                mRangeBarList.add(position, mRangeBar);
                mRemoveBarButtonList.add(position, mRemoveBarButton);
            } else {
                organizeTimeCodesForSaving(position);
            }

        }

        private void initializeBars(int position){

            if(mTimeCodeListStart.size() > mRangeBarList.size()) {
                int startPosition = mTimeCodeListStart.get(position);
                int endPosition = mTimeCodeListEnd.get(position);
                mStartTimeTextView.setText(mContext.getString(R.string.starts_at, Utils.getClockFromTimeCode(mContext, startPosition)));
                mEndTimeTextView.setText(mContext.getString(R.string.ends_at, Utils.getClockFromTimeCode(mContext, endPosition)));
                mRangeBar.setRangePinsByIndices(startPosition, endPosition);
            }else {
                mStartTimeTextView.setText(mContext.getString(R.string.starts_at, Utils.getClockFromTimeCode(mContext, mTimeCodeStart)));
                mEndTimeTextView.setText(mContext.getString(R.string.ends_at, Utils.getClockFromTimeCode(mContext, mTimeCodeEnd)));
            }

            mRangeBar.setOnRangeBarChangeListener(this);
            mRangeBar.setPinTextFormatter(new RangeBar.PinTextFormatter() {
                @Override
                public String getText(String value) {

                    int timeCode = Integer.parseInt(value);

                    if(timeCode >= mTimeCodeEnd){
                        mTimeCodeStart = mTimeCodeEnd;
                        mTimeCodeEnd = timeCode;
                    } else {
                        mTimeCodeEnd = timeCode;
                    }

                    return Utils.getClockFromTimeCode(mContext, timeCode);
                }
            });

        }

        private void organizeTimeCodesForSaving(int position){

            if(mTimeCodeListStart.size() <= position) {
                mTimeCodeListStart.add(position, mTimeCodeStart);
                mTimeCodeListEnd.add(position, mTimeCodeEnd);
                mRangeBarList.add(position, mRangeBar);
                mRemoveBarButtonList.add(position, mRemoveBarButton);
            } else {
                mTimeCodeListStart.set(position, mTimeCodeStart);
                mTimeCodeListEnd.set(position, mTimeCodeEnd);
                mRangeBarList.set(position, mRangeBar);
                mRemoveBarButtonList.set(position, mRemoveBarButton);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mTimeCodeListStart.remove(position);
            mTimeCodeListEnd.remove(position);
            mRangeBarList.remove(position);
            mRemoveBarButtonList.remove(position);
            mBarCount--;
            notifyItemRemoved(position);
        }

        @Override
        public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
            organizeTimeCodesForSaving(getLayoutPosition());

            mStartTimeTextView.setText(mContext.getString(R.string.starts_at, Utils.getClockFromTimeCode(mContext, leftPinIndex)));
            mEndTimeTextView.setText(mContext.getString(R.string.ends_at, Utils.getClockFromTimeCode(mContext, rightPinIndex)));
        }
    }
}
