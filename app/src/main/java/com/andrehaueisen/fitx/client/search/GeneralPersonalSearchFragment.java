package com.andrehaueisen.fitx.client.search;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.client.PersonalBoothActivity;
import com.andrehaueisen.fitx.client.dialogFragment.DatePickerDialogFragment;
import com.andrehaueisen.fitx.client.dialogFragment.TimePickerDialogFragment;
import com.andrehaueisen.fitx.client.firebase.FirebaseFilter;
import com.andrehaueisen.fitx.models.AttributedPhoto;
import com.andrehaueisen.fitx.models.Client;
import com.andrehaueisen.fitx.models.Gym;
import com.andrehaueisen.fitx.models.PersonalFitClass;
import com.andrehaueisen.fitx.models.PersonalTrainer;
import com.andrehaueisen.fitx.shared.PlacePhoto;
import com.andrehaueisen.fitx.shared.adapters.MainObjectiveAdapter;
import com.andrehaueisen.fitx.shared.dialogFragments.ClassDurationDialogFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeneralPersonalSearchFragment extends Fragment implements DatePickerDialogFragment.DateCallBack,
        TimePickerDialogFragment.TimeCallBack, ClassDurationDialogFragment.DurationCallBack,
        FirebaseFilter.QueryKeys, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, PlacePhoto.PlacePhotosCallback{

    private final String TAG = GeneralPersonalSearchFragment.class.getSimpleName();

    private ProgressDialog mProgressDialog;

    @BindView(R.id.choose_date_button) Button mChooseDayButton;
    @BindView(R.id.choose_class_start_time_button) Button mChooseStartTimeButton;
    @BindView(R.id.choose_duration_button) Button mChooseDurationButton;
    @BindView(R.id.activate_search_button) FloatingActionButton mSearchButton;

    @BindView(R.id.client_place_image_view) ImageView mPlaceImageView;
    @BindView(R.id.client_gym_name) TextView mPlaceNameTextView;
    @BindView(R.id.client_gym_address) TextView mPlaceAddressTextView;
    @BindView(R.id.attributions_text_view) TextView mPhotoAttributionsTextView;

    @BindView(R.id.specialties_recycler_view) RecyclerView mSpecialtiesRecyclerView;
    private MainObjectiveAdapter mObjectiveAdapter;

    private Gym mClientGym;

    private FirebaseFilter mFirebaseFilter;

    private PersonalFitClass mPersonalFitClass;
    private Client mClient;
    private PlacePhoto mPlacePhoto;

    private Calendar mDateCalendar;
    private Calendar mTimeCalendar;
    private int mClassDuration;
    private String mMainObjective;
    private AttributedPhoto mAttributedPhoto;

    private GoogleApiClient mGoogleApiClient;

    public static GeneralPersonalSearchFragment newInstance() {
        return new GeneralPersonalSearchFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if(savedInstanceState == null || savedInstanceState.isEmpty()) {
            String clientKey = Utils.getSharedPreferences(getContext()).getString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, null);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child(Constants.FIREBASE_LOCATION_CLIENT).child(clientKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        mClient = dataSnapshot.getValue(Client.class);
                        mGoogleApiClient.connect();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null) {
            mClient = savedInstanceState.getParcelable(Constants.CLIENT_SAVED_STATE_KEY);
            mPersonalFitClass = savedInstanceState.getParcelable(Constants.FIT_CLASS_SAVED_STATE_KEY);
            mDateCalendar = (Calendar) savedInstanceState.getSerializable(Constants.CLASS_DATE_CALENDAR_SAVED_STATE_KEY);
            mTimeCalendar = (Calendar) savedInstanceState.getSerializable(Constants.CLASS_TIME_CALENDAR_SAVED_STATE_KEY);
            mClassDuration = savedInstanceState.getInt(Constants.CLASS_DURATION_SAVED_STATE_KEY);
            mMainObjective = savedInstanceState.getString(Constants.MAIN_OBJECTIVE_SAVED_STATE_KEY);
            mAttributedPhoto = savedInstanceState.getParcelable(Constants.GYM_PHOTO_SAVED_STATE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_general_personal_search, container, false);
        ButterKnife.bind(this, view);

        mSpecialtiesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mSpecialtiesRecyclerView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onStart() {

        if(mPersonalFitClass != null){
            setButtonsSavedState();
            bindClientData();
            bindPlaceData();
        }else{
            mPersonalFitClass = new PersonalFitClass();
        }

        setButtonsClickListeners();
        setAutocompletePlaceFragment();

        super.onStart();
    }

    public void bindClientData() {

        if(mMainObjective != null){
            mObjectiveAdapter = new MainObjectiveAdapter(getContext(), getResources().getStringArray(R.array.specialty_types), mMainObjective);
        }else {
            mObjectiveAdapter = new MainObjectiveAdapter(getContext(), getResources().getStringArray(R.array.specialty_types), mClient.getMainObjective());
        }

        mSpecialtiesRecyclerView.setAdapter(mObjectiveAdapter);
        mClientGym = mClient.getClientGym();

    }

    private void bindPlaceData() {

        if(mClientGym != null) {
            mPlaceNameTextView.setText(mClientGym.getName());
            mPlaceAddressTextView.setText(mClientGym.getAddress());

            if (mAttributedPhoto == null) {
                ArrayList<String> placeIds = new ArrayList<>();
                placeIds.add(mClientGym.getPlaceId());
                mPlacePhoto.initializeTask(placeIds, false);
            } else {
                setGymPhoto(mAttributedPhoto);
            }
        }else{
            setGymPhoto(null);
        }
    }

    private void setButtonsSavedState(){
        setDayButtonText(mDateCalendar);
        setStartTimeButtonText(mTimeCalendar);
        setDurationButtonText(mClassDuration);
    }

    private void setDayButtonText(Calendar calendar){

        if(calendar != null) {
            Locale locale = Utils.getCurrentLocale(getContext());
            String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", locale);
            mChooseDayButton.setText(simpleDateFormat.format(calendar.getTime()) + " " + dayOfWeek);
        }

    }

    private void setStartTimeButtonText(Calendar calendar){

        if(calendar != null) {
            Locale locale = Utils.getCurrentLocale(getContext());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", locale);
            mChooseStartTimeButton.setText(getString(R.string.class_start_time, simpleDateFormat.format(calendar.getTime())));
        }

    }

    private void setDurationButtonText(int duration){

        if (duration != 0) {
            String textDuration = Utils.getClassDurationText(getContext(), duration);
            mChooseDurationButton.setText(getString(R.string.class_duration_is, textDuration));
        }
    }

    private void setButtonsClickListeners() {

        mChooseDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerDialogFragment();
                datePicker.setTargetFragment(GeneralPersonalSearchFragment.this, 0);
                datePicker.show(getFragmentManager(), "Date Picker");
            }
        });

        mChooseStartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerDialogFragment();
                timePicker.setTargetFragment(GeneralPersonalSearchFragment.this, 1);
                timePicker.show(getFragmentManager(), "TimePicker");
            }
        });

        mChooseDurationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClassConfigurationDialogFragment();
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPersonalFitClass.setMainObjective(mObjectiveAdapter.getMainObjective());

                if (mClientGym != null) {
                    mPersonalFitClass.setPlaceName(mClientGym.getName());
                    mPersonalFitClass.setPlaceLongitude(mClientGym.getLongitude());
                    mPersonalFitClass.setPlaceLatitude(mClientGym.getLatitude());
                    mPersonalFitClass.setPlaceAddress(mClientGym.getAddress());
                } else {
                    Utils.generateInfoToast(getContext(), getString(R.string.reinforce_choose_working_out_place)).show();
                }

                if (mPersonalFitClass.getMainObjective() == null || mPersonalFitClass.getDateCode() == null || mPersonalFitClass.getStartTimeCode() == null ||
                        mPersonalFitClass.getDurationCode() == null || mClientGym == null) {

                    if (mPersonalFitClass.getMainObjective() == null) {
                        Utils.generateInfoToast(getContext(), getString(R.string.reinforce_choose_objective)).show();
                    }

                    if (mPersonalFitClass.getDateCode() == null) {
                        Utils.generateInfoToast(getContext(), getString(R.string.reinforce_choose_date)).show();
                    }

                    if (mPersonalFitClass.getStartTimeCode() == null) {
                        Utils.generateInfoToast(getContext(), getString(R.string.reinforce_choose_start_time)).show();
                    }

                    if (mPersonalFitClass.getDurationCode() == null) {
                        Utils.generateInfoToast(getContext(), getString(R.string.reinforce_choose_duration)).show();
                    }

                    if (mClientGym == null) {
                        Utils.generateInfoToast(getContext(), getString(R.string.reinforce_choose_working_out_place)).show();
                    }
                } else {
                    querySuitablePersonal();
                    mProgressDialog = new ProgressDialog(getContext());
                    mProgressDialog.setTitle(getString(R.string.searching_for_personal));
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.show();
                }
            }
        });

    }

    private void setAutocompletePlaceFragment(){

        TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = telephonyManager.getNetworkCountryIso();

        AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry(countryIso).setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT).build();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.client_place_autocomplete_fragment);

        EditText editText = ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input));
        editText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        editText.setTextSize(16.0f);
        editText.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));

        ImageButton imageButton = ((ImageButton)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button));
        imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_dark_24dp));

        autocompleteFragment.setFilter(filter);
        autocompleteFragment.setHint(getString(R.string.try_new_working_out_place_hint));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                String name = place.getName().toString();
                String address = place.getAddress().toString();
                String placeId = place.getId();

                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);

                ArrayList<String> placesId = new ArrayList<>();
                placesId.add(placeId);

                mClientGym = new Gym(placeId, name, address, latitude, longitude);
                mPlacePhoto.initializeTask(placesId, false);
                mPlaceNameTextView.setText(mClientGym.getName());
                mPlaceAddressTextView.setText(mClientGym.getAddress());
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onSinglePhotoReady(AttributedPhoto attributedPhoto) {
        setGymPhoto(attributedPhoto);
    }

    private void setGymPhoto(AttributedPhoto attributedPhoto){
        if (attributedPhoto != null) {

            if (attributedPhoto.getBitmap() != null) {
                mPlaceImageView.setImageBitmap(attributedPhoto.getBitmap());

            } else {
                Glide.with(this).load(R.drawable.neutral_background).centerCrop().into(mPlaceImageView);
            }

            if (attributedPhoto.getAttribution() != null) {
                String attribution = Html.fromHtml(attributedPhoto.getAttribution().toString()).toString();
                mPhotoAttributionsTextView.setVisibility(View.VISIBLE);
                mPhotoAttributionsTextView.setText(this.getString(R.string.taken_by_attribution, attribution));

            } else {
                mPhotoAttributionsTextView.setVisibility(View.GONE);
            }

            mAttributedPhoto = attributedPhoto;
        } else {
            Glide.with(this).load(R.drawable.neutral_background).centerCrop().into(mPlaceImageView);
            mPhotoAttributionsTextView.setVisibility(View.GONE);
            mAttributedPhoto = new AttributedPhoto();
        }


    }

    private void querySuitablePersonal() {
        mFirebaseFilter = new FirebaseFilter(GeneralPersonalSearchFragment.this);
        mFirebaseFilter.startChainQuery(mPersonalFitClass.getDateCode());
    }

    @Override
    public void onAgendaCodesReady() {
        mFirebaseFilter.filterByPlace(mClientGym);
    }

    @Override
    public void getPlaceFilteredKeys(ArrayList<String> placeFilteredKeys) {
        mFirebaseFilter.filterBySpecialty(mPersonalFitClass.getMainObjective(), placeFilteredKeys);
    }

    @Override
    public void getSpecialtyFilterKeys(ArrayList<String> specialtyFilteredKeys) {
        mFirebaseFilter.filterBySchedule(mPersonalFitClass.getDateCode(), mPersonalFitClass.getStartTimeCode(), mPersonalFitClass.getDurationCode(), specialtyFilteredKeys);
    }

    @Override
    public void getChosenPersonalInformation(ArrayList<String> chosenPersonalKeys) {
        mFirebaseFilter.getPersonals(chosenPersonalKeys);
    }

    /**
     * Receives the final personal list
     *
     * @param chosenPersonalTrainers
     */
    @Override
    public void onPersonalInformationReady(ArrayList<PersonalTrainer> chosenPersonalTrainers, ArrayList<String> chosenPersonalKeys) {
        mProgressDialog.dismiss();
        actOnSearchResult(chosenPersonalTrainers, chosenPersonalKeys);
    }

    private void actOnSearchResult(ArrayList<PersonalTrainer> chosenPersonalTrainers, ArrayList<String> chosenPersonalKeys) {

        if ((chosenPersonalTrainers == null || chosenPersonalTrainers.size() == 0) && (chosenPersonalKeys == null || chosenPersonalKeys.size() == 0)) {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setTitle(getString(R.string.upsy_doopsi)).setMessage(R.string.no_personal_found_at_date).
                    setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create();

            alertDialog.show();

        } else {
            Intent intent = new Intent(getContext(), PersonalBoothActivity.class);
            intent.putParcelableArrayListExtra(Constants.PERSONAL_LIST_EXTRA_KEY, chosenPersonalTrainers);
            intent.putStringArrayListExtra(Constants.CHOSEN_PERSONAL_KEYS_EXTRA_KEY, chosenPersonalKeys);
            intent.putExtra(Constants.FIT_CLASS_EXTRA_KEY, mPersonalFitClass);

            startActivity(intent);
        }

    }

    @Override
    public void setDate(Calendar calendar) {

        setDayButtonText(calendar);

        convertDateBeforeSaving(calendar);
        mDateCalendar = calendar;
    }

    @Override
    public void setTime(Calendar calendar) {

        setStartTimeButtonText(calendar);

        convertStartTimeBeforeSaving(calendar);
        mTimeCalendar = calendar;
    }

    private void showClassConfigurationDialogFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");

        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        ClassDurationDialogFragment classDurationDialogFragment = ClassDurationDialogFragment.newInstance();
        classDurationDialogFragment.setTargetFragment(this, 2);
        classDurationDialogFragment.show(fragmentTransaction, "dialog");
    }

    @Override
    public void setDuration(int duration) {
        setDurationButtonText(duration);

        mPersonalFitClass.setDurationCode(duration);

        mClassDuration = duration;
    }

    private void convertDateBeforeSaving(Calendar calendar) {
        String uniformDateFormat = new SimpleDateFormat("MMddyyyy", Utils.getCurrentLocale(getContext())).format(calendar.getTime());
        mPersonalFitClass.setDateCode(uniformDateFormat);
    }

    private void convertStartTimeBeforeSaving(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        mPersonalFitClass.setStartTimeCode(Utils.getTimeCodeFromClock(hour, minutes));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlacePhoto = new PlacePhoto(mGoogleApiClient, GeneralPersonalSearchFragment.this);
        bindClientData();
        bindPlaceData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelable(Constants.CLIENT_SAVED_STATE_KEY, mClient);
        outState.putParcelable(Constants.FIT_CLASS_SAVED_STATE_KEY, mPersonalFitClass);
        outState.putSerializable(Constants.CLASS_DATE_CALENDAR_SAVED_STATE_KEY, mDateCalendar);
        outState.putSerializable(Constants.CLASS_TIME_CALENDAR_SAVED_STATE_KEY, mTimeCalendar);
        outState.putInt(Constants.CLASS_DURATION_SAVED_STATE_KEY, mClassDuration);
        outState.putString(Constants.MAIN_OBJECTIVE_SAVED_STATE_KEY, mObjectiveAdapter.getMainObjective());
        outState.putParcelable(Constants.GYM_PHOTO_SAVED_STATE_KEY, mAttributedPhoto);


        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPhotosReady(ArrayList<AttributedPhoto> attributedPhotos) {

    }

    @Override
    public void onSinglePhotoReady(AttributedPhoto attributedPhoto, int position) {

    }
}

