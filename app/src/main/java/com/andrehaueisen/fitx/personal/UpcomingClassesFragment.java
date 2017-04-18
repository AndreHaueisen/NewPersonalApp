package com.andrehaueisen.fitx.personal;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.client.firebase.FirebaseProfileImageCatcher;
import com.andrehaueisen.fitx.models.PersonalFitClass;
import com.andrehaueisen.fitx.personal.adapters.PersonalClassesAdapter;
import com.andrehaueisen.fitx.utilities.Constants;
import com.andrehaueisen.fitx.utilities.CustomTextView;
import com.andrehaueisen.fitx.utilities.Utils;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by andre on 9/8/2016.
 */

public class UpcomingClassesFragment extends Fragment implements ChildEventListener, FirebaseProfileImageCatcher.FirebaseOnArrayProfileCallback {

    private static final String TAG = UpcomingClassesFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private CustomTextView mNoClassesPlaceHolder;
    private CardView mCardView;
    private DatabaseReference mDatabaseReference;
    private PersonalClassesAdapter mAdapter;
    private ArrayList<PersonalFitClass> mConfirmedPersonalFitClasses;
    private FirebaseProfileImageCatcher mImageCatcher;
    private boolean mAnimationController;

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
        setRecyclerViewAnimations();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mCardView = (CardView) view.findViewById(R.id.no_class_card_place_holder);
        mNoClassesPlaceHolder = (CustomTextView) view.findViewById(R.id.no_class_place_holder);
        mNoClassesPlaceHolder.setText(getString(R.string.no_class_confirmed_message));

        mImageCatcher = new FirebaseProfileImageCatcher(this);

        if(savedInstanceState != null){
            Parcelable recyclerState = savedInstanceState.getParcelable(Constants.RECYCLER_VIEW_SAVED_STATE_KEY);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerState);
            mConfirmedPersonalFitClasses = savedInstanceState.getParcelableArrayList(Constants.CONFIRMED_PERSONALS_CLASSES_SAVED_STATE_KEY);
            if(mConfirmedPersonalFitClasses != null && !mConfirmedPersonalFitClasses.isEmpty()){
                mCardView.setVisibility(View.GONE);
            }
        }

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

        if (getActivity() != null && personalFitClass.isConfirmed()) {
            mConfirmedPersonalFitClasses.add(personalFitClass);
            mImageCatcher.getClientProfilePicture(getActivity(), personalFitClass.getClientKey(), mConfirmedPersonalFitClasses.size() - 1);
            //mRecyclerView.smoothScrollToPosition(mConfirmedPersonalFitClasses.size() - 1);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        PersonalFitClass personalFitClass = dataSnapshot.getValue(PersonalFitClass.class);

        if (getActivity() != null && personalFitClass.isConfirmed()) {
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
                mAdapter.changePersonalFitClass(i, personalFitClass);
                mAdapter.notifyItemChanged(i);
            } else {
                mConfirmedPersonalFitClasses.add(personalFitClass);
                mAdapter.addPersonalFitClass(personalFitClass);
                mAdapter.notifyItemInserted(mConfirmedPersonalFitClasses.size());
                changeStatus();
            }

        } else {

            for (int i = 0; i < mConfirmedPersonalFitClasses.size(); i++) {
                if (personalFitClass.getClassKey().equals(mConfirmedPersonalFitClasses.get(i).getClassKey())) {
                    mConfirmedPersonalFitClasses.remove(i);
                    mAdapter.removePersonalFitClass(i);
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

        if (getActivity() != null && personalFitClass.isConfirmed()) {
            for (int i = 0; i < mConfirmedPersonalFitClasses.size(); i++) {
                if (personalFitClass.getClassKey().equals(mConfirmedPersonalFitClasses.get(i).getClassKey())) {
                    mConfirmedPersonalFitClasses.remove(i);
                    mAdapter.removePersonalFitClass(i);
                    mAdapter.notifyItemRemoved(i);
                    changeStatus();
                }
            }
        }

    }

    private void setRecyclerViewAnimations(){

        final SlideInLeftAnimator animator = new SlideInLeftAnimator(new AccelerateDecelerateInterpolator());
        mRecyclerView.setItemAnimator(animator);

        final Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (0 == recyclerView.computeVerticalScrollOffset() && !mAnimationController) {
                    toolbar.animate().z(1).alpha(1.0f).start();
                    mAnimationController = true;
                } else if(mAnimationController){
                    toolbar.animate().z(8).alpha(0.95f).start();
                    mAnimationController = false;
                }
            }
        });

    }

    private void changeStatus() {

        if (mConfirmedPersonalFitClasses != null && mConfirmedPersonalFitClasses.size() != 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mCardView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mCardView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constants.CONFIRMED_PERSONALS_CLASSES_SAVED_STATE_KEY, mConfirmedPersonalFitClasses);
        outState.putParcelable(Constants.RECYCLER_VIEW_SAVED_STATE_KEY, mRecyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        mDatabaseReference.removeEventListener(this);
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
            mAdapter.addPersonalFitClass(mConfirmedPersonalFitClasses.get(mPositionOnArray));
            mAdapter.notifyItemChanged(mPositionOnArray);
            changeStatus();
        }
    }

    @Override
    public void onProfileImageReady(byte[] personProfileImage, int positionOnArray) {
        new LoadImageTask(positionOnArray).execute(personProfileImage);
    }
}
