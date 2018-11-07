package com.semicolon.salonat.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.semicolon.salonat.R;
import com.semicolon.salonat.fragments.Fragment_Service_In_Salon;
import com.semicolon.salonat.models.ServiceModel;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceInSalonAdapter extends ExpandableRecyclerViewAdapter<ParentViewHolder,MyChildViewHolder> {
    private Context context;
    private Fragment fragment;
    private Map<String,Boolean> map;
    public ServiceInSalonAdapter(List<? extends ExpandableGroup> groups, Context context,Fragment fragment) {
        super(groups);
        this.context=context;
        this.fragment=fragment;
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
    public void onBindChildViewHolder(final MyChildViewHolder holder, final int flatPosition, final ExpandableGroup group, final int childIndex) {
        ServiceModel.Sub_Service sub_service = (ServiceModel.Sub_Service) group.getItems().get(childIndex);
        holder.BindData(sub_service.getService_title(),sub_service.getSalon_time_take(),sub_service.getSalon_cost(),sub_service.getState_checked());


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
                Fragment_Service_In_Salon fragment_service_in_salon = (Fragment_Service_In_Salon) fragment;

                if (holder.checkBox.isChecked())
                {
                    fragment_service_in_salon.RemoveItem(sub_service);

                    map.put(sub_service.getId_service(),false);
                    holder.checkBox.setChecked(false);
                }else
                    {
                          fragment_service_in_salon.AddItem(sub_service);
                        map.put(sub_service.getId_service(),true);

                        holder.checkBox.setChecked(true);


                    }
            }

        });



        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceModel.Sub_Service sub_service = (ServiceModel.Sub_Service) group.getItems().get(childIndex);
                Fragment_Service_In_Salon fragment_service_in_salon = (Fragment_Service_In_Salon) fragment;

                Log.e("checked",holder.checkBox.isChecked()+"___");


                if (holder.checkBox.isChecked())
                {
                    fragment_service_in_salon.AddItem(sub_service);

                    map.put(sub_service.getId_service(),true);
                    holder.checkBox.setChecked(true);
                }else
                {
                    fragment_service_in_salon.RemoveItem(sub_service);

                    map.put(sub_service.getId_service(),false);

                    holder.checkBox.setChecked(false);


                }
            }
        });
    }

    @Override
    public void onBindGroupViewHolder(ParentViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.BindData(group.getTitle());

    }

    public void RestData()
    {
        map.clear();
        notifyDataSetChanged();
    }
}
