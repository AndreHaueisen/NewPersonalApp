package com.andrehaueisen.fitx.personal.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.andrehaueisen.fitx.utilities.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.utilities.Utils;
import com.andrehaueisen.fitx.models.PersonalFitClass;
import com.andrehaueisen.fitx.personal.PersonalActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by andre on 10/15/2016.
 */

public class ClassUpdateListenerService extends Service {

    private final String TAG = ClassUpdateListenerService.class.getSimpleName();

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    ChildEventListener mChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            final Context context = getApplication().getApplicationContext();
            final Bitmap appIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

            if (dataSnapshot.exists()) {
                PersonalFitClass personalFitClass = dataSnapshot.getValue(PersonalFitClass.class);

                if (!personalFitClass.isConfirmed()) {

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                            .setLargeIcon(appIcon)
                            .setColor(context.getResources().getColor(R.color.colorPrimaryDark))
                            .setSmallIcon(R.drawable.ic_new_class_24dp)
                            .setContentTitle(context.getString(R.string.new_class_notification_personal))
                            .setContentText(context.getString(R.string.action_require_on_new_class_personal))
                            .setPriority(NotificationCompat.PRIORITY_HIGH);


                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, PersonalActivity.class), 0);
                    builder.setContentIntent(pendingIntent);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, builder.build());

                }
            }


        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            final Context context = getApplication().getApplicationContext();
            final Bitmap appIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

            if (dataSnapshot.exists()) {
                PersonalFitClass personalFitClass = dataSnapshot.getValue(PersonalFitClass.class);

                if (!personalFitClass.isConfirmed()) {

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                            .setLargeIcon(appIcon)
                            .setColor(context.getResources().getColor(R.color.colorPrimaryDark))
                            .setSmallIcon(R.drawable.ic_class_canceled_24dp)
                            .setContentTitle(context.getString(R.string.class_canceled_notification_personal))
                            .setPriority(NotificationCompat.PRIORITY_HIGH);


                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, PersonalActivity.class), 0);
                    builder.setContentIntent(pendingIntent);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, builder.build());

                    Utils.updateWidget(getApplicationContext());
                }
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public static final int NOTIFICATION_ID = 1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service called!!!!!!!!!!!!");
        SharedPreferences sharedPreferences = Utils.getSharedPreferences(getApplicationContext());
        String personalKey = sharedPreferences.getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null);

        if (personalKey != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_LOCATION_PERSONAL_CLASSES).child(personalKey);
            new NotifyNewClassTask().execute(this);
            Utils.updateWidget(getApplicationContext());
        }

        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.updateWidget(getApplicationContext());
        Log.i(TAG, "onDestroy service called!!!");
        mDatabase.removeEventListener(mChildEventListener);
    }

    private class NotifyNewClassTask extends AsyncTask<Context, Void, Void> {

        @Override
        protected Void doInBackground(Context... params) {

            if (mDatabase != null) {
                mDatabase.addChildEventListener(mChildEventListener);
            }

            return null;
        }

    }
}

