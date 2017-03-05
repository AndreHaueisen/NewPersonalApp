package com.andrehaueisen.fitx.personal.drawer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.models.Gym;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;


public class ProfessionalProfileActivity extends AppCompatActivity {

    private final String TAG = ProfessionalProfileActivity.class.getSimpleName();

    public interface OnProfileDataReadyCallback {
        void saveDataOnDatabase();
    }

    public interface PlaceFragmentCallback{
        void onPlaceSelected(Gym gym);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_professional_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.professional_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setAutocompletePlaceFragment();
    }

    private void setAutocompletePlaceFragment(){

        final PlaceFragmentCallback placeFragmentCallback = (PlaceFragmentCallback) getSupportFragmentManager().findFragmentById(R.id.work_places_fragment);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = telephonyManager.getNetworkCountryIso();

        AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry(countryIso).setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT).build();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setFilter(filter);
        autocompleteFragment.setHint("Choose working places");

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

                Gym gym = new Gym(placeId, name, address, latitude, longitude);

                placeFragmentCallback.onPlaceSelected(gym);
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

        switch (item.getItemId()){

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_confirm_changes_menu_button:
                saveProfileInformation();
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return true;
        }
    }

    private void saveProfileInformation(){

        OnProfileDataReadyCallback profileDataReadyCallback;

        profileDataReadyCallback = (OnProfileDataReadyCallback) getSupportFragmentManager().findFragmentById(R.id.personal_basic_presentation_fragment);
        profileDataReadyCallback.saveDataOnDatabase();

        profileDataReadyCallback = (OnProfileDataReadyCallback) getSupportFragmentManager().findFragmentById(R.id.work_places_fragment);
        profileDataReadyCallback.saveDataOnDatabase();

        profileDataReadyCallback = (OnProfileDataReadyCallback) getSupportFragmentManager().findFragmentById(R.id.specialties_fragment);
        profileDataReadyCallback.saveDataOnDatabase();
    }
}
