package com.semicolon.salonat.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.semicolon.salonat.models.SalonModel;

import org.greenrobot.eventbus.EventBus;

public class Fragment_Location_In_Salon extends Fragment implements OnMapReadyCallback{
    private static final String TAG = "data";
    private SalonModel salonModel;
    private SupportMapFragment fragment;
    private GoogleMap mMap;
    private FloatingActionButton fab;
    private double lat=0.0,lng=0.0;
    private final float zoom = 16.5f;
    private Marker marker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_in_salon,container,false);
        initView(view);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap!=null)
        {
            mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.setTrafficEnabled(false);
            mMap.setIndoorEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(),R.raw.maps));
            fab.setVisibility(View.VISIBLE);
            AddMarker(Double.parseDouble(salonModel.getAddress_google_lat()),Double.parseDouble(salonModel.getAddress_google_long()));
            }
    }
    public static Fragment_Location_In_Salon getInstance(SalonModel salonModel)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,salonModel);
        Fragment_Location_In_Salon fragment_location_in_salon = new Fragment_Location_In_Salon();
        fragment_location_in_salon.setArguments(bundle);
        return fragment_location_in_salon;
    }
    private void initView(View view) {

        fab = view.findViewById(R.id.fab);
        if (!EventBus.getDefault().isRegistered(getActivity()))
        {
            EventBus.getDefault().register(getActivity());
        }


        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            salonModel = (SalonModel) bundle.getSerializable(TAG);
            UpdateUI(salonModel);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(salonModel.getAddress_google_lat()),Double.parseDouble(salonModel.getAddress_google_long())),zoom));
            }
        });
    }

    private void initMap() {

        if (fragment==null)
        {
            fragment = SupportMapFragment.newInstance();
            fragment.getMapAsync(this);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.map,fragment).commit();

    }

    private void UpdateUI(SalonModel salonModel) {
        lat = Double.parseDouble(salonModel.getAddress_google_lat());
        lng = Double.parseDouble(salonModel.getAddress_google_long());
        initMap();
    }

    private void AddMarker(double myLat, double myLng) {
        if (marker==null)
        {
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(myLat,myLng)).icon(BitmapDescriptorFactory.fromBitmap(getBitmap())));

        }else
            {
                marker.setPosition(new LatLng(myLat,myLng));
            }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(salonModel.getAddress_google_lat()),Double.parseDouble(salonModel.getAddress_google_long())),zoom));

    }
    private Bitmap getBitmap()
    {
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






}
