package com.andrehaueisen.fitx.pojo;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.firebase.database.Exclude;

/**
 * Created by andre on 11/3/2016.
 */

public class AttributedPhoto implements Parcelable {

    @Exclude
    private CharSequence attribution;
    @Exclude
    private Bitmap bitmap;

    public AttributedPhoto() {

    }

    public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
        this.attribution = attribution;
        this.bitmap = bitmap;
    }

    public CharSequence getAttribution() {
        return attribution;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    protected AttributedPhoto(Parcel in) {
        attribution = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        bitmap = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(attribution);
        dest.writeValue(bitmap);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AttributedPhoto> CREATOR = new Parcelable.Creator<AttributedPhoto>() {
        @Override
        public AttributedPhoto createFromParcel(Parcel in) {
            return new AttributedPhoto(in);
        }

        @Override
        public AttributedPhoto[] newArray(int size) {
            return new AttributedPhoto[size];
        }
    };
}
