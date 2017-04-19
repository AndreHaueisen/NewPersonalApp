package com.andrehaueisen.fitx.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by andre on 10/8/2016.
 */

public class PersonalWeekAgenda implements Parcelable {

    private ArrayList<Integer> mMONDAY;
    private ArrayList<Integer> mTUESDAY;
    private ArrayList<Integer> mWEDNESDAY;
    private ArrayList<Integer> mTHURSDAY;
    private ArrayList<Integer> mFRIDAY;
    private ArrayList<Integer> mSATURDAY;
    private ArrayList<Integer> mSUNDAY;

    public PersonalWeekAgenda() {
        super();
    }

    public PersonalWeekAgenda(ArrayList<Integer> MONDAY, ArrayList<Integer> TUESDAY, ArrayList<Integer> WEDNESDAY,
                              ArrayList<Integer> THURSDAY, ArrayList<Integer> FRIDAY, ArrayList<Integer> SATURDAY,
                              ArrayList<Integer> SUNDAY) {

        mMONDAY = MONDAY;
        mTUESDAY = TUESDAY;
        mWEDNESDAY = WEDNESDAY;
        mTHURSDAY = THURSDAY;
        mFRIDAY = FRIDAY;
        mSATURDAY = SATURDAY;
        mSUNDAY = SUNDAY;
    }

    public void setDayCodes(){

    }

    public ArrayList<Integer> getMONDAY() {
        return mMONDAY;
    }

    public void setMONDAY(ArrayList<Integer> MONDAY) {
        mMONDAY = MONDAY;
    }

    public ArrayList<Integer> getTUESDAY() {
        return mTUESDAY;
    }

    public void setTUESDAY(ArrayList<Integer> TUESDAY) {
        mTUESDAY = TUESDAY;
    }

    public ArrayList<Integer> getWEDNESDAY() {
        return mWEDNESDAY;
    }

    public void setWEDNESDAY(ArrayList<Integer> WEDNESDAY) {
        mWEDNESDAY = WEDNESDAY;
    }

    public ArrayList<Integer> getFRIDAY() {
        return mFRIDAY;
    }

    public void setFRIDAY(ArrayList<Integer> FRIDAY) {
        mFRIDAY = FRIDAY;
    }

    public ArrayList<Integer> getTHURSDAY() {
        return mTHURSDAY;
    }

    public void setTHURSDAY(ArrayList<Integer> THURSDAY) {
        mTHURSDAY = THURSDAY;
    }

    public ArrayList<Integer> getSATURDAY() {
        return mSATURDAY;
    }

    public void setSATURDAY(ArrayList<Integer> SATURDAY) {
        mSATURDAY = SATURDAY;
    }

    public ArrayList<Integer> getSUNDAY() {
        return mSUNDAY;
    }

    public void setSUNDAY(ArrayList<Integer> SUNDAY) {
        mSUNDAY = SUNDAY;
    }

    protected PersonalWeekAgenda(Parcel in) {
        if (in.readByte() == 0x01) {
            mMONDAY = new ArrayList<>();
            in.readList(mMONDAY, Integer.class.getClassLoader());
        } else {
            mMONDAY = null;
        }
        if (in.readByte() == 0x01) {
            mTUESDAY = new ArrayList<>();
            in.readList(mTUESDAY, Integer.class.getClassLoader());
        } else {
            mTUESDAY = null;
        }
        if (in.readByte() == 0x01) {
            mWEDNESDAY = new ArrayList<>();
            in.readList(mWEDNESDAY, Integer.class.getClassLoader());
        } else {
            mWEDNESDAY = null;
        }
        if (in.readByte() == 0x01) {
            mTHURSDAY = new ArrayList<>();
            in.readList(mTHURSDAY, Integer.class.getClassLoader());
        } else {
            mTHURSDAY = null;
        }
        if (in.readByte() == 0x01) {
            mFRIDAY = new ArrayList<>();
            in.readList(mFRIDAY, Integer.class.getClassLoader());
        } else {
            mFRIDAY = null;
        }
        if (in.readByte() == 0x01) {
            mSATURDAY = new ArrayList<>();
            in.readList(mSATURDAY, Integer.class.getClassLoader());
        } else {
            mSATURDAY = null;
        }
        if (in.readByte() == 0x01) {
            mSUNDAY = new ArrayList<>();
            in.readList(mSUNDAY, Integer.class.getClassLoader());
        } else {
            mSUNDAY = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mMONDAY == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mMONDAY);
        }
        if (mTUESDAY == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mTUESDAY);
        }
        if (mWEDNESDAY == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mWEDNESDAY);
        }
        if (mTHURSDAY == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mTHURSDAY);
        }
        if (mFRIDAY == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mFRIDAY);
        }
        if (mSATURDAY == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mSATURDAY);
        }
        if (mSUNDAY == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mSUNDAY);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PersonalWeekAgenda> CREATOR = new Parcelable.Creator<PersonalWeekAgenda>() {
        @Override
        public PersonalWeekAgenda createFromParcel(Parcel in) {
            return new PersonalWeekAgenda(in);
        }

        @Override
        public PersonalWeekAgenda[] newArray(int size) {
            return new PersonalWeekAgenda[size];
        }
    };
}
