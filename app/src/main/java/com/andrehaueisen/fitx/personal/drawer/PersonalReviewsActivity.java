package com.andrehaueisen.fitx.personal.drawer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.andrehaueisen.fitx.utilities.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.utilities.Utils;
import com.andrehaueisen.fitx.models.Review;
import com.andrehaueisen.fitx.personal.adapters.ReviewAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

public class PersonalReviewsActivity extends AppCompatActivity implements ChildEventListener {

    private ArrayList<Review> mReviews = new ArrayList<>();
    private RecyclerView mReviewRecyclerView;
    private ReviewAdapter mReviewAdapter;

    private DatabaseReference mDatabaseReference;
    private boolean mHasScreenRotated = false;

    //TODO check if there is not a better way to save state (try removing mHasScreenRotated)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null){
            mHasScreenRotated = false;
        }else {
            mReviews = savedInstanceState.getParcelableArrayList(Constants.REVIEWS_SAVED_STATE_KEY);
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
        mDatabaseReference.child(Constants.FIREBASE_LOCATION_PERSONAL_REVIEWS).child(personalKey).addChildEventListener(this);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (!mHasScreenRotated) {
            if (dataSnapshot.exists()) {

                Review review = dataSnapshot.getValue(Review.class);
                mReviews.add(review);
                mReviewAdapter.notifyItemInserted(mReviews.size());

            } else {
                Utils.generateInfoToast(PersonalReviewsActivity.this, getString(R.string.no_reviews_yet)).show();
            }
        } else {
            mHasScreenRotated = false;
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mHasScreenRotated = true;
        outState.putParcelableArrayList(Constants.REVIEWS_SAVED_STATE_KEY, mReviews);
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
