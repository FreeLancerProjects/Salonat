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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.semicolon.salonat.R;
import com.semicolon.salonat.activities.HomeActivity;
import com.semicolon.salonat.adapters.NotificationAdapter;
import com.semicolon.salonat.models.MyReservationModel;
import com.semicolon.salonat.models.UserModel;
import com.semicolon.salonat.remote.Api;
import com.semicolon.salonat.singletone.UserSingleTone;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentNotifications extends Fragment {
    private ImageView image_back;
    private RecyclerView recView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private ProgressBar progBar;
    private LinearLayout ll_no_not;
    private HomeActivity homeActivity;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification,container,false);
        initView(view);
        return  view;
    }

    public static FragmentNotifications getInstance()
    {
        FragmentNotifications fragmentNotifications = new FragmentNotifications();
        return fragmentNotifications;
    }
    private void initView(View view) {
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        homeActivity = (HomeActivity) getActivity();
        image_back = view.findViewById(R.id.image_back);
        recView = view.findViewById(R.id.recView);
        progBar = view.findViewById(R.id.progBar);
        ll_no_not = view.findViewById(R.id.ll_no_not);

        manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.Back();
            }
        });

        getNotifications(userModel.getUser_id());



    }

    private void getNotifications(String user_id) {
        Api.getService()
                .getNotifications(user_id)
                .enqueue(new Callback<List<MyReservationModel>>() {
                    @Override
                    public void onResponse(Call<List<MyReservationModel>> call, Response<List<MyReservationModel>> response) {
                        if (response.isSuccessful())
                        {
                            progBar.setVisibility(View.GONE);
                            if (response.body().size()>0)
                            {
                                ll_no_not.setVisibility(View.GONE);
                                adapter = new NotificationAdapter(getActivity(),response.body(),FragmentNotifications.this);
                                recView.setAdapter(adapter);
                            }else
                                {
                                    ll_no_not.setVisibility(View.VISIBLE);

                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MyReservationModel>> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        progBar.setVisibility(View.GONE);
                        Toast.makeText(homeActivity, R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setItem(MyReservationModel myReservationModel) {
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_home_container,Fragment_Payment.getInstance(myReservationModel)).addToBackStack("fragment_payment").commit();

    }
}
