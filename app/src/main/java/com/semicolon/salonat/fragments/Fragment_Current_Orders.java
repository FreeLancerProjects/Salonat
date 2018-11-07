package com.semicolon.salonat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.semicolon.salonat.R;
import com.semicolon.salonat.activities.HomeActivity;
import com.semicolon.salonat.adapters.CurrentOrderAdapter;
import com.semicolon.salonat.models.ItemModel;
import com.semicolon.salonat.models.ServiceModel;
import com.semicolon.salonat.tags.Tags;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Current_Orders extends Fragment {
    private static String TAG="Data";
    private RecyclerView recView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter adapter;
    private ItemModel itemModel,itemModel2;
    private List<ServiceModel.Sub_Service> sub_serviceList;
    private HomeActivity homeActivity;
    private int cost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_orders,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Current_Orders getInstance(ItemModel itemModel)
    {
        Fragment_Current_Orders fragment_current_orders = new Fragment_Current_Orders();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,itemModel);
        fragment_current_orders.setArguments(bundle);
        return fragment_current_orders;
    }
    private void initView(View view) {
        homeActivity = (HomeActivity) getActivity();
        sub_serviceList = new ArrayList<>();

        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            itemModel = (ItemModel) bundle.getSerializable(TAG);
            cost = Integer.parseInt(itemModel.getReservation_cost());
            sub_serviceList.addAll(itemModel.getServiceModelList());
            adapter = new CurrentOrderAdapter(getActivity(),sub_serviceList,itemModel.getFrom(),this);
            recView.setAdapter(adapter);
        }

    }

    public void RemoveItem(int pos, ServiceModel.Sub_Service subService)
    {

        //itemModel.RemoveItem(itemModel,subService);

        sub_serviceList.remove(pos);
        adapter.notifyItemRemoved(pos);



        if (itemModel.getFrom().equals(Tags.in_salon))
        {


            cost = cost-Integer.parseInt(subService.getSalon_cost());


        }else if (itemModel.getFrom().equals(Tags.in_home))
        {


            cost = cost- Integer.parseInt(subService.getHome_cost());

        }

        itemModel2 = new ItemModel(itemModel.getSalon_id(),itemModel.getSalon_main_photo(),itemModel.getSalon_name(),itemModel.getSalon_rate(),itemModel.getGalleryModelList(),sub_serviceList,String.valueOf(cost),itemModel.getFrom());

        if (sub_serviceList.size()==0)
        {

           homeActivity.RemoveItemFromCurrentOrders();


        }else
            {
                homeActivity.UpdateItemModel(itemModel2);
            }
    }
}
