package com.andrehaueisen.fitx.personal.drawer;

/*public class MapActivity extends AppCompatActivity implements ValueEventListener {

    private final String TAG = MapActivity.class.getSimpleName();

    private RecyclerView mPlacesRecyclerView;
    private PlacesAdapter mPlacesAdapter;

    private ArrayList<Gym> mPersonalWorkingPlaces;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mPlacesRecyclerView = (RecyclerView) findViewById(R.id.working_places_recycler_view);

        mPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPlacesRecyclerView.setItemAnimator(new SlideInRightAnimator());

        FloatingActionButton addPlaceFAB = (FloatingActionButton) findViewById(R.id.add_place_fab);
        addPlaceFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try{
                    startActivityForResult(builder.build(MapActivity.this), Constants.PLACE_PICKER_REQUEST);
                }catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException nps){
                    Log.e(TAG, "Shit went wrong");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String personalUniqueKey = Utils.getSharedPreferences(this).getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null);
        mDatabase.child(Constants.FIREBASE_LOCATION_GYMS).child(personalUniqueKey).addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        GenericTypeIndicator<ArrayList<Gym>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Gym>>() {};
        if(dataSnapshot.exists()) {
            mPersonalWorkingPlaces = dataSnapshot.getValue(genericTypeIndicator);
        } else {
            mPersonalWorkingPlaces = new ArrayList<>();
        }
        setAdapter(mPersonalWorkingPlaces);
    }

    private void setAdapter(ArrayList<Gym> workingPlaces){

        mPlacesAdapter = new PlacesAdapter(this, workingPlaces, true);
        mPlacesRecyclerView.setAdapter(mPlacesAdapter);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_changes_menu, menu);
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == Constants.PLACE_PICKER_REQUEST){
            if(resultCode == Activity.RESULT_OK){

                Place place = PlacePicker.getPlace(this, data);
                String name = place.getName().toString();
                String address = place.getAddress().toString();
                String placeId = place.getId();

                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);

                Gym personalWorkingPlace = new Gym(placeId, name, address, latitude, longitude);

                mPersonalWorkingPlaces.add(personalWorkingPlace);

                mPlacesAdapter.notifyItemInserted(mPlacesAdapter.getItemCount()+1);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_confirm_changes_menu_button:

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        mDatabase.removeEventListener(this);
        super.onStop();
    }
} */
