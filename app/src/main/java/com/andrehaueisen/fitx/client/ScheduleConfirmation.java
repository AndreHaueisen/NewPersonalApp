package com.andrehaueisen.fitx.client;

import android.app.Activity;

import com.andrehaueisen.fitx.utilities.Utils;
import com.andrehaueisen.fitx.client.firebase.AppointmentMaker;
import com.andrehaueisen.fitx.models.PersonalFitClass;
import com.andrehaueisen.fitx.models.PersonalTrainer;

/**
 * Created by andre on 10/13/2016.
 */

public class ScheduleConfirmation implements AppointmentMaker.LastCheckCallback {


    private PersonalTrainer mPersonalTrainer;
    private AppointmentMaker mAppointmentMaker;
    private PersonalFitClass mPersonalFitClass;
    private Activity mActivity;

    public ScheduleConfirmation(Activity activity, PersonalTrainer personalTrainer, PersonalFitClass fitClass) {
        mPersonalTrainer = personalTrainer;
        mPersonalFitClass = fitClass;
        mActivity = activity;
    }

    public void makeAppointment(){
        final String personalKey = Utils.encodeEmail(mPersonalTrainer.getEmail());

        mAppointmentMaker = new AppointmentMaker(personalKey, mPersonalTrainer.getName(), mPersonalFitClass, ScheduleConfirmation.this, mActivity);
        mAppointmentMaker.startSafeCheck();
    }


    @Override
    public void onAgendaCodesReady() {
        mAppointmentMaker.checkForTimeRestrictions();
    }


}
