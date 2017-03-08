package com.andrehaueisen.fitx.client.firebase;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.andrehaueisen.fitx.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by andre on 3/6/2017.
 */

public class FirebaseBackgroundImageCatcher {

    private static final String TAG = FirebaseBackgroundImageCatcher.class.getSimpleName();

    private FirebaseBackgroundCallback mFirebaseBackgroundCallback;

    public FirebaseBackgroundImageCatcher(Activity activity) {
        mFirebaseBackgroundCallback = (FirebaseBackgroundCallback) activity;
    }

    public FirebaseBackgroundImageCatcher(Fragment fragment) {
        mFirebaseBackgroundCallback = (FirebaseBackgroundCallback) fragment;
    }

    public interface FirebaseBackgroundCallback{
        void onBackgroundImageReady(byte[] personBackgroundImage, String personalKey);
    }

    public void getPersonalProfilePictureWithKey(Activity activity, final String personalKey){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_ROOT_URL)
                .child("personalTrainer/" + personalKey + "/" + Constants.PERSONAL_BACKGROUND_PICTURE_NAME);


        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnCompleteListener(activity, new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()) {
                    byte[] imageByteArray = task.getResult();
                    mFirebaseBackgroundCallback.onBackgroundImageReady(imageByteArray, personalKey);
                } else {
                    Log.e(TAG, task.getException().getMessage() + " Not image found!!");
                    mFirebaseBackgroundCallback.onBackgroundImageReady(null, null);
                }
            }
        });

    }
}
