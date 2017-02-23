package com.andrehaueisen.fitx.personal.drawer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.personal.adapters.AgendaAdapter;
import com.andrehaueisen.fitx.personal.firebase.PersonalDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

/**
 * Created by andre on 9/18/2016.
 */

public class ScheduleArrangementFragment extends Fragment implements AgendaAdapter.BarCounter, ValueEventListener{

    private AgendaAdapter mAgendaAdapter;
    private RecyclerView mRangeBarRecyclerView;

    private int mWeekDay;
    private int mBarCount = 0;

    @Override
    public int getBarCount() {
        return mBarCount;
    }

    public static ScheduleArrangementFragment newInstance(Bundle bundle) {
        bundle.getInt(Constants.WEEK_DAY_BUNDLE_KEY, 0);

        ScheduleArrangementFragment fragment = new ScheduleArrangementFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String personalKey = Utils.getSharedPreferences(getContext()).getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.FIREBASE_LOCATION_AGENDA).child(personalKey).addListenerForSingleValueEvent(this);

        mWeekDay = getArguments().getInt(Constants.WEEK_DAY_BUNDLE_KEY);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        ArrayList<Integer> agendaTimeCodesEnd = new ArrayList<>();
        ArrayList<Integer> agendaTimeCodesStart = new ArrayList<>();

        if (dataSnapshot.exists()) {

            GenericTypeIndicator<ArrayList<Integer>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Integer>>() {};
            String weekDay;

            weekDay = PersonalDatabase.getWeekDayTitle(mWeekDay);

            agendaTimeCodesStart = dataSnapshot.child(weekDay).child(Constants.AGENDA_CODES_START_LIST).getValue(genericTypeIndicator);
            agendaTimeCodesEnd = dataSnapshot.child(weekDay).child(Constants.AGENDA_CODES_END_LIST).getValue(genericTypeIndicator);
        }

        mAgendaAdapter = new AgendaAdapter(getContext(), this, agendaTimeCodesStart, agendaTimeCodesEnd);
        mRangeBarRecyclerView.setAdapter(mAgendaAdapter);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule_arrangement, container, false);

        Activity activity = getActivity();
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if( actionBar != null ) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mRangeBarRecyclerView = (RecyclerView) view.findViewById(R.id.range_bar_recycler_view);

        mRangeBarRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRangeBarRecyclerView.setItemAnimator(new SlideInRightAnimator());

        FloatingActionButton addRangeBarFAB = (FloatingActionButton) view.findViewById(R.id.add_range_bar_fab);
        addRangeBarFAB.setOnClickListener(addRangeBarClickListener);

        String[] weekDays = getResources().getStringArray(R.array.week_days);
        TextView weekDayTextView = (TextView) view.findViewById(R.id.week_day_text_view);
        weekDayTextView.setText(weekDays[mWeekDay]);

        return view;
    }

    View.OnClickListener addRangeBarClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if(mRangeBarRecyclerView.getAdapter() == null){
                mRangeBarRecyclerView.setAdapter(mAgendaAdapter);
            }
            mBarCount++;
            mAgendaAdapter.addBarCount();
            mAgendaAdapter.notifyItemInserted(mAgendaAdapter.getItemCount()+1);
        }
    };

    public boolean hasTimeConflicts(){

        String[] weekDays = getResources().getStringArray(R.array.week_days);
        if (mAgendaAdapter.hasTimeConflicts() != 0) {
            Utils.generateWarningToast(getContext(), getString(R.string.fix_time_conflicts, weekDays[mWeekDay])).show();
            return true;
        }else {
            return false;
        }
    }

    public AgendaAdapter getScheduleAdapter(){
        return mAgendaAdapter;
    }
}