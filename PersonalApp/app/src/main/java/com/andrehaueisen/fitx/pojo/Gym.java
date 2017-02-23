package com.andrehaueisen.fitx.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

/**
 * Created by andre on 10/2/2016.
 */

public class Gym implements Parcelable {

    private String mPlaceId;
    private String mName;
    private String mAddress;
    private String mLatitude;
    private String mLongitude;

    @Exclude
    private AttributedPhoto mAttributedPhoto;

    public Gym() {
        super();
    }

    public Gym(String placeId, String name, String address, String latitude, String longitude) {
        mPlaceId = placeId;
        mName = name;
        mAddress = address;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public void setPlaceId(String placeId) {
        mPlaceId = placeId;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String latitude) {
        mLatitude = latitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String longitude) {
        mLongitude = longitude;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    @Exclude
    public AttributedPhoto getAttributedPhoto() {
        return mAttributedPhoto;
    }

    @Exclude
    public void setAttributedPhoto(AttributedPhoto attributedPhoto) {
        mAttributedPhoto = attributedPhoto;
    }

    protected Gym(Parcel in) {
        mPlaceId = in.readString();
        mName = in.readString();
        mAddress = in.readString();
        mLatitude = in.readString();
        mLongitude = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPlaceId);
        dest.writeString(mName);
        dest.writeString(mAddress);
        dest.writeString(mLatitude);
        dest.writeString(mLongitude);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Gym> CREATOR = new Parcelable.Creator<Gym>() {
        @Override
        public Gym createFromParcel(Parcel in) {
            return new Gym(in);
        }

        @Override
        public Gym[] newArray(int size) {
            return new Gym[size];
        }
    };
}
