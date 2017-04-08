package com.andrehaueisen.fitx.client.firebase;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.andrehaueisen.fitx.utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by andre on 10/12/2016.
 */

public class FirebaseProfileImageCatcher {

    private final String TAG = FirebaseProfileImageCatcher.class.getSimpleName();

    private FirebaseProfileCallback mProfileCallback;
    private FirebaseOnArrayProfileCallback mOnArrayProfileCallback;
    private FirebasePersonalProfileCallback mPersonalProfileCallback;

    public interface FirebaseProfileCallback {
        void onProfileImageReady(byte[] personProfileImage);
    }

    public interface FirebaseOnArrayProfileCallback {
        void onProfileImageReady(@Nullable byte[] personProfileImage, int positionOnArray);
    }

    public interface FirebasePersonalProfileCallback{
        void onProfileImageReady(byte[] personProfileImage, String personalKey);
    }

    public FirebaseProfileImageCatcher(RecyclerView.ViewHolder viewHolder) {
        mProfileCallback = (FirebaseProfileCallback) viewHolder;
    }

    public FirebaseProfileImageCatcher(Fragment fragment){
        mOnArrayProfileCallback = (FirebaseOnArrayProfileCallback) fragment;
    }

    public FirebaseProfileImageCatcher(Activity activity){
        mPersonalProfileCallback = (FirebasePersonalProfileCallback) activity;
    }

    public void getPersonalProfilePicture(String personalKey, final int positionOnArray){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_ROOT_URL)
                .child("personalTrainer/" + personalKey + "/" + Constants.PERSONAL_PROFILE_PICTURE_NAME);


        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()) {
                    byte[] imageByteArray = task.getResult();
                    mOnArrayProfileCallback.onProfileImageReady(imageByteArray, positionOnArray);
                } else {
                    Log.e(TAG, task.getException().getMessage() + " Not image found!!");
                    mOnArrayProfileCallback.onProfileImageReady(null, 0);
                }
            }
        });
    }



    public void getPersonalProfilePicture(Activity activity, String personalKey){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_ROOT_URL)
                .child("personalTrainer/" + personalKey + "/" + Constants.PERSONAL_PROFILE_PICTURE_NAME);


        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnCompleteListener(activity, new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()) {
                    byte[] imageByteArray = task.getResult();
                    mProfileCallback.onProfileImageReady(imageByteArray);
                } else {
                    Log.e(TAG, task.getException().getMessage() + " Not image found!!");
                    mProfileCallback.onProfileImageReady(null);
                }
            }
        });

    }

    public void getPersonalProfilePictureWithKey(Activity activity, final String personalKey){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_ROOT_URL)
                .child("personalTrainer/" + personalKey + "/" + Constants.PERSONAL_PROFILE_PICTURE_NAME);


        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnCompleteListener(activity, new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()) {
                    byte[] imageByteArray = task.getResult();
                    mPersonalProfileCallback.onProfileImageReady(imageByteArray, personalKey);
                } else {
                    Log.e(TAG, task.getException().getMessage() + " Not image found!!");
                    mPersonalProfileCallback.onProfileImageReady(null, null);
                }
            }
        });

    }

    public void getClientProfilePicture(Activity activity, String clientKey, final int positionOnArray){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_ROOT_URL)
                .child("client/" + clientKey + "/" + Constants.CLIENT_PROFILE_PICTURE_NAME);


        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnCompleteListener(activity, new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()) {
                    byte[] imageByteArray = task.getResult();
                    mOnArrayProfileCallback.onProfileImageReady(imageByteArray, positionOnArray);
                } else {
                    Log.e(TAG, task.getException().getMessage() + " Not image found!!");
                    mOnArrayProfileCallback.onProfileImageReady(null, 0);
                }
            }
        });
    }
}
