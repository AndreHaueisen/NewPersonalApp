package com.andrehaueisen.fitx.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andre on 9/7/2016.
 */
public class Client implements Parcelable {

    private String mName;
    private String mEmail;
    private String mMainObjective;
    private Gym mClientGym;


    public Client() {
        super();
    }

    public Client(String name, String email, String mainObjective, Gym clientGym) {

        mName = name;
        mEmail = email;
        mMainObjective = mainObjective;
        mClientGym = clientGym;
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

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
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


    protected Client(Parcel in) {
        mName = in.readString();
        mEmail = in.readString();
        mMainObjective = in.readString();
        mClientGym = (Gym) in.readValue(Gym.class.getClassLoader());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mEmail);
        dest.writeString(mMainObjective);
        dest.writeValue(mClientGym);
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
