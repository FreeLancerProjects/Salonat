package com.semicolon.salonat.adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.semicolon.salonat.R;
import com.semicolon.salonat.fragments.Fragment_Home;
import com.semicolon.salonat.models.SalonModel;
import com.semicolon.salonat.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AllSalonsAdapter extends RecyclerView.Adapter<AllSalonsAdapter.Holder> {
    private Context context;
    private List<SalonModel> salonModelList;
    private Fragment fragment;
    public AllSalonsAdapter(Context context, List<SalonModel> salonModelList,Fragment fragment) {
        this.fragment = fragment;
        this.context = context;
        this.salonModelList = salonModelList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.salon_row, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        SalonModel salonModel = salonModelList.get(position);
        holder.BindData(salonModel);
        final Fragment_Home fragment_home = (Fragment_Home) fragment;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalonModel salonModel = salonModelList.get(holder.getAdapterPosition());
                fragment_home.setItem(salonModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        return salonModelList.size();
    }

    class Holder extends RecyclerView.ViewHolder  {
        private RoundedImageView image;
        private TextView tv_name, tv_type, tv_address;
        private SimpleRatingBar rateBar;

        public Holder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_address = itemView.findViewById(R.id.tv_address);
            rateBar = itemView.findViewById(R.id.rateBar);
            rateBar.setEnabled(false);
            rateBar.setClickable(false);
            rateBar.setSelected(false);
            rateBar.setFocusable(false);
            rateBar.setFocusableInTouchMode(false);



        }

        private void BindData(final SalonModel salonModel)
        {
            Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL+salonModel.getMain_photo())).into(image);
            tv_name.setText(salonModel.getTitle());
            tv_type.setText("صالون/ حريمي");
            tv_address.setText(salonModel.getAddress());

            SimpleRatingBar.AnimationBuilder animationBuilder = rateBar.getAnimationBuilder();
            animationBuilder.setDuration(1000)
                    .setRatingTarget((float)(salonModel.getSalon_stars_num()))
                    .setInterpolator(new AccelerateInterpolator())
                    .setRepeatMode(ValueAnimator.RESTART)
                    .setRepeatCount(0)
                    .start();

        }




    }
}
