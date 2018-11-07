package com.semicolon.salonat.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.salonat.R;
import com.semicolon.salonat.activities.HomeActivity;
import com.semicolon.salonat.models.ItemModel;
import com.semicolon.salonat.models.ResponsModel;
import com.semicolon.salonat.models.ServiceModel;
import com.semicolon.salonat.models.UserModel;
import com.semicolon.salonat.remote.Api;
import com.semicolon.salonat.share.Common;
import com.semicolon.salonat.singletone.UserSingleTone;
import com.semicolon.salonat.tags.Tags;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Reservations extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{
    private static String TAG = "Data";
    private TextView tv_reservation_cost;
    private ItemModel itemModel;
    private LinearLayout ll_choose_date, ll_choose_time;
    private TextView tv_date, tv_time;
    private DatePickerDialog dpd;
    private TimePickerDialog tpd;
    private String reserve_date = "", reserve_time = "";
    private Button btn_book;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private HomeActivity homeActivity;
    private EditText edt_address;
    private String address = "";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservaion, container, false);
        initView(view);
        return view;
    }

    public static Fragment_Reservations getInstance(ItemModel itemModel)

    {
        Fragment_Reservations fragment_reservations = new Fragment_Reservations();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG, itemModel);
        fragment_reservations.setArguments(bundle);
        return fragment_reservations;
    }

    private void initView(View view) {
        homeActivity = (HomeActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();

        edt_address = view.findViewById(R.id.edt_address);
        ll_choose_date = view.findViewById(R.id.ll_choose_date);
        ll_choose_time = view.findViewById(R.id.ll_choose_time);
        tv_date = view.findViewById(R.id.tv_date);
        tv_time = view.findViewById(R.id.tv_time);
        btn_book = view.findViewById(R.id.btn_book);

        tv_reservation_cost = view.findViewById(R.id.tv_reservation_cost);
        Bundle bundle = getArguments();
        if (bundle != null) {
            itemModel = (ItemModel) bundle.getSerializable(TAG);
            UpdateUI(itemModel);

        }

        CreateDatePickerDialog();
        CreateTimePickerDialog();

        ll_choose_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd.show(getActivity().getFragmentManager(), "إختر التاريخ");
            }
        });
        ll_choose_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tpd.show(getActivity().getFragmentManager(), "إختر الوقت");
            }
        });

        btn_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userModel = userSingleTone.getUserModel();

                if (userModel == null) {
                    Common.CreateUserNotSignInAlertDialog(getActivity());
                } else {
                    CheckDataForBooking();
                }
            }
        });
    }

    private void CheckDataForBooking() {
        if (TextUtils.isEmpty(reserve_date)) {
            Toast.makeText(getActivity(), getString(R.string.choose_date), Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(reserve_time)) {
            Toast.makeText(getActivity(), getString(R.string.choose_time), Toast.LENGTH_LONG).show();

        }

        if (itemModel.getFrom().equals(Tags.in_salon)) {
            if (!TextUtils.isEmpty(reserve_date) && !TextUtils.isEmpty(reserve_time)) {

                Book(reserve_date, reserve_time);
            }
        } else if (itemModel.getFrom().equals(Tags.in_home)) {
            address = edt_address.getText().toString();
            if (!TextUtils.isEmpty(reserve_date) && !TextUtils.isEmpty(reserve_time) && !TextUtils.isEmpty(address)) {
                edt_address.setError(null);
                Book(reserve_date, reserve_time);
            }else
                {
                    if (TextUtils.isEmpty(address))
                    {
                        edt_address.setError(getString(R.string.address_req));
                    }
                }
        }


    }


    private void Book(final String reserve_date, String reserve_time)
    {

        for (ServiceModel.Sub_Service sub_service : itemModel.getServiceModelList()) {
            Log.e("item", sub_service.getSalon_cost());
            Log.e("item", sub_service.getHome_cost());
            Log.e("item", sub_service.getService_title());


        }

        final ProgressDialog dialog = Common.createProgressDialog(getActivity(), getString(R.string.booking));
        dialog.show();
        List<String> serviceIdsList = new ArrayList<>();
        for (ServiceModel.Sub_Service sub_service : itemModel.getServiceModelList()) {
            serviceIdsList.add(sub_service.getId_service());
        }
        Api.getService()
                .book(userModel.getUser_id(), itemModel.getSalon_id(),
                        itemModel.getReservation_cost(),
                        reserve_date, reserve_time,
                        address,
                       "0.0",
                        "0.0",
                        itemModel.getFrom(),
                        serviceIdsList
                )
                .enqueue(new Callback<ResponsModel>() {
                    @Override
                    public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                        if (response.isSuccessful()) {
                            dialog.dismiss();

                            if (response.body().getSuccess_resevation() == 1) {
                                homeActivity.ClearDataAfterReservation();
                                Toast.makeText(getActivity(), R.string.booked_succ, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponsModel> call, Throwable t) {
                        dialog.dismiss();
                        Log.e("Error", t.getMessage());
                        Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void UpdateUI(ItemModel itemModel)
    {
        if (itemModel != null) {
            if (itemModel.getFrom().equals(Tags.in_salon)) {
                edt_address.setVisibility(View.GONE);
            } else {
                edt_address.setVisibility(View.VISIBLE);


            }
            this.itemModel = itemModel;
            tv_reservation_cost.setText(itemModel.getReservation_cost() + " " + getString(R.string.sar));
        }
    }
    private void CreateDatePickerDialog()
    {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        Calendar min = Calendar.getInstance(Locale.getDefault());
        min.setTime(new Date());
        dpd = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setAccentColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        dpd.setOkColor(ContextCompat.getColor(getActivity(), R.color.white));
        dpd.setCancelColor(ContextCompat.getColor(getActivity(), R.color.white));
        dpd.setLocale(Locale.getDefault());
        dpd.setMinDate(min);
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
    }
    private void CreateTimePickerDialog()
    {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        Calendar min = Calendar.getInstance(Locale.getDefault());
        min.setTime(new Date());
        tpd = TimePickerDialog.newInstance(
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false

        );
        tpd.setVersion(TimePickerDialog.Version.VERSION_2);
        tpd.setAccentColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        tpd.setOkColor(ContextCompat.getColor(getActivity(), R.color.white));
        tpd.setCancelColor(ContextCompat.getColor(getActivity(), R.color.white));
        tpd.setLocale(Locale.getDefault());
        tpd.setMinTime(min.get(Calendar.HOUR_OF_DAY), min.get(Calendar.MINUTE), min.get(Calendar.SECOND));
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String d = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyy", Locale.getDefault());
        SimpleDateFormat dateFormat_en = new SimpleDateFormat("dd-mm-yyy", Locale.ENGLISH);

        Date date = null, date2 = null;
        try {
            date = dateFormat.parse(d);
            date2 = dateFormat_en.parse(d);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-mm-yyyy", Locale.ENGLISH);

        String r_date = dateFormat1.format(date);
        reserve_date = dateFormat2.format(date2);

        tv_date.setText(r_date);
        Log.e("res_date", reserve_date);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = hourOfDay + ":" + minute;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat_en = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

        Date date = null, date2 = null;
        try {
            date = dateFormat.parse(time);
            date2 = dateFormat_en.parse(time);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);

        String r_time = dateFormat1.format(date);
        reserve_time = dateFormat2.format(date2);
        tv_time.setText(r_time);
        Log.e("res_time", reserve_time);

    }






}

