package com.semicolon.salonat.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.semicolon.salonat.R;
import com.semicolon.salonat.fragments.Fragment_Current_Orders;
import com.semicolon.salonat.models.ServiceModel;
import com.semicolon.salonat.tags.Tags;

import java.util.List;

public class CurrentOrderAdapter extends RecyclerView.Adapter<CurrentOrderAdapter.Holder> {
    private Context context;
    private List<ServiceModel.Sub_Service> sub_serviceList;
    private String from;
    private Fragment_Current_Orders fragment_current_orders;

    public CurrentOrderAdapter(Context context, List<ServiceModel.Sub_Service> sub_serviceList, String from, Fragment fragment) {
        this.context = context;
        this.sub_serviceList = sub_serviceList;
        this.from = from;
        this.fragment_current_orders = (Fragment_Current_Orders) fragment;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.current_order_row, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        ServiceModel.Sub_Service sub_service = sub_serviceList.get(position);
        holder.BindData(sub_service);
        holder.image_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceModel.Sub_Service sub_service = sub_serviceList.get(holder.getAdapterPosition());

                fragment_current_orders.RemoveItem(holder.getAdapterPosition(),sub_service);
            }
        });

    }

    @Override
    public int getItemCount() {
        return sub_serviceList.size();
    }

    class Holder extends RecyclerView.ViewHolder  {
        private TextView tv_name, tv_price,tv_time,tv_date;
        private ImageView image_delete;

        public Holder(View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_date = itemView.findViewById(R.id.tv_date);
            image_delete = itemView.findViewById(R.id.image_delete);




        }


        private void BindData(ServiceModel.Sub_Service subService)
        {
            tv_name.setText(subService.getService_title());

            if (from.equals(Tags.in_salon))
            {
                tv_price.setText(subService.getSalon_cost());

            }else if (from.equals(Tags.in_home))
            {
                tv_price.setText(subService.getHome_cost());

            }

            tv_time.setText(subService.getSalon_time_take());
        }



    }
}
