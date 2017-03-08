package com.andrehaueisen.fitx.client;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.client.adapters.DrawerAdapter;
import com.andrehaueisen.fitx.client.drawer.ClientProfileActivity;
import com.andrehaueisen.fitx.client.search.PersonalSearchActivity;
import com.andrehaueisen.fitx.client.services.ClassUpdateListenerService;
import com.andrehaueisen.fitx.models.ClassReceipt;
import com.andrehaueisen.fitx.register.SignInActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.matrixxun.starry.badgetextview.MaterialBadgeTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.andrehaueisen.fitx.R.string.upcoming_classes_fragment_tag;

public class ClientActivity extends AppCompatActivity {

    private final String TAG = ClientActivity.class.getSimpleName();

    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<ClassReceipt> mClassReceipts = new ArrayList<>();

    @BindView(R.id.badge_text_view) MaterialBadgeTextView mBadgeTextView;
    @BindView(R.id.client_drawer_layout) DrawerLayout mDrawer;
    @BindView(R.id.drawer_options_list_vew) RecyclerView mDrawerRecyclerView;
    @Nullable @BindView(R.id.class_filter_image_view) ImageView mClassFilterImageView;

    private ActionBarDrawerToggle mBarDrawerToggle;
    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_client);

        ButterKnife.bind(this);

        mDrawerRecyclerView.setHasFixedSize(true);
        mDrawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment toBeConfirmedClassesFragment = fragmentManager.findFragmentByTag(getString(R.string.to_be_confirmed_classes_fragment_tag));

        if(toBeConfirmedClassesFragment == null) {
            toBeConfirmedClassesFragment = ToBeConfirmedClientClassesFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.classes_fragment_container, toBeConfirmedClassesFragment, getString(R.string.to_be_confirmed_classes_fragment_tag)).commitNow();
        }

        if(Utils.getSmallestScreenWidth(this) < 600) {
            mClassFilterImageView.setOnClickListener(classFilterClickListener);
        } else {
            setupClassesFragmentOnTablet();
        }

        String clientKey = Utils.getSharedPreferences(this).getString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, null);
        mDatabaseReference.child(Constants.FIREBASE_LOCATION_CLASSES_RECEIPT).child(clientKey).addValueEventListener(mMissingReviewClassesListener);

        setAdapter();
        setToolbar();
        setDrawer();
        startClassListenerService();
    }

    private View.OnClickListener classFilterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment toBeConfirmedClassesFragment = fragmentManager.findFragmentByTag(getString(R.string.to_be_confirmed_classes_fragment_tag));
            Fragment upcomingClassesFragment = fragmentManager.findFragmentByTag(getString(upcoming_classes_fragment_tag));


            if(toBeConfirmedClassesFragment == null){
                toBeConfirmedClassesFragment = ToBeConfirmedClientClassesFragment.newInstance();
                fragmentTransaction.add(R.id.classes_fragment_container, toBeConfirmedClassesFragment, getString(R.string.to_be_confirmed_classes_fragment_tag));
            }

            if(upcomingClassesFragment == null){
                upcomingClassesFragment = ConfirmedClientClassesFragment.newInstance();
                fragmentTransaction.add(R.id.classes_fragment_container, upcomingClassesFragment, getString(upcoming_classes_fragment_tag));
            }

            fragmentTransaction.commit();

            Drawable classFilterDrawable;
            if(toBeConfirmedClassesFragment.isVisible()) {

                classFilterDrawable = getDrawable(R.drawable.class_status_animated_vector_drawable_end_to_start);
                mClassFilterImageView.setImageDrawable(classFilterDrawable);

                if (classFilterDrawable instanceof Animatable) {
                    ((Animatable) classFilterDrawable).start();
                }
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.hide(toBeConfirmedClassesFragment);
                transaction.show(upcomingClassesFragment);
                transaction.commit();

            } else {

                classFilterDrawable = getDrawable(R.drawable.class_status_animated_vector_drawable_start_to_end);
                mClassFilterImageView.setImageDrawable(classFilterDrawable);

                if(classFilterDrawable instanceof Animatable)
                    ((Animatable) classFilterDrawable).start();

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.hide(upcomingClassesFragment);
                transaction.show(toBeConfirmedClassesFragment);
                transaction.commit();

            }

        }
    };

    private void setupClassesFragmentOnTablet(){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment toBeConfirmedClassesFragment = fragmentManager.findFragmentByTag(getString(R.string.to_be_confirmed_classes_fragment_tag));
        Fragment upcomingClassesFragment = fragmentManager.findFragmentByTag(getString(upcoming_classes_fragment_tag));

        if(toBeConfirmedClassesFragment == null){
            toBeConfirmedClassesFragment = ToBeConfirmedClientClassesFragment.newInstance();
            fragmentTransaction.add(R.id.classes_fragment_container, toBeConfirmedClassesFragment, getString(R.string.to_be_confirmed_classes_fragment_tag));
        }

        if(upcomingClassesFragment == null){
            upcomingClassesFragment = ConfirmedClientClassesFragment.newInstance();
            fragmentTransaction.add(R.id.classes_fragment_container_2, upcomingClassesFragment, getString(upcoming_classes_fragment_tag));
        }

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    private void setAdapter(){

        String[] drawerTitles = getResources().getStringArray(R.array.client_drawer_list);
        String[] drawerIconsNames = getResources().getStringArray(R.array.client_drawer_icons_list);
        int[] iconsId = getIconsId(drawerIconsNames);

        DrawerAdapter drawerAdapter = new DrawerAdapter(ClientActivity.this, drawerTitles, iconsId);
        mDrawerRecyclerView.setAdapter(drawerAdapter);
    }

    private int[] getIconsId(String[] iconsNames){

        int[] iconsId = new int[iconsNames.length];
        for(int i = 0; i < iconsNames.length; i++){

            int iconId = getResources().getIdentifier(iconsNames[i], "drawable", getPackageName());
            iconsId[i] = iconId;
        }

        return iconsId;
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mBadgeTextView.setBadgeCount(0, false);
        mBadgeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClientActivity.this, ReviewPersonalActivity.class);
                intent.putParcelableArrayListExtra(Constants.CLASS_RECEIPTS_EXTRA_KEY, mClassReceipts);
                startActivity(intent);
            }
        });

        FloatingActionButton searchSpecificButton = ButterKnife.findById(this, R.id.search_personal_fab);
        searchSpecificButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClientActivity.this, PersonalSearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setDrawer(){

        final GestureDetector gestureDetector = new GestureDetector(ClientActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        mDrawerRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = mDrawerRecyclerView.findChildViewUnder(e.getX(), e.getY());

                if(child != null && gestureDetector.onTouchEvent(e)){
                    mDrawer.closeDrawers();
                    actOnDrawerClick(child);
                    return true;
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        
        mBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.open_drawer, R.string.close_drawer);
        mDrawer.addDrawerListener(mBarDrawerToggle);

    }

        private ValueEventListener mMissingReviewClassesListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mClassReceipts = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ClassReceipt classReceipt = snapshot.getValue(ClassReceipt.class);
                    if (!classReceipt.getGotReview()) {
                        mClassReceipts.add(classReceipt);
                    }
                }
                mBadgeTextView.setBadgeCount(mClassReceipts.size());
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void startClassListenerService(){
        Log.i(TAG, "Start service called!!!!!!!!!!!!");
        if (!Utils.isServiceRunning(this, ClassUpdateListenerService.class)) {
            Log.i(TAG, "Service not running reported!!!!!!!!!!!!");
            mServiceIntent = new Intent(this, ClassUpdateListenerService.class);
            startService(mServiceIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return mBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void actOnDrawerClick(View childView){

        int childPosition = mDrawerRecyclerView.getChildAdapterPosition(childView);

        Intent intent;

        switch (childPosition){

            case 0:
                break;

            case 1:
                intent = new Intent(this, ClientProfileActivity.class);
                startActivity(intent);
                break;

            case 2:

                break;

            case 3:

                break;

            case 4:
                break;

            case 5:

                if(mServiceIntent != null) {
                    stopService(mServiceIntent);
                }

                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        cleanConfig();
                        Utils.generateInfoToast(ClientActivity.this, getString(R.string.logged_out)).show();
                        Intent backToBeginIntent = new Intent(ClientActivity.this, SignInActivity.class);
                        startActivity(backToBeginIntent);
                        finish();
                    }
                });

            default:
                break;
        }
    }

    private void cleanConfig(){
        Utils.getSharedPreferences(this).edit().clear().apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseReference.removeEventListener(mMissingReviewClassesListener);
    }
}
