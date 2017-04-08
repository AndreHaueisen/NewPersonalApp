package com.andrehaueisen.fitx.client;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.andrehaueisen.fitx.utilities.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.client.adapters.ExpandableClassesAdapter;
import com.andrehaueisen.fitx.client.firebase.FirebaseBackgroundImageCatcher;
import com.andrehaueisen.fitx.client.firebase.FirebaseProfileImageCatcher;
import com.andrehaueisen.fitx.models.ClassReceipt;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andre on 10/23/2016.
 */

public class ReviewPersonalActivity extends AppCompatActivity implements FirebaseProfileImageCatcher.FirebasePersonalProfileCallback, FirebaseBackgroundImageCatcher.FirebaseBackgroundCallback {

    private final String TAG = ReviewPersonalActivity.class.getSimpleName();

    private ArrayList<ClassReceipt> mClassReceipts;
    private ExpandableClassesAdapter mExpandableClassesAdapter;
    @BindView(R.id.review_personal_recycler_view) RecyclerView mRecyclerView;

    //private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_review_personal);
        ButterKnife.bind(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.client_review_personal_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mClassReceipts = getIntent().getParcelableArrayListExtra(Constants.CLASS_RECEIPTS_EXTRA_KEY);
        if(mClassReceipts != null && mClassReceipts.size() != 0){
            setupAdapter();
        }else{
            //TODO put placeHolder
        }

        if(savedInstanceState != null){
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(Constants.RECYCLER_VIEW_SAVED_STATE_KEY));
        }else{
            getPersonalPictures();
        }

       /* mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.loading_past_classes));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show(); */

    }

    private void getPersonalPictures() {
        FirebaseProfileImageCatcher profileImageCatcher = new FirebaseProfileImageCatcher(this);
        FirebaseBackgroundImageCatcher backgroundImageCatcher = new FirebaseBackgroundImageCatcher(this);

        ArrayList<String> personalKeys = new ArrayList<>();

        for (ClassReceipt classReceipt : mClassReceipts){
            personalKeys.add(classReceipt.getPersonalKey());
        }

        ArrayList<String> uniquePersonalKeys = new ArrayList<>(new HashSet<>(personalKeys));

        for(String personalKey : uniquePersonalKeys){
            profileImageCatcher.getPersonalProfilePictureWithKey(this, personalKey);
            backgroundImageCatcher.getPersonalProfilePictureWithKey(this, personalKey);
        }
    }

    private void setupAdapter() {
        mExpandableClassesAdapter = new ExpandableClassesAdapter(this, mClassReceipts);
        mRecyclerView.setAdapter(mExpandableClassesAdapter);
        mRecyclerView.setHasFixedSize(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(Constants.RECYCLER_VIEW_SAVED_STATE_KEY, mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onProfileImageReady(byte[] personProfileImage, String personalKey) {
        new LoadImageTask(personalKey, true).execute(personProfileImage);
    }

    @Override
    public void onBackgroundImageReady(byte[] personBackgroundImage, String personalKey) {
        new LoadImageTask(personalKey, false).execute(personBackgroundImage);
    }

    private class LoadImageTask extends AsyncTask<byte[], Void, Integer> {

        private String mPersonalKey;
        private boolean mIsProfileImage;

        LoadImageTask(String personalKey, boolean isProfileImage){
            mPersonalKey = personalKey;
            mIsProfileImage = isProfileImage;
        }

        @Override
        protected Integer doInBackground(byte[]... params) {

            byte[] image = params[0];
            int position = 0;
            try {
                if (image != null && image.length != 0) {
                    ClassReceipt classReceipt;

                    for (position = 0; position < mClassReceipts.size(); position++) {
                        classReceipt = mClassReceipts.get(position);
                        if(classReceipt.getPersonalKey().equals(mPersonalKey)){
                            if(mIsProfileImage) {
                                mClassReceipts.get(position).setPersonalProfileImage(Glide.with(ReviewPersonalActivity.this).load(image).asBitmap().into(100, 100).get());
                            }else{
                                mClassReceipts.get(position).setPersonalBackgroundImage(Glide.with(ReviewPersonalActivity.this).load(image).asBitmap().into(640, 360).get());
                            }

                            position++;
                            break;
                        }
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }

            return position-1;
        }

        @Override
        protected void onPostExecute(Integer position) {
            super.onPostExecute(position);
            mExpandableClassesAdapter.notifyItemChanged(position);
        }
    }
}
