package com.semicolon.salonat.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.salonat.R;
import com.semicolon.salonat.activities.HomeActivity;
import com.semicolon.salonat.adapters.AllSalonsAdapter;
import com.semicolon.salonat.models.SalonModel;
import com.semicolon.salonat.remote.Api;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Home extends Fragment {

    private RecyclerView recView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter adapter;
    private ProgressBar progBar;
    private TextView tv_no_salon;
    private List<SalonModel> salonModelList;
    private HomeActivity homeActivity;
    private Fragment_Salon_details fragment_salon_details;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        homeActivity = (HomeActivity) getActivity();
        salonModelList = new ArrayList<>();
        tv_no_salon = view.findViewById(R.id.tv_no_salon);
        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(),R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);
        adapter = new AllSalonsAdapter(getActivity(),salonModelList,this);
        recView.setNestedScrollingEnabled(false);
        recView.setAdapter(adapter);
        getSalons();

    }

    public static Fragment_Home getInstance()
    {
        return new Fragment_Home();
    }

    private void getSalons() {

        Api.getService()
                .getAllSalons()
                .enqueue(new Callback<List<SalonModel>>() {
                    @Override
                    public void onResponse(Call<List<SalonModel>> call, Response<List<SalonModel>> response) {
                        if (response.isSuccessful())
                        {
                            progBar.setVisibility(View.GONE);
                            salonModelList.clear();
                            salonModelList.addAll(response.body());
                            if (salonModelList.size()>0)
                            {
                                tv_no_salon.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }else
                                {
                                    tv_no_salon.setVisibility(View.VISIBLE);

                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SalonModel>> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        progBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void setItem(SalonModel salonModel) {
        homeActivity.UpdateTitle(salonModel.getTitle());

        if (fragment_salon_details==null)
        {
            fragment_salon_details = Fragment_Salon_details.getInstance(salonModel);
        }
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_home_container,fragment_salon_details).addToBackStack("fragment_salon_details").commit();

    }
}
