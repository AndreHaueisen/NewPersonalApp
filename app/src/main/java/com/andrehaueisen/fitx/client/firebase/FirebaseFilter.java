package com.andrehaueisen.fitx.client.firebase;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.client.search.GeneralPersonalSearchFragment;
import com.andrehaueisen.fitx.models.Gym;
import com.andrehaueisen.fitx.models.PersonalTrainer;
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
 * Created by andre on 10/7/2016.
 */

public class FirebaseFilter {

    private QueryKeys mQueryKeys;

    private HashMap<String, ArrayList<Integer>> mAgendaTimeCodesEndHashMap;
    private HashMap<String, ArrayList<Integer>> mAgendaTimeCodesStartHashMap;


    public interface QueryKeys{
        void onAgendaCodesReady();
        void getPlaceFilteredKeys(ArrayList<String> placeFilteredKeys);
        void getSpecialtyFilterKeys(ArrayList<String> specialtyFilteredKeys);
        void getChosenPersonalInformation(ArrayList<String> chosenPersonalKeys);
        void onPersonalInformationReady(ArrayList<PersonalTrainer> chosenPersonalTrainers, ArrayList<String> chosenPersonalKeys);
    }

    public FirebaseFilter(GeneralPersonalSearchFragment searchFragment) {
        mQueryKeys = (QueryKeys) searchFragment;
    }

    public void startChainQuery(String dateCode){
        getAgendaCodes(Utils.getWeekDayFromDateCode(dateCode));
    }

