package com.andrehaueisen.fitx.personal;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.utilities.Utils;
import com.andrehaueisen.fitx.personal.adapters.DrawerAdapter;
import com.andrehaueisen.fitx.personal.drawer.PersonalReviewsActivity;
import com.andrehaueisen.fitx.personal.drawer.ProfessionalProfileActivity;
import com.andrehaueisen.fitx.personal.drawer.ScheduleArrangementActivity;
import com.andrehaueisen.fitx.personal.services.ClassUpdateListenerService;
import com.andrehaueisen.fitx.register.SignInActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.andrehaueisen.fitx.R.string.upcoming_classes_fragment_tag;

public class PersonalActivity extends AppCompatActivity {

    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 100;
    private final String TAG = PersonalActivity.class.getSimpleName();

    private DrawerLayout mDrawer;
    private RecyclerView mDrawerRecyclerView;
    private ImageView mClassFilterImageView;

    private ActionBarDrawerToggle mBarDrawerToggle;
    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_personal);

        mDrawerRecyclerView = (RecyclerView) findViewById(R.id.drawer_options_list_vew);
        mDrawerRecyclerView.setHasFixedSize(true);
        mDrawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(Utils.getSmallestScreenWidth(this) < 600) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment toBeConfirmedClassesFragment = fragmentManager.findFragmentByTag(getString(R.string.to_be_confirmed_classes_fragment_tag));

            if(toBeConfirmedClassesFragment == null) {
                toBeConfirmedClassesFragment = ToBeConfirmedClassesFragment.newInstance();
                fragmentManager.beginTransaction().add(R.id.classes_fragment_container, toBeConfirmedClassesFragment, getString(R.string.to_be_confirmed_classes_fragment_tag)).commit();
            }

            mClassFilterImageView = (ImageView) findViewById(R.id.class_filter_image_view);
            mClassFilterImageView.setOnClickListener(classFilterClickListener);
        } else {
            setupClassesFragmentOnTablet();
        }

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
                toBeConfirmedClassesFragment = ToBeConfirmedClassesFragment.newInstance();
                fragmentTransaction.add(R.id.classes_fragment_container, toBeConfirmedClassesFragment, getString(R.string.to_be_confirmed_classes_fragment_tag));
            }

            if(upcomingClassesFragment == null){
                upcomingClassesFragment = UpcomingClassesFragment.newInstance();
                fragmentTransaction.add(R.id.classes_fragment_container, upcomingClassesFragment, getString(R.string.upcoming_classes_fragment_tag));
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
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.show(upcomingClassesFragment);
                transaction.commit();

            } else {

                classFilterDrawable = getDrawable(R.drawable.class_status_animated_vector_drawable_start_to_end);
                mClassFilterImageView.setImageDrawable(classFilterDrawable);

                if(classFilterDrawable instanceof Animatable)
                    ((Animatable) classFilterDrawable).start();

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.hide(upcomingClassesFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
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
            toBeConfirmedClassesFragment = ToBeConfirmedClassesFragment.newInstance();
            fragmentTransaction.add(R.id.classes_fragment_container, toBeConfirmedClassesFragment, getString(R.string.to_be_confirmed_classes_fragment_tag));
        }

        if(upcomingClassesFragment == null){
            upcomingClassesFragment = UpcomingClassesFragment.newInstance();
            fragmentTransaction.add(R.id.classes_fragment_container_2, upcomingClassesFragment, getString(upcoming_classes_fragment_tag));
        }

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    private void setAdapter(){

        String[] drawerTitles = getResources().getStringArray(R.array.personal_drawer_list);
        String[] drawerIconsNames = getResources().getStringArray(R.array.personal_drawer_icons_list);
        int[] iconsId = getIconsId(drawerIconsNames);

        DrawerAdapter drawerAdapter = new DrawerAdapter(PersonalActivity.this, drawerTitles, iconsId);
        mDrawerRecyclerView.setAdapter(drawerAdapter);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
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
    }

    private void setDrawer(){

        mDrawer = (DrawerLayout) findViewById(R.id.personal_drawer_layout);

        final GestureDetector gestureDetector = new GestureDetector(PersonalActivity.this, new GestureDetector.SimpleOnGestureListener() {

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

    private void startClassListenerService(){
        Log.i(TAG, "Start service called!!!!!!!!!!!!");
        if (!Utils.isServiceRunning(this, ClassUpdateListenerService.class)) {
            Log.i(TAG, "Service not running reported!!!!!!!!!!!!");
            mServiceIntent = new Intent(this, ClassUpdateListenerService.class);
            startService(mServiceIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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

                intent = new Intent(this, ScheduleArrangementActivity.class);
                startActivity(intent);
                break;

            case 2:

                intent = new Intent(this, ProfessionalProfileActivity.class);
                startActivity(intent);
                break;

            case 3:

                intent = new Intent(this, PersonalReviewsActivity.class);
                startActivity(intent);
                break;

            case 4:


                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        cleanConfig();
                        Utils.generateInfoToast(PersonalActivity.this, getString(R.string.logged_out)).show();

                        if(mServiceIntent != null) {
                            stopService(mServiceIntent);
                        }

                        Intent backToBeginIntent = new Intent(PersonalActivity.this, SignInActivity.class);
                        startActivity(backToBeginIntent);
                        finish();
                    }
                });

                break;
            default:
                break;
        }
    }

    private void cleanConfig(){
        Utils.getSharedPreferences(this).edit().clear().apply();
    }
}
