package com.semicolon.salonat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.semicolon.salonat.R;
import com.semicolon.salonat.adapters.MyPagerAdapter;
import com.semicolon.salonat.models.SalonModel;

public class Fragment_Salon_Vote extends Fragment {
    private static String TAG = "data";
    private ViewPager pager;
    private TabLayout tab;
    private MyPagerAdapter adapter;
    private SalonModel salonModel;
    private Fragment_Vote_In_Home fragment_vote_in_home;
    private Fragment_Vote_In_Salon fragment_vote_in_salon;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_salon_service,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Salon_Vote getInstance(SalonModel salonModel)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,salonModel);
        Fragment_Salon_Vote fragment_salon_vote = new Fragment_Salon_Vote();
        fragment_salon_vote.setArguments(bundle);
        return fragment_salon_vote;
    }
    private void initView(View view) {

        pager = view.findViewById(R.id.pager);
        tab = view.findViewById(R.id.tab);
        tab.setupWithViewPager(pager);

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            salonModel = (SalonModel) bundle.getSerializable(TAG);
            UpdateUI(salonModel);
        }



    }


    private void UpdateUI(SalonModel salonModel) {
        adapter = new MyPagerAdapter(getChildFragmentManager());

        if (fragment_vote_in_home==null)
        {
            fragment_vote_in_home = Fragment_Vote_In_Home.getInstance(salonModel);
        }

        if (fragment_vote_in_salon ==null)
        {
           fragment_vote_in_salon =  Fragment_Vote_In_Salon.getInstance(salonModel);
        }
        adapter.AddFragment(fragment_vote_in_salon);
        adapter.AddFragment(fragment_vote_in_home);

        adapter.AddTitle(getString(R.string.in_salon));
        adapter.AddTitle(getString(R.string.in_home));

        pager.setAdapter(adapter);

        for (int i =0;i<tab.getTabCount();i++)
        {
            View childAt = ((ViewGroup) tab.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
            params.setMargins(20,0,20,0);
            tab.requestLayout();
        }
    }
}
