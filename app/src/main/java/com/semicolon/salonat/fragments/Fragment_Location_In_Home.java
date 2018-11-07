package com.semicolon.salonat.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.semicolon.salonat.R;
import com.semicolon.salonat.models.LocationModel;
import com.semicolon.salonat.models.SalonModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class Fragment_Location_In_Home extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "data";

    private SalonModel salonModel;
    private final String fineLoc = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int fine_req = 1021, gps_req = 225;
    private AlertDialog gps_dialog;
    private SupportMapFragment fragment;
    private GoogleMap mMap;
    private double myLat = 0.0, myLng = 0.0;
    private FloatingActionButton fab;
    private final float zoom = 16.5f;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Marker marker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_in_home, container, false);
        initView(view);
        return view;
    }


    public static Fragment_Location_In_Home getInstance(SalonModel salonModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG, salonModel);
        Fragment_Location_In_Home fragment_location_in_home = new Fragment_Location_In_Home();
        fragment_location_in_home.setArguments(bundle);
        return fragment_location_in_home;
    }

    private void initView(View view) {
        fab = view.findViewById(R.id.fab);

        Bundle bundle = getArguments();
        if (bundle != null) {
            salonModel = (SalonModel) bundle.getSerializable(TAG);
            UpdateUI(salonModel);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLat, myLng), zoom));
            }
        });
    }

    private void UpdateUI(SalonModel salonModel) {

        initMap();

    }

    private void initMap() {

        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fragment.getMapAsync(this);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.map, fragment).commit();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.setTrafficEnabled(false);
            mMap.setIndoorEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.maps));

            checkGpsPermission();
        }
    }

    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenForLocationUpdate(LocationModel locationModel) {
        Log.e("lat1", locationModel.getLat() + "__");
        Log.e("lng", locationModel.getLng() + "__");
        myLat = locationModel.getLat();
        myLng = locationModel.getLng();
        fab.setVisibility(View.VISIBLE);
        AddMarker(myLat, myLng);

    }

    private void AddMarker(double myLat, double myLng) {
        if (marker==null)
        {
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(myLat,myLng)).icon(BitmapDescriptorFactory.fromBitmap(getBitmap())));

        }else
        {
            marker.setPosition(new LatLng(myLat,myLng));
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLat,myLng),zoom));

    }

    private Bitmap getBitmap() {
        Bitmap bitmap1 = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.user_map_icon);
        int newWidth = 80;
        int newHeight = 80;
        float scaleWidth = ((float)newWidth/bitmap1.getWidth());
        float scaleHeight = ((float)newHeight/bitmap1.getHeight());

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        Bitmap returnedBitmap = Bitmap.createBitmap(bitmap1,0,0,bitmap1.getWidth(),bitmap1.getHeight(),matrix,true);
        return returnedBitmap;
    }

    private void checkGpsPermission() {

        if (ContextCompat.checkSelfPermission(getActivity(), fineLoc) != PackageManager.PERMISSION_GRANTED) {
            String[] perm = {fineLoc};
            ActivityCompat.requestPermissions(getActivity(), perm, fine_req);
        } else {
            if (isGpsOpen()) {
                initGoogleApiClient();
            } else {
                CreateGpsDialog();
            }
        }
    }


    private void CreateGpsDialog() {
        gps_dialog = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .create();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_dialog, null);
        TextView tv_msg = view.findViewById(R.id.tv_msg);
        tv_msg.setText(R.string.gps_will_open);
        Button doneBtn = view.findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps_dialog.dismiss();
                openGps();
            }
        });

        gps_dialog.getWindow().getAttributes().windowAnimations = R.style.dialog;
        gps_dialog.setView(view);
        gps_dialog.setCanceledOnTouchOutside(false);
        gps_dialog.show();

    }

    private void openGps() {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        getActivity().startActivityForResult(intent, gps_req);

    }

    private boolean isGpsOpen() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == fine_req) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isGpsOpen()) {
                        initGoogleApiClient();
                    } else {
                        CreateGpsDialog();
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gps_req) {
            if (isGpsOpen()) {
                initGoogleApiClient();
            } else {
                Toast.makeText(getActivity(), R.string.cnnot_open_gps, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        StartLocationUpdate();
    }

    private void StartLocationUpdate() {
        Log.e("1","1");
        initLocationRequest();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(locationRequest, new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        }, Looper.myLooper());
    }

    private void initLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(60000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient!=null)
        {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("sfsdfsdfsdfsdfs",location.getLatitude()+"");
        Log.e("sfsdfsdfsdfsdfs",location.getLongitude()+"");

        fab.setVisibility(View.VISIBLE);
        myLat = location.getLatitude();
        myLng = location.getLongitude();
        AddMarker(location.getLatitude(),location.getLongitude());
    }
}
