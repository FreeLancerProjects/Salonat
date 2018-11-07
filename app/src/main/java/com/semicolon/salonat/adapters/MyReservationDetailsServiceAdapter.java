package com.semicolon.salonat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.semicolon.salonat.R;
import com.semicolon.salonat.models.MyReservationModel;
import com.semicolon.salonat.tags.Tags;

import java.util.List;

public class MyReservationDetailsServiceAdapter extends RecyclerView.Adapter<MyReservationDetailsServiceAdapter.Holder> {
    private Context context;
    private List<MyReservationModel.Service> serviceList;
    public MyReservationDetailsServiceAdapter(Context context, List<MyReservationModel.Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_reservation_service_row, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        MyReservationModel.Service service = serviceList.get(position);
        holder.BindData(service);


    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    class Holder extends RecyclerView.ViewHolder  {
        private TextView tv_name,tv_time;

        public Holder(View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);




        }


        private void BindData(MyReservationModel.Service service)
        {
            tv_name.setText(service.getService_title());
            if (service.getService_type().equals(Tags.in_home))
            {
                tv_time.setText(service.getHome_time_take());

            }else if (service.getService_type().equals(Tags.in_salon))
            {
                tv_time.setText(service.getSalon_time_take());

            }
        }



    }
}
