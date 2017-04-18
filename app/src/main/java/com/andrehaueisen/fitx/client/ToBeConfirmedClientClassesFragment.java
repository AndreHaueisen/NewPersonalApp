package com.andrehaueisen.fitx.client;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.andrehaueisen.fitx.client.adapters.ClientClassesAdapter;
import com.andrehaueisen.fitx.client.firebase.ClientDatabase;
import com.andrehaueisen.fitx.client.firebase.FirebaseProfileImageCatcher;
import com.andrehaueisen.fitx.models.ClassReceipt;
import com.andrehaueisen.fitx.models.ClientFitClass;
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

import static com.andrehaueisen.fitx.utilities.Utils.getSharedPreferences;

/**
 * Created by andre on 11/21/2016.
 */

public class ToBeConfirmedClientClassesFragment extends Fragment implements ClientClassesAdapter.ClassCallback, ChildEventListener,
        FirebaseProfileImageCatcher.FirebaseOnArrayProfileCallback {

    private static final String TAG = ToBeConfirmedClientClassesFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private CardView mCardView;
    private CustomTextView mNoClassesPlaceHolder;
    private DatabaseReference mDatabaseReference;
    private ArrayList<ClientFitClass> mWaitingConfirmationClientFitClasses;
    private ClientClassesAdapter mAdapter;
    private FirebaseProfileImageCatcher mImageCatcher;
    private String mPersonalKey;
    private boolean mAnimationController;


    public static ToBeConfirmedClientClassesFragment newInstance() {
        return new ToBeConfirmedClientClassesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        String clientKey = getSharedPreferences(getContext()).getString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, null);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_LOCATION_CLIENT_CLASSES).child(clientKey);
        mDatabaseReference.addChildEventListener(this);
        mDatabaseReference.keepSynced(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_to_be_confirmed_classes, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.upcoming_classes_recycler_view);
        setRecyclerViewAnimations();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mCardView = (CardView) view.findViewById(R.id.no_class_card_place_holder);
        mNoClassesPlaceHolder = (CustomTextView) view.findViewById(R.id.no_class_confirmed_place_holder);
        mNoClassesPlaceHolder.setText(getString(R.string.client_no_class_scheduled_message));
        mImageCatcher = new FirebaseProfileImageCatcher(this);

        if(savedInstanceState != null){
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(Constants.RECYCLER_VIEW_SAVED_STATE_KEY));
            mWaitingConfirmationClientFitClasses = savedInstanceState.getParcelableArrayList(Constants.NOT_CONFIRMED_CLIENT_CLASSES_SAVED_STATE_KEY);
            if(mWaitingConfirmationClientFitClasses!= null && !mWaitingConfirmationClientFitClasses.isEmpty()){
                mCardView.setVisibility(View.GONE);
            }
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mWaitingConfirmationClientFitClasses == null) {
            mWaitingConfirmationClientFitClasses = new ArrayList<>();
        }

        mAdapter = new ClientClassesAdapter(this, mWaitingConfirmationClientFitClasses);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (getActivity() != null && dataSnapshot.exists()) {

            ClientFitClass clientFitClass = dataSnapshot.getValue(ClientFitClass.class);
            mPersonalKey = clientFitClass.getPersonalKey();

            if(clientFitClass != null) {
                if (Utils.isClassOnThePast(clientFitClass)) {

                    createClassReceipt(clientFitClass);

                } else {

                    if (!clientFitClass.isConfirmed()) {
                        mWaitingConfirmationClientFitClasses.add(clientFitClass);
                        mImageCatcher.getPersonalProfilePicture(clientFitClass.getPersonalKey(), mWaitingConfirmationClientFitClasses.size() - 1);
                        //mRecyclerView.smoothScrollToPosition(mWaitingConfirmationClientFitClasses.size() - 1);
                    }

                }
            }
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        if (getActivity() != null && dataSnapshot.exists()) {

            ClientFitClass clientFitClass = dataSnapshot.getValue(ClientFitClass.class);

            if (!clientFitClass.isConfirmed()) {
                for (int i = 0; i < mWaitingConfirmationClientFitClasses.size(); i++) {
                    if (clientFitClass.getClassKey().equals(mWaitingConfirmationClientFitClasses.get(i).getClassKey())) {
                        mWaitingConfirmationClientFitClasses.set(i, clientFitClass);
                        mAdapter.changeClientFitClass(i, clientFitClass);
                        mAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            } else {
                for (int i = 0; i < mWaitingConfirmationClientFitClasses.size(); i++) {
                    if (clientFitClass.getClassKey().equals(mWaitingConfirmationClientFitClasses.get(i).getClassKey())) {
                        mWaitingConfirmationClientFitClasses.remove(i);

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

        if(getActivity() != null && !clientFitClass.isConfirmed()) {
            for (int i = 0; i < mWaitingConfirmationClientFitClasses.size(); i++) {
                if (clientFitClass.getClassKey().equals(mWaitingConfirmationClientFitClasses.get(i).getClassKey())) {
                    mWaitingConfirmationClientFitClasses.remove(i);
                    mAdapter.removeClientFitClass(i);
                    mAdapter.notifyItemRemoved(i);
                    changeStatus();
                    break;
                }
            }
        }

    }

    private void setRecyclerViewAnimations(){

        final SlideInLeftAnimator animator = new SlideInLeftAnimator(new AccelerateDecelerateInterpolator());
        mRecyclerView.setItemAnimator(animator);

        final Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        final FloatingActionButton fab = ((FloatingActionButton) getActivity().findViewById(R.id.search_personal_fab));

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

                if (recyclerView.getChildCount() != 0)
                    if (dy > 0) {
                        fab.hide();
                    } else {
                        fab.show();
                    }
                else {
                    fab.show();
                }
            }
        });

    }

    private void createClassReceipt(ClientFitClass clientFitClass) {

        SharedPreferences sharedPreferences = Utils.getSharedPreferences(getContext());
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
            mCardView.setVisibility(View.GONE);
        } else {
            mCardView.setVisibility(View.VISIBLE);
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

        outState.putParcelable(Constants.RECYCLER_VIEW_SAVED_STATE_KEY, mRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArrayList(Constants.NOT_CONFIRMED_CLIENT_CLASSES_SAVED_STATE_KEY, mWaitingConfirmationClientFitClasses);
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
                    mWaitingConfirmationClientFitClasses.get(mPositionOnArray).setClassProfileImage(Glide.with(getContext()).load(image).asBitmap().into(100, 100).get());
                }
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.addClientFitClass(mWaitingConfirmationClientFitClasses.get(mPositionOnArray));
            mAdapter.notifyItemInserted(mPositionOnArray);
            changeStatus();
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}