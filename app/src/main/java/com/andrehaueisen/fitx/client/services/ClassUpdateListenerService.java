package com.andrehaueisen.fitx.client.services;

import android.app.ActivityManager;
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
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.client.ClientActivity;
import com.andrehaueisen.fitx.models.ClientFitClass;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ClassUpdateListenerService extends Service {


    private final String TAG = ClassUpdateListenerService.class.getSimpleName();

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    ChildEventListener mChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            final Context context = getApplication().getApplicationContext();
            final Bitmap appIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

            if (dataSnapshot.exists()) {
                ClientFitClass clientFitClass = dataSnapshot.getValue(ClientFitClass.class);

                if (clientFitClass.isConfirmed()) {

                    String dateCode = clientFitClass.getDateCode();
                    String date = Utils.getWrittenDateFromDateCode(context, dateCode);
                    String placeName = clientFitClass.getPlaceName();
                    String classStartTime = Utils.getClockFromTimeCode(context, clientFitClass.getStartTimeCode());
                    String classEndTime = Utils.getClockFromTimeCode(context, clientFitClass.getStartTimeCode() + clientFitClass.getDurationCode());

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                            .setLargeIcon(appIcon)
                            .setColor(context.getResources().getColor(R.color.colorPrimaryDark))
                            .setSmallIcon(R.drawable.ic_new_class_24dp)
                            .setContentTitle(context.getString(R.string.new_class_notification_title_client))
                            .setContentText(context.getString(R.string.new_class_notification_client, date, placeName))
                            .setPriority(NotificationCompat.PRIORITY_HIGH);


                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, ClientActivity.class), 0);
                    builder.setContentIntent(pendingIntent);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, builder.build());

                    updateWidget();
                }
            }

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

            final Context context = getApplication().getApplicationContext();
            final Bitmap appIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

            if (dataSnapshot.exists()) {
                ClientFitClass clientFitClass = dataSnapshot.getValue(ClientFitClass.class);

                if (clientFitClass.isConfirmed()) {

                    String dateCode = clientFitClass.getDateCode();
                    String date = Utils.getWrittenDateFromDateCode(context, dateCode);
                    String placeName = clientFitClass.getPlaceName();
                    String classStartTime = Utils.getClockFromTimeCode(context, clientFitClass.getStartTimeCode());
                    String classEndTime = Utils.getClockFromTimeCode(context, clientFitClass.getStartTimeCode() + clientFitClass.getDurationCode());

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                            .setLargeIcon(appIcon)
                            .setColor(context.getResources().getColor(R.color.colorPrimaryDark))
                            .setSmallIcon(R.drawable.ic_class_canceled_24dp)
                            .setContentTitle(context.getString(R.string.class_canceled_notification_title_client))
                            .setContentText(context.getString(R.string.class_canceled_notification_client, placeName))
                            .setPriority(NotificationCompat.PRIORITY_HIGH);


                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, ClientActivity.class), 0);
                    builder.setContentIntent(pendingIntent);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, builder.build());

                    updateWidget();
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

    public static final int NOTIFICATION_ID = 2;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service called!!!!!!!!!!!!");
        SharedPreferences sharedPreferences = Utils.getSharedPreferences(getApplicationContext());
        String clientKey = sharedPreferences.getString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, null);

        if (clientKey != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_LOCATION_CLIENT_CLASSES).child(clientKey);
            new ClassUpdateListenerService.NotifyNewClassTask().execute(this);
        }

        return START_STICKY;
    }


    private boolean isAppOnForeground(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }

        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private void updateWidget() {
        Context context = getApplicationContext();
        Intent updateWidgetIntent = new Intent(Constants.ACTION_DATA_UPDATED).setPackage(context.getPackageName());
        context.sendBroadcast(updateWidgetIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
