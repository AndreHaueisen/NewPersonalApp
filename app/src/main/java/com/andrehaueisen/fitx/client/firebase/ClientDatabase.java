package com.andrehaueisen.fitx.client.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.models.ClassReceipt;
import com.andrehaueisen.fitx.models.Client;
import com.andrehaueisen.fitx.models.ClientFitClass;
import com.andrehaueisen.fitx.models.PersonalFitClass;
import com.andrehaueisen.fitx.models.PersonalTrainer;
import com.andrehaueisen.fitx.models.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static com.andrehaueisen.fitx.personal.firebase.PersonalDatabase.removeAgendaRestrictions;
import static com.andrehaueisen.fitx.personal.firebase.PersonalDatabase.removeClassFromClientSide;

/**
 * Created by Carla on 14/09/2016.
 */

public class ClientDatabase {

    private static final String TAG = ClientDatabase.class.getSimpleName();

    public static void saveClientToDatabase(final Activity activity, Client client){

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        final String personalUniqueId = Utils.encodeEmail(client.getEmail());

        SharedPreferences sharedPreferences = Utils.getSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, personalUniqueId);
        editor.putString(Constants.SHARED_PREF_CLIENT_NAME, client.getName());
        editor.commit();

        database.child(Constants.FIREBASE_LOCATION_CLIENT).child(personalUniqueId)
                .setValue(client).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG, "Client saved");
            }
        });

    }

    public static void updateClient(final Activity activity, Client client){

        String clientKey = Utils.getSharedPreferences(activity).getString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, null);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> clientHashMap = new HashMap<>();
        clientHashMap.put("mainObjective", client.getMainObjective());
        clientHashMap.put("clientGym", client.getClientGym());
        databaseReference.child(Constants.FIREBASE_LOCATION_CLIENT).child(clientKey).updateChildren(clientHashMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i(TAG, "Client main objective and gym updated");
            }
        });
    }

    public static void scheduleClass(final Activity activity, String personalKey, String dateCode, int classStartCode, int classEndCode){

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        ArrayList<Integer> classStartCodes = new ArrayList<>();
        ArrayList<Integer> classEndCodes = new ArrayList<>();

        classStartCodes.add(classStartCode);
        classEndCodes.add(classEndCode);

        HashMap<String, ArrayList<Integer>> classTimeCodesHashMap = new HashMap<>();
        classTimeCodesHashMap.put(Constants.AGENDA_CODES_START_LIST, classStartCodes);
        classTimeCodesHashMap.put(Constants.AGENDA_CODES_END_LIST, classEndCodes);

        database.child(Constants.FIREBASE_LOCATION_PERSONAL_AGENDA_RESTRICTIONS).child(String.valueOf(dateCode))
                .child(personalKey).setValue(classTimeCodesHashMap).addOnSuccessListener(activity, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utils.generateSuccessToast(activity, activity.getString(R.string.class_scheduled)).show();
                Log.i(TAG, "Class scheduled! YAY!");
            }
        }).addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utils.generateErrorToast(activity, activity.getString(R.string.class_not_scheduled)).show();
                Log.i(TAG, "Error scheduling class! DAMN!");
            }
        });
    }

    public static void scheduleClass(final Activity activity, ArrayList<Integer> restrictionStartTimeCodes, ArrayList<Integer> restrictionEndTimeCodes,
                                     String personalKey, String dateCode, int classStartCode, int classEndCode){

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        restrictionStartTimeCodes.add(classStartCode);
        restrictionEndTimeCodes.add(classEndCode);

        HashMap<String, ArrayList<Integer>> classTimeCodesHashMap = new HashMap<>();
        classTimeCodesHashMap.put(Constants.AGENDA_CODES_START_LIST, restrictionStartTimeCodes);
        classTimeCodesHashMap.put(Constants.AGENDA_CODES_END_LIST, restrictionEndTimeCodes);

        database.child(Constants.FIREBASE_LOCATION_PERSONAL_AGENDA_RESTRICTIONS).child(dateCode)
                .child(personalKey).setValue(classTimeCodesHashMap).addOnSuccessListener(activity, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utils.generateSuccessToast(activity, activity.getString(R.string.class_scheduled)).show();
                Log.i(TAG, "Class scheduled! YAY!");
            }
        }).addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utils.generateErrorToast(activity, activity.getString(R.string.class_not_scheduled)).show();
                Log.i(TAG, "Error scheduling class! DAMN!");
            }
        });
    }

    public static void saveClassInformation(final Activity activity, PersonalFitClass personalFitClass, final String personalKey, String personalName){

        SharedPreferences sharedPreferences = Utils.getSharedPreferences(activity);
        final String clientKey = sharedPreferences.getString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, null);
        String clientName = sharedPreferences.getString(Constants.SHARED_PREF_CLIENT_NAME, null);
        personalFitClass.setClientKey(clientKey);
        personalFitClass.setClientName(clientName);

        final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        //fitClassHashMap.put(Constants.FIREBASE_LOCATION_CLIENT_CLASSES_KEYS + "/" + clientKey, personalFitClass)

        final String classKey = databaseRef.push().getKey();
        personalFitClass.setClassKey(classKey);

        ClientFitClass clientFitClass = Utils.extractClientFitClass(personalFitClass, personalKey, personalName);

        HashMap<String, Object> personalFitClassHashMap = new HashMap<>();
        personalFitClassHashMap.put(classKey, personalFitClass);

        HashMap<String, Object> clientFitClassHashMap = new HashMap<>();
        clientFitClassHashMap.put(classKey, clientFitClass);

        databaseRef.child(Constants.FIREBASE_LOCATION_PERSONAL_CLASSES).child(personalKey).updateChildren(personalFitClassHashMap).addOnSuccessListener(activity, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Class saved on personal side");
            }
        }).addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "Class not saved on personal side. BUahh!");
            }
        });

        databaseRef.child(Constants.FIREBASE_LOCATION_CLIENT_CLASSES).child(clientKey).updateChildren(clientFitClassHashMap).addOnSuccessListener(activity, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Class saved on client side");
            }
        }).addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "Class not saved on client side. BUahh!");
            }
        });
    }

    public static void saveClassReceipt(final ClassReceipt classReceipt){

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        databaseRef.child(Constants.FIREBASE_LOCATION_CLASSES_RECEIPT).child(classReceipt.getClientKey()).child(classReceipt.getClassKey())
                .setValue(classReceipt).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                deleteClassFromClientSide(classReceipt);
            }
        });
    }

    public static void saveReview(final Review review, final String personalKey, final String classKey, final String clientKey,
                                  final ArrayList<ClassReceipt> classReceipts) {

        final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        databaseRef.child(Constants.FIREBASE_LOCATION_PERSONAL_REVIEWS).child(personalKey).child(classKey).setValue(review).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Review saved!!!!");
            }
        });

        databaseRef.child(Constants.FIREBASE_LOCATION_PERSONAL_TRAINER).child(personalKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    PersonalTrainer personalTrainer = dataSnapshot.getValue(PersonalTrainer.class);
                    int reviewCounter = personalTrainer.getReviewCounter() + 1;
                    float grade = (personalTrainer.getGrade() + review.getGrade()) / 2;

                    personalTrainer.setGrade(grade);
                    personalTrainer.setReviewCounter(reviewCounter);

                    HashMap<String, Object> trainerHashMap = new HashMap<>();
                    trainerHashMap.put(personalKey, personalTrainer);

                    databaseRef.child(Constants.FIREBASE_LOCATION_PERSONAL_TRAINER).updateChildren(trainerHashMap);

                    HashMap<String, Object> gotReviewsHM = new HashMap<>();
                    gotReviewsHM.put("gotReview", true);
                    databaseRef.child(Constants.FIREBASE_LOCATION_CLASSES_RECEIPT).child(clientKey).child(classKey).updateChildren(gotReviewsHM);
                } else {
                    Log.e(TAG, "Personal trainer not found!!!!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void deleteClassFromClientSide(ClassReceipt classReceipt){

        final String personalKey = classReceipt.getPersonalKey();
        final String clientKey = classReceipt.getClientKey();
        final String classKey = classReceipt.getClassKey();
        final Integer startTimeCode = classReceipt.getStartTimeCode();
        int durationCode = classReceipt.getDurationCode();
        final String dateCode = classReceipt.getDateCode();
        final int endTimeCode = startTimeCode + durationCode;

        final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        databaseRef.child(Constants.FIREBASE_LOCATION_PERSONAL_CLASSES).child(personalKey).child(classKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Class deleted!");
                removeAgendaRestrictions(databaseRef, dateCode, personalKey, startTimeCode, endTimeCode);
                removeClassFromClientSide(databaseRef, clientKey, classKey);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Deletion failed!");
            }
        });

    }

    public static void deleteClassFromClientSide(final Context context, ClientFitClass clientFitClass, final String clientKey ) {

        Integer durationCode = clientFitClass.getDurationCode();

        final String classKey = clientFitClass.getClassKey();
        final String dateCode = clientFitClass.getDateCode();
        final String personalKey = clientFitClass.getPersonalKey();
        final Integer startTimeCode = clientFitClass.getStartTimeCode();
        final int endTimeCode = startTimeCode + durationCode;


        final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        databaseRef.child(Constants.FIREBASE_LOCATION_PERSONAL_CLASSES).child(personalKey).child(classKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Class deleted!");
                removeAgendaRestrictions(databaseRef, dateCode, personalKey, startTimeCode, endTimeCode);
                removeClassFromClientSide(databaseRef, clientKey, classKey);
                Utils.generateSuccessToast(context, context.getString(R.string.class_cancellation_confirmation)).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Deletion failed!");
            }
        });
    }

    public static void saveProfilePicsToFirebase(final Activity activity, Bitmap bitmap){

        final String clientUniqueKey = Utils.getSharedPreferences(activity).getString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, null);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_ROOT_URL);

        try {

            String fileName = Constants.CLIENT_PROFILE_PICTURE_NAME;

            StorageReference personalProfileReference = storageReference.child("client/" + clientUniqueKey + "/" + fileName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //TODO study which image format is better here: jpeg(less quality, smaller size) or png(better quality, bigger size)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

            UploadTask uploadTask = personalProfileReference.putStream(inputStream);
            uploadTask.addOnSuccessListener(activity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Utils.getSharedPreferences(activity).edit().putString(Constants.SHARED_PREF_CLIENT_PHOTO_URI_PATH, downloadUrl.toString()).apply();
                    //saveProfilePicsUrl(downloadUrl, personalUniqueKey);
                    Log.i(TAG, downloadUrl.getPath());
                    Utils.generateSuccessToast(activity, "Profile picture " + downloadUrl.getLastPathSegment() + " saved").show();
                }
            }).addOnFailureListener(activity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, e.getMessage());
                    Utils.generateErrorToast(activity, "Image upload failure").show();
                }
            });

        }catch (NullPointerException npe){
            Log.e(TAG, npe.getMessage());
        }


    }


}
