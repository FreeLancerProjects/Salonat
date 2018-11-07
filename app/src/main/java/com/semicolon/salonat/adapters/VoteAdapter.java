package com.semicolon.salonat.adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.semicolon.salonat.R;
import com.semicolon.salonat.models.VoteModel;

import java.util.List;

public class VoteAdapter extends RecyclerView.Adapter<VoteAdapter.Holder> {
    private Context context;
    List<VoteModel> voteModelList;

    public VoteAdapter(Context context, List<VoteModel> voteModelList) {
        this.context = context;
        this.voteModelList = voteModelList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vote_row, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        VoteModel voteModel = voteModelList.get(position);
        holder.BindData(voteModel);



    }

    @Override
    public int getItemCount() {
        return voteModelList.size();
    }

    class Holder extends RecyclerView.ViewHolder  {
        private TextView tv_name, tv_rate_state;
        private SimpleRatingBar rateBar;

        public Holder(View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_rate_state = itemView.findViewById(R.id.tv_rate_state);
            rateBar = itemView.findViewById(R.id.rateBar);




        }


        private void BindData(VoteModel voteModel)
        {
            tv_name.setText(voteModel.getComment_person());
            tv_rate_state.setText(voteModel.getComment_detail());
            SimpleRatingBar.AnimationBuilder animationBuilder = rateBar.getAnimationBuilder();
            animationBuilder.setRepeatCount(0)
                    .setRepeatMode(ValueAnimator.RESTART)
                    .setInterpolator(new AccelerateInterpolator())
                    .setRatingTarget((float)(Integer.parseInt(voteModel.getStars_num())))
                    .start();
        }



    }
}
