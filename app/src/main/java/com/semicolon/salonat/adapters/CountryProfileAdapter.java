package com.semicolon.salonat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.semicolon.salonat.R;
import com.semicolon.salonat.fragments.Fragment_Profile;
import com.semicolon.salonat.models.Country_City_Model;

import java.util.List;
import java.util.Locale;

public class CountryProfileAdapter extends RecyclerView.Adapter<CountryProfileAdapter.MyHolder> {

    private Context context;
    private List<Country_City_Model> country_city_modelList;
    private Fragment_Profile fragment_profile;

    public CountryProfileAdapter(Context context, List<Country_City_Model> country_city_modelList,Fragment_Profile fragment_profile) {
        this.context = context;
        this.country_city_modelList = country_city_modelList;
        this.fragment_profile = fragment_profile;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.country_city_row,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        Country_City_Model country_city_model = country_city_modelList.get(position);
        holder.BindData(country_city_model);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Country_City_Model country_city_model = country_city_modelList.get(holder.getAdapterPosition());
                fragment_profile.setCountryItem(country_city_model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return country_city_modelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        public MyHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
        }

        private void BindData(Country_City_Model country_city_model)
        {
            String lang = Locale.getDefault().getLanguage();
            if (lang.equals("ar"))
            {
                tv_title.setText(country_city_model.getAr_name());
            }else if (lang.equals("en"))
            {
                tv_title.setText(country_city_model.getEn_name());

            }

        }
    }


}
