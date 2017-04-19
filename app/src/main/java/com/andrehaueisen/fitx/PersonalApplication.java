package com.andrehaueisen.fitx;

import android.support.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

/**
 * Created by andre on 12/6/2016.
 */
public class PersonalApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
    }

}
