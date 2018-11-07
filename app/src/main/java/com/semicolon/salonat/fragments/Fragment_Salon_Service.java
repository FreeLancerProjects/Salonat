package com.semicolon.salonat.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.semicolon.salonat.R;
import com.semicolon.salonat.activities.HomeActivity;
import com.semicolon.salonat.adapters.MyPagerAdapter;
import com.semicolon.salonat.models.ItemModel;
import com.semicolon.salonat.models.SalonModel;
import com.semicolon.salonat.singletone.ItemSingleTone;
import com.semicolon.salonat.tags.Tags;

public class Fragment_Salon_Service extends Fragment {
    private static final String TAG="data";
    private ViewPager pager;
    private TabLayout tab;
    private MyPagerAdapter adapter;
    private SalonModel salonModel;
    private Fragment_Service_In_Home fragment_service_in_home;
    private Fragment_Service_In_Salon fragment_service_in_salon;
    private ItemModel  itemModel;
    private ItemSingleTone itemSingleTone;
    private HomeActivity homeActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_salon_service,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Salon_Service getInstance(SalonModel salonModel)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,salonModel);
        Fragment_Salon_Service fragment_salon_service = new Fragment_Salon_Service();
        fragment_salon_service.setArguments(bundle);
        return fragment_salon_service;
    }
    private void initView(View view) {
        itemSingleTone = ItemSingleTone.getInstance();

        homeActivity = (HomeActivity) getActivity();
        pager = view.findViewById(R.id.pager);
        tab = view.findViewById(R.id.tab);
        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            salonModel = (SalonModel) bundle.getSerializable(TAG);
            UpdateUI(salonModel);
        }

    }

    private void UpdateUI(SalonModel salonModel) {
        adapter = new MyPagerAdapter(getChildFragmentManager());

        /*fragment_service_in_home = Fragment_Service_In_Home.getInstance(salonModel);
        fragment_service_in_salon = Fragment_Service_In_Salon.getInstance(salonModel);*/

        if (fragment_service_in_home==null)
        {
            fragment_service_in_home = Fragment_Service_In_Home.getInstance(salonModel);

        }

        if (fragment_service_in_salon==null)
        {
            fragment_service_in_salon = Fragment_Service_In_Salon.getInstance(salonModel);
        }
        adapter.AddFragment(fragment_service_in_salon);
        adapter.AddFragment(fragment_service_in_home);

        adapter.AddTitle(getString(R.string.in_salon));
        adapter.AddTitle(getString(R.string.in_home));

        pager.setAdapter(adapter);
        tab.setupWithViewPager(pager);


        for (int i =0;i<tab.getTabCount();i++)
        {
            View childAt = ((ViewGroup) tab.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
            params.setMargins(20,0,20,0);
            tab.requestLayout();
        }

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                itemModel = itemSingleTone.getItemModel();
                if (itemModel!=null)
                {
                    if (position==0)
                    {

                        Log.e("pos0","0");
                        if (itemModel.getFrom().equals(Tags.in_home))
                        {
                            String msg = getString(R.string.cart_contain)+" "+itemModel.getServiceModelList().size()+" "+getString(R.string.item)+"\n"+getString(R.string.do_del_item);
                            CreateAlertClearItemCart(msg);
                        }


                    }else if (position==1)
                    {
                        Log.e("pos1","1");

                        Log.e("itemModel",itemModel.getFrom()+"_2");

                        if (itemModel.getFrom().equals(Tags.in_salon))
                        {
                            Log.e("itemModel",itemModel.getFrom()+"_2");

                            String msg = getString(R.string.cart_contain)+" "+itemModel.getServiceModelList().size()+" "+getString(R.string.item)+"\n"+getString(R.string.do_del_item);
                            CreateAlertClearItemCart(msg);
                        }
                    }
                }



            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
                if (pager.getCurrentItem()==0)
                {
                    fragment_service_in_home.ResetData();

                }else if (pager.getCurrentItem()==1)
                {
                    fragment_service_in_salon.ResetData();

                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                int current_page_pos = pager.getCurrentItem();
                if (current_page_pos==0)
                {
                    pager.setCurrentItem(1);
                }else if (current_page_pos==1)
                {
                    pager.setCurrentItem(0);

                }
            }
        });

        alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog;
        alertDialog.setView(view);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();




    }


}
