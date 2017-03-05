package com.andrehaueisen.fitx.personal;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.client.firebase.FirebaseImageCatcher;
import com.andrehaueisen.fitx.personal.adapters.PersonalClassesAdapter;
import com.andrehaueisen.fitx.models.PersonalFitClass;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jetradar.desertplaceholder.DesertPlaceholder;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToBeConfirmedClassesFragment extends Fragment implements ChildEventListener, FirebaseImageCatcher.FirebaseCallback {

    private static final String TAG = ToBeConfirmedClassesFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private DesertPlaceholder mNoClassesPlaceHolder;
    private DatabaseReference mDatabaseReference;
    private PersonalClassesAdapter mAdapter;
    private ArrayList<PersonalFitClass> mWaitingConfirmationPersonalFitClasses;
    private FirebaseImageCatcher mImageCatcher;

    public static ToBeConfirmedClassesFragment newInstance() {
        return new ToBeConfirmedClassesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        String personalKey = Utils.getSharedPreferences(getContext()).getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_LOCATION_PERSONAL_CLASSES).child(personalKey);
        mDatabaseReference.addChildEventListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_to_be_confirmed_classes, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.upcoming_classes_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new SlideInRightAnimator());

        mNoClassesPlaceHolder = (DesertPlaceholder) view.findViewById(R.id.no_class_confirmed_place_holder);
        mNoClassesPlaceHolder.setMessage(getString(R.string.no_class_scheduled_message));

        mImageCatcher = new FirebaseImageCatcher(this);

        if(savedInstanceState != null){
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(Constants.RECYCLER_VIEW_SAVED_STATE_KEY));
            mWaitingConfirmationPersonalFitClasses = savedInstanceState.getParcelableArrayList(Constants.NOT_CONFIRMED_PERSONALS_CLASSES_SAVED_STATE_KEY);
            if(mWaitingConfirmationPersonalFitClasses!= null && !mWaitingConfirmationPersonalFitClasses.isEmpty()){
                mNoClassesPlaceHolder.setVisibility(View.GONE);
            }
        }

        changeStatus();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mWaitingConfirmationPersonalFitClasses == null) {
            mWaitingConfirmationPersonalFitClasses = new ArrayList<>();
        }

        mAdapter = new PersonalClassesAdapter(this, mWaitingConfirmationPersonalFitClasses);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        PersonalFitClass personalFitClass = dataSnapshot.getValue(PersonalFitClass.class);

        if (!personalFitClass.isConfirmed()) {
            mWaitingConfirmationPersonalFitClasses.add(personalFitClass);
            mImageCatcher.getClientProfilePicture(getActivity(), personalFitClass.getClientKey(), mWaitingConfirmationPersonalFitClasses.size() - 1);
            mAdapter.notifyItemInserted(mWaitingConfirmationPersonalFitClasses.size());
        }

        if(mWaitingConfirmationPersonalFitClasses.size() == 1)
            changeStatus();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        //TODO check
        PersonalFitClass personalFitClass = dataSnapshot.getValue(PersonalFitClass.class);

        if (!personalFitClass.isConfirmed()) {
            for (int i = 0; i < mWaitingConfirmationPersonalFitClasses.size(); i++) {
                if (personalFitClass.getClassKey().equals(mWaitingConfirmationPersonalFitClasses.get(i).getClassKey())) {
                    mWaitingConfirmationPersonalFitClasses.set(i, personalFitClass);
                    mAdapter.notifyItemChanged(i);
                    break;
                }
            }
        } else {
        for (int i = 0; i < mWaitingConfirmationPersonalFitClasses.size(); i++) {
            if (personalFitClass.getClassKey().equals(mWaitingConfirmationPersonalFitClasses.get(i).getClassKey())) {
                mWaitingConfirmationPersonalFitClasses.remove(i);
                mAdapter.notifyItemRemoved(i);
                changeStatus();
                break;
            }
        }
    }

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

        PersonalFitClass personalFitClass = dataSnapshot.getValue(PersonalFitClass.class);

        if (!personalFitClass.isConfirmed()) {
            for (int i = 0; i < mWaitingConfirmationPersonalFitClasses.size(); i++) {
                if (personalFitClass.getClassKey().equals(mWaitingConfirmationPersonalFitClasses.get(i).getClassKey())) {
                    mWaitingConfirmationPersonalFitClasses.remove(i);
                    mAdapter.notifyItemRemoved(i);
                    changeStatus();
                }
            }
        }

    }

    private void changeStatus() {

        if (mWaitingConfirmationPersonalFitClasses != null && mWaitingConfirmationPersonalFitClasses.size() != 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoClassesPlaceHolder.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mNoClassesPlaceHolder.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Constants.RECYCLER_VIEW_SAVED_STATE_KEY, mRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArrayList(Constants.NOT_CONFIRMED_PERSONALS_CLASSES_SAVED_STATE_KEY, mWaitingConfirmationPersonalFitClasses);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        mDatabaseReference.removeEventListener(this);
        super.onDestroy();
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private class LoadImageTask extends AsyncTask<byte[], Void, Void> {

        private int mPositionOnArray;

        LoadImageTask(int positionOnArray){
            mPositionOnArray = positionOnArray;
        }

        @Override
        protected Void doInBackground(byte[]... params) {

            byte[] image = params[0];
            try {
                if (image != null && image.length != 0 && mPositionOnArray > -1) {
                    mWaitingConfirmationPersonalFitClasses.get(mPositionOnArray).setClassProfileImage(Glide.with(getContext()).load(image).asBitmap().into
                            (100, 100).get());
                }
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyItemChanged(mPositionOnArray);
        }
    }

    @Override
    public void onProfileImageReady(byte[] personProfileImage, int positionOnArray) {
        new LoadImageTask(positionOnArray).execute(personProfileImage);
    }

    @Override
    public void onProfileImageReady(byte[] personProfileImage) {

    }

    @Override
    public void onFrontBodyImageReady(byte[] personFrontImage) {

    }

    @Override
    public void onPersonalPicsReady(String classKey, ArrayList<byte[]> personPhotos) {

    }

    @Override
    public void onProfileImageReady(byte[] personProfileImage, String personalKey) {

    }
}
