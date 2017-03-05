package com.andrehaueisen.fitx.client;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.client.adapters.ExpandableClassesAdapter;
import com.andrehaueisen.fitx.client.firebase.FirebaseImageCatcher;
import com.andrehaueisen.fitx.models.ClassDetailed;
import com.andrehaueisen.fitx.models.ClassReceipt;
import com.andrehaueisen.fitx.models.ClassResume;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

/**
 * Created by andre on 10/23/2016.
 */

public class ReviewPersonalActivity extends AppCompatActivity implements FirebaseImageCatcher.FirebaseCallback {

    private final String TAG = ReviewPersonalActivity.class.getSimpleName();

    private ArrayList<ClassReceipt> mClassReceipts;
    private RecyclerView mRecyclerView;
    private ExpandableClassesAdapter mExpandableClassesAdapter;
    //private ProgressDialog mProgressDialog;

    //private HashMap<String, ArrayList<byte[]>> mHeadBodyPersonalPicsHM = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_review_personal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.client_review_personal_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.review_personal_recycler_view);
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
        FirebaseImageCatcher imageCatcher = new FirebaseImageCatcher(this);

        ArrayList<String> personalKeys = new ArrayList<>();

        for (ClassReceipt classReceipt : mClassReceipts){
            personalKeys.add(classReceipt.getPersonalKey());
        }

        ArrayList<String> uniquePersonalKeys = new ArrayList<>(new HashSet<>(personalKeys));

        for(String personalKey : uniquePersonalKeys){
            imageCatcher.getPersonalProfilePictureWithKey(this, personalKey);
        }
    }

    private void setupAdapter() {
        ArrayList<ClassResume> classResumes = setClassResumes();
        mExpandableClassesAdapter = new ExpandableClassesAdapter(classResumes, this, mClassReceipts);
        mRecyclerView.setAdapter(mExpandableClassesAdapter);
        mRecyclerView.setHasFixedSize(true);

    }

    private ArrayList<ClassResume> setClassResumes() {

        ArrayList<ClassResume> classResumes = new ArrayList<>();

        for (ClassReceipt classReceipt : mClassReceipts) {

            String personalName = classReceipt.getPersonalName();
            String classDate = Utils.getWrittenDateFromDateCode(this, classReceipt.getDateCode());
            String classPlaceName = classReceipt.getPlaceName();
            Bitmap personalProfileImage = classReceipt.getPersonalProfileImage();

            ClassDetailed classDetailed = new ClassDetailed(personalProfileImage, personalName, classDate, classPlaceName);

            ArrayList<ClassDetailed> detailedClasses = new ArrayList<>();
            detailedClasses.add(classDetailed);

            ClassResume classResume = new ClassResume(personalProfileImage, personalName, classDate, classPlaceName, detailedClasses);

            classResumes.add(classResume);
        }

        //mProgressDialog.dismiss();

        return classResumes;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(Constants.RECYCLER_VIEW_SAVED_STATE_KEY, mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onProfileImageReady(byte[] personProfileImage, String personalKey) {
        new LoadImageTask(personalKey).execute(personProfileImage);
    }

    private class LoadImageTask extends AsyncTask<byte[], Void, Integer> {

        private String mPersonalKey;

        LoadImageTask(String personalKey){
            mPersonalKey = personalKey;
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
                            mClassReceipts.get(position).setPersonalProfileImage(Glide.with(ReviewPersonalActivity.this).load(image).asBitmap().into(100, 100).get());
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
            mExpandableClassesAdapter.notifyParentChanged(position);
        }
    }

    @Override
    public void onProfileImageReady(byte[] personProfileImage) {

    }

    @Override
    public void onPersonalPicsReady(String classKey, ArrayList<byte[]> personPhotos) {
    }

    @Override
    public void onFrontBodyImageReady(byte[] personFrontImage) {

    }

    @Override
    public void onProfileImageReady(byte[] personProfileImage, int positionOnArray) {

    }
}
