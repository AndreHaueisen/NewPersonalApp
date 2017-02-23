package com.andrehaueisen.fitx.pojo;

import android.graphics.Bitmap;

/**
 * Created by andre on 10/24/2016.
 */

public class ClassDetailed {

    private Bitmap mPersonalHeadPhoto;
    private byte[] mPersonalPosterPhoto;
    private String mPersonalName;
    private String mClassDate;
    private String mClassLocation;

    public ClassDetailed() {
        super();
    }

    public ClassDetailed(Bitmap personalHeadPhoto, byte[] personalPosterPhoto, String personalName, String classDate, String classLocation) {
        mPersonalHeadPhoto = personalHeadPhoto;
        mPersonalPosterPhoto = personalPosterPhoto;
        mPersonalName = personalName;
        mClassDate = classDate;
        mClassLocation = classLocation;
    }

    public ClassDetailed(Bitmap personalHeadPhoto, String personalName, String classDate, String classLocation) {
        mPersonalHeadPhoto = personalHeadPhoto;
        mPersonalName = personalName;
        mClassDate = classDate;
        mClassLocation = classLocation;
    }

    public Bitmap getPersonalHeadPhoto() {
        return mPersonalHeadPhoto;
    }

    public void setPersonalHeadPhoto(Bitmap personalHeadPhoto) {
        mPersonalHeadPhoto = personalHeadPhoto;
    }

    public byte[] getPersonalPosterPhoto() {
        return mPersonalPosterPhoto;
    }

    public void setPersonalPosterPhoto(byte[] personalPosterPhoto) {
        mPersonalPosterPhoto = personalPosterPhoto;
    }

    public String getClassDate() {
        return mClassDate;
    }

    public void setClassDate(String classDate) {
        mClassDate = classDate;
    }

    public String getPersonalName() {
        return mPersonalName;
    }

    public void setPersonalName(String personalName) {
        mPersonalName = personalName;
    }


    public String getClassLocation() {
        return mClassLocation;
    }

    public void setClassLocation(String classLocation) {
        mClassLocation = classLocation;
    }
}
