package com.andrehaueisen.fitx.personal.drawer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.personal.adapters.ReviewAdapter;
import com.andrehaueisen.fitx.pojo.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

public class PersonalReviewsActivity extends AppCompatActivity implements ValueEventListener {

    private ArrayList<Review> mReviews = new ArrayList<>();
    private RecyclerView mReviewRecyclerView;
    private ReviewAdapter mReviewAdapter;

    private DatabaseReference mDatabaseReference;
    private boolean mHasScreenRotated = false;

    //TODO check if there is not a better way to save state (try removing mHasScreenRotated)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            mReviews = savedInstanceState.getParcelableArrayList(Constants.REVIEWS_SAVED_STATE_KEY);
            mHasScreenRotated = savedInstanceState.getBoolean(Constants.HAS_SCREEN_ROTATED_SAVED_STATE_KEY);
        }

        setContentView(R.layout.activity_personal_reviews);

        Toolbar toolbar = (Toolbar) findViewById(R.id.personal_reviews_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mReviewRecyclerView = (RecyclerView) findViewById(R.id.personal_reviews_recycler_view);
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewRecyclerView.setItemAnimator(new SlideInRightAnimator());

        mReviewAdapter = new ReviewAdapter(this, mReviews);
        mReviewRecyclerView.setAdapter(mReviewAdapter);

    }


    @Override
    public void onStart() {
        super.onStart();

        String personalKey = Utils.getSharedPreferences(this).getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child(Constants.FIREBASE_LOCATION_PERSONAL_REVIEWS).child(personalKey).addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if(!mHasScreenRotated) {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Review review = snapshot.getValue(Review.class);
                    mReviews.add(review);
                    mReviewAdapter.notifyItemInserted(mReviews.size());
                }

            } else {
                Utils.generateInfoToast(PersonalReviewsActivity.this, getString(R.string.no_reviews_yet)).show();
            }
        }else {
            mHasScreenRotated = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mHasScreenRotated = true;
        outState.putParcelableArrayList(Constants.REVIEWS_SAVED_STATE_KEY, mReviews);
        outState.putBoolean(Constants.HAS_SCREEN_ROTATED_SAVED_STATE_KEY, mHasScreenRotated);
    }

    @Override
    protected void onStop() {
        mDatabaseReference.removeEventListener(this);
        super.onStop();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
