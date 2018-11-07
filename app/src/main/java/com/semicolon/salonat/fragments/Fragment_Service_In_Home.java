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
import com.semicolon.salonat.adapters.ServiceInHomeSalonAdapter;
import com.semicolon.salonat.models.GroupModel;
import com.semicolon.salonat.models.ItemModel;
import com.semicolon.salonat.models.SalonModel;
import com.semicolon.salonat.models.ServiceModel;
import com.semicolon.salonat.remote.Api;
import com.semicolon.salonat.singletone.ItemSingleTone;
import com.semicolon.salonat.tags.Tags;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Service_In_Home extends Fragment {
    private static final String TAG = "data";
    private RecyclerView recView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter adapter;
    private ProgressBar progBar;
    private SalonModel salonModel;
    private TextView tv_no_service;
    private ItemSingleTone itemSingleTone;
    private ItemModel itemModel;
    private List<ServiceModel.Sub_Service> sub_serviceList;
    private HomeActivity homeActivity;
    private List<GroupModel> groupModelList;
    private List<ServiceModel> serviceModelList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_in_home,container,false);
        initView(view);
        Log.e("oncreate","oncreate2");

        return view;
    }

    public static Fragment_Service_In_Home getInstance(SalonModel salonModel)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,salonModel);

        Fragment_Service_In_Home fragment_service_in_home = new Fragment_Service_In_Home();
        fragment_service_in_home.setArguments(bundle);
        return fragment_service_in_home;
    }
    private void initView(View view) {
        sub_serviceList = new ArrayList<>();
        homeActivity = (HomeActivity) getActivity();
        itemSingleTone = ItemSingleTone.getInstance();
        tv_no_service = view.findViewById(R.id.tv_no_service);
        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(),R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);
        recView.setNestedScrollingEnabled(true);
        Bundle bundle =getArguments();
        if (bundle!=null)
        {
            salonModel = (SalonModel) bundle.getSerializable(TAG);
            UpdateUI(salonModel);
        }
    }

    private void UpdateUI(SalonModel salonModel) {
        getAllService(salonModel.getId_salon());
    }
    private void getAllService(String id_salon) {
        Api.getService()
                .getServices(id_salon, Tags.in_home)
                .enqueue(new Callback<List<ServiceModel>>() {
                    @Override
                    public void onResponse(Call<List<ServiceModel>> call, Response<List<ServiceModel>> response) {

                        if (response.isSuccessful())
                        {
                            progBar.setVisibility(View.GONE);
                            if (response.body().size()>0)
                            {
                                tv_no_service.setVisibility(View.GONE);
                                serviceModelList = response.body();
                                UpdateAdapterData(response.body());
                            }else
                            {
                                tv_no_service.setVisibility(View.VISIBLE);

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ServiceModel>> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        progBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void UpdateAdapterData(List<ServiceModel> serviceModelList) {
        groupModelList = new ArrayList<>();

        for (ServiceModel serviceModel:serviceModelList)
        {
            GroupModel groupModel = new GroupModel(serviceModel.getCategory_title(),serviceModel.getSub_service());
            groupModelList.add(groupModel);
        }

        adapter = new ServiceInHomeSalonAdapter(groupModelList,getActivity(),this);
        ServiceInHomeSalonAdapter serviceInHomeSalonAdapter = (ServiceInHomeSalonAdapter) adapter;
        recView.setAdapter(adapter);

        /*for (int i=groupModelList.size()-1;i>=0;i--)
        {

            serviceInHomeSalonAdapter.toggleGroup(i);
            adapter.notifyDataSetChanged();


        }*/






    }

    public void AddItem(ServiceModel.Sub_Service subService)
    {

        homeActivity.AddItemInHome(subService,salonModel);
        /*if (itemModel==null)
        {
            sub_serviceList.add(subService);
            itemModel = new ItemModel(salonModel.getId_salon(),salonModel.getMain_photo(),salonModel.getTitle(),String.valueOf(salonModel.getSalon_stars_num()),salonModel.getGallary(),sub_serviceList,subService.getSalon_cost(),Tags.in_home);
            homeActivity.UpdateCartNot(itemModel.getServiceModelList().size());
            homeActivity.UpdateReservationCostTotal(Integer.parseInt(itemModel.getReservation_cost()));
            itemSingleTone.setItemModel(itemModel);

        }else
        {
            if (TextUtils.isEmpty(itemModel.getReservation_cost()))
            {
                itemModel.setReservation_cost(subService.getSalon_cost());
            }else
            {
                itemModel.setReservation_cost(String.valueOf(Integer.parseInt(itemModel.getReservation_cost())+Integer.parseInt(subService.getSalon_cost())));
            }


            itemModel.AddItem(itemModel,subService);
            homeActivity.UpdateCartNot(itemModel.getServiceModelList().size());
            homeActivity.UpdateReservationCostTotal(Integer.parseInt(itemModel.getReservation_cost()));
            itemSingleTone.setItemModel(itemModel);


        }
*/
    }

    public void RemoveItem(ServiceModel.Sub_Service subService)
    {

        homeActivity.RemoveItemInHome(subService);
       /* if (itemModel!=null)
        {
            itemModel.RemoveItem(itemModel,subService);
            int reservation_cost =Integer.parseInt(itemModel.getReservation_cost())-Integer.parseInt(subService.getSalon_cost());
            itemModel.setReservation_cost(String.valueOf(reservation_cost));
            homeActivity.UpdateCartNot(itemModel.getServiceModelList().size());
            if (itemModel.getServiceModelList().size()>0)
            {
                homeActivity.UpdateReservationCostTotal(Integer.parseInt(itemModel.getReservation_cost()));
            }else
            {
                homeActivity.UpdateReservationCostTotal(0);

            }
        }*/

    }

    public void ResetData()
    {
        UpdateAdapterData(serviceModelList);
    }
}
