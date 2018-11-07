package com.semicolon.salonat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.semicolon.salonat.R;
import com.semicolon.salonat.adapters.MyReservationAdapter;
import com.semicolon.salonat.models.MyReservationModel;
import com.semicolon.salonat.models.UserModel;
import com.semicolon.salonat.remote.Api;
import com.semicolon.salonat.singletone.UserSingleTone;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_MyReservations extends Fragment {
    private RecyclerView recView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private ProgressBar progBar;
    private LinearLayout ll_no_reserve;
    private List<MyReservationModel> myReservationModelList;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myreservations,container,false);
        initView(view);
        return view;
    }

    public static Fragment_MyReservations getInstance()
    {
        return new Fragment_MyReservations();
    }
    private void initView(View view) {
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        myReservationModelList = new ArrayList<>();
        ll_no_reserve = view.findViewById(R.id.ll_no_reserve);
        progBar = view.findViewById(R.id.progBar);
        recView = view.findViewById(R.id.recView);
        manager  = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);
        adapter = new MyReservationAdapter(getActivity(),myReservationModelList,this);
        recView.setAdapter(adapter);
        getMyReservationData(userModel.getUser_id());
    }

    private void getMyReservationData(String user_id) {
        Api.getService()
                .getMyReservations(user_id)
                .enqueue(new Callback<List<MyReservationModel>>() {
                    @Override
                    public void onResponse(Call<List<MyReservationModel>> call, Response<List<MyReservationModel>> response) {
                        if (response.isSuccessful())
                        {
                            progBar.setVisibility(View.GONE);
                            if (response.body().size()>0)
                            {
                                myReservationModelList.addAll(response.body());
                                adapter.notifyDataSetChanged();
                                ll_no_reserve.setVisibility(View.GONE);
                            }else
                                {
                                    ll_no_reserve.setVisibility(View.VISIBLE);

                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MyReservationModel>> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        progBar.setVisibility(View.GONE);
                    }
                });
    }


    public void setItemModel(MyReservationModel myReservationModel) {

    }




}
