package com.andrehaueisen.fitx.pojo;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

/**
 * Created by andre on 10/20/2016.
 */

public class ClassReceipt implements Parcelable {

    @Exclude
    private Bitmap mPersonalProfileImage;

    private String mDateCode;
    private Integer mStartTimeCode;
    private Integer mDurationCode;
    private String mClientKey;
    private String mClassKey;
    private String mMainObjective;
    private String mPlaceAddress;
    private String mPlaceName;
    private String mClientName;
    private String mPersonalName;
    private String mPersonalKey;
    private Boolean mGotReview;

    public ClassReceipt() {
        mGotReview = false;
    }

    public ClassReceipt(Integer startTimeCode, String dateCode, Integer durationCode, String clientKey, String classKey, String mainObjective, String placeName,
                        String placeAddress, String personalName, String clientName, String personalKey) {
        mStartTimeCode = startTimeCode;
        mDateCode = dateCode;
        mDurationCode = durationCode;
        mClientKey = clientKey;
        mClassKey = classKey;
        mMainObjective = mainObjective;
        mPlaceName = placeName;
        mPlaceAddress = placeAddress;
        mPersonalName = personalName;
        mClientName = clientName;
        mPersonalKey = personalKey;
        mGotReview = false;
    }

    public Bitmap getPersonalProfileImage() {
        return mPersonalProfileImage;
    }

    public void setPersonalProfileImage(Bitmap personalProfileImage) {
        mPersonalProfileImage = personalProfileImage;
    }

    public String getDateCode() {
        return mDateCode;
    }

    public void setDateCode(String dateCode) {
        mDateCode = dateCode;
    }

    public Integer getStartTimeCode() {
        return mStartTimeCode;
    }

    public void setStartTimeCode(Integer startTimeCode) {
        mStartTimeCode = startTimeCode;
    }

    public Integer getDurationCode() {
        return mDurationCode;
    }

    public void setDurationCode(Integer durationCode) {
        mDurationCode = durationCode;
    }

    public String getClientKey() {
        return mClientKey;
    }

    public void setClientKey(String clientKey) {
        mClientKey = clientKey;
    }

    public String getClassKey() {
        return mClassKey;
    }

    public void setClassKey(String classKey) {
        mClassKey = classKey;
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

    public String getPlaceName() {
        return mPlaceName;
    }

    public void setPlaceName(String placeName) {
        mPlaceName = placeName;
    }

    public String getClientName() {
        return mClientName;
    }

    public void setClientName(String clientName) {
        mClientName = clientName;
    }

    public String getPersonalName() {
        return mPersonalName;
    }

    public void setPersonalName(String personalName) {
        mPersonalName = personalName;
    }

    public String getPersonalKey() {
        return mPersonalKey;
    }

    public void setPersonalKey(String personalKey) {
        mPersonalKey = personalKey;
    }

    public Boolean getGotReview() {
        return mGotReview;
    }

    public void setGotReview(Boolean gotReview) {
        mGotReview = gotReview;
    }

    protected ClassReceipt(Parcel in) {
        mPersonalProfileImage = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        mDateCode = in.readString();
        mStartTimeCode = in.readByte() == 0x00 ? null : in.readInt();
        mDurationCode = in.readByte() == 0x00 ? null : in.readInt();
        mClientKey = in.readString();
        mClassKey = in.readString();
        mMainObjective = in.readString();
        mPlaceAddress = in.readString();
        mPlaceName = in.readString();
        mClientName = in.readString();
        mPersonalName = in.readString();
        mPersonalKey = in.readString();
        byte mGotReviewVal = in.readByte();
        mGotReview = mGotReviewVal == 0x02 ? null : mGotReviewVal != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mPersonalProfileImage);
        dest.writeString(mDateCode);
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
        dest.writeString(mClientKey);
        dest.writeString(mClassKey);
        dest.writeString(mMainObjective);
        dest.writeString(mPlaceAddress);
        dest.writeString(mPlaceName);
        dest.writeString(mClientName);
        dest.writeString(mPersonalName);
        dest.writeString(mPersonalKey);
        if (mGotReview == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (mGotReview ? 0x01 : 0x00));
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ClassReceipt> CREATOR = new Parcelable.Creator<ClassReceipt>() {
        @Override
        public ClassReceipt createFromParcel(Parcel in) {
            return new ClassReceipt(in);
        }

        @Override
        public ClassReceipt[] newArray(int size) {
            return new ClassReceipt[size];
        }
    };
}