package com.andrehaueisen.fitx.client.firebase;

import android.app.Activity;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.client.ScheduleConfirmation;
import com.andrehaueisen.fitx.pojo.PersonalFitClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by andre on 10/14/2016.
 */

public class AppointmentMaker {

    private Activity mActivity;
    private String mPersonalKey;
    private String mPersonalName;
    private PersonalFitClass mPersonalFitClass;
    private LastCheckCallback mLastCheckCallback;

    private HashMap<String, ArrayList<Integer>> mAgendaTimeCodesEndHashMap;
    private HashMap<String, ArrayList<Integer>> mAgendaTimeCodesStartHashMap;

    public interface LastCheckCallback {
        void onAgendaCodesReady();
    }


    public AppointmentMaker(String personalKey, String personalName, PersonalFitClass personalFitClass, ScheduleConfirmation confirmationFragment,
                            Activity activity) {
        mPersonalKey = personalKey;
        mPersonalName = personalName;
        mPersonalFitClass = personalFitClass;
        mLastCheckCallback = (LastCheckCallback) confirmationFragment;
        mActivity = activity;
    }

    public void startSafeCheck() {
        getAgendaCodes();
    }

    private void getAgendaCodes() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child(Constants.FIREBASE_LOCATION_AGENDA).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String weekDay = Utils.getWeekDayFromDateCode(mPersonalFitClass.getDateCode());
                    mAgendaTimeCodesEndHashMap = new HashMap<>();
                    mAgendaTimeCodesStartHashMap = new HashMap<>();

                    ArrayList<Integer> agendaTimeCodesEnd;
                    ArrayList<Integer> agendaTimeCodesStart;

                    GenericTypeIndicator<ArrayList<Integer>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Integer>>() {
                    };
                    String key;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        key = snapshot.getKey();
                        agendaTimeCodesEnd = snapshot.child(weekDay).child(Constants.AGENDA_CODES_END_LIST).getValue(genericTypeIndicator);
                        if (agendaTimeCodesEnd != null) {
                            mAgendaTimeCodesEndHashMap.put(key, agendaTimeCodesEnd);
                        }

