package com.andrehaueisen.fitx.personal.drawer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.models.AttributedPhoto;
import com.andrehaueisen.fitx.models.Gym;
import com.andrehaueisen.fitx.personal.firebase.PersonalDatabase;
import com.andrehaueisen.fitx.shared.PlacePhoto;
import com.andrehaueisen.fitx.shared.adapters.PlacesAdapter;
import com.andrehaueisen.fitx.utilities.Constants;
import com.andrehaueisen.fitx.utilities.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;


public class WorkPlacesFragment extends Fragment implements ProfessionalProfileActivity.PlaceFragmentCallback,
        ProfessionalProfileActivity.OnProfileDataReadyCallback, ValueEventListener, PlacePhoto.PlacePhotosCallback {

    private final String TAG = WorkPlacesFragment.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private RecyclerView mWorkPlacesRecyclerView;
    private PlacesAdapter mPlacesAdapter;
    private PlacePhoto mPlacePhoto;
    private DatabaseReference mDatabaseReference;

    private ArrayList<Gym> mGyms;

    public static WorkPlacesFragment newInstance() {
        return new WorkPlacesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        mPlacePhoto = new PlacePhoto(mGoogleApiClient,  WorkPlacesFragment.this);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .build();

        mGoogleApiClient.connect();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        String personalUniqueKey = Utils.getSharedPreferences(getContext()).getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null);
        mDatabaseReference.child(Constants.FIREBASE_LOCATION_GYMS).child(personalUniqueKey).addListenerForSingleValueEvent(this);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null && !savedInstanceState.isEmpty()) {
            mGyms = savedInstanceState.getParcelableArrayList(Constants.GYMS_SAVED_STATE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_work_places, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mWorkPlacesRecyclerView = (RecyclerView) view.findViewById(R.id.work_places_recycler_view);
        mWorkPlacesRecyclerView.setHasFixedSize(true);
        mWorkPlacesRecyclerView.setLayoutManager(layoutManager);
        mWorkPlacesRecyclerView.setItemAnimator(new SlideInDownAnimator());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        setAdapter();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        GenericTypeIndicator<ArrayList<Gym>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Gym>>() {};

        if(dataSnapshot.exists()) {
            mGyms.addAll(dataSnapshot.getValue(genericTypeIndicator));
            mPlacesAdapter.notifyDataSetChanged();
            changeRecyclerViewVisibility();
        }
        collectPlacePhotos();
    }

    private void collectPlacePhotos(){
        ArrayList<String> placeIds = new ArrayList<>();
        for(Gym gym : mGyms){
            placeIds.add(gym.getPlaceId());
        }

        if(mPlacePhoto != null) {
            mPlacePhoto.initializeTask(placeIds, true);
        } else {
            mPlacePhoto = new PlacePhoto(mGoogleApiClient, this);
            mPlacePhoto.initializeTask(placeIds, true);
        }

    }

    private void setAdapter(){
        if(mGyms == null) {
            mGyms = new ArrayList<>();
        }

        mPlacesAdapter = new PlacesAdapter(getContext(), mGyms, this);
        mWorkPlacesRecyclerView.setAdapter(mPlacesAdapter);
        changeRecyclerViewVisibility();
    }

    @Override
    public void onPhotosReady(ArrayList<AttributedPhoto> attributedPhotos) {

        for(int i = 0; i < mGyms.size(); i++){
            mGyms.get(i).setAttributedPhoto(attributedPhotos.get(i));
            mPlacesAdapter.notifyItemChanged(i);
            changeRecyclerViewVisibility();
        }

    }

    @Override
    public void onSinglePhotoReady(AttributedPhoto attributedPhoto, int position) {
        mGyms.get(position).setAttributedPhoto(attributedPhoto);
        mPlacesAdapter.notifyItemInserted(position);
        changeRecyclerViewVisibility();
        mWorkPlacesRecyclerView.smoothScrollToPosition(position);
    }

     public void changeRecyclerViewVisibility(){
        if(mPlacesAdapter.getItemCount() != 0){
            mWorkPlacesRecyclerView.setVisibility(View.VISIBLE);
        }else {
            mWorkPlacesRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPlaceSelected(Gym gym) {
        ArrayList<String> placesId = new ArrayList<>();
        placesId.add(gym.getPlaceId());

        mGyms.add(gym);
        mPlacePhoto.initializeTask(placesId, false, mGyms.size()-1);
    }

    @Override
    public void saveDataOnDatabase() {
        PersonalDatabase.savePersonalWorkingPlaces(getContext(), mGyms);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, databaseError.getMessage());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constants.GYMS_SAVED_STATE_KEY, mGyms);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onSinglePhotoReady(AttributedPhoto attributedPhoto) {

    }
}
