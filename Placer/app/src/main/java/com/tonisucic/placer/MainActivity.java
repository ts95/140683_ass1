package com.tonisucic.placer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.tonisucic.placer.unit.Distance;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    static final String TAG = MainActivity.class.getSimpleName();

    FloatingActionButton mAddActionButton;

    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Geocoder mGeocoder;
    LocationSource.OnLocationChangedListener mOnLocationChangedListener;

    Location mLocation;
    WeatherRestClient.Forecast mForecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGeocoder = new Geocoder(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAddActionButton = (FloatingActionButton) findViewById(R.id.ab_to_place);
        mAddActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLocation == null || mForecast == null)
                    return;

                Distance altitude = Distance.fromMeters(mLocation.getAltitude());

                Address address;

                try {
                    List<Address> addresses = mGeocoder.getFromLocation(
                            mLocation.getLatitude(), mLocation.getLongitude(), 1);

                    if (!addresses.isEmpty()) {
                        address = addresses.get(0);
                    } else {
                        return;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "mGeocoder exception", e);
                    return;
                }

                String place = address.getSubAdminArea();

                startPlaceActivity(altitude, place, mForecast);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "#onMapReady() called");

        mMap = googleMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener listener) {
                mOnLocationChangedListener = listener;
            }

            @Override
            public void deactivate() {
                mOnLocationChangedListener = null;
            }
        });

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation != null) {
            moveCameraToLocation(mLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "#onConnectionSuspended() called");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "#onLocationChanged() called");

        mLocation = location;

        moveCameraToLocation(mLocation);

        if (mOnLocationChangedListener != null)
            mOnLocationChangedListener.onLocationChanged(mLocation);

        if (mForecast == null) {
            WeatherRestClient.getForecast(latLngFromLocation(mLocation),
                    new WeatherRestClient.ForecastHandler() {
                        @Override
                        public void onSuccess(@NotNull WeatherRestClient.Forecast forecast) {
                            mForecast = forecast;
                        }

                        @Override
                        public void onFailure(String responseString, Throwable throwable) {
                            Log.e(MainActivity.class.getSimpleName(), responseString, throwable);
                        }
                    }
            );
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, connectionResult.getErrorMessage());
    }

    void moveCameraToLocation(Location location) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngFromLocation(location)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    LatLng latLngFromLocation(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    void startPlaceActivity(Distance altitude, String place, WeatherRestClient.Forecast forecast) {
        Intent placeActivityIntent = new Intent(MainActivity.this, PlaceActivity.class);
        placeActivityIntent.putExtra("altitude", altitude);
        placeActivityIntent.putExtra("place", place);
        placeActivityIntent.putExtra("forecast", forecast);

        startActivity(placeActivityIntent);
    }
}
