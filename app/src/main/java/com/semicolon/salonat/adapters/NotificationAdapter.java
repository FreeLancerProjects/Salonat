package com.semicolon.salonat.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.semicolon.salonat.R;
import com.semicolon.salonat.fragments.FragmentNotifications;
import com.semicolon.salonat.models.MyReservationModel;
import com.semicolon.salonat.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final int ITEM_NORMAL=1;
    private final int ITEM_PAYMENT=2;

    private Context context;
    private List<MyReservationModel> myReservationModelList;
    private FragmentNotifications fragmentNotifications;

    public NotificationAdapter(Context context, List<MyReservationModel> myReservationModelList,FragmentNotifications fragmentNotifications) {
        this.context = context;
        this.myReservationModelList = myReservationModelList;
        this.fragmentNotifications = fragmentNotifications;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==ITEM_NORMAL)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.normal_notification_row,parent,false);
            return new MyNormalHolder(view);
        }else
            {
                View view = LayoutInflater.from(context).inflate(R.layout.paid_notification_row,parent,false);
                return new MyPaidHolder(view);
            }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyReservationModel myReservationModel = myReservationModelList.get(position);
        if (holder instanceof MyNormalHolder)
        {

            MyNormalHolder myNormalHolder = (MyNormalHolder) holder;
            myNormalHolder.BindData(myReservationModel);

        }else if (holder instanceof MyPaidHolder)
        {

            final MyPaidHolder myPaidHolder = (MyPaidHolder) holder;

            myPaidHolder.BindData(myReservationModel);
            myPaidHolder.payment_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyReservationModel myReservationModel2 = myReservationModelList.get(myPaidHolder.getAdapterPosition());

                    fragmentNotifications.setItem(myReservationModel2);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return myReservationModelList.size();
    }


    public class MyNormalHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView tv_date,tv_details;
        public MyNormalHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_details = itemView.findViewById(R.id.tv_details);

        }

        public void BindData(MyReservationModel myReservationModel)
        {
            Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL+myReservationModel.getMain_photo())).into(image);
            tv_date.setText(myReservationModel.getApproved_date());

            if (myReservationModel.getApproved().equals(Tags.salon_refuse_after_pay)||myReservationModel.getApproved().equals(Tags.salon_refuse_befor_pay))
            {
                tv_details.setText(myReservationModel.getTitle()+" "+context.getString(R.string.refuse_reserv));
            }else if (myReservationModel.getApproved().equals(Tags.salon_accept_after_pay))
            {
                tv_details.setText(myReservationModel.getTitle()+" "+context.getString(R.string.conf_reserve));

            }
        }
    }

    public class MyPaidHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView tv_date,tv_details;
        private Button payment_btn;
        public MyPaidHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_details = itemView.findViewById(R.id.tv_details);
            payment_btn = itemView.findViewById(R.id.payment_btn);

        }

        public void BindData(MyReservationModel myReservationModel)
        {
            Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL+myReservationModel.getMain_photo())).into(image);
            tv_date.setText(myReservationModel.getApproved_date());

            tv_details.setText(myReservationModel.getTitle()+" "+context.getString(R.string.accept_reserve));

        }
    }

    @Override
    public int getItemViewType(int position) {
        MyReservationModel myReservationModel = myReservationModelList.get(position);
        if (myReservationModel.getApproved().equals("2")||myReservationModel.getApproved().equals("4")||myReservationModel.getApproved().equals("5"))
        {
            return ITEM_NORMAL;
        }else
            {
                return ITEM_PAYMENT;
            }
    }
}
