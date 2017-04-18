package com.andrehaueisen.fitx;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

/**
 * Created by andre on 12/6/2016.
 */
public class PersonalApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
    }

}
