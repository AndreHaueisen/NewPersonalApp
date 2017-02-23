package com.andrehaueisen.fitx.personal.firebase;

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
import com.andrehaueisen.fitx.personal.adapters.AgendaAdapter;
import com.andrehaueisen.fitx.pojo.Gym;
import com.andrehaueisen.fitx.pojo.PersonalFitClass;
import com.andrehaueisen.fitx.pojo.PersonalTrainer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.andrehaueisen.fitx.Utils.getSharedPreferences;


/**
 * Created by Carla on 13/09/2016.
 */

public class PersonalDatabase {

    private static final String TAG = PersonalDatabase.class.getSimpleName();


    public static void savePersonalToDatabase(Activity activity, PersonalTrainer personalTrainer){

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        final String personalUniqueId = Utils.encodeEmail(personalTrainer.getEmail());

        SharedPreferences sharedPreferences = Utils.getSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, personalUniqueId);
        editor.putString(Constants.SHARED_PREF_PERSONAL_NAME, personalTrainer.getName());
        editor.commit();

        database.child(Constants.FIREBASE_LOCATION_PERSONAL_TRAINER).child(personalUniqueId)
            .setValue(personalTrainer).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG, "Personal saved");
            }
        });
    }

    public static void saveSpecialtiesToDatabase(final Context context, ArrayList<String> specialties, final boolean showToast){

        String personalUniqueKey = getSharedPreferences(context).getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null);

        Map<String, Object> mapSpecialties = new HashMap<>();
        mapSpecialties.put("specialties", specialties);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child(Constants.FIREBASE_LOCATION_SPECIALTIES).child(personalUniqueKey).updateChildren(mapSpecialties, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(showToast) {
                    Utils.generateSuccessToast(context, context.getString(R.string.specialties_saved)).show();
                }
                Log.i(TAG, "Specialties saved");
            }
        });
    }

    //TODO check if it is necessary
    /*public static void createClassLocation(final Activity activity){
        String personalUniqueKey = getSharedPreferences(activity).getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.FIREBASE_LOCATION_PERSONAL_CLASSES).child(personalUniqueKey).setValue(personalUniqueKey).addOnSuccessListener(activity, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utils.generateToast(activity, "Classes location created");
            }
        });
    } */

    public static void savePersonalWorkingPlaces(final Context context, ArrayList<Gym> gyms){

        String personalUniqueKey = getSharedPreferences(context).getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.FIREBASE_LOCATION_GYMS).child(personalUniqueKey).setValue(gyms, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Utils.generateSuccessToast(context, context.getString(R.string.places_saved)).show();
                Log.i(TAG, "Places saved");
            }
        });
    }

    public static void savePersonalScheduleToDatabase(Context context, ArrayList<AgendaAdapter> agendaAdapters){

        String personalUniqueKey = getSharedPreferences(context).getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> mapAgendaTimeCodes = new HashMap<>();

        for(int i = 0; i < 7; i++) {

            ArrayList<Integer> agendaTimeCodesStart = agendaAdapters.get(i).getTimeCodeListStart();
            Collections.sort(agendaTimeCodesStart);
            String weekDay = getWeekDayTitle(i);

            mapAgendaTimeCodes.put(weekDay + "/" + Constants.AGENDA_CODES_START_LIST, agendaTimeCodesStart);
        }

        for(int i = 0; i < 7; i++) {

            ArrayList<Integer> agendaTimeCodesEnd = agendaAdapters.get(i).getTimeCodeListEnd();
            Collections.sort(agendaTimeCodesEnd);
            String weekDay = getWeekDayTitle(i);

            mapAgendaTimeCodes.put(weekDay + "/" + Constants.AGENDA_CODES_END_LIST, agendaTimeCodesEnd);
        }

        try {
            database.child(Constants.FIREBASE_LOCATION_AGENDA).child(personalUniqueKey).updateChildren(mapAgendaTimeCodes, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Log.i(TAG, "Agenda start codes saved");
                }
            });

        }catch (NullPointerException npe){
            Log.e(TAG, "WeekDay not found");
        }
    }

    public static String getWeekDayTitle(int weekDayCode){

        switch (weekDayCode){
            case 0:
                return Constants.WEEK_DAY_MONDAY_KEY;
            case 1:
                return Constants.WEEK_DAY_TUESDAY_KEY;
            case 2:
                return Constants.WEEK_DAY_WEDNESDAY_KEY;
            case 3:
                return Constants.WEEK_DAY_THURSDAY_KEY;
            case 4:
                return Constants.WEEK_DAY_FRIDAY_KEY;
            case 5:
                return Constants.WEEK_DAY_SATURDAY_KEY;
            case 6:
                return Constants.WEEK_DAY_SUNDAY_KEY;

            default:
                return null;
        }
    }

    public static void saveProfilePicsToFirebase(final Activity activity, Bitmap bitmap){

        final String personalUniqueKey = Utils.getSharedPreferences(activity).getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_ROOT_URL);

            try {

                String fileName = Constants.PERSONAL_PROFILE_PICTURE_NAME;

                StorageReference personalProfileReference = storageReference.child("personalTrainer/" + personalUniqueKey + "/" + fileName);
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
                        Utils.getSharedPreferences(activity).edit().putString(Constants.SHARED_PREF_PERSONAL_PHOTO_URI_PATH, downloadUrl.toString()).apply();
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

    //Not needed for now -- evaluate later
   /* private static void saveProfilePicsUrl(final Uri downloadUri, String personalUniqueKey){

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> uriHashMap = new HashMap<>();
        uriHashMap.put(Constants.FIREBASE_LOCATION_PERSONAL_URI, downloadUri.getPath());

        database.child(Constants.FIREBASE_LOCATION_PERSONAL_TRAINER).child(personalUniqueKey).updateChildren(uriHashMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference mDatabaseReference) {
                Log.i(TAG, downloadUri.getPath() + " saved");
            }
        });
    } */

    public static void confirmClass(final Activity activity, final String classKey, final String clientKey){

        String personalName = Utils.getSharedPreferences(activity).getString(Constants.SHARED_PREF_PERSONAL_NAME, null);
        final String personalKey = Utils.getSharedPreferences(activity).getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null);

        final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> confirmClassHashMap = new HashMap<>();
        confirmClassHashMap.put("confirmed", true);
        confirmClassHashMap.put("personalName", personalName);

        databaseRef.child(Constants.FIREBASE_LOCATION_PERSONAL_CLASSES).child(personalKey).child(classKey).updateChildren(confirmClassHashMap).addOnSuccessListener(activity, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Class confirmed on personal side!");
            }
        });

        databaseRef.child(Constants.FIREBASE_LOCATION_CLIENT_CLASSES).child(clientKey).child(classKey).updateChildren(confirmClassHashMap).addOnSuccessListener(activity, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Class confirmed on client side!");
            }
        });
    }

    public static void deleteClassFromPersonalSide(final Context context, PersonalFitClass personalFitClass){

        Integer durationCode = personalFitClass.getDurationCode();

        final String clientKey = personalFitClass.getClientKey();
        final String classKey = personalFitClass.getClassKey();
        final String dateCode = personalFitClass.getDateCode();
        final Integer startTimeCode = personalFitClass.getStartTimeCode();

        final int endTimeCode = startTimeCode + durationCode;

        final String personalKey = Utils.getSharedPreferences(context).getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null);

        final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        databaseRef.child(Constants.FIREBASE_LOCATION_PERSONAL_CLASSES).child(personalKey).child(classKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Class deleted!");
                removeAgendaRestrictions(databaseRef, dateCode, personalKey, startTimeCode, endTimeCode);
                removeClassFromClientSide(databaseRef, clientKey, classKey);
                Utils.generateSuccessToast(context, context.getString(R.string.class_deletion_confirmation)).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Deletion failed!");
            }
        });

    }

    public static void removeClassFromClientSide(DatabaseReference databaseRef, String clientKey, String classKey){

        databaseRef.child(Constants.FIREBASE_LOCATION_CLIENT_CLASSES).child(clientKey).child(classKey).removeValue();
    }

    public static void removeAgendaRestrictions(final DatabaseReference databaseRef, final String dateCode, final String personalKey, final Integer
            startTimeCode, final Integer endTimeCode){

        databaseRef.child(Constants.FIREBASE_LOCATION_PERSONAL_AGENDA_RESTRICTIONS).child(String.valueOf(dateCode)).child(personalKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Integer>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Integer>>() {};
                ArrayList<Integer> startTimeCodes = dataSnapshot.child(Constants.AGENDA_CODES_START_LIST).getValue(genericTypeIndicator);
                ArrayList<Integer> endTimeCodes = dataSnapshot.child(Constants.AGENDA_CODES_END_LIST).getValue(genericTypeIndicator);

                startTimeCodes.remove(startTimeCode);
                endTimeCodes.remove(endTimeCode);

                databaseRef.child(Constants.FIREBASE_LOCATION_PERSONAL_AGENDA_RESTRICTIONS).child(String.valueOf(dateCode)).child(personalKey).child(Constants.AGENDA_CODES_START_LIST).setValue(startTimeCodes);
                databaseRef.child(Constants.FIREBASE_LOCATION_PERSONAL_AGENDA_RESTRICTIONS).child(String.valueOf(dateCode)).child(personalKey).child(Constants.AGENDA_CODES_END_LIST).setValue(endTimeCodes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
