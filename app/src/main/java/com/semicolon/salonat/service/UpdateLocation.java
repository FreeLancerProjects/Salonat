package com.semicolon.salonat.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.semicolon.salonat.models.LocationModel;

import org.greenrobot.eventbus.EventBus;

public class UpdateLocation extends Service implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;


    @Override
    public void onCreate() {
        super.onCreate();
        initGoogleApiClient();
    }

    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdate();
    }

    private void startLocationUpdate() {
        initLocationRequest();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback()
                {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                }
                , Looper.myLooper());
    }

    private void initLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000*60);
        locationRequest.setInterval(1000*60);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient!=null)
            googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("lat",location.getLatitude()+"_");
        Log.e("lng",location.getLongitude()+"_");
        if (googleApiClient!=null&&googleApiClient.isConnected())
        {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(new LocationCallback());
        }
        EventBus.getDefault().post(new LocationModel(location.getLatitude(),location.getLongitude()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (googleApiClient!=null&&googleApiClient.isConnected())
        {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(new LocationCallback());
            googleApiClient.disconnect();
        }
    }
}
