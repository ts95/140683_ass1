package com.tonisucic.placer.unit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

import io.realm.RealmObject;

/**
 * Created by tonisucic on 16.09.2016.
 */
public class Temperature extends RealmObject implements Parcelable {

    public static Temperature fromCelsius(double celsius) {
        return new Temperature(celsius + 273.15);
    }

    public static Temperature fromFahrenheit(double fahrenheit) {
        return new Temperature((fahrenheit + 459.67) * 5/9);
    }

    public static Temperature fromKelvin(double kelvin) {
        return new Temperature(kelvin);
    }

    private double mKelvin;

    public  Temperature() {
        super();
    }

    private Temperature(double kelvin) {
        mKelvin = kelvin;
    }

    public double getKelvin() {
        return mKelvin;
    }

    public double getCelsius() {
        return mKelvin - 273.15;
    }

    public double getFahrenheit() {
        return (mKelvin * 9/5) - 459.67;
    }

    public String getKelvinString() {
        return String.format(Locale.getDefault(), "%.1f°K", getKelvin());
    }

    public String getCelsiusString() {
        return String.format(Locale.getDefault(), "%.1f°C", getCelsius());
    }

    public String getFahrenheitString() {
        return String.format(Locale.getDefault(), "%.1f°F", getFahrenheit());
    }

    public String getLocaleString() {
        return UnitLocale.getDefault() == UnitLocale.imperial ?
                 getFahrenheitString() : getCelsiusString();
    }

    protected Temperature(Parcel in) {
        mKelvin = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mKelvin);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Temperature> CREATOR = new Parcelable.Creator<Temperature>() {
        @Override
        public Temperature createFromParcel(Parcel in) {
            return new Temperature(in);
        }

        @Override
        public Temperature[] newArray(int size) {
            return new Temperature[size];
        }
    };
}
