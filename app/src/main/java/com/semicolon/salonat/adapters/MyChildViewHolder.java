package com.semicolon.salonat.adapters;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.semicolon.salonat.R;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

public class MyChildViewHolder extends ChildViewHolder {
    private TextView tv_name,tv_time,tv_price;
    public CheckBox checkBox;
    public MyChildViewHolder(View itemView) {
        super(itemView);
        tv_name = itemView.findViewById(R.id.tv_name);
        tv_time = itemView.findViewById(R.id.tv_time);
        tv_price = itemView.findViewById(R.id.tv_price);

        checkBox = itemView.findViewById(R.id.check_box);
    }

    public void BindData(String name ,String time,String price,int state)
    {
        tv_name.setText(name);
        tv_time.setText(time);
        tv_price.setText(price);
        if (state==0)
        {
            checkBox.setChecked(false);
        }else if (state==1)
        {
            checkBox.setChecked(true);
        }
    }
}
