package com.andrehaueisen.fitx.client.drawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrehaueisen.fitx.utilities.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.utilities.Utils;
import com.andrehaueisen.fitx.client.firebase.ClientDatabase;
import com.andrehaueisen.fitx.models.AttributedPhoto;
import com.andrehaueisen.fitx.models.Client;
import com.andrehaueisen.fitx.models.Gym;
import com.andrehaueisen.fitx.personal.drawer.PersonalBasicPresentationFragment;
import com.andrehaueisen.fitx.personal.drawer.dialogFragment.PictureSelectionMethodDialogFragment;
import com.andrehaueisen.fitx.shared.PlacePhoto;
import com.andrehaueisen.fitx.shared.adapters.MainObjectiveAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
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

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.Glide.with;

public class ClientProfileActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, PlacePhoto.PlacePhotosCallback {

    private final String TAG = ClientProfileActivity.class.getSimpleName();

    private String mPhotoUriPath;
    private int mLastClickedImageViewCode;

    @BindView(R.id.client_place_image_view)
    ImageView mPlaceImageView;
    @BindView(R.id.client_gym_name)
    TextView mPlaceNameTextView;
    @BindView(R.id.client_gym_address)
    TextView mPlaceAddressTextView;
    @BindView(R.id.attributions_text_view)
    TextView mPhotoAttributionsTextView;
    @BindView(R.id.specialties_recycler_view)
    RecyclerView mSpecialtiesRecyclerView;
    @Nullable @BindView(R.id.contrast_view)
    View mContrastView;

    private MainObjectiveAdapter mObjectiveAdapter;
    private Gym mClientGym;
    private CircleImageView mProfileImage;
    private PlacePhoto mPlacePhoto;

