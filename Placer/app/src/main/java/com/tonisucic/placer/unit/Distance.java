package com.tonisucic.placer.unit;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.tonisucic.placer.R;

import io.realm.RealmObject;

/**
 * Created by tonisucic on 16.09.2016.
 */
public class Distance extends RealmObject implements Parcelable {

    public static Distance fromMeters(double meters) {
        return new Distance(meters);
    }

    public static Distance fromFeet(double feet) {
        return new Distance(feet * 0.3048);
    }

    private double mMeters;

    public Distance() {
        super();
    }

    private Distance(double meters) {
        mMeters = meters;
    }

    public double getMeters() {
        return mMeters;
    }

    public double getFeet() {
        return mMeters * 3.2808399;
    }

    public String getMetersString(Resources res) {
        int meters = (int) getMeters();
        return res.getQuantityString(R.plurals.meter, meters, meters);
    }

    public String getFeetString(Resources res) {
        int feet = (int) getFeet();
        return res.getQuantityString(R.plurals.foot, feet, feet);
    }

    public String getLocaleString(Resources res) {
        return UnitLocale.getDefault() == UnitLocale.imperial ?
                getFeetString(res) : getMetersString(res);
    }

    protected Distance(Parcel in) {
        mMeters = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mMeters);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Distance> CREATOR = new Parcelable.Creator<Distance>() {
        @Override
        public Distance createFromParcel(Parcel in) {
            return new Distance(in);
        }

        @Override
        public Distance[] newArray(int size) {
            return new Distance[size];
        }
    };
}
