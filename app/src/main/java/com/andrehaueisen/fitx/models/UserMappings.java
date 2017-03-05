package com.andrehaueisen.fitx.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andre on 11/20/2016.
 */

public class UserMappings implements Parcelable {

    private String mEncodedEmail;
    private boolean mIsPersonal;

    public UserMappings() {

    }

    public UserMappings(String encodedEmail, boolean isPersonal) {
        mEncodedEmail = encodedEmail;
        mIsPersonal = isPersonal;
    }

    public String getEncodedEmail() {
        return mEncodedEmail;
    }

    public void setEncodedEmail(String encodedEmail) {
        mEncodedEmail = encodedEmail;
    }

    public boolean isPersonal() {
        return mIsPersonal;
    }

    public void setPersonal(boolean personal) {
        mIsPersonal = personal;
    }

    protected UserMappings(Parcel in) {
        mEncodedEmail = in.readString();
        mIsPersonal = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mEncodedEmail);
        dest.writeByte((byte) (mIsPersonal ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserMappings> CREATOR = new Parcelable.Creator<UserMappings>() {
        @Override
        public UserMappings createFromParcel(Parcel in) {
            return new UserMappings(in);
        }

        @Override
        public UserMappings[] newArray(int size) {
            return new UserMappings[size];
        }
    };
}
