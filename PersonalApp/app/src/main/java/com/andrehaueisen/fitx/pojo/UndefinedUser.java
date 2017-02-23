package com.andrehaueisen.fitx.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

/**
 * Created by andre on 11/20/2016.
 */

public class UndefinedUser implements Parcelable {

    @Nullable private String mName;
    private String mEmail;
    private String mUid;
    @Nullable private String mPhotoPath;
    //private boolean mIsClient;
    //private boolean mIsPersonal;

    public UndefinedUser() {
     //   mIsClient = false;
     //   mIsPersonal = false;
    }

    public UndefinedUser(String name, String email, String uid, String photoPath) {
        mName = name;
        mEmail = email;
        mUid = uid;
        mPhotoPath = photoPath;
      //  mIsClient = false;
      //  mIsPersonal = false;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public String getPhotoPath() {
        return mPhotoPath;
    }

    public void setPhotoPath(String photoPath) {
        mPhotoPath = photoPath;
    }

  /*  public boolean isClient() {
        return mIsClient;
    }

    public void setClient(boolean client) {
        mIsClient = client;
    }

    public boolean isPersonal() {
        return mIsPersonal;
    }

    public void setPersonal(boolean personal) {
        mIsPersonal = personal;
    } */

    protected UndefinedUser(Parcel in) {
        mName = in.readString();
        mEmail = in.readString();
        mUid = in.readString();
        mPhotoPath = in.readString();
    //    mIsClient = in.readByte() != 0x00;
        //   mIsPersonal = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mEmail);
        dest.writeString(mUid);
        dest.writeString(mPhotoPath);
    //    dest.writeByte((byte) (mIsClient ? 0x01 : 0x00));
    //    dest.writeByte((byte) (mIsPersonal? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UndefinedUser> CREATOR = new Parcelable.Creator<UndefinedUser>() {
        @Override
        public UndefinedUser createFromParcel(Parcel in) {
            return new UndefinedUser(in);
        }

        @Override
        public UndefinedUser[] newArray(int size) {
            return new UndefinedUser[size];
        }
    };
}
