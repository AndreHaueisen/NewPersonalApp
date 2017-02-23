package com.andrehaueisen.fitx.pojo;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;

/**
 * Created by andre on 11/21/2016.
 */

public class ClientFitClass implements Parcelable {

    @Exclude
    @Nullable
    private Bitmap mClassProfileImage;

    private String mDateCode;
    private String mClassKey;
    private String mPlaceLongitude;
    private String mPlaceLatitude;
    private String mPlaceName;
    private Integer mStartTimeCode;
    private Integer mDurationCode;
    private String mPersonalKey;
    private String mMainObjective;
    private String mPlaceAddress;
    private String mPersonalName;
    private boolean mIsConfirmed;

    public ClientFitClass() {
        mIsConfirmed = false;
    }

    public ClientFitClass(String dateCode, String classKey, String placeLongitude, String placeLatitude, String placeName, Integer startTimeCode,
                          Integer durationCode, String personalKey, String clientName, String personalName, String mainObjective,
                          String placeAddress, boolean isConfirmed) {
        mDateCode = dateCode;
        mClassKey = classKey;
        mPlaceLongitude = placeLongitude;
        mPlaceLatitude = placeLatitude;
        mPlaceName = placeName;
        mStartTimeCode = startTimeCode;
        mDurationCode = durationCode;
        mPersonalKey = personalKey;
        mPersonalName = personalName;
        mMainObjective = mainObjective;
        mPlaceAddress = placeAddress;
        mIsConfirmed = isConfirmed;
    }

    @Nullable
    public Bitmap getClassProfileImage() {
        return mClassProfileImage;
    }

    public void setClassProfileImage(@Nullable Bitmap classProfileImage) {
        mClassProfileImage = classProfileImage;
    }

    public String getDateCode() {
        return mDateCode;
    }

    public void setDateCode(String dateCode) {
        mDateCode = dateCode;
    }

    public String getClassKey() {
        return mClassKey;
    }

    public void setClassKey(String classKey) {
        mClassKey = classKey;
    }

    public String getPlaceLongitude() {
        return mPlaceLongitude;
    }

    public void setPlaceLongitude(String placeLongitude) {
        mPlaceLongitude = placeLongitude;
    }

    public String getPlaceLatitude() {
        return mPlaceLatitude;
    }

    public void setPlaceLatitude(String placeLatitude) {
        mPlaceLatitude = placeLatitude;
    }

    public String getPlaceName() {
        return mPlaceName;
    }

    public void setPlaceName(String placeName) {
        mPlaceName = placeName;
    }

    public Integer getStartTimeCode() {
        return mStartTimeCode;
    }

    public void setStartTimeCode(int startTimeCode) {
        mStartTimeCode = startTimeCode;
    }

    public Integer getDurationCode() {
        return mDurationCode;
    }

    public void setDurationCode(int durationCode) {
        mDurationCode = durationCode;
    }

    public String getPersonalKey() {
        return mPersonalKey;
    }

    public void setPersonalKey(String personalKey) {
        mPersonalKey = personalKey;
    }

    public String getPersonalName() {
        return mPersonalName;
    }

    public void setPersonalName(String personalName) {
        mPersonalName = personalName;
    }

    public String getMainObjective() {
        return mMainObjective;
    }

    public void setMainObjective(String mainObjective) {
        mMainObjective = mainObjective;
    }

    public String getPlaceAddress() {
        return mPlaceAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        mPlaceAddress = placeAddress;
    }

    public boolean isConfirmed() {
        return mIsConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        mIsConfirmed = confirmed;
    }

    protected ClientFitClass(Parcel in) {
        mDateCode = in.readString();
        mClassKey = in.readString();
        mPlaceLongitude = in.readString();
        mPlaceLatitude = in.readString();
        mPlaceName = in.readString();
        mStartTimeCode = in.readByte() == 0x00 ? null : in.readInt();
        mDurationCode = in.readByte() == 0x00 ? null : in.readInt();
        mPersonalKey = in.readString();
        mPersonalName = in.readString();
        mMainObjective = in.readString();
        mPlaceAddress = in.readString();
        mClassProfileImage = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        mIsConfirmed = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDateCode);
        dest.writeString(mClassKey);
        dest.writeString(mPlaceLongitude);
        dest.writeString(mPlaceLatitude);
        dest.writeString(mPlaceName);
        if (mStartTimeCode == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mStartTimeCode);
        }
        if (mDurationCode == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mDurationCode);
        }
        dest.writeString(mPersonalKey);
        dest.writeString(mPersonalName);
        dest.writeString(mMainObjective);
        dest.writeString(mPlaceAddress);
        dest.writeValue(mClassProfileImage);
        dest.writeByte((byte) (mIsConfirmed ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<com.andrehaueisen.fitx.pojo.PersonalFitClass> CREATOR = new Parcelable.Creator<com.andrehaueisen.fitx.pojo.PersonalFitClass>() {
        @Override
        public com.andrehaueisen.fitx.pojo.PersonalFitClass createFromParcel(Parcel in) {
            return new com.andrehaueisen.fitx.pojo.PersonalFitClass(in);
        }

        @Override
        public com.andrehaueisen.fitx.pojo.PersonalFitClass[] newArray(int size) {
            return new com.andrehaueisen.fitx.pojo.PersonalFitClass[size];
        }
    };

}
