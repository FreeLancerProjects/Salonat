package com.semicolon.salonat.fragments;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.semicolon.salonat.R;
import com.semicolon.salonat.activities.HomeActivity;
import com.semicolon.salonat.adapters.MyPagerSliderAdapter;
import com.semicolon.salonat.models.ItemModel;
import com.semicolon.salonat.models.SalonModel;
import com.semicolon.salonat.singletone.ItemSingleTone;

import java.util.Timer;
import java.util.TimerTask;

public class Fragment_Salon_details extends Fragment {
    private static final String TAG = "data";
    private ViewPager pager_main_photo;
    private TabLayout tab_main_photo;
    private MyPagerSliderAdapter adapter_main_photo;
    private TextView tv_name,tv_type;
    private SimpleRatingBar rateBar;
    private SalonModel salonModel;
    private TimerTask timerTask;
    private Timer timer;
    private ImageView image_back;
    private HomeActivity homeActivity;
    private Fragment_Salon_Service fragment_salon_service;
    private Fragment_Salon_Location fragment_salon_location;
    private Fragment_Salon_Vote fragment_salon_vote;
    private Button btn_service,btn_location,btn_vote;
    private ItemModel itemModel;
    private ItemSingleTone itemSingleTone;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_salon_details,container,false);
        initView(view);
        Log.e("Fragment_Salon_details","oncreate");
        return view;
    }

    public static Fragment_Salon_details getInstance(SalonModel salonModel)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,salonModel);
        Fragment_Salon_details fragment_salon_details = new Fragment_Salon_details();
        fragment_salon_details.setArguments(bundle);
        return fragment_salon_details;
    }

    private void initView(View view) {
        itemSingleTone = ItemSingleTone.getInstance();

        homeActivity = (HomeActivity) getActivity();
        image_back = view.findViewById(R.id.image_back);
        tv_name = view.findViewById(R.id.tv_name);
        tv_type = view.findViewById(R.id.tv_type);


        rateBar = view.findViewById(R.id.rateBar);

        rateBar.setEnabled(false);
        rateBar.setClickable(false);
        rateBar.setSelected(false);
        rateBar.setFocusable(false);
        rateBar.setFocusableInTouchMode(false);
        pager_main_photo = view.findViewById(R.id.pager_main_photo);
        btn_service = view.findViewById(R.id.btn_service);
        btn_location = view.findViewById(R.id.btn_location);
        btn_vote = view.findViewById(R.id.btn_vote);
        tab_main_photo = view.findViewById(R.id.tab_main_photo);
        pager_main_photo.beginFakeDrag();
        tab_main_photo.setupWithViewPager(pager_main_photo);

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            salonModel = (SalonModel) bundle.getSerializable(TAG);
            UpdateUI(salonModel);
        }
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.Back();
            }
        });

        btn_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_details_container);
                if (!(fragment instanceof Fragment_Salon_Service))
                {
                    fragment_salon_service = Fragment_Salon_Service.getInstance(salonModel);

                    getChildFragmentManager().beginTransaction().add(R.id.fragment_details_container,fragment_salon_service).addToBackStack("fragment_salon_service").commit();

                    btn_service.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
                    btn_location.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.unselected_color));
                    btn_vote.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.unselected_color));

                }

            }
        });
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                itemModel = itemSingleTone.getItemModel();
                if (itemModel==null)
                {
                    Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_details_container);
                    if (!(fragment instanceof Fragment_Salon_Location))
                    {
                        fragment_salon_location = Fragment_Salon_Location.getInstance(salonModel);

                        getChildFragmentManager().beginTransaction().add(R.id.fragment_details_container,fragment_salon_location).addToBackStack("fragment_salon_location").commit();

                        btn_service.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.unselected_color));
                        btn_location.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
                        btn_vote.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.unselected_color));

                    }
                }else
                    {
                        if (itemModel.getServiceModelList().size()>0)
                        {
                            String msg = getString(R.string.cart_contain)+" "+itemModel.getServiceModelList().size()+" "+getString(R.string.item)+"\n"+getString(R.string.do_del_item);
                            CreateAlertClearItemCart(msg);
                        }else
                            {
                                Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_details_container);
                                if (!(fragment instanceof Fragment_Salon_Location))
                                {
                                    fragment_salon_location = Fragment_Salon_Location.getInstance(salonModel);

                                    getChildFragmentManager().beginTransaction().add(R.id.fragment_details_container,fragment_salon_location).addToBackStack("fragment_salon_location").commit();

                                    btn_service.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.unselected_color));
                                    btn_location.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
                                    btn_vote.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.unselected_color));

                                }
                            }
                    }



            }
        });
        btn_vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                itemModel = itemSingleTone.getItemModel();
                if (itemModel==null)
                {
                    Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_details_container);
                    if (!(fragment instanceof Fragment_Salon_Vote))
                    {
                        fragment_salon_vote = Fragment_Salon_Vote.getInstance(salonModel);

                        getChildFragmentManager().beginTransaction().add(R.id.fragment_details_container,fragment_salon_vote).addToBackStack("fragment_salon_vote").commit();

                        btn_service.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.unselected_color));
                        btn_location.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.unselected_color));
                        btn_vote.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));

                    }
                }else
                {
                    if (itemModel.getServiceModelList().size()>0)
                    {
                        String msg = getString(R.string.cart_contain)+" "+itemModel.getServiceModelList().size()+" "+getString(R.string.item)+"\n"+getString(R.string.do_del_item);
                        CreateAlertClearItemCart(msg);
                    }else
                        {
                            Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_details_container);
                            if (!(fragment instanceof Fragment_Salon_Vote))
                            {
                                fragment_salon_vote = Fragment_Salon_Vote.getInstance(salonModel);

                                getChildFragmentManager().beginTransaction().add(R.id.fragment_details_container,fragment_salon_vote).addToBackStack("fragment_salon_vote").commit();

                                btn_service.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.unselected_color));
                                btn_location.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.unselected_color));
                                btn_vote.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));

                            }
                        }
                }




            }
        });

    }

    private void CreateAlertClearItemCart(String msg)
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .create();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_clear_cart,null);
        TextView tv_msg = view.findViewById(R.id.tv_msg);
        Button doneBtn = view.findViewById(R.id.doneBtn);
        final Button cancelBtn = view.findViewById(R.id.cancelBtn);
        tv_msg.setText(msg);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.ClearItemModel();
                alertDialog.dismiss();

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();


            }
        });

        alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog;
        alertDialog.setView(view);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();




    }
    private void UpdateUI(final SalonModel salonModel) {
        tv_name.setText(salonModel.getTitle());
        tv_type.setText("صالون/حريمي");
        SimpleRatingBar.AnimationBuilder animationBuilder = rateBar.getAnimationBuilder();
        animationBuilder.setDuration(1000)
                .setRatingTarget((float)(salonModel.getSalon_stars_num()))
                .setInterpolator(new AccelerateInterpolator())
                .setRepeatMode(ValueAnimator.RESTART)
                .setRepeatCount(0)
                .start();


        if (fragment_salon_service==null)
        {
            fragment_salon_service = Fragment_Salon_Service.getInstance(salonModel);

        }

        if (fragment_salon_location==null)
        {
            fragment_salon_location = Fragment_Salon_Location.getInstance(salonModel);

        }

        if (fragment_salon_vote==null)
        {
            fragment_salon_vote = Fragment_Salon_Vote.getInstance(salonModel);

        }
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_details_container,fragment_salon_service).commit();

        adapter_main_photo = new MyPagerSliderAdapter(getChildFragmentManager());

        for (SalonModel.GalleryModel galleryModel :salonModel.getGallary())
        {
            adapter_main_photo.AddFragment(Fragment_Image_Slider.getInstance(galleryModel));
        }

        pager_main_photo.setAdapter(adapter_main_photo);
        if (salonModel.getGallary().size()>1)
        {
            timer =  new Timer();
            timerTask = new MyTimerTask();
            timer.scheduleAtFixedRate(timerTask,3000,3000);
        }



    }

    private class MyTimerTask extends TimerTask{

        @Override
        public void run() {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (pager_main_photo.getCurrentItem()<salonModel.getGallary().size()-1)
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        timerTask.cancel();
        timer.purge();
        timer.cancel();

    }
}