    private PictureSelectionMethodDialogFragment mPictureSelectionDialogFragment;
    private Client mClient;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_client_profile);

        ButterKnife.bind(this);

        if (savedInstanceState != null && savedInstanceState.getParcelable(Constants.CLIENT_BUNDLE_KEY) != null) {
            mClient = savedInstanceState.getParcelable(Constants.CLIENT_BUNDLE_KEY);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mPlaceImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mClientGym != null) {
                    //geo:0,0?q=latitude,longitude(label)
                    Uri addressUri = Uri.parse("geo:0,0?q=" + mClientGym.getLatitude() + "," + mClientGym.getLongitude());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, addressUri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);

                    } else {
                        Utils.generateInfoToast(ClientProfileActivity.this, getString(R.string.no_map_app_detected)).show();
                    }
                }
            }
        });

        mPlacePhoto = new PlacePhoto(mGoogleApiClient, this);

        mSpecialtiesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        configureProfileImageView();
        setProfileImage();

        setAutocompletePlaceFragment();
    }

    private void configureProfileImageView() {
        mProfileImage = (CircleImageView) findViewById(R.id.profile_image_view);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");

                if (prev != null) {
                    fragmentTransaction.remove(prev);
                }
                fragmentTransaction.addToBackStack(null);

                int imageId = view.getId();
                Bundle bundle = new Bundle();

                switch (imageId) {
                    case R.id.profile_image_view:
                        mLastClickedImageViewCode = Constants.PERSONAL_PROFILE_PICTURE;
                        bundle.putInt(Constants.IMAGE_CODE_BUNDLE_KEY, Constants.PERSONAL_PROFILE_PICTURE);
                        break;

                }

                mPictureSelectionDialogFragment = PictureSelectionMethodDialogFragment.newInstance(bundle);
                mPictureSelectionDialogFragment.show(fragmentTransaction, "dialog");
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.CLIENT_BUNDLE_KEY, mClient);
    }

    private void setProfileImage() {
        mPhotoUriPath = Utils.getSharedPreferences(this).getString(Constants.SHARED_PREF_CLIENT_PHOTO_URI_PATH, null);

        if (mPhotoUriPath != null) {
            Uri photoUri = Uri.parse(mPhotoUriPath);
            with(this).load(photoUri).into(mProfileImage);

        } else {
            with(this).load(R.drawable.head_placeholder).into(mProfileImage);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Google api places services connected!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Api places services failed!");
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();

        String clientKey = Utils.getSharedPreferences(this).getString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, null);

        if (mClient == null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child(Constants.FIREBASE_LOCATION_CLIENT).child(clientKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        mClient = dataSnapshot.getValue(Client.class);
                        bindClientData();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            bindClientData();
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    public void bindClientData() {

        mObjectiveAdapter = new MainObjectiveAdapter(this, getResources().getStringArray(R.array.specialty_types), mClient.getMainObjective());
        mSpecialtiesRecyclerView.setAdapter(mObjectiveAdapter);

        mClientGym = mClient.getClientGym();

        bindPlaceData();
    }

    private void bindPlaceData() {

        if (mClientGym != null) {
            mPlaceNameTextView.setText(mClientGym.getName());
            mPlaceAddressTextView.setText(mClientGym.getAddress());

            ArrayList<String> placeIds = new ArrayList<>();
            placeIds.add(mClientGym.getPlaceId());
            mPlacePhoto.initializeTask(placeIds, false);

        } else {
            hideGymViews();
            hideAttributionViews();
            Glide.with(this).load(R.drawable.neutral_background).centerCrop().into(mPlaceImageView);
        }
    }

    @Override
    public void onSinglePhotoReady(AttributedPhoto attributedPhoto) {

        if (attributedPhoto != null) {

            mPlaceImageView.setImageBitmap(attributedPhoto.getBitmap());
            showGymViews();
            showAttributionViews();

        } else {
            Glide.with(this).load(R.drawable.neutral_background).centerCrop().into(mPlaceImageView);
            showGymViews();
            hideAttributionViews();
        }
    }

    private void hideGymViews(){
        mPlaceNameTextView.setVisibility(View.INVISIBLE);
        mPlaceAddressTextView.setVisibility(View.INVISIBLE);
    }

    private void hideAttributionViews(){
        if(mContrastView != null){
            mContrastView.setVisibility(View.INVISIBLE);
        }
        mPhotoAttributionsTextView.setVisibility(View.INVISIBLE);
    }

    private void showGymViews(){
        mPlaceNameTextView.setVisibility(View.VISIBLE);
        mPlaceAddressTextView.setVisibility(View.VISIBLE);
    }

    private void showAttributionViews(){
        if(mContrastView != null){
            mContrastView.setVisibility(View.VISIBLE);
        }
        mPhotoAttributionsTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Uri photoUri = mPictureSelectionDialogFragment.getProfilePicsUri();
            File photoFile = mPictureSelectionDialogFragment.getImageFile();

            placeImageOnImageView(photoUri, photoFile);

        } else if (requestCode == Constants.REQUEST_IMAGE_LOAD && resultCode == Activity.RESULT_OK) {

            Uri photoUri = data.getData();
            placeImageOnImageView(photoUri, null);
        }

    }

    private void placeImageOnImageView(Uri photoUri, @Nullable File photoFile) {

        mPhotoUriPath = photoUri.toString();
        if (photoFile != null) {

            String uriLastPath = photoUri.getLastPathSegment();
            switch (uriLastPath) {
                case Constants.PERSONAL_PROFILE_PICTURE_NAME:

                    mProfileImage.setBackground(null);
                    with(this).load(photoFile).asBitmap().into(mProfileImage);
                    break;

                default:
                    Log.e(PersonalBasicPresentationFragment.class.getSimpleName(), "Image code not found");
            }
        } else {

            switch (mLastClickedImageViewCode) {
                case Constants.PERSONAL_PROFILE_PICTURE:

                    with(this).loadFromMediaStore(photoUri).asBitmap().into(mProfileImage);
                    break;

                default:
                    Log.e(PersonalBasicPresentationFragment.class.getSimpleName(), "Image code not found");
            }

        }
    }

    private void setAutocompletePlaceFragment() {

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = telephonyManager.getNetworkCountryIso();

        AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry(countryIso).setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT).build();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.client_place_autocomplete_fragment);
        autocompleteFragment.setFilter(filter);
        autocompleteFragment.setHint(getString(R.string.choose_your_gym_hint));

        EditText editText = ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input));
        editText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        editText.setTextSize(16.0f);
        editText.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));

        ImageButton imageButton = ((ImageButton) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button));
        imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_dark_24dp));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                String name = place.getName().toString();
                String address = place.getAddress().toString();
                String placeId = place.getId();


                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);

                mClientGym = new Gym(placeId, name, address, latitude, longitude);
                mClient.setClientGym(mClientGym);
                bindPlaceData();
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_changes_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_confirm_changes_menu_button:
                saveDataOnDatabase();
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return true;
        }
    }

    public void saveDataOnDatabase() {

        SharedPreferences.Editor editor = Utils.getSharedPreferences(this).edit();
        editor.putString(Constants.SHARED_PREF_CLIENT_PHOTO_URI_PATH, mPhotoUriPath).apply();

        Bitmap profilePicture;
        if (mProfileImage.getDrawable().getCurrent() instanceof GlideBitmapDrawable) {
            profilePicture = ((GlideBitmapDrawable) mProfileImage.getDrawable().getCurrent()).getBitmap();
        } else {
            profilePicture = ((BitmapDrawable) mProfileImage.getDrawable().getCurrent()).getBitmap();
        }

        ClientDatabase.saveProfilePicsToFirebase(this, profilePicture);

        mClient.setMainObjective(mObjectiveAdapter.getMainObjective());
        mClient.setClientGym(mClientGym);

        ClientDatabase.updateClient(this, mClient);
    }

    @Override
    public void onPhotosReady(ArrayList<AttributedPhoto> attributedPhotos) {

    }

    @Override
    public void onSinglePhotoReady(AttributedPhoto attributedPhoto, int position) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

}
