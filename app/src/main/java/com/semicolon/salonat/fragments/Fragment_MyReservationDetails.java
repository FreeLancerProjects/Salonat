package com.semicolon.salonat.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.semicolon.salonat.R;
import com.semicolon.salonat.activities.HomeActivity;
import com.semicolon.salonat.adapters.MyReservationDetailsServiceAdapter;
import com.semicolon.salonat.models.MyReservationModel;
import com.semicolon.salonat.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Fragment_MyReservationDetails extends Fragment {
    public static final String TAG="DATA";
    private KenBurnsView image;
    private ImageView image_back;
    private TextView tv_name,tv_phone,tv_address,tv_cost,tv_date;
    private LinearLayout ll_call;
    private RecyclerView recView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private HomeActivity homeActivity;
    private List<MyReservationModel.Service> serviceList;
    private MyReservationModel myReservationModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myresrevation_details,container,false);
        initView(view);
        return view;
    }

    public static Fragment_MyReservationDetails getInstance(MyReservationModel myReservationModel)
    {
        Fragment_MyReservationDetails fragment_myReservationDetails = new Fragment_MyReservationDetails();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,myReservationModel);
        fragment_myReservationDetails.setArguments(bundle);
        return fragment_myReservationDetails;
    }

    private void initView(View view) {
        serviceList = new ArrayList<>();
        homeActivity = (HomeActivity) getActivity();
        image = view.findViewById(R.id.image);
        image_back = view.findViewById(R.id.image_back);
        tv_name = view.findViewById(R.id.tv_name);
        tv_phone = view.findViewById(R.id.tv_phone);
        tv_address = view.findViewById(R.id.tv_address);
        tv_cost = view.findViewById(R.id.tv_cost);
        tv_date = view.findViewById(R.id.tv_date);
        ll_call = view.findViewById(R.id.ll_call);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);
        adapter = new MyReservationDetailsServiceAdapter(getActivity(),serviceList);
        recView.setAdapter(adapter);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.Back();
            }
        });



        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            myReservationModel = (MyReservationModel) bundle.getSerializable(TAG);
            UpdateUI(myReservationModel);
        }


        ll_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+myReservationModel.getPhone()));
                startActivity(intent);
            }
        });

    }

    private void UpdateUI(MyReservationModel myReservationModel) {

        Picasso.with(getActivity()).load(Uri.parse(Tags.IMAGE_URL+myReservationModel.getMain_photo())).into(image);
        tv_name.setText(myReservationModel.getTitle());
        tv_phone.setText(myReservationModel.getPhone());
        tv_address.setText(myReservationModel.getAddress());
        tv_cost.setText(myReservationModel.getReservation_cost()+getString(R.string.sar));
        tv_date.setText(myReservationModel.getReservation_date_st()+"-"+myReservationModel.getReservation_time());
        serviceList.addAll(myReservationModel.getReservation_sevice());
        adapter.notifyDataSetChanged();
    }


}
