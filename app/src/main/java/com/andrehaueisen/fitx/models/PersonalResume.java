package com.andrehaueisen.fitx.models;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

/**
 * Created by andre on 12/8/2016.
 */

public class PersonalResume implements Parent<PersonalDetailed> {

    private String mName;
    private float mGrade;
    private String mPersonalKey;

    private List<PersonalDetailed> mPersonalDetaileds;

    public PersonalResume(){

    }

    public PersonalResume(String name, float grade, List<PersonalDetailed> personalDetaileds, String personalKey) {
        mName = name;
        mGrade = grade;
        mPersonalDetaileds = personalDetaileds;
        mPersonalKey = personalKey;
    }

    @Override
    public List<PersonalDetailed> getChildList() {
        return mPersonalDetaileds;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public float getGrade() {
        return mGrade;
    }

    public void setGrade(float grade) {
        mGrade = grade;
    }

    public String getPersonalKey() {
        return mPersonalKey;
    }

    public void setPersonalKey(String personalKey) {
        mPersonalKey = personalKey;
    }
}