                        agendaTimeCodesStart = snapshot.child(weekDay).child(Constants.AGENDA_CODES_START_LIST).getValue(genericTypeIndicator);
                        if (agendaTimeCodesEnd != null) {
                            mAgendaTimeCodesStartHashMap.put(key, agendaTimeCodesStart);
                        }
                    }
                }

                mLastCheckCallback.onAgendaCodesReady();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void checkForTimeRestrictions() {

        final GenericTypeIndicator<ArrayList<Integer>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Integer>>() {};

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.FIREBASE_LOCATION_PERSONAL_AGENDA_RESTRICTIONS).child(String.valueOf(mPersonalFitClass.getDateCode())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int classEndTimeCode = mPersonalFitClass.getStartTimeCode() + mPersonalFitClass.getDurationCode();

                if (dataSnapshot.exists()) {
                    ArrayList<Integer> agendaTimeCodesStart;
                    ArrayList<Integer> agendaTimeCodesEnd;

                    ArrayList<Integer> restrictionStartTimeCodes;
                    ArrayList<Integer> restrictionEndTimeCodes;

                    agendaTimeCodesStart = mAgendaTimeCodesStartHashMap.get(mPersonalKey);
                    agendaTimeCodesEnd = mAgendaTimeCodesEndHashMap.get(mPersonalKey);

                    DataSnapshot snapshot = dataSnapshot.child(mPersonalKey);

                    //if there are restrictions, analyze them and if is the case, add to chosenKeys. Else, add keys if compatible with weak schedule.
                    if (snapshot.exists()) {
                        restrictionStartTimeCodes = snapshot.child(Constants.AGENDA_CODES_START_LIST).getValue(genericTypeIndicator);
                        restrictionEndTimeCodes = snapshot.child(Constants.AGENDA_CODES_END_LIST).getValue(genericTypeIndicator);

                        if (canScheduleClass(agendaTimeCodesStart, agendaTimeCodesEnd, restrictionStartTimeCodes, restrictionEndTimeCodes, mPersonalFitClass.getStartTimeCode(), classEndTimeCode)) {
                            ClientDatabase.scheduleClass(mActivity, restrictionStartTimeCodes, restrictionEndTimeCodes,
                                    mPersonalKey, mPersonalFitClass.getDateCode(), mPersonalFitClass.getStartTimeCode(), classEndTimeCode);
                            ClientDatabase.saveClassInformation(mActivity, mPersonalFitClass, mPersonalKey, mPersonalName);
                        }

                    } else {
                        for (int i = 0; i < agendaTimeCodesStart.size(); i++) {
                            if (mPersonalFitClass.getStartTimeCode() >= agendaTimeCodesStart.get(i) && classEndTimeCode <= agendaTimeCodesEnd.get(i)) {
                                ClientDatabase.scheduleClass(mActivity, mPersonalKey, mPersonalFitClass.getDateCode(), mPersonalFitClass.getStartTimeCode(), classEndTimeCode);
                                ClientDatabase.saveClassInformation(mActivity, mPersonalFitClass, mPersonalKey, mPersonalName);
                                break;
                            }
                        }
                    }

                } else {
                    ClientDatabase.scheduleClass(mActivity, mPersonalKey, mPersonalFitClass.getDateCode(), mPersonalFitClass.getStartTimeCode(), classEndTimeCode);
                    ClientDatabase.saveClassInformation(mActivity, mPersonalFitClass, mPersonalKey, mPersonalName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean canScheduleClass(ArrayList<Integer> agendaTimeCodesStart, ArrayList<Integer> agendaTimeCodesEnd,
                                     ArrayList<Integer> restrictionStartTimeCodes, ArrayList<Integer> restrictionEndTimeCodes,
                                     int classStartTime, int classEndTime){

        uniteAgendaWithRestrictions(agendaTimeCodesStart, agendaTimeCodesEnd, restrictionStartTimeCodes, restrictionEndTimeCodes);
        sortAgendaInAscendingOrder(agendaTimeCodesStart, agendaTimeCodesEnd);

        return isFreeToSchedule(agendaTimeCodesStart, agendaTimeCodesEnd, classStartTime, classEndTime);
    }

    private void uniteAgendaWithRestrictions(ArrayList<Integer> agendaTimeCodesStart, ArrayList<Integer> agendaTimeCodesEnd,
                                             ArrayList<Integer> restrictionStartTimeCodes, ArrayList<Integer> restrictionEndTimeCodes){

        int restrictionStart;
        int restrictionEnd;

        for(int i = 0; i < restrictionStartTimeCodes.size(); i++) {
            restrictionStart = restrictionStartTimeCodes.get(i);
            restrictionEnd = restrictionEndTimeCodes.get(i);

            agendaTimeCodesStart.add(restrictionEnd);
            agendaTimeCodesEnd.add(restrictionStart);
        }

    }

    private void sortAgendaInAscendingOrder(ArrayList<Integer> agendaTimeCodesStart, ArrayList<Integer> agendaTimeCodesEnd){
        Collections.sort(agendaTimeCodesStart);
        Collections.sort(agendaTimeCodesEnd);
    }

    private boolean isFreeToSchedule(ArrayList<Integer> agendaTimeCodesStart, ArrayList<Integer> agendaTimeCodesEnd, int classStartTime, int classEndTime) {
        boolean isFreeToSchedule = false;

        for(int j = 0; j < agendaTimeCodesStart.size(); j++) {
            if (classStartTime >= agendaTimeCodesStart.get(j) && classEndTime <= agendaTimeCodesEnd.get(j)) {
                isFreeToSchedule = true;
                break;
            }
        }

        return isFreeToSchedule;
    }
}
