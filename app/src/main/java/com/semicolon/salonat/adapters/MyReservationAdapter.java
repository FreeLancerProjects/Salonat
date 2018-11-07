package com.semicolon.salonat.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.semicolon.salonat.R;
import com.semicolon.salonat.fragments.Fragment_MyReservations;
import com.semicolon.salonat.models.MyReservationModel;
import com.semicolon.salonat.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyReservationAdapter extends RecyclerView.Adapter<MyReservationAdapter.Holder> {
    private Context context;
    private List<MyReservationModel> myReservationModelList;
    private Fragment_MyReservations fragment_myReservations;
    public MyReservationAdapter(Context context, List<MyReservationModel> myReservationModelList, Fragment fragment) {
        this.context = context;
        this.myReservationModelList = myReservationModelList;
        this.fragment_myReservations = (Fragment_MyReservations) fragment;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_reservation_row, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        MyReservationModel myReservationModel = myReservationModelList.get(position);
        holder.BindData(myReservationModel);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyReservationModel myReservationModel = myReservationModelList.get(holder.getAdapterPosition());
                fragment_myReservations.setItemModel(myReservationModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        return myReservationModelList.size();
    }

    class Holder extends RecyclerView.ViewHolder  {
        private TextView tv_name,tv_date;
        private ImageView image;

        public Holder(View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_date = itemView.findViewById(R.id.tv_date);
            image = itemView.findViewById(R.id.image);




        }


        private void BindData(MyReservationModel myReservationModel)
        {
            tv_name.setText(myReservationModel.getTitle());
            tv_date.setText(myReservationModel.getApproved_date());
            Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL+myReservationModel.getMain_photo())).into(image);

        }



    }
}
