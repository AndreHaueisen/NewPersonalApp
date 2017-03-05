package com.andrehaueisen.fitx.models;

import android.graphics.Bitmap;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andre on 10/24/2016.
 */

public class ClassResume implements Parent<ClassDetailed>{

    private Bitmap mPersonalHeadPhoto;
    private String mPersonalName;
    private String mClassDate;
    private String mClassLocation;
    private ArrayList<ClassDetailed> mClassesDetailed;

    public ClassResume() {
        super();
    }

    public ClassResume(Bitmap personalHeadPhoto, String personalName, String classDate, String classLocation, ArrayList<ClassDetailed> classesDetailed) {
        mPersonalHeadPhoto = personalHeadPhoto;
        mPersonalName = personalName;
        mClassDate = classDate;
        mClassLocation = classLocation;
        mClassesDetailed = classesDetailed;
    }

    @Override
    public List<ClassDetailed> getChildList() {
        return mClassesDetailed;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public Bitmap getPersonalHeadPhoto() {
        return mPersonalHeadPhoto;
    }

    public void setPersonalHeadPhoto(Bitmap personalHeadPhoto) {
        mPersonalHeadPhoto = personalHeadPhoto;
    }

    public String getPersonalName() {
        return mPersonalName;
    }

    public void setPersonalName(String personalName) {
        mPersonalName = personalName;
    }

    public String getClassDate() {
        return mClassDate;
    }

    public void setClassDate(String classDate) {
        mClassDate = classDate;
    }

    public String getClassLocation() {
        return mClassLocation;
    }

    public void setClassLocation(String classLocation) {
        mClassLocation = classLocation;
    }

    public ArrayList<ClassDetailed> getClassesDetailed() {
        return mClassesDetailed;
    }

    public void setClassesDetailed(ArrayList<ClassDetailed> classesDetailed) {
        mClassesDetailed = classesDetailed;
    }
}
