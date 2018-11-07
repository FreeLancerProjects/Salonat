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
import com.semicolon.salonat.adapters.VoteAdapter;
import com.semicolon.salonat.models.SalonModel;
import com.semicolon.salonat.models.VoteModel;
import com.semicolon.salonat.remote.Api;
import com.semicolon.salonat.tags.Tags;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Vote_In_Salon extends Fragment {
    private static final String TAG = "data";

    private RecyclerView recView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter adapter;
    private ProgressBar progBar;
    private SalonModel salonModel;
    private List<VoteModel> voteModelList;
    private TextView tv_no_comm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_salon_vote,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Vote_In_Salon getInstance(SalonModel salonModel)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,salonModel);
        Fragment_Vote_In_Salon fragment_vote_in_salon = new Fragment_Vote_In_Salon();
        fragment_vote_in_salon.setArguments(bundle);
        return fragment_vote_in_salon;
    }
    private void initView(View view) {
        voteModelList = new ArrayList<>();
        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(),R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        tv_no_comm = view.findViewById(R.id.tv_no_comm);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);
        adapter = new VoteAdapter(getActivity(),voteModelList);
        recView.setAdapter(adapter);
        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            salonModel = (SalonModel) bundle.getSerializable(TAG);
            UpdateUI(salonModel);
        }
    }

    private void UpdateUI(SalonModel salonModel) {

        getVotes(salonModel.getId_salon());
    }
    private void getVotes(String id_salon) {
        Api.getService()
                .getClientsVotes(id_salon, Tags.in_salon)
                .enqueue(new Callback<List<VoteModel>>() {
                    @Override
                    public void onResponse(Call<List<VoteModel>> call, Response<List<VoteModel>> response) {
                        if (response.isSuccessful())
                        {
                            progBar.setVisibility(View.GONE);
                            voteModelList.clear();
                            voteModelList.addAll(response.body());

                            if (voteModelList.size()>0)
                            {
                                tv_no_comm.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }else
                            {
                                tv_no_comm.setVisibility(View.VISIBLE);

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<VoteModel>> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                        progBar.setVisibility(View.GONE);
                    }
                });
    }

}

