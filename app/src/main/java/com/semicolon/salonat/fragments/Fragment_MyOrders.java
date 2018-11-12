package com.semicolon.salonat.fragments;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.semicolon.salonat.R;
import com.semicolon.salonat.adapters.MyPagerAdapter;
import com.semicolon.salonat.adapters.MyPagerSliderAdapter;
import com.semicolon.salonat.models.ItemModel;
import com.semicolon.salonat.models.SalonModel;

import java.util.Timer;
import java.util.TimerTask;

public class Fragment_MyOrders extends Fragment {
    private static String TAG = "DATA";
    private ViewPager pager_main_photo,pager;
    private TabLayout tab_main_photo,tab;
    private MyPagerSliderAdapter adapter_main_photo;
    private MyPagerAdapter myPagerAdapter;
    private TextView tv_name,tv_type;
    private SimpleRatingBar rateBar;
    private TimerTask timerTask;
    private Timer timer;
    private Fragment_Reservations fragment_reservations;
    private Fragment_Current_Orders fragment_current_orders;
    private ItemModel itemModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_orders,container,false);
        initView(view);
        return view;
    }

    public static Fragment_MyOrders getInstance(ItemModel itemModel)
    {
        Fragment_MyOrders fragment_myOrders = new Fragment_MyOrders();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,itemModel);
        fragment_myOrders.setArguments(bundle);
        return fragment_myOrders;
    }
    private void initView(View view) {
        pager = view.findViewById(R.id.pager);
        tab = view.findViewById(R.id.tab);
        tab.setupWithViewPager(pager);
        ////////////////////////////////////////
        tv_name = view.findViewById(R.id.tv_name);
        tv_type = view.findViewById(R.id.tv_type);
        rateBar = view.findViewById(R.id.rateBar);
        rateBar.setIndicator(true);

        pager_main_photo = view.findViewById(R.id.pager_main_photo);
        tab_main_photo = view.findViewById(R.id.tab_main_photo);
        pager_main_photo.beginFakeDrag();
        tab_main_photo.setupWithViewPager(pager_main_photo);
        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            itemModel = (ItemModel) bundle.getSerializable(TAG);
            UpdateUI(itemModel);
        }


    }

    private void UpdateUI(ItemModel itemModel) {
        tv_name.setText(itemModel.getSalon_name());
        tv_type.setText("صالون حريمي");
        SimpleRatingBar.AnimationBuilder animationBuilder = rateBar.getAnimationBuilder();
        animationBuilder.setDuration(1000)
                .setRatingTarget((float)(Integer.parseInt(itemModel.getSalon_rate())))
                .setInterpolator(new AccelerateInterpolator())
                .setRepeatMode(ValueAnimator.RESTART)
                .setRepeatCount(0)
                .start();

        adapter_main_photo = new MyPagerSliderAdapter(getChildFragmentManager());

        for (SalonModel.GalleryModel galleryModel :itemModel.getGalleryModelList())
        {
            adapter_main_photo.AddFragment(Fragment_Image_Slider.getInstance(galleryModel));
        }

        pager_main_photo.setAdapter(adapter_main_photo);
        if (itemModel.getGalleryModelList().size()>1)
        {
            timer =  new Timer();
            timerTask = new MyTimerTask();
            timer.scheduleAtFixedRate(timerTask,3000,3000);
        }

        fragment_current_orders = Fragment_Current_Orders.getInstance(itemModel);
        fragment_reservations = Fragment_Reservations.getInstance(itemModel);
        myPagerAdapter = new MyPagerAdapter(getChildFragmentManager());
        myPagerAdapter.AddFragment(fragment_reservations);
        myPagerAdapter.AddFragment(fragment_current_orders);
        myPagerAdapter.AddTitle(getString(R.string.book));
        myPagerAdapter.AddTitle(getString(R.string.my_orders));

        pager.setAdapter(myPagerAdapter);



    }

    private class MyTimerTask extends TimerTask{

        @Override
        public void run() {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (pager_main_photo.getCurrentItem()<itemModel.getGalleryModelList().size()-1)
                        {
                            pager_main_photo.setCurrentItem(pager_main_photo.getCurrentItem()+1);
                        }else
                        {
                            pager_main_photo.setAdapter(adapter_main_photo);

                        }
                    }
                });
            }catch (NullPointerException e){}
            catch (Exception e){}

        }
    }

    public void UpdateItemModel(ItemModel itemModel)
    {
        fragment_reservations.UpdateUI(itemModel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timerTask!=null)
        {
            timerTask.cancel();

        }

        if (timer!=null)
        {
            timer.purge();
            timer.cancel();
        }


    }
}
