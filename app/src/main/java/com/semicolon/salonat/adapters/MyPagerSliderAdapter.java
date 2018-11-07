package com.semicolon.salonat.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyPagerSliderAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;
    public MyPagerSliderAdapter(FragmentManager fm) {
        super(fm);
        fragmentList = new ArrayList<>();
    }

    public void AddFragment(Fragment fragment)
    {
        fragmentList.add(fragment);
    }
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

}
