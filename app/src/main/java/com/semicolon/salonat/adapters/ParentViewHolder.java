package com.semicolon.salonat.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.semicolon.salonat.R;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

public class ParentViewHolder extends GroupViewHolder {
    private ImageView image_arrow;
    private TextView tv_title;
    public ParentViewHolder(View itemView) {
        super(itemView);

        image_arrow = itemView.findViewById(R.id.image_arrow);
        tv_title = itemView.findViewById(R.id.tv_title);
    }

    public void BindData(String title)
    {
        tv_title.setText(title);
    }

    @Override
    public void expand() {
        image_arrow.animate().rotation(180f).start();
    }

    @Override
    public void collapse() {

        image_arrow.animate().rotation(0f).start();

    }
}
