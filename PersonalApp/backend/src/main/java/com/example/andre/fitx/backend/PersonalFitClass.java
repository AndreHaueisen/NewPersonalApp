package com.example.andre.fitx.backend;

/**
 * Created by andre on 1/31/2017.
 */

public class PersonalFitClass {

    private String mDateCode;
    private String mClassKey;
    private String mPlaceLongitude;
    private String mPlaceLatitude;
    private String mPlaceName;
    private String mClientKey;
    private String mMainObjective;
    private String mPlaceAddress;
    private String mClientName;

    private Integer mStartTimeCode;
    private Integer mDurationCode;

    private boolean mIsConfirmed;

    public PersonalFitClass(){
        mIsConfirmed = false;
    }

    public PersonalFitClass(String dateCode, String classKey, String placeLongitude, String placeLatitude, String placeName, Integer startTimeCode,
                            Integer durationCode, String clientKey, String clientName, String mainObjective,
                            String placeAddress, boolean isConfirmed) {
        mDateCode = dateCode;
        mClassKey = classKey;
        mPlaceLongitude = placeLongitude;
        mPlaceLatitude = placeLatitude;
        mPlaceName = placeName;
        mStartTimeCode = startTimeCode;
        mDurationCode = durationCode;
        mClientKey = clientKey;
        mClientName = clientName;
        mMainObjective = mainObjective;
        mPlaceAddress = placeAddress;
        mIsConfirmed = isConfirmed;
    }

    public PersonalFitClass(String dateCode, String placeLongitude, String placeLatitude, String placeName, Integer startTimeCode,
                            Integer durationCode, String clientKey, String clientName, String mainObjective,
                            String placeAddress, boolean isConfirmed) {

        mDateCode = dateCode;
        mPlaceLongitude = placeLongitude;
        mPlaceLatitude = placeLatitude;
        mPlaceName = placeName;
        mStartTimeCode = startTimeCode;
        mDurationCode = durationCode;
        mClientKey = clientKey;
        mClientName = clientName;
        mMainObjective = mainObjective;
        mPlaceAddress = placeAddress;
        mIsConfirmed = isConfirmed;
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

    public String getClientKey() {
        return mClientKey;
    }

    public void setClientKey(String clientKey) {
        mClientKey = clientKey;
    }

    public String getClientName() {
        return mClientName;
    }

    public void setClientName(String clientName) {
        mClientName = clientName;
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
}
