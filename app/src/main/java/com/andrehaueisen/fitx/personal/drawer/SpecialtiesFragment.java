package com.andrehaueisen.fitx.personal.drawer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.personal.firebase.PersonalDatabase;
import com.andrehaueisen.fitx.shared.adapters.SpecialtiesAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by andre on 9/26/2016.
 */

public class SpecialtiesFragment extends Fragment implements ValueEventListener, ProfessionalProfileActivity.OnProfileDataReadyCallback {

    private final String TAG = SpecialtiesFragment.class.getSimpleName();

    private RecyclerView mSpecialtiesRecyclerView;

    private DatabaseReference mDatabase;
    private SpecialtiesAdapter mSpecialtiesAdapter;
    private ArrayList<String> mSpecialties;


    public static SpecialtiesFragment newInstance(){
        return new SpecialtiesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        String personalUniqueKey = Utils.getSharedPreferences(getContext()).getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null);
        mDatabase.child(Constants.FIREBASE_LOCATION_SPECIALTIES).child(personalUniqueKey).child("specialties").addListenerForSingleValueEvent(this);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && !savedInstanceState.isEmpty()){
            mSpecialties = savedInstanceState.getStringArrayList(Constants.SPECIALTIES_SAVED_STATE_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_specialties, container, false);

        mSpecialtiesRecyclerView = (RecyclerView) view.findViewById(R.id.specialties_recycler_view);
        mSpecialtiesRecyclerView.setHasFixedSize(true);
        mSpecialtiesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        setAdapter();
    }

    private void setAdapter(){
        if(mSpecialties == null){
            mSpecialties = new ArrayList<>();
        }

        mSpecialtiesAdapter = new SpecialtiesAdapter(getContext(), getResources().getStringArray(R.array.specialty_types), mSpecialties);
        mSpecialtiesRecyclerView.setAdapter(mSpecialtiesAdapter);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        if(dataSnapshot.exists()) {
            GenericTypeIndicator<ArrayList<String>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<String>>() {};
            mSpecialties.addAll(dataSnapshot.getValue(genericTypeIndicator));
            mSpecialtiesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void saveDataOnDatabase() {
        PersonalDatabase.saveSpecialtiesToDatabase(getContext(), mSpecialtiesAdapter.getSelectedSpecialties(), true);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList( Constants.SPECIALTIES_SAVED_STATE_KEY, mSpecialties);
    }

    @Override
    public void onDestroy() {
        mDatabase.removeEventListener(this);
        super.onDestroy();
    }



}
