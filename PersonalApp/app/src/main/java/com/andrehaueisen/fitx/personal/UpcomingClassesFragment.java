package com.andrehaueisen.fitx.personal;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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
 * Created by andre on 9/8/2016.
 */

public class UpcomingClassesFragment extends Fragment implements ChildEventListener, FirebaseImageCatcher.FirebaseCallback {

    private static final String TAG = UpcomingClassesFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private DesertPlaceholder mNoClassesPlaceHolder;
    private DatabaseReference mDatabaseReference;
    private PersonalClassesAdapter mAdapter;
    private ArrayList<PersonalFitClass> mConfirmedPersonalFitClasses;
    private FirebaseImageCatcher mImageCatcher;

    public static Fragment newInstance() {
        return new UpcomingClassesFragment();
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

        View view = inflater.inflate(R.layout.fragment_upcoming_classes, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.upcoming_classes_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new SlideInRightAnimator());

        if(savedInstanceState != null){
            Parcelable recyclerState = savedInstanceState.getParcelable(Constants.RECYCLER_VIEW_SAVED_STATE_KEY);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerState);
            mConfirmedPersonalFitClasses = savedInstanceState.getParcelableArrayList(Constants.CONFIRMED_PERSONALS_CLASSES_SAVED_STATE_KEY);
        }

        mNoClassesPlaceHolder = (DesertPlaceholder) view.findViewById(R.id.no_class_place_holder);
        mNoClassesPlaceHolder.setMessage(getString(R.string.no_class_confirmed_message));

        mImageCatcher = new FirebaseImageCatcher(this);

        changeStatus();

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (mConfirmedPersonalFitClasses == null) {
            mConfirmedPersonalFitClasses = new ArrayList<>();
        }

        mAdapter = new PersonalClassesAdapter(this, mConfirmedPersonalFitClasses);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        PersonalFitClass personalFitClass = dataSnapshot.getValue(PersonalFitClass.class);

        if (personalFitClass.isConfirmed()) {
            mConfirmedPersonalFitClasses.add(personalFitClass);
            mImageCatcher.getClientProfilePicture(getActivity(), personalFitClass.getClientKey(), mConfirmedPersonalFitClasses.size() - 1);
            mAdapter.notifyItemInserted(mConfirmedPersonalFitClasses.size());
            mRecyclerView.smoothScrollToPosition(mConfirmedPersonalFitClasses.size() - 1);
        }

        changeStatus();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        PersonalFitClass personalFitClass = dataSnapshot.getValue(PersonalFitClass.class);

        if (personalFitClass.isConfirmed()) {
            boolean isClassOnAdapter = false;
            int i;
            for (i = 0; i < mConfirmedPersonalFitClasses.size(); i++) {
                if (personalFitClass.getClassKey().equals(mConfirmedPersonalFitClasses.get(i).getClassKey())) {
                    isClassOnAdapter = true;
                    break;
                }
            }

            if (isClassOnAdapter) {
                mConfirmedPersonalFitClasses.set(i, personalFitClass);
                mAdapter.notifyItemChanged(i);
            } else {
                mConfirmedPersonalFitClasses.add(personalFitClass);
                mAdapter.notifyItemInserted(mConfirmedPersonalFitClasses.size());
                changeStatus();
            }

        } else {

            for (int i = 0; i < mConfirmedPersonalFitClasses.size(); i++) {
                if (personalFitClass.getClassKey().equals(mConfirmedPersonalFitClasses.get(i).getClassKey())) {
                    mConfirmedPersonalFitClasses.remove(i);
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

        if (personalFitClass.isConfirmed()) {
            for (int i = 0; i < mConfirmedPersonalFitClasses.size(); i++) {
                if (personalFitClass.getClassKey().equals(mConfirmedPersonalFitClasses.get(i).getClassKey())) {
                    mConfirmedPersonalFitClasses.remove(i);
                    mAdapter.notifyItemRemoved(i);
                    changeStatus();
                }
            }
        }

    }

    private void changeStatus() {

        if (mConfirmedPersonalFitClasses != null && mConfirmedPersonalFitClasses.size() != 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoClassesPlaceHolder.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mNoClassesPlaceHolder.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constants.CONFIRMED_PERSONALS_CLASSES_SAVED_STATE_KEY, mConfirmedPersonalFitClasses);
        outState.putParcelable(Constants.RECYCLER_VIEW_SAVED_STATE_KEY, mRecyclerView.getLayoutManager().onSaveInstanceState());
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
                if (image != null && image.length != 0) {
                    mConfirmedPersonalFitClasses.get(mPositionOnArray).setClassProfileImage(Glide.with(getContext()).load(image).asBitmap().into(100, 100)
                            .get());
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
