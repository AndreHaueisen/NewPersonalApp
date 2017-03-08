package com.andrehaueisen.fitx.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andre on 9/7/2016.
 */
//TODO see about payment
public class PersonalTrainer implements Parcelable {

    private String mName;
    private String mCref;
    private String mCrefState;
    private String mEmail;
    private String mProfilePicsUri;
    private boolean mHasStudio;
    private float mGrade;
    private int mReviewCounter;
    private int m15MinutePrice;


    public PersonalTrainer(){
        mGrade = 4.50f;
        mReviewCounter = 1;
    }

    public PersonalTrainer(String name, String cref, String crefState, String email, String profilePicsUri,
                           boolean hasStudio, float grade, int reviewCounter, int a15MinutePrice) {

        mName = name;
        mCref = cref;
        mCrefState = crefState;
        mEmail = email;
        mProfilePicsUri = profilePicsUri;
        mHasStudio = hasStudio;
        mGrade = grade;
        mReviewCounter = reviewCounter;
        m15MinutePrice = a15MinutePrice;

    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCref() {
        return mCref;
    }

    public void setCref(String cref) {
        mCref = cref;
    }

    public String getCrefState() {
        return mCrefState;
    }

    public void setCrefState(String crefState) {
        mCrefState = crefState;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

     public String getProfilePicsUri() {
        return mProfilePicsUri;
    }

    public void setProfilePicsUri(String profilePicsUri) {
        mProfilePicsUri = profilePicsUri;
    }

    public float getGrade() {
        return mGrade;
    }

    public void setGrade(float grade) {
        mGrade = grade;
    }

    public int getReviewCounter() {
        return mReviewCounter;
    }

    public void setReviewCounter(int reviewCounter) {
        mReviewCounter = reviewCounter;
    }

    public int get15MinutePrice() {
        return m15MinutePrice;
    }

    public void set15MinutePrice(int m15MinutePrice) {
        this.m15MinutePrice = m15MinutePrice;
    }

    protected PersonalTrainer(Parcel in) {
        mName = in.readString();
        mCref = in.readString();
        mCrefState = in.readString();
        mEmail = in.readString();
        mProfilePicsUri = in.readString();
        mHasStudio = in.readByte() != 0x00;
        mGrade = in.readFloat();
        mReviewCounter = in.readInt();
        m15MinutePrice = in.readInt();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mCref);
        dest.writeString(mCrefState);
        dest.writeString(mEmail);
        dest.writeString(mProfilePicsUri);
        dest.writeByte((byte) (mHasStudio ? 0x01 : 0x00));
        dest.writeFloat(mGrade);
        dest.writeInt(mReviewCounter);
        dest.writeInt(m15MinutePrice);

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PersonalTrainer> CREATOR = new Parcelable.Creator<PersonalTrainer>() {
        @Override
        public PersonalTrainer createFromParcel(Parcel in) {
            return new PersonalTrainer(in);
        }

        @Override
        public PersonalTrainer[] newArray(int size) {
            return new PersonalTrainer[size];
        }
    };
}
