package com.andrehaueisen.fitx.personal.drawer;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.personal.adapters.WorkingTimesAdapter;
import com.andrehaueisen.fitx.personal.firebase.PersonalDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeekScheduleResumeFragment extends Fragment implements ValueEventListener {

    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseReference;

    public static Fragment newInstance() {
        return new WeekScheduleResumeFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        String personalKey = Utils.getSharedPreferences(getContext()).getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child(Constants.FIREBASE_LOCATION_AGENDA).child(personalKey).addValueEventListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_week_schedule_resume, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.week_days_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        if (dataSnapshot.exists()) {
            SparseArray<ArrayList<Integer>> agendaTimeCodesEndSA = new SparseArray<>();
            SparseArray<ArrayList<Integer>> agendaTimeCodesStartSA = new SparseArray<>();

            ArrayList<Integer> agendaTimeCodesEnd;
            ArrayList<Integer> agendaTimeCodesStart;

            GenericTypeIndicator<ArrayList<Integer>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Integer>>() {};
            String weekDay;

            for (int i = 0; i < 7; i++) {

                weekDay = PersonalDatabase.getWeekDayTitle(i);

                agendaTimeCodesEnd = dataSnapshot.child(weekDay).child(Constants.AGENDA_CODES_END_LIST).getValue(genericTypeIndicator);
                if (agendaTimeCodesEnd != null) {
                    agendaTimeCodesEndSA.put(i, agendaTimeCodesEnd);
                }

                agendaTimeCodesStart = dataSnapshot.child(weekDay).child(Constants.AGENDA_CODES_START_LIST).getValue(genericTypeIndicator);
                if (agendaTimeCodesStart != null) {
                    agendaTimeCodesStartSA.put(i, agendaTimeCodesStart);
                }
            }

            mRecyclerView.setAdapter(new WeekdayAdapter(agendaTimeCodesStartSA, agendaTimeCodesEndSA));
        }
    }

    @Override
    public void onStop() {
        mDatabaseReference.removeEventListener(this);
        super.onStop();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    class WeekdayAdapter extends RecyclerView.Adapter<WeekdayAdapter.WeekDayScheduleViewHolder>  {

        private SparseArray<ArrayList<Integer>> mAgendaTimeCodesStartSA;
        private SparseArray<ArrayList<Integer>> mAgendaTimeCodesEndSA;

        public WeekdayAdapter(SparseArray<ArrayList<Integer>> agendaTimeCodesStartSA, SparseArray<ArrayList<Integer>> agendaTimeCodesEndSA) {

            mAgendaTimeCodesStartSA = agendaTimeCodesStartSA;
            mAgendaTimeCodesEndSA = agendaTimeCodesEndSA;
        }

        @Override
        public WeekDayScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_week_day_schedule, parent, false);
            return new WeekDayScheduleViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(WeekDayScheduleViewHolder holder, int position) {

            TextDrawable textDrawable = TextDrawable.builder().beginConfig().toUpperCase().fontSize(40)
                    .textColor(Color.WHITE)
                    .endConfig().buildRound(getDayText(position), getResources().getColor(R.color.colorPrimaryDark));

            holder.mDayImageView.setImageDrawable(textDrawable);
            holder.onBindItem(mAgendaTimeCodesStartSA.get(position), mAgendaTimeCodesEndSA.get(position));

        }

        @Override
        public int getItemCount() {
            return 7;
        }

        private String getDayText(int weekDay) {
            return getResources().getStringArray(R.array.personal_week_days_names)[weekDay];
        }

        protected class WeekDayScheduleViewHolder extends RecyclerView.ViewHolder{

            private ImageView mDayImageView;
            private RecyclerView mRecyclerView;

            public WeekDayScheduleViewHolder(View itemView) {
                super(itemView);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

                mDayImageView = (ImageView) itemView.findViewById(R.id.day_image_view);
                mRecyclerView = (RecyclerView) itemView.findViewById(R.id.times_recycler_view);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(layoutManager);

            }

            private void onBindItem(ArrayList<Integer> timesStart, ArrayList<Integer> timesEnd){
                mRecyclerView.setAdapter(new WorkingTimesAdapter(getContext(), timesStart, timesEnd));
            }
        }
    }


}
