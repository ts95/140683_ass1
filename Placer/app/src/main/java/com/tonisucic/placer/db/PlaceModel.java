package com.tonisucic.placer.db;

import com.tonisucic.placer.unit.Distance;
import com.tonisucic.placer.unit.Temperature;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by tonisucic on 16.09.2016.
 */
public class PlaceModel extends RealmObject {

    private Distance mAltitude;
    private Temperature mTemp;
    private String mPlace;
    private Date mCreatedAt;

    public PlaceModel() {
        super();
    }

    public PlaceModel(Distance altitude, Temperature temp, String place) {
        mAltitude = altitude;
        mTemp = temp;
        mPlace = place;
        mCreatedAt = new Date();
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public Distance getAltitude() {
        return mAltitude;
    }

    public Temperature getTemp() {
        return mTemp;
    }

    public String getPlace() {
        return mPlace;
    }
}
