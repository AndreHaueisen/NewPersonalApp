package com.andrehaueisen.fitx.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andre on 9/7/2016.
 */

public class BodyMeasures implements Parcelable {

    private float weight;

    private double height;
    private double bust;
    private double chest;
    private double waist;
    private double hips;

    public BodyMeasures(float weight, double height, double bust, double chest, double waist, double hips) {
        this.weight = weight;
        this.height = height;
        this.bust = bust;
        this.chest = chest;
        this.waist = waist;
        this.hips = hips;
    }

    public BodyMeasures() {
        super();
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getBust() {
        return bust;
    }

    public void setBust(double bust) {
        this.bust = bust;
    }

    public double getChest() {
        return chest;
    }

    public void setChest(double chest) {
        this.chest = chest;
    }

    public double getWaist() {
        return waist;
    }

    public void setWaist(double waist) {
        this.waist = waist;
    }

    public double getHips() {
        return hips;
    }

    public void setHips(double hips) {
        this.hips = hips;
    }

    protected BodyMeasures(Parcel in) {
        weight = in.readFloat();
        height = in.readDouble();
        bust = in.readDouble();
        chest = in.readDouble();
        waist = in.readDouble();
        hips = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(weight);
        dest.writeDouble(height);
        dest.writeDouble(bust);
        dest.writeDouble(chest);
        dest.writeDouble(waist);
        dest.writeDouble(hips);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BodyMeasures> CREATOR = new Parcelable.Creator<BodyMeasures>() {
        @Override
        public BodyMeasures createFromParcel(Parcel in) {
            return new BodyMeasures(in);
        }

        @Override
        public BodyMeasures[] newArray(int size) {
            return new BodyMeasures[size];
        }
    };
}
