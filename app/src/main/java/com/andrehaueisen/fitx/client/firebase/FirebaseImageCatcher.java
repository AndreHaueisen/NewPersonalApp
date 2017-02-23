package com.andrehaueisen.fitx.client.firebase;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.andrehaueisen.fitx.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by andre on 10/12/2016.
 */

public class FirebaseImageCatcher {

    private final String TAG = FirebaseImageCatcher.class.getSimpleName();

    private FirebaseCallback mFirebaseCallback;

    public interface FirebaseCallback {
        void onProfileImageReady(byte[] personProfileImage);
        void onProfileImageReady(byte[] personProfileImage, int positionOnArray);
        void onProfileImageReady(byte[] personProfileImage, String personalKey);
        void onFrontBodyImageReady(byte [] personFrontImage);
        void onPersonalPicsReady(String classKey, ArrayList<byte[]> personPhotos);
    }

    public FirebaseImageCatcher(RecyclerView.ViewHolder viewHolder) {
        mFirebaseCallback = (FirebaseCallback) viewHolder;
    }

    public FirebaseImageCatcher(Fragment fragment){
        mFirebaseCallback = (FirebaseCallback) fragment;
    }

    public FirebaseImageCatcher(Activity activity){
        mFirebaseCallback = (FirebaseCallback) activity;
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
                    mFirebaseCallback.onProfileImageReady(imageByteArray, positionOnArray);
                } else {
                    Log.e(TAG, task.getException().getMessage() + " Not image found!!");
                    mFirebaseCallback.onProfileImageReady(null);
                }
            }
        });
    }

    public void getPersonalProfilePicture(Activity activity, String personalKey, final int positionOnArray){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_ROOT_URL)
                .child("personalTrainer/" + personalKey + "/" + Constants.PERSONAL_PROFILE_PICTURE_NAME);


        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnCompleteListener(activity, new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()) {
                    byte[] imageByteArray = task.getResult();
                    mFirebaseCallback.onProfileImageReady(imageByteArray, positionOnArray);
                } else {
                    Log.e(TAG, task.getException().getMessage() + " Not image found!!");
                    mFirebaseCallback.onProfileImageReady(null);
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
                    mFirebaseCallback.onProfileImageReady(imageByteArray);
                } else {
                    Log.e(TAG, task.getException().getMessage() + " Not image found!!");
                    mFirebaseCallback.onProfileImageReady(null);
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
                    mFirebaseCallback.onProfileImageReady(imageByteArray, personalKey);
                } else {
                    Log.e(TAG, task.getException().getMessage() + " Not image found!!");
                    mFirebaseCallback.onProfileImageReady(null);
                }
            }
        });

    }

    public void getClientProfilePicture(Activity activity, String clientKey){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_ROOT_URL)
                .child("client/" + clientKey + "/" + Constants.CLIENT_PROFILE_PICTURE_NAME);


        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnCompleteListener(activity, new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()) {
                    byte[] imageByteArray = task.getResult();
                    mFirebaseCallback.onProfileImageReady(imageByteArray);
                } else {
                    Log.e(TAG, task.getException().getMessage() + " Not image found!!");
                    mFirebaseCallback.onProfileImageReady(null);
                }
            }
        });
    }

    public void getClientProfilePicture(String clientKey, final int positionOnArray){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_ROOT_URL)
                .child("client/" + clientKey + "/" + Constants.CLIENT_PROFILE_PICTURE_NAME);


        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()) {
                    byte[] imageByteArray = task.getResult();
                    mFirebaseCallback.onProfileImageReady(imageByteArray, positionOnArray);
                } else {
                    Log.e(TAG, task.getException().getMessage() + " Not image found!!");
                    mFirebaseCallback.onProfileImageReady(null);
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
                    mFirebaseCallback.onProfileImageReady(imageByteArray, positionOnArray);
                } else {
                    Log.e(TAG, task.getException().getMessage() + " Not image found!!");
                    mFirebaseCallback.onProfileImageReady(null);
                }
            }
        });
    }

    public void getFrontBodyPicture(Activity activity, String personalKey){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_ROOT_URL)
                .child("personalTrainer/" + personalKey + "/" + Constants.CLIENT_PROFILE_PICTURE_NAME);


        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnCompleteListener(activity, new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()) {
                    byte[] imageByteArray = task.getResult();
                    mFirebaseCallback.onFrontBodyImageReady(imageByteArray);
                }else {
                    Log.e(TAG, task.getException().getMessage() + " Not image found!!");
                    mFirebaseCallback.onFrontBodyImageReady(null);
                }
            }
        });
    }

 /*   public void getAllPersonalImages(final Activity activity, final String personalKey, final String classKey){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_ROOT_URL)
                .child("personalTrainer/" + personalKey + "/" + Constants.PERSONAL_PROFILE_PICTURE_NAME);


        final long ONE_MEGABYTE = 1024 * 1024;


        storageReference.getBytes(ONE_MEGABYTE).addOnCompleteListener(activity, new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()) {
                    byte[] headImageByteArray = task.getResult();
                    goForFrontBodyImage(activity, personalKey, classKey, headImageByteArray);
                } else {
                    byte[] noPictureFound = null;
                    goForFrontBodyImage(activity, personalKey, classKey, noPictureFound);
                    Log.e(TAG, task.getException().getMessage() + " Not image found!!");
                }
            }
        });
    } */

    /*public void goForFrontBodyImage(Activity activity, String personalKey, final String classKey, final byte[] headImageByteArray){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_ROOT_URL)
                .child("personalTrainer/" + personalKey + "/" + //WRONG!! Constants.PERSONAL_PROFILE_PICTURE_NAME //WRONG!!);

        final ArrayList<byte[]> photosArray = new ArrayList<>();
        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnCompleteListener(activity, new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()) {

                    byte[] bodyImageByteArray = task.getResult();

                    ArrayList<byte[]> photosArray = new ArrayList<>();

                    photosArray.add(headImageByteArray);
                    photosArray.add(bodyImageByteArray);

                    mFirebaseCallback.onPersonalPicsReady(classKey, photosArray);
                }else {
                    photosArray.add(headImageByteArray);
                    photosArray.add(null);
                    mFirebaseCallback.onPersonalPicsReady(classKey, photosArray);
                    Log.e(TAG, task.getException().getMessage() + " Failed to load images");
                }
            }
        });
    }*/
}
