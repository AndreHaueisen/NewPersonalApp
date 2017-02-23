package com.andrehaueisen.fitx.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andre on 9/7/2016.
 */

public class Review implements Parcelable {

    private float grade;
    private String text;

    public Review() {
        super();
    }

    public Review(float grade, String text) {
        this.grade = grade;
        this.text = text;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    protected Review(Parcel in) {
        grade = in.readFloat();
        text = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(grade);
        dest.writeString(text);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
