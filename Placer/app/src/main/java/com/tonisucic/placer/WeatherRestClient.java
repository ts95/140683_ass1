package com.tonisucic.placer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tonisucic.placer.unit.Temperature;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Toni Sucic on 15.09.2016.
 */
public class WeatherRestClient {

    static final String TAG = WeatherRestClient.class.getSimpleName();

    static final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast";
    static final String APP_KEY = "d3e78a6c74fc541ddc6bf68be74315ac";

    static AsyncHttpClient mClient = new AsyncHttpClient();

    public static void getForecast(LatLng latLng, final ForecastHandler handler) {
        RequestParams params = new RequestParams();
        params.put("lat", latLng.latitude);
        params.put("lon", latLng.longitude);

        get("/city", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray list = response.getJSONArray("list");
                    JSONObject info = list.getJSONObject(0);
                    JSONObject main = info.getJSONObject("main");

                    double temp = main.getDouble("temp");
                    double tempMin = main.getDouble("temp_min");
                    double tempMax = main.getDouble("temp_max");
                    double humidity = main.getDouble("humidity");

                    handler.onSuccess(
                        new Forecast(
                            Temperature.fromKelvin(temp),
                            Temperature.fromKelvin(tempMax),
                            Temperature.fromKelvin(tempMin),
                            humidity
                        )
                    );
                } catch (JSONException ex) {
                    handler.onFailure(null, ex);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.onFailure(responseString, throwable);
            }
        });
    }

    private static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        params.add("APPID", APP_KEY);
        mClient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        params.add("APPID", APP_KEY);
        mClient.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static class Forecast implements Parcelable {

        private Temperature mTemp;
        private Temperature mTempMax;
        private Temperature mTempMin;
        private double mHumidity;

        public Forecast(Temperature temp, Temperature tempMax, Temperature tempMin, double humidity) {
            mTemp = temp;
            mTempMax = tempMax;
            mTempMin = tempMin;
            mHumidity = humidity;
        }

        protected Forecast(Parcel parcel) {
            mTemp = parcel.readParcelable(Temperature.class.getClassLoader());
            mTempMax = parcel.readParcelable(Temperature.class.getClassLoader());
            mTempMin = parcel.readParcelable(Temperature.class.getClassLoader());
            mHumidity = parcel.readDouble();
        }

        public Temperature getTemp() {
            return mTemp;
        }

        public Temperature getTempMax() {
            return mTempMax;
        }

        public Temperature getTempMin() {
            return mTempMin;
        }

        public double getHumidity() {
            return mHumidity;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int i) {
            dest.writeParcelable(mTemp, 0);
            dest.writeParcelable(mTempMax, 0);
            dest.writeParcelable(mTempMin, 0);
            dest.writeDouble(mHumidity);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public Forecast createFromParcel(Parcel in) {
                return new Forecast(in);
            }

            public Forecast[] newArray(int size) {
                return new Forecast[size];
            }
        };
    }

    public interface ForecastHandler {

        void onSuccess(@NotNull Forecast forecast);
        void onFailure(String responseString, Throwable throwable);
    }
}
