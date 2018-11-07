package com.semicolon.salonat.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.semicolon.salonat.R;
import com.semicolon.salonat.fragments.Fragment_Service_In_Home;
import com.semicolon.salonat.models.ServiceModel;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.listeners.ExpandCollapseListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceInHomeSalonAdapter extends ExpandableRecyclerViewAdapter<ParentViewHolder,MyChildViewHolder> implements ExpandCollapseListener{
    private Context context;
    private Fragment fragment;
    private Map<String,Boolean> map;


    public ServiceInHomeSalonAdapter(List<? extends ExpandableGroup> groups, Context context, Fragment fragment) {
        super(groups);
        this.context=context;
        this.fragment = fragment;
        this.map = new HashMap<>();

    }

    @Override
    public ParentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_row,parent,false);
        return new ParentViewHolder(view);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.child_row,parent,false);
        return new MyChildViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(final MyChildViewHolder holder, int flatPosition, final ExpandableGroup group, final int childIndex) {


        ServiceModel.Sub_Service sub_service = (ServiceModel.Sub_Service) group.getItems().get(childIndex);
        holder.BindData(sub_service.getService_title(),sub_service.getSalon_time_take(),sub_service.getHome_cost(),sub_service.getState_checked());

        Log.e("pos",holder.getAdapterPosition()+"___$$");
        Log.e("flatpos",flatPosition+"___!!");


        if (map.size()>0)
        {
            for (String key :map.keySet())
            {
                Log.e("Key",key);

            }




            if (map.get(sub_service.getId_service())!=null)
            {
                if (map.get(sub_service.getId_service())==true)
                {
                    holder.checkBox.setChecked(true);
                }else
                {
                    holder.checkBox.setChecked(false);

                }
            }else
            {
                if (sub_service.getState_checked()==0)
                {
                    map.put(sub_service.getId_service(),false);
                }else if (sub_service.getState_checked()==1)
                {
                    map.put(sub_service.getId_service(),true);

                }
            }

        }else
        {
            if (sub_service.getState_checked()==0)
            {
                map.put(sub_service.getId_service(),false);
            }else if (sub_service.getState_checked()==1)
            {
                map.put(sub_service.getId_service(),true);

            }
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceModel.Sub_Service sub_service = (ServiceModel.Sub_Service) group.getItems().get(childIndex);
                Fragment_Service_In_Home fragment_service_in_home = (Fragment_Service_In_Home) fragment;

                if (holder.checkBox.isChecked())
                {
                    map.put(sub_service.getId_service(),false);
                    holder.checkBox.setChecked(false);
                    fragment_service_in_home.RemoveItem(sub_service);
                }else
                {
                    map.put(sub_service.getId_service(),true);

                    holder.checkBox.setChecked(true);
                    fragment_service_in_home.AddItem(sub_service);


                }
            }

        });



        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceModel.Sub_Service sub_service = (ServiceModel.Sub_Service) group.getItems().get(childIndex);
                Fragment_Service_In_Home fragment_service_in_home = (Fragment_Service_In_Home) fragment;

                Log.e("checked",holder.checkBox.isChecked()+"___");


                if (holder.checkBox.isChecked())
                {
                    map.put(sub_service.getId_service(),true);
                    holder.checkBox.setChecked(true);
                    fragment_service_in_home.AddItem(sub_service);

                }else
                {
                    map.put(sub_service.getId_service(),false);

                    holder.checkBox.setChecked(false);
                    fragment_service_in_home.RemoveItem(sub_service);



                }
            }
        });
    }

    @Override
    public void onBindGroupViewHolder(ParentViewHolder holder, final int flatPosition, final ExpandableGroup group) {
        holder.BindData(group.getTitle());


    }


}
