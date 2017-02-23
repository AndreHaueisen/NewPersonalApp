package com.andrehaueisen.fitx.pojo;

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
    private String mPhoneNumber;
    private String mEmail;
    private String mBirthday;
    private String mProfilePicsUri;
    private boolean mHasStudio;
    private float mGrade;
    private int mReviewCounter;


    public PersonalTrainer(){
        mGrade = 4.50f;
        mReviewCounter = 1;
    }

    public PersonalTrainer(String name, String cref, String crefState, String phoneNumber, String email, String birthday, String profilePicsUri,
                           boolean
            hasStudio, float grade, int reviewCounter) {
        mName = name;
        mCref = cref;
        mCrefState = crefState;
        mPhoneNumber = phoneNumber;
        mEmail = email;
        mBirthday = birthday;
        mProfilePicsUri = profilePicsUri;
        mHasStudio = hasStudio;
        mGrade = grade;
        mReviewCounter = reviewCounter;

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

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getBirthday() {
        return mBirthday;
    }

    public void setBirthday(String birthday) {
        mBirthday = birthday;
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

    protected PersonalTrainer(Parcel in) {
        mName = in.readString();
        mCref = in.readString();
        mCrefState = in.readString();
        mPhoneNumber = in.readString();
        mEmail = in.readString();
        mBirthday = in.readString();
        mProfilePicsUri = in.readString();
        mHasStudio = in.readByte() != 0x00;
        mGrade = in.readFloat();
        mReviewCounter = in.readInt();

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
        dest.writeString(mPhoneNumber);
        dest.writeString(mEmail);
        dest.writeString(mBirthday);
        dest.writeString(mProfilePicsUri);
        dest.writeByte((byte) (mHasStudio ? 0x01 : 0x00));
        dest.writeFloat(mGrade);
        dest.writeInt(mReviewCounter);

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
