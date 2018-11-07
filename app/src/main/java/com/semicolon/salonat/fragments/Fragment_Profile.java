package com.semicolon.salonat.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.lamudi.phonefield.PhoneInputLayout;
import com.semicolon.salonat.R;
import com.semicolon.salonat.activities.HomeActivity;
import com.semicolon.salonat.adapters.CityProfileAdapter;
import com.semicolon.salonat.adapters.CountryProfileAdapter;
import com.semicolon.salonat.models.Country_City_Model;
import com.semicolon.salonat.models.UserModel;
import com.semicolon.salonat.preference.Preferences;
import com.semicolon.salonat.remote.Api;
import com.semicolon.salonat.share.Common;
import com.semicolon.salonat.singletone.UserSingleTone;
import com.semicolon.salonat.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.semicolon.salonat.tags.Tags.update_phone;

public class Fragment_Profile extends Fragment {
    private static final String TAG = "user_data";
    private RoundedImageView image;
    private KenBurnsView image_bg;
    private ImageView image_update_photo;
    private TextView tv_name,tv_phone,tv_email, tv_country, tv_city,tv_address;
    private LinearLayout ll_update_name;
    private CardView cardView_update_phone,cardView_update_email,cardView_update_password,cardView_update_country,cardView_update_city,cardView_update_address;
    private final  int img1_req_profile=12,img2_req_doc=13;
    private ProgressDialog updateImagedialog;
    private UserModel userModel;
    private AlertDialog updatedialog;
    private ProgressDialog progressDialog;
    private UserSingleTone userSingleTone;
    private Preferences preferences;
    private AlertDialog cityDialog,countryDialog;
    private List<Country_City_Model.CityModel> cityModelList;
    private List<Country_City_Model> country_city_modelList;
    private HomeActivity homeActivity;
    private final String read_per = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final int read_req = 102;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        initView(view);
        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            userModel = (UserModel) bundle.getSerializable(TAG);
            UpdateUi(userModel);
        }
        return view;
    }

    public static Fragment_Profile getInstance(UserModel userModel)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,userModel);
        Fragment_Profile fragment = new Fragment_Profile();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initView(View view)
    {
        cityModelList = new ArrayList<>();
        country_city_modelList = new ArrayList<>();
        homeActivity = (HomeActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();
        preferences = Preferences.getInstance();
        image = view.findViewById(R.id.image);
        image_bg = view.findViewById(R.id.image_bg);
        image_update_photo = view.findViewById(R.id.image_update_photo);
        tv_name = view.findViewById(R.id.tv_name);
        tv_phone = view.findViewById(R.id.tv_phone);
        tv_email = view.findViewById(R.id.tv_email);
        tv_country = view.findViewById(R.id.tv_country);
        tv_city = view.findViewById(R.id.tv_city);
        tv_address = view.findViewById(R.id.tv_address);

        ll_update_name = view.findViewById(R.id.ll_update_name);
        cardView_update_phone = view.findViewById(R.id.cardView_update_phone);
        cardView_update_email = view.findViewById(R.id.cardView_update_email);
        cardView_update_password = view.findViewById(R.id.cardView_update_password);
        cardView_update_city = view.findViewById(R.id.cardView_update_city);
        cardView_update_country = view.findViewById(R.id.cardView_update_country);
        cardView_update_address = view.findViewById(R.id.cardView_update_address);


        image_update_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission(read_req);

            }
        });

        ll_update_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_UpdateProfile(Tags.update_full_name);
            }
        });
        cardView_update_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_UpdateProfile(Tags.update_email);
            }
        });

        cardView_update_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_UpdateProfile(Tags.update_address);
            }
        });

        cardView_update_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateAlertDialog_UpdateProfile(update_phone);
            }
        });

        cardView_update_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_UpdateProfile(Tags.update_password);
            }
        });

        cardView_update_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView_update_city.setEnabled(false);
                if (country_city_modelList.size()>0)
                {
                    for (Country_City_Model country_city_model:country_city_modelList)
                    {
                        if (country_city_model.getId_country().equals(userModel.getId_country()))
                        {
                            createCityDialog(country_city_model.getSub_city());
                            break;
                        }
                    }


                }
            }
        });

        cardView_update_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView_update_country.setEnabled(false);

                if (country_city_modelList.size()>0)
                {
                    createCountryDialog(country_city_modelList);
                }else
                {
                    getCountry("1");
                }
            }
        });

        getCountry("0");
    }

    private void CheckPermission(int req)
    {
        if (ContextCompat.checkSelfPermission(getActivity(),read_per)!= PackageManager.PERMISSION_GRANTED)
        {
            String [] perm = {read_per};
            ActivityCompat.requestPermissions(getActivity(),perm,req);
        }else
        {
            if (req==read_req)
            {
                SelectImage(img1_req_profile);

            }
        }
    }
    private void UpdateUi(UserModel userModel) {
        Log.e("name",userModel.getUser_full_name());
        Log.e("phone",userModel.getUser_phone());
        Log.e("email",userModel.getUser_email());
        Log.e("country",userModel.getAr_name()+" _ "+userModel.getEn_name());

        Picasso.with(getActivity()).load(Uri.parse(Tags.IMAGE_URL+userModel.getUser_photo())).into(image);
        Picasso.with(getActivity()).load(Uri.parse(Tags.IMAGE_URL+userModel.getUser_photo())).into(image_bg);

        tv_name.setText(userModel.getUser_full_name());
        tv_email.setText(userModel.getUser_email());
        tv_phone.setText(userModel.getUser_phone());
        tv_address.setText(userModel.getUser_address());
        if (Locale.getDefault().getLanguage().equals("en"))
        {
            tv_country.setText(userModel.getEn_name());
            tv_city.setText(userModel.getEn_city_title());
        }else
        {
            tv_country.setText(userModel.getAr_name());
            tv_city.setText(userModel.getAr_city_title());



        }


    }



    private void createCityDialog(List<Country_City_Model.CityModel> cityModelList)
    {
        cardView_update_city.setEnabled(true);

        cityDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .create();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.country_city_dialog,null);
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.ch_city));
        RecyclerView recView  = view.findViewById(R.id.recView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        RecyclerView.Adapter adapter = new CityProfileAdapter(getActivity(),cityModelList,this);
        recView.setLayoutManager(manager);
        recView.setAdapter(adapter);

        cityDialog.getWindow().getAttributes().windowAnimations = R.style.dialog;
        cityDialog.setView(view);
        cityDialog.show();
        cityDialog.setCanceledOnTouchOutside(false);
        cardView_update_city.setEnabled(true);



    }

    private void createCountryDialog(List<Country_City_Model> country_city_modelList)
    {




        countryDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .create();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.country_city_dialog,null);
        TextView tv_title  = view.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.ch_country));
        RecyclerView recView  = view.findViewById(R.id.recView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        RecyclerView.Adapter adapter = new CountryProfileAdapter(getActivity(),country_city_modelList,this);
        recView.setLayoutManager(manager);
        recView.setAdapter(adapter);

        countryDialog.getWindow().getAttributes().windowAnimations = R.style.dialog;
        countryDialog.setView(view);
        countryDialog.show();
        countryDialog.setCanceledOnTouchOutside(false);
        cardView_update_country.setEnabled(true);




    }
    private void CreateAlertDialog_UpdateProfile(final String type)
    {
        updatedialog = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_update_profile,null);
        final TextView tv_title = view.findViewById(R.id.tv_title);
        final EditText edt_update = view.findViewById(R.id.edt_update);
        final EditText edt_newPassword = view.findViewById(R.id.edt_newPassword);
        final PhoneInputLayout edt_check_phone = view.findViewById(R.id.edt_check_phone);
        Button btn_update = view.findViewById(R.id.btn_update);
        Button btn_close = view.findViewById(R.id.btn_close);

        if (type.equals(Tags.update_full_name))
        {
            tv_title.setText(R.string.upd_name);
            edt_update.setInputType(InputType.TYPE_CLASS_TEXT);
            edt_newPassword.setVisibility(View.GONE);
            edt_update.setHint(R.string.name);
            if (userModel!=null)
            {
                edt_update.setText(userModel.getUser_full_name());


            }
        }else if (type.equals(Tags.update_phone))
        {
            tv_title.setText(R.string.upd_phone);
            edt_update.setInputType(InputType.TYPE_CLASS_PHONE);
            edt_newPassword.setVisibility(View.GONE);
            edt_update.setHint(R.string.phone_number);
            if (userModel!=null)
            {
                edt_update.setText(userModel.getUser_phone());


            }
        }else if (type.equals(Tags.update_email))
        {
            tv_title.setText(R.string.upd_email);
            edt_update.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            edt_newPassword.setVisibility(View.GONE);
            edt_update.setHint(R.string.email_address);
            if (userModel!=null)
            {
                edt_update.setText(userModel.getUser_email());


            }
        }else if (type.equals(Tags.update_address))
        {
            tv_title.setText(R.string.upd_address);
            edt_update.setInputType(InputType.TYPE_CLASS_TEXT);
            edt_newPassword.setVisibility(View.GONE);
            edt_update.setHint(R.string.address);
            if (userModel!=null)
            {
                edt_update.setText(userModel.getUser_address());


            }
        }
        else if (type.equals(Tags.update_password))
        {

            tv_title.setText(R.string.upd_password);
            edt_update.setTransformationMethod(PasswordTransformationMethod.getInstance());
            edt_newPassword.setVisibility(View.VISIBLE);
            edt_update.setHint(R.string.old_pass);

        }

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedialog.dismiss();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedialog.dismiss();
                if (type.equals(Tags.update_full_name))
                {
                    String m_name = edt_update.getText().toString();
                    if (!TextUtils.isEmpty(m_name))
                    {
                        edt_update.setError(null);

                        Common.CloseKeyBoard(getActivity(),edt_update);
                        progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.uptng_name));
                        progressDialog.show();
                        update_name(m_name);

                    }else
                    {
                        edt_update.setError(getString(R.string.name_req));
                    }

                }
                if (type.equals(Tags.update_address))
                {
                    String m_address = edt_update.getText().toString();
                    if (!TextUtils.isEmpty(m_address))
                    {
                        edt_update.setError(null);

                        Common.CloseKeyBoard(getActivity(),edt_update);
                        progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.uptng_address));
                        progressDialog.show();
                        update_address(m_address);

                    }else
                    {
                        edt_update.setError(getString(R.string.address_req));
                    }

                }
                else if (type.equals(update_phone))
                {

                    String m_phone = edt_update.getText().toString();
                    if (!TextUtils.isEmpty(m_phone))
                    {
                        if (!m_phone.startsWith("+"))
                        {
                            m_phone = "+"+m_phone;
                        }
                        edt_check_phone.setPhoneNumber(m_phone);

                    }

                    if (TextUtils.isEmpty(m_phone))
                    {
                        edt_update.setError(getString(R.string.phone_req));


                    }else if (!edt_check_phone.isValid())
                    {
                        edt_update.setError(getString(R.string.inv_phone));

                    }
                    else
                    {
                        Common.CloseKeyBoard(getActivity(),edt_update);
                        edt_update.setError(null);
                        progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.uptng_phone));
                        progressDialog.show();
                        update_phone(m_phone);

                    }
                }else if (type.equals(Tags.update_email))
                {

                    String m_email = edt_update.getText().toString();

                    if (TextUtils.isEmpty(m_email))
                    {
                        edt_update.setError(getString(R.string.email_req));

                    }else if (!Patterns.EMAIL_ADDRESS.matcher(m_email).matches())
                    {
                        edt_update.setError(getString(R.string.inv_email));

                    }else
                    {
                        Common.CloseKeyBoard(getActivity(),edt_update);

                        edt_update.setError(null);
                        progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.uptng_email));
                        progressDialog.show();
                        update_email(m_email);

                    }

                }else if (type.equals(Tags.update_password))
                {

                    Log.e("upd","password");
                    String m_oldPassword = edt_update.getText().toString();
                    String m_newPassword = edt_newPassword.getText().toString();

                    if (!TextUtils.isEmpty(m_oldPassword)&&!TextUtils.isEmpty(m_newPassword))
                    {
                        Common.CloseKeyBoard(getActivity(),edt_update);
                        edt_update.setError(null);
                        edt_newPassword.setError(null);

                        progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.updtng_pass));
                        progressDialog.show();
                        update_Password(m_oldPassword,m_newPassword);

                    }else
                    {
                        if (TextUtils.isEmpty(m_oldPassword))
                        {
                            edt_update.setError(getString(R.string.pass_req));

                        }else
                        {
                            edt_update.setError(null);

                        }

                        if (TextUtils.isEmpty(m_newPassword))
                        {
                            edt_newPassword.setError(getString(R.string.newpass_req));

                        }
                        else
                        {
                            edt_newPassword.setError(null);

                        }

                    }





                }
            }
        });
        updatedialog.getWindow().getAttributes().windowAnimations=R.style.dialog;
        updatedialog.setView(view);
        updatedialog.show();




    }


    private void getCountry(final String state)
    {
        Api.getService()
                .getCountry_City()
               .enqueue(new Callback<List<Country_City_Model>>() {
                   @Override
                   public void onResponse(Call<List<Country_City_Model>> call, Response<List<Country_City_Model>> response) {
                       if (response.isSuccessful())
                       {
                           country_city_modelList.clear();
                           country_city_modelList.addAll(response.body());
                           cityModelList.clear();
                           if (state.equals("1"))
                           {
                               createCountryDialog(response.body());

                           }

                       }
                   }

                   @Override
                   public void onFailure(Call<List<Country_City_Model>> call, Throwable t) {
                       Log.e("Error",t.getMessage());
                       Toast.makeText(homeActivity,R.string.something, Toast.LENGTH_SHORT).show();
                   }
               });
    }
    private void SelectImage(int img_req)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        getActivity().startActivityForResult(intent.createChooser(intent,"Select Image"),img_req);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==img1_req_profile&&resultCode== Activity.RESULT_OK&&data!=null)
        {
            Uri uri = data.getData();
            UpdateImage(uri,"user_photo");

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==read_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]!=PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getActivity(),R.string.per_den, Toast.LENGTH_SHORT).show();
                }else
                {
                    SelectImage(img1_req_profile);


                }
            }
        }
    }
    private void UpdateImage(Uri uri,String part_name) {

        updateImagedialog = Common.createProgressDialog(getActivity(),getString(R.string.upd_img));
        updateImagedialog.show();

        RequestBody name_part = Common.getRequestBodyText(userModel.getUser_full_name());
        RequestBody email_part = Common.getRequestBodyText(userModel.getUser_email());
        RequestBody phone_part = Common.getRequestBodyText(userModel.getUser_phone());
        RequestBody country_part = Common.getRequestBodyText(userModel.getId_country());
        RequestBody address_part = Common.getRequestBodyText(userModel.getUser_address());
        RequestBody city_part = Common.getRequestBodyText(userModel.getUser_city());
        MultipartBody.Part image_part =Common.getMultiPart(getActivity(),uri,part_name);

        Api.getService()
                .UpdateProfileImage(userModel.getUser_id(),phone_part,country_part,email_part,name_part,city_part,address_part,image_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        if (response.isSuccessful())
                        {
                            updateImagedialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {
                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.upd_succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.em_ph_exist, Toast.LENGTH_SHORT).show();

                            }                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        updateImagedialog.dismiss();
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();

                    }
                });



    }

    private void update_name(String newName)
    {
        Log.e("name",newName);

        RequestBody name_part = Common.getRequestBodyText(newName);
        RequestBody email_part = Common.getRequestBodyText(userModel.getUser_email());
        RequestBody phone_part = Common.getRequestBodyText(userModel.getUser_phone());
        RequestBody country_part = Common.getRequestBodyText(userModel.getId_country());
        RequestBody address_part = Common.getRequestBodyText(userModel.getUser_address());

        RequestBody city_part = Common.getRequestBodyText(userModel.getUser_city());


        Api.getService().UpdateProfileData(userModel.getUser_id(),phone_part,country_part,email_part,name_part,city_part,address_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {
                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.upd_succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.em_ph_exist, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void update_phone(String newPhone)
    {
        Log.e("phone",newPhone);
        RequestBody name_part = Common.getRequestBodyText(userModel.getUser_full_name());
        RequestBody email_part = Common.getRequestBodyText(userModel.getUser_email());
        RequestBody phone_part = Common.getRequestBodyText(newPhone);
        RequestBody country_part = Common.getRequestBodyText(userModel.getId_country());
        RequestBody address_part = Common.getRequestBodyText(userModel.getUser_address());
        RequestBody city_part = Common.getRequestBodyText(userModel.getUser_city());

        Api.getService().UpdateProfileData(userModel.getUser_id(),phone_part,country_part,email_part,name_part,city_part,address_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {
                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.upd_succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.em_ph_exist, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void update_email(String newEmail)
    {
        Log.e("email",newEmail);

        RequestBody name_part = Common.getRequestBodyText(userModel.getUser_full_name());
        RequestBody email_part = Common.getRequestBodyText(newEmail);
        RequestBody phone_part = Common.getRequestBodyText(userModel.getUser_phone());
        RequestBody country_part = Common.getRequestBodyText(userModel.getId_country());
        RequestBody address_part = Common.getRequestBodyText(userModel.getUser_address());
        RequestBody city_part = Common.getRequestBodyText(userModel.getUser_city());

        Api.getService().UpdateProfileData(userModel.getUser_id(),phone_part,country_part,email_part,name_part,city_part,address_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {

                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.upd_succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.em_ph_exist, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void update_Password(String oldPass,String newPass)
    {
        Api.getService().UpdatePassword(userModel.getUser_id(),oldPass,newPass)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();

                            Log.e("dddd",response.body().getSuccess_update_pass()+"");
                            if (response.body().getSuccess_update_pass()==0)
                            {

                                Toast.makeText(getActivity(), R.string.wrong_oldpass, Toast.LENGTH_SHORT).show();
                            }else if (response.body().getSuccess_update_pass()==1)
                            {


                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.upd_succ, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void  UpdateUserData(UserModel userModel)
    {
        this.userModel = userModel;
        this.userSingleTone.setUserModel(userModel);
        this.preferences.create_update_userData(getActivity(),userModel);
        UpdateUi(userModel);
        homeActivity.UpdateUi(userModel);

    }

    private void update_city(String newCity)
    {
        Log.e("city",newCity);
        progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.updng_city));
        progressDialog.show();

        RequestBody name_part = Common.getRequestBodyText(userModel.getUser_full_name());
        RequestBody email_part = Common.getRequestBodyText(userModel.getUser_email());
        RequestBody phone_part = Common.getRequestBodyText(userModel.getUser_phone());
        RequestBody country_part = Common.getRequestBodyText(userModel.getId_country());
        RequestBody address_part = Common.getRequestBodyText(userModel.getUser_address());

        RequestBody city_part = Common.getRequestBodyText(newCity);


        Api.getService().UpdateProfileData(userModel.getUser_id(),phone_part,country_part,email_part,name_part,city_part,address_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {
                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.upd_succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.em_ph_exist, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void update_Country(String countryId)
    {

        progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.upd_country));
        progressDialog.show();
        RequestBody name_part = Common.getRequestBodyText(userModel.getUser_full_name());
        RequestBody email_part = Common.getRequestBodyText(userModel.getUser_email());
        RequestBody phone_part = Common.getRequestBodyText(userModel.getUser_phone());
        RequestBody country_part = Common.getRequestBodyText(countryId);
        RequestBody address_part = Common.getRequestBodyText(userModel.getUser_address());
        RequestBody city_part = Common.getRequestBodyText(userModel.getUser_city());


        Api.getService().UpdateProfileData(userModel.getUser_id(),phone_part,country_part,email_part,name_part,city_part,address_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {
                                UpdateUserData(response.body());
                                createCityDialog(cityModelList);
                                Toast.makeText(getActivity(), R.string.upd_succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.em_ph_exist, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void update_address(String address)
    {

        RequestBody name_part = Common.getRequestBodyText(userModel.getUser_full_name());
        RequestBody email_part = Common.getRequestBodyText(userModel.getUser_email());
        RequestBody phone_part = Common.getRequestBodyText(userModel.getUser_phone());
        RequestBody country_part = Common.getRequestBodyText(userModel.getId_country());
        RequestBody address_part = Common.getRequestBodyText(address);
        RequestBody city_part = Common.getRequestBodyText(userModel.getUser_city());


        Api.getService().UpdateProfileData(userModel.getUser_id(),phone_part,country_part,email_part,name_part,city_part,address_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {
                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.upd_succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.em_ph_exist, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setCityItem(Country_City_Model.CityModel cityModel) {
        cityDialog.dismiss();
        update_city(cityModel.getId_city());

    }

    public void setCountryItem(Country_City_Model country_city_model) {

        countryDialog.dismiss();
        cityModelList.clear();
        cityModelList.addAll(country_city_model.getSub_city());
        update_Country(country_city_model.getId_country());
    }

}
