package com.andrehaueisen.fitx.client;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
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
import android.widget.ImageView;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.client.adapters.DrawerAdapter;
import com.andrehaueisen.fitx.client.adapters.SectionPagerAdapterClient;
import com.andrehaueisen.fitx.client.drawer.ClientProfileActivity;
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

public class ClientActivity extends AppCompatActivity {

    private final String TAG = ClientActivity.class.getSimpleName();

    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private MaterialBadgeTextView mBadgeTextView;
    private ArrayList<ClassReceipt> mClassReceipts = new ArrayList<>();
    private DrawerLayout mDrawer;
    private RecyclerView mDrawerRecyclerView;

    private ActionBarDrawerToggle mBarDrawerToggle;
    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_client);

        mDrawerRecyclerView = (RecyclerView) findViewById(R.id.drawer_options_list_vew);
        mDrawerRecyclerView.setHasFixedSize(true);
        mDrawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String clientKey = Utils.getSharedPreferences(this).getString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, null);
        mDatabaseReference.child(Constants.FIREBASE_LOCATION_CLASSES_RECEIPT).child(clientKey).addListenerForSingleValueEvent(mMissingReviewClassesListener);

        setAdapter();
        setToolbar();
        setDrawer();
        setTabLayout();
        startClassListenerService();
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

        mBadgeTextView = (MaterialBadgeTextView) findViewById(R.id.badge_text_view);
        mBadgeTextView.setBadgeCount(0, false);
        mBadgeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClientActivity.this, ReviewPersonalActivity.class);
                intent.putParcelableArrayListExtra(Constants.CLASS_RECEIPTS_EXTRA_KEY, mClassReceipts);
                startActivity(intent);
            }
        });

        ImageView searchSpecificButton = (ImageView) findViewById(R.id.search_specific_image_button);
        searchSpecificButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClientActivity.this, SearchSpecificPersonalActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setDrawer(){

        //  LayoutTransition layoutTransition = new LayoutTransition();
        //  layoutTransition.setDuration(200);
        //  layoutTransition.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
        //  layoutTransition.setStartDelay(LayoutTransition.APPEARING, 0);
        //  layoutTransition.setStartDelay(LayoutTransition.DISAPPEARING, 0);
        //  layoutTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);

        //  AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        //  appBarLayout.setLayoutTransition(layoutTransition);

        mDrawer = (DrawerLayout) findViewById(R.id.client_drawer_layout);

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

        //TODO review ActionBarDrawerToggle last 2 arguments
        mBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.cref_number, R.string.birthday);
        mDrawer.addDrawerListener(mBarDrawerToggle);

    }

    private void setTabLayout(){

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        SectionPagerAdapterClient pagerAdapter = new SectionPagerAdapterClient(getSupportFragmentManager(), this);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

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
}
