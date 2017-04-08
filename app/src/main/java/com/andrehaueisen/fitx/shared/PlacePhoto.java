package com.andrehaueisen.fitx.shared;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.andrehaueisen.fitx.utilities.Utils;
import com.andrehaueisen.fitx.models.AttributedPhoto;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

/**
 * Created by andre on 11/3/2016.
 */

public class PlacePhoto implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    public interface PlacePhotosCallback {
        void onPhotosReady(ArrayList<AttributedPhoto> attributedPhotos);

        void onSinglePhotoReady(AttributedPhoto attributedPhoto);

        void onSinglePhotoReady(AttributedPhoto attributedPhoto, int position);
    }

    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private PlacePhotosCallback mPlacePhotosCallback;

    public PlacePhoto(GoogleApiClient googleApiClient, Fragment fragment) {
        mContext = fragment.getContext();
        mGoogleApiClient = googleApiClient;
        mPlacePhotosCallback = (PlacePhotosCallback) fragment;
    }

    public PlacePhoto(GoogleApiClient googleApiClient, Activity activity) {
        mContext = activity;
        mGoogleApiClient = googleApiClient;
        mPlacePhotosCallback = (PlacePhotosCallback) activity;
    }

    public void initializeTask(ArrayList<String> placeIds, boolean isMultipleInsert) {
        new PlacePhotoTask(isMultipleInsert).execute(placeIds);
    }

    public void initializeTask(ArrayList<String> placeIds, boolean isMultipleInsert, int position) {
        new PlacePhotoTask(isMultipleInsert, position).execute(placeIds);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Utils.generateSuccessToast(mContext, "Google api places services connected!").show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Utils.generateErrorToast(mContext, "Api places services failed!").show();
    }

    class PlacePhotoTask extends AsyncTask<ArrayList<String>, Void, ArrayList<AttributedPhoto>> {

        private ArrayList<AttributedPhoto> mAttributedPhotos;
        private int mWidth;
        private int mHeight;
        private int mPosition = -1;
        private boolean mIsMultipleInsert;

        public PlacePhotoTask(boolean isMultipleInsert) {
            mAttributedPhotos = new ArrayList<>();
            mWidth = 500;
            mHeight = 500;
            mIsMultipleInsert = isMultipleInsert;
        }

        public PlacePhotoTask(boolean isMultipleInsert, int position) {
            mAttributedPhotos = new ArrayList<>();
            mWidth = 500;
            mHeight = 500;
            mIsMultipleInsert = isMultipleInsert;
            mPosition = position;
        }

        @Override
        protected ArrayList<AttributedPhoto> doInBackground(ArrayList<String>... strings) {

            if (strings.length != 1) {
                return null;
            }

            ArrayList<String> placeIds = strings[0];

            for (String placeId : placeIds) {

                AttributedPhoto attributedPhoto = null;

                PlacePhotoMetadataResult result = Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId).await();

                if (result.getStatus().isSuccess()) {
                    PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();

                    if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {

                        PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                        CharSequence attribution = photo.getAttributions();

                        Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                                .getBitmap();

                        attributedPhoto = new AttributedPhoto(attribution, image);

                    }

                    photoMetadataBuffer.release();
                }
                mAttributedPhotos.add(attributedPhoto);
            }

            return mAttributedPhotos;
        }

        @Override
        protected void onPostExecute(ArrayList<AttributedPhoto> attributedPhotos) {

            if (mIsMultipleInsert) {
                mPlacePhotosCallback.onPhotosReady(attributedPhotos);
            } else {

                if (mPosition != -1)
                    mPlacePhotosCallback.onSinglePhotoReady(attributedPhotos.get(0), mPosition);
                else
                    mPlacePhotosCallback.onSinglePhotoReady(attributedPhotos.get(0));
            }

        }
    }
}