    private void getAgendaCodes(final String weekDay){

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child(Constants.FIREBASE_LOCATION_AGENDA).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mAgendaTimeCodesEndHashMap = new HashMap<>();
                    mAgendaTimeCodesStartHashMap = new HashMap<>();

                    ArrayList<Integer> agendaTimeCodesEnd;
                    ArrayList<Integer> agendaTimeCodesStart;

                    GenericTypeIndicator<ArrayList<Integer>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Integer>>() {};
                    String key;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        key = snapshot.getKey();
                        agendaTimeCodesEnd = snapshot.child(weekDay).child(Constants.AGENDA_CODES_END_LIST).getValue(genericTypeIndicator);
                        if(agendaTimeCodesEnd != null) {
                            mAgendaTimeCodesEndHashMap.put(key, agendaTimeCodesEnd);
                        }

                        agendaTimeCodesStart = snapshot.child(weekDay).child(Constants.AGENDA_CODES_START_LIST).getValue(genericTypeIndicator);
                        if(agendaTimeCodesEnd != null) {
                            mAgendaTimeCodesStartHashMap.put(key, agendaTimeCodesStart);
                        }
                    }
                }

                if(mAgendaTimeCodesStartHashMap != null && !mAgendaTimeCodesStartHashMap.isEmpty()) {
                    mQueryKeys.onAgendaCodesReady();
                }else {
                    mQueryKeys.onPersonalInformationReady(null, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void filterByPlace(final Gym membershipPlace){

        final ArrayList<String> personalKeys = new ArrayList<>();

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child(Constants.FIREBASE_LOCATION_GYMS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String key;
                    GenericTypeIndicator<ArrayList<Gym>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Gym>>() {};
                    ArrayList<Gym> workingPlaces;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        key = snapshot.getKey();

                        workingPlaces = snapshot.getValue(genericTypeIndicator);

                        //membershipPlace will always have size 1
                        for(Gym workingPlace : workingPlaces){
                            if(workingPlace.getPlaceId().equals(membershipPlace.getPlaceId()) ){
                                personalKeys.add(key);
                            }
                        }
                    }

                    if(!personalKeys.isEmpty()) {
                        mQueryKeys.getPlaceFilteredKeys(personalKeys);
                    }else {
                        mQueryKeys.onPersonalInformationReady(null, null);
                    }

                } else {
                    mQueryKeys.onPersonalInformationReady(null, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //TODO REFACTOR DATABASE
    public void filterBySpecialty(final String mainObjective, final ArrayList<String> placeFilteredPersonalKeys){

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child(Constants.FIREBASE_LOCATION_SPECIALTIES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && !placeFilteredPersonalKeys.isEmpty()){
                    GenericTypeIndicator<ArrayList<String>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<String>>() {};
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        ArrayList<String> specialties = snapshot.child("specialties").getValue(genericTypeIndicator);
                        //Remove key from personal list if it doesn't contain user main objective
                        if(!specialties.contains(mainObjective) && placeFilteredPersonalKeys.contains(snapshot.getKey())){
                            placeFilteredPersonalKeys.remove(snapshot.getKey());
                        }
                    }
                    mQueryKeys.getSpecialtyFilterKeys(placeFilteredPersonalKeys);
                } else {
                    mQueryKeys.onPersonalInformationReady(null, null);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void filterBySchedule(String dateCode, final int classStartTime, final int classDuration, final ArrayList<String> personalKeys){

        final ArrayList<String> chosenKeys = new ArrayList<>();
        final GenericTypeIndicator<ArrayList<Integer>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Integer>>() {};

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.FIREBASE_LOCATION_PERSONAL_AGENDA_RESTRICTIONS).child(String.valueOf(dateCode)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int classEndTime = classStartTime + classDuration;

                if(dataSnapshot.exists()){
                    ArrayList<Integer> agendaTimeCodesStart;
                    ArrayList<Integer> agendaTimeCodesEnd;
                    ArrayList<Integer> restrictionStartTimeCodes;
                    ArrayList<Integer> restrictionEndTimeCodes;

                    for(String key : personalKeys) {

                        agendaTimeCodesStart = mAgendaTimeCodesStartHashMap.get(key);
                        agendaTimeCodesEnd = mAgendaTimeCodesEndHashMap.get(key);

                        DataSnapshot snapshot = dataSnapshot.child(key);

                        //if there are restrictions, analyze them and if is the case, add to chosenKeys. Else, add keys if compatible with weak schedule.
                        if (snapshot.exists()) {
                            restrictionStartTimeCodes = snapshot.child(Constants.AGENDA_CODES_START_LIST).getValue(genericTypeIndicator);
                            restrictionEndTimeCodes = snapshot.child(Constants.AGENDA_CODES_END_LIST).getValue(genericTypeIndicator);

                            if (canScheduleClass(agendaTimeCodesStart, agendaTimeCodesEnd, restrictionStartTimeCodes, restrictionEndTimeCodes, classStartTime, classEndTime)) {
                                chosenKeys.add(key);
                            }

                        } else {
                            if(agendaTimeCodesStart != null && agendaTimeCodesEnd != null) {
                                for (int i = 0; i < agendaTimeCodesStart.size(); i++) {
                                    if (classStartTime >= agendaTimeCodesStart.get(i) && classEndTime <= agendaTimeCodesEnd.get(i)) {
                                        chosenKeys.add(key);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    mQueryKeys.getChosenPersonalInformation(chosenKeys);
                }else {

                    if(!mAgendaTimeCodesStartHashMap.isEmpty()) {
                        ArrayList<Integer> agendaTimeCodesStart;
                        ArrayList<Integer> agendaTimeCodesEnd;

                        for (String key : personalKeys) {
                            agendaTimeCodesStart = mAgendaTimeCodesStartHashMap.get(key);
                            agendaTimeCodesEnd = mAgendaTimeCodesEndHashMap.get(key);

                            if(agendaTimeCodesStart != null) {
                                for (int i = 0; i < agendaTimeCodesStart.size(); i++) {
                                    if (classStartTime >= agendaTimeCodesStart.get(i) && classEndTime <= agendaTimeCodesEnd.get(i)) {
                                        chosenKeys.add(key);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    mQueryKeys.getChosenPersonalInformation(chosenKeys);
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

    public void getPersonals(final ArrayList<String> keys){

        final ArrayList<PersonalTrainer> personalTrainers = new ArrayList<>();
        final ArrayList<String> chosenPersonalKeys = new ArrayList<>();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.FIREBASE_LOCATION_PERSONAL_TRAINER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(String key : keys) {

                        DataSnapshot snapshot = dataSnapshot.child(key);
                        if(snapshot.exists()) {
                            personalTrainers.add(snapshot.getValue(PersonalTrainer.class));
                            chosenPersonalKeys.add(key);
                        }
                    }

                    mQueryKeys.onPersonalInformationReady(personalTrainers, chosenPersonalKeys);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
