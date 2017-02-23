package com.andrehaueisen.fitx.pojo;

/**
 * Created by andre on 12/8/2016.
 */

public class PersonalDetailed {

    private int mReviewCounter;
    private String mCref;

    public PersonalDetailed() {
    }

    public PersonalDetailed(int reviewCounter, String cref) {

        mReviewCounter = reviewCounter;
        mCref = cref;
    }


    public String getCref() {
        return mCref;
    }

    public void setCref(String cref) {
        mCref = cref;
    }


    public int getReviewCounter() {
        return mReviewCounter;
    }

    public void setReviewCounter(int reviewCounter) {
        mReviewCounter = reviewCounter;
    }
}
