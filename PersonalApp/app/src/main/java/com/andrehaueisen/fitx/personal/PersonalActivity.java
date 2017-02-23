package com.andrehaueisen.fitx.personal;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
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

import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.personal.adapters.DrawerAdapter;
import com.andrehaueisen.fitx.personal.adapters.TrainerActivityPagerAdapter;
import com.andrehaueisen.fitx.personal.drawer.PersonalReviewsActivity;
import com.andrehaueisen.fitx.personal.drawer.ProfessionalProfileActivity;
import com.andrehaueisen.fitx.personal.drawer.ScheduleArrangementActivity;
import com.andrehaueisen.fitx.register.SignInActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class PersonalActivity extends AppCompatActivity {

    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 100;
    private final String TAG = PersonalActivity.class.getSimpleName();

    private DrawerLayout mDrawer;
    private RecyclerView mDrawerRecyclerView;

    private ActionBarDrawerToggle mBarDrawerToggle;
    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_personal);

        mDrawerRecyclerView = (RecyclerView) findViewById(R.id.drawer_options_list_vew);
        mDrawerRecyclerView.setHasFixedSize(true);
        mDrawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setAdapter();
        setToolbar();
        setDrawer();

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        TrainerActivityPagerAdapter pagerAdapter = new TrainerActivityPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        //startClassListenerService();

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

        //TODO review ActionBarDrawerToggle last 2 arguments
        mBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.cref_number, R.string.birthday);
        mDrawer.addDrawerListener(mBarDrawerToggle);

    }

    /*private void startClassListenerService(){
        Log.i(TAG, "Start service called!!!!!!!!!!!!");
        if (!Utils.isServiceRunning(this, ClassUpdateListenerService.class)) {
            Log.i(TAG, "Service not running reported!!!!!!!!!!!!");
            mServiceIntent = new Intent(this, ClassUpdateListenerService.class);
            startService(mServiceIntent);
        }
    } */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.personal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mBarDrawerToggle.onOptionsItemSelected(item)){
            return true;

        }else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment toBeConfirmedClassesFragment = fragmentManager.findFragmentByTag(getString(R.string.to_be_confirmed_classes_fragment_tag));
            Fragment upcomingClassesFragment = fragmentManager.findFragmentByTag(getString(R.string.upcoming_classes_fragment_tag));

            if(upcomingClassesFragment == null){
                fragmentManager.beginTransaction().add(upcomingClassesFragment, getString(R.string.upcoming_classes_fragment_tag)).commit();
            }

            if(toBeConfirmedClassesFragment == null){
                fragmentManager.beginTransaction().add(toBeConfirmedClassesFragment, getString(R.string.upcoming_classes_fragment_tag)).commit();
            }

            switch(item.getItemId()){
                case R.id.action_show_upcoming:
                    fragmentManager.beginTransaction().show(upcomingClassesFragment).hide(toBeConfirmedClassesFragment);

                case R.id.action_show_to_be_confirmed:
                    fragmentManager.beginTransaction().show(toBeConfirmedClassesFragment).hide(upcomingClassesFragment);
            }

        }

       /* case R.id.action_change_view_menu_button:

        if(mViewPager.getCurrentItem() == 7) {

            mViewPager.setCurrentItem(mLastPagerItem, false);

            final Animation toWeekViewRotateAnimation = new RotateAnimation(-90.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            toWeekViewRotateAnimation.setDuration(400);
            toWeekViewRotateAnimation.setFillAfter(true);

            mToolbar.findViewById(R.id.action_change_view_menu_button).startAnimation(toWeekViewRotateAnimation);

        }else {
            mViewPager.setCurrentItem(7, false);

            final Animation fromWeekViewRotateAnimation = new RotateAnimation(0.0f, -90.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            fromWeekViewRotateAnimation.setDuration(400);
            fromWeekViewRotateAnimation.setFillAfter(true);

            mToolbar.findViewById(R.id.action_change_view_menu_button).startAnimation(fromWeekViewRotateAnimation);
        }

        return true; */

        return true;
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

            case 7:

                if(mServiceIntent != null) {
                    stopService(mServiceIntent);
                }
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        cleanConfig();
                        Utils.generateInfoToast(PersonalActivity.this, getString(R.string.logged_out)).show();
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
