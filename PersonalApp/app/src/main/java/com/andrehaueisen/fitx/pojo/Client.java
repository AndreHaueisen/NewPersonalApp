package com.andrehaueisen.fitx.pojo;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andre on 9/7/2016.
 */
//TODO check about how to implement payment
//TODO check about saving and retrieving user images
public class Client implements Parcelable {

    private String mName;
    private String mBirthday;
    private String mEmail;
    private String mPhoneNumber;
    private String mMainObjective;
    private Gym mClientGym;

    private BodyMeasures mBodyMeasures;


    public Client() {
        super();
    }

    public Client(String name, String birthday, String email, String phoneNumber, String mainObjective, Gym clientGym, BodyMeasures bodyMeasures,
                  Location trainingLocations) {

        mName = name;
        mBirthday = birthday;
        mEmail = email;
        mPhoneNumber = phoneNumber;
        mMainObjective = mainObjective;
        mClientGym = clientGym;
        mBodyMeasures = bodyMeasures;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getBirthday() {
        return mBirthday;
    }

    public void setBirthday(String birthday) {
        mBirthday = birthday;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getMainObjective() {
        return mMainObjective;
    }

    public void setMainObjective(String mainObjective) {
        mMainObjective = mainObjective;
    }

    public Gym getClientGym() {
        return mClientGym;
    }

    public void setClientGym(Gym clientGym) {
        mClientGym = clientGym;
    }

    public BodyMeasures getBodyMeasures() {
        return mBodyMeasures;
    }

    public void setBodyMeasures(BodyMeasures bodyMeasures) {
        mBodyMeasures = bodyMeasures;
    }


    protected Client(Parcel in) {
        mName = in.readString();
        mBirthday = in.readString();
        mEmail = in.readString();
        mPhoneNumber = in.readString();
        mMainObjective = in.readString();
        mClientGym = (Gym) in.readValue(Gym.class.getClassLoader());
        mBodyMeasures = (BodyMeasures) in.readValue(BodyMeasures.class.getClassLoader());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mBirthday);
        dest.writeString(mEmail);
        dest.writeString(mPhoneNumber);
        dest.writeString(mMainObjective);
        dest.writeValue(mClientGym);
        dest.writeValue(mBodyMeasures);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Client> CREATOR = new Parcelable.Creator<Client>() {
        @Override
        public Client createFromParcel(Parcel in) {
            return new Client(in);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }
    };
}
