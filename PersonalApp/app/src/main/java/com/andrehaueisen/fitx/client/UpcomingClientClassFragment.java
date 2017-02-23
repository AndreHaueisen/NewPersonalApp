package com.andrehaueisen.fitx.client;


import android.content.SharedPreferences;
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
import com.andrehaueisen.fitx.client.adapters.ClientClassesAdapter;
import com.andrehaueisen.fitx.client.firebase.ClientDatabase;
import com.andrehaueisen.fitx.client.firebase.FirebaseImageCatcher;
import com.andrehaueisen.fitx.pojo.ClassReceipt;
import com.andrehaueisen.fitx.pojo.ClientFitClass;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jetradar.desertplaceholder.DesertPlaceholder;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingClientClassFragment extends Fragment implements ClientClassesAdapter.ClassCallback, ChildEventListener, FirebaseImageCatcher.FirebaseCallback {

    private static final String TAG = UpcomingClientClassFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private DesertPlaceholder mNoClassesPlaceHolder;
    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<ClientFitClass> mConfirmedClientFitClasses;
    private ClientClassesAdapter mAdapter;
    private FirebaseImageCatcher mImageCatcher;
    private String mPersonalKey;

    public static UpcomingClientClassFragment newInstance() {
        return new UpcomingClientClassFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        String clientKey = Utils.getSharedPreferences(getContext()).getString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, null);
        mDatabaseReference.child(Constants.FIREBASE_LOCATION_CLIENT_CLASSES).child(clientKey).addChildEventListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_upcoming_classes, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.upcoming_classes_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());

        if(savedInstanceState != null && !savedInstanceState.isEmpty()){
            mConfirmedClientFitClasses = savedInstanceState.getParcelableArrayList(Constants.CONFIRMED_CLIENT_CLASSES_SAVED_STATE_KEY);
            Parcelable recyclerState = savedInstanceState.getParcelable(Constants.RECYCLER_VIEW_SAVED_STATE_KEY);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerState);
        }

        mImageCatcher = new FirebaseImageCatcher(this);
        mNoClassesPlaceHolder = (DesertPlaceholder) view.findViewById(R.id.no_class_place_holder);
        mNoClassesPlaceHolder.setMessage(getString(R.string.client_no_class_confirmed_message));
        return view;
    }




    @Override
    public void onStart() {
        super.onStart();

        if(mConfirmedClientFitClasses == null) {
            mConfirmedClientFitClasses = new ArrayList<>();
        }
        mAdapter = new ClientClassesAdapter(this, mConfirmedClientFitClasses);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot.exists()) {

            ClientFitClass clientFitClass = dataSnapshot.getValue(ClientFitClass.class);
            mPersonalKey = clientFitClass.getPersonalKey();

            if (Utils.isClassOnThePast(clientFitClass)) {
                //TODO do some work on badges
                createClassReceipt(clientFitClass);

            } else {

                if (clientFitClass.isConfirmed()) {
                    mConfirmedClientFitClasses.add(clientFitClass);
                    mImageCatcher.getPersonalProfilePicture(clientFitClass.getPersonalKey(), mConfirmedClientFitClasses.size() - 1);
                    mAdapter.notifyItemInserted(mConfirmedClientFitClasses.size());
                    mRecyclerView.smoothScrollToPosition(mConfirmedClientFitClasses.size() - 1);
                }

                changeStatus();
            }
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        if (dataSnapshot.exists()) {

            ClientFitClass clientFitClass = dataSnapshot.getValue(ClientFitClass.class);

            if (clientFitClass.isConfirmed()) {
                boolean isClassOnAdapter = false;
                int i;
                for (i = 0; i < mConfirmedClientFitClasses.size(); i++) {
                    if (clientFitClass.getClassKey().equals(mConfirmedClientFitClasses.get(i).getClassKey())) {
                        isClassOnAdapter = true;
                        break;
                    }
                }

                if (isClassOnAdapter) {
                    mConfirmedClientFitClasses.set(i, clientFitClass);
                    mAdapter.notifyItemChanged(i);
                } else {
                    mConfirmedClientFitClasses.add(clientFitClass);
                    mAdapter.notifyItemInserted(mConfirmedClientFitClasses.size());
                    changeStatus();
                }

            } else {

                for (int i = 0; i < mConfirmedClientFitClasses.size(); i++) {
                    if (clientFitClass.getClassKey().equals(mConfirmedClientFitClasses.get(i).getClassKey())) {
                        mConfirmedClientFitClasses.remove(i);
                        mAdapter.notifyItemRemoved(i);
                        changeStatus();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

        ClientFitClass clientFitClass = dataSnapshot.getValue(ClientFitClass.class);

        if (clientFitClass.isConfirmed()) {
            for (int i = 0; i < mConfirmedClientFitClasses.size(); i++) {
                if (clientFitClass.getClassKey().equals(mConfirmedClientFitClasses.get(i).getClassKey())) {
                    mConfirmedClientFitClasses.remove(i);
                    mAdapter.notifyItemRemoved(i);
                    changeStatus();
                    break;
                }
            }
        }

    }

    private void createClassReceipt(ClientFitClass clientFitClass) {

        SharedPreferences sharedPreferences = Utils.getSharedPreferences(getActivity());
        String clientKey = sharedPreferences.getString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, null);
        String clientName = sharedPreferences.getString(Constants.SHARED_PREF_CLIENT_NAME, null);

        ClassReceipt classReceipt = new ClassReceipt(clientFitClass.getStartTimeCode(), clientFitClass.getDateCode(),
                clientFitClass.getDurationCode(), clientKey, clientFitClass.getClassKey(), clientFitClass.getMainObjective(),
                clientFitClass.getPlaceName(), clientFitClass.getPlaceAddress(), clientFitClass.getPersonalName(),
                clientName, clientFitClass.getPersonalKey());

        ClientDatabase.saveClassReceipt(classReceipt);

    }

    private void changeStatus() {

        if (mAdapter.getItemCount() != 0) {
            mNoClassesPlaceHolder.setVisibility(View.GONE);
        } else {
            mNoClassesPlaceHolder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public String getPersonalKey() {
        if (mPersonalKey != null) {
            return mPersonalKey;
        } else {
            return "";
        }
    }

    @Override
    public void onProfileImageReady(byte[] personProfileImage, int positionOnArray) {
        new LoadImageTask(positionOnArray).execute(personProfileImage);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(Constants.CONFIRMED_CLIENT_CLASSES_SAVED_STATE_KEY, mConfirmedClientFitClasses);
        outState.putParcelable(Constants.RECYCLER_VIEW_SAVED_STATE_KEY, mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onDestroy() {
        mDatabaseReference.removeEventListener(this);
        super.onDestroy();
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
                    mConfirmedClientFitClasses.get(mPositionOnArray).setClassProfileImage(Glide.with(getContext()).load(image).asBitmap().into(100, 100).get
                            ());
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
    public void onProfileImageReady(byte[] personProfileImage) {

    }

    @Override
    public void onFrontBodyImageReady(byte[] personFrontImage) {

    }

    @Override
    public void onPersonalPicsReady(String classKey, ArrayList<byte[]> personPhotos) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onProfileImageReady(byte[] personProfileImage, String personalKey) {

    }
}
