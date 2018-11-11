package com.semicolon.salonat.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.salonat.R;
import com.semicolon.salonat.adapters.CityAdapter;
import com.semicolon.salonat.adapters.CountryAdapter;
import com.semicolon.salonat.models.Country_City_Model;
import com.semicolon.salonat.models.LocationModel;
import com.semicolon.salonat.models.UserModel;
import com.semicolon.salonat.preference.Preferences;
import com.semicolon.salonat.remote.Api;
import com.semicolon.salonat.service.UpdateLocation;
import com.semicolon.salonat.share.Common;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private ImageView image_back;
    private CircleImageView image;
    private EditText edt_name,edt_email,edt_address,edt_phone,edt_password;
    private TextView tv_country,tv_city;
   // private PhoneInputLayout edt_check_phone;
    private Button btn_signup;
    private final String read_permition = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final int read_req = 2000,img=202;
    private Uri uri = null;
    private String country="",city="";
    private AlertDialog country_dialog,city_dialog,gps_dialog;
    private final String fineLoc = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int fine_req = 1021,gps_req = 225;
    private double myLat=0.0,myLng=0.0;
    private Intent intenService;
    private List<Country_City_Model> country_city_modelList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        country_city_modelList = new ArrayList<>();
        image_back = findViewById(R.id.image_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        image = findViewById(R.id.image);
        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        edt_address = findViewById(R.id.edt_address);
        edt_phone = findViewById(R.id.edt_phone);
        edt_password = findViewById(R.id.edt_password);
        tv_country = findViewById(R.id.tv_country);
        tv_city = findViewById(R.id.tv_city);
        //edt_check_phone = findViewById(R.id.edt_check_phone);
        btn_signup = findViewById(R.id.btn_signup);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission();
            }
        });

        tv_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (country_city_modelList.size()>0)
                {
                    country_dialog.show();

                }else
                    {
                        getCountry_CityData();

                        if (country_city_modelList.size()>0)
                        {
                            country_dialog.show();

                        }
                    }
            }
        });

        tv_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(country)||country_city_modelList.size()==0)
                {
                    Toast.makeText(SignUpActivity.this,R.string.ch_country, Toast.LENGTH_SHORT).show();
                }else
                    {
                        city_dialog.show();
                    }
            }
        });

        getCountry_CityData();
        checkGpsPermission();



    }

    private void getCountry_CityData() {
        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.lod_cont_cit));
        dialog.show();
        Api.getService()
                .getCountry_City()
                .enqueue(new Callback<List<Country_City_Model>>() {
                    @Override
                    public void onResponse(Call<List<Country_City_Model>> call, Response<List<Country_City_Model>> response) {
                        if (response.isSuccessful())
                        {
                            country_city_modelList.clear();
                            country_city_modelList.addAll(response.body());
                            CreateCountryDialog(response.body());
                            dialog.dismiss();

                        }
                    }

                    @Override
                    public void onFailure(Call<List<Country_City_Model>> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        dialog.dismiss();
                        Toast.makeText(SignUpActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkGpsPermission()
    {

        if (ContextCompat.checkSelfPermission(this,fineLoc)!=PackageManager.PERMISSION_GRANTED)
        {
            String [] perm = {fineLoc};
            ActivityCompat.requestPermissions(this,perm,fine_req);
        }else
            {
                if (isGpsOpen())
                {
                    StartLocationUpdate();
                }else
                    {
                        CreateGpsDialog();
                    }
            }
    }
    private void StartLocationUpdate()
    {
        intenService = new Intent(this, UpdateLocation.class);
        startService(intenService);
    }
    private void StopLocationUpdate()
    {
        if (intenService!=null)
        {
            stopService(intenService);
        }
    }
    private void CreateGpsDialog()
    {
        gps_dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .create();

        View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog,null);
        TextView tv_msg = view.findViewById(R.id.tv_msg);
        tv_msg.setText(R.string.gps_will_open);
        Button doneBtn = view.findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps_dialog.dismiss();
                openGps();
            }
        });

        gps_dialog.getWindow().getAttributes().windowAnimations=R.style.dialog;
        gps_dialog.setView(view);
        gps_dialog.setCanceledOnTouchOutside(false);
        gps_dialog.show();

    }
    private void openGps()
    {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent,gps_req);

    }
    private boolean isGpsOpen()
    {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager!=null)
        {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return false;
    }
    private void CreateCountryDialog(List<Country_City_Model> country_city_modelList)
    {
        View view = LayoutInflater.from(this).inflate(R.layout.country_city_dialog,null);
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(R.string.ch_country);
        RecyclerView recView = view.findViewById(R.id.recView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recView.setLayoutManager(manager);
        RecyclerView.Adapter adapter = new CountryAdapter(this,country_city_modelList);
        recView.setAdapter(adapter);
        country_dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        country_dialog.getWindow().getAttributes().windowAnimations = R.style.dialog;
        country_dialog.setCanceledOnTouchOutside(false);
        country_dialog.setView(view);

    }
    private void CreateCityDialog(List<Country_City_Model.CityModel> cityModelList)
    {
        View view = LayoutInflater.from(this).inflate(R.layout.country_city_dialog,null);
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(R.string.ch_city);
        RecyclerView recView = view.findViewById(R.id.recView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recView.setLayoutManager(manager);
        RecyclerView.Adapter adapter = new CityAdapter(this,cityModelList);
        recView.setAdapter(adapter);

        city_dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        city_dialog.getWindow().getAttributes().windowAnimations = R.style.dialog;
        city_dialog.setCanceledOnTouchOutside(false);
        city_dialog.setView(view);
        city_dialog.show();

    }

    public void setCountryItem(Country_City_Model countryItem)
    {
        country_dialog.dismiss();
        country = countryItem.getId_country();
        tv_city.setHint(getString(R.string.ch_city));
        city="";

        String lang = Locale.getDefault().getLanguage();
        if (lang.equals("ar"))
        {
            tv_country.setText(countryItem.getAr_name());
        }else if (lang.equals("en"))
        {
            tv_country.setText(countryItem.getEn_name());

        }

        CreateCityDialog(countryItem.getSub_city());
    }
    public void setCityItem(Country_City_Model.CityModel cityItem)
    {
        city_dialog.dismiss();
        city = cityItem.getId_city();
        String lang = Locale.getDefault().getLanguage();

        if (lang.equals("ar"))
        {
            tv_city.setText(cityItem.getAr_city_title());
        }else if (lang.equals("en"))
        {
            tv_city.setText(cityItem.getEn_city_title());

        }

    }
    private void CheckData()
    {
        String m_name = edt_name.getText().toString();
        String m_email = edt_email.getText().toString();
        String m_address = edt_address.getText().toString();
        String m_phone = edt_phone.getText().toString();
        String m_password = edt_password.getText().toString();


        if (!TextUtils.isEmpty(m_name)&&
                !TextUtils.isEmpty(m_phone)&&
                !TextUtils.isEmpty(country)&&
                !TextUtils.isEmpty(city)&&
                !TextUtils.isEmpty(m_email)&&
                !TextUtils.isEmpty(m_address)&&
                !TextUtils.isEmpty(m_password)&&
                uri!=null&&
                Patterns.EMAIL_ADDRESS.matcher(m_email).matches()) {
            edt_address.setError(null);
            edt_email.setError(null);
            edt_name.setError(null);
            edt_phone.setError(null);
            edt_password.setError(null);
            Common.CloseKeyBoard(this,edt_name);
            SignUp(m_name,m_email,m_phone,m_address,m_password,country,city,uri);
        }else
            {
                if (TextUtils.isEmpty(m_name))
                {
                    edt_name.setError(getString(R.string.name_req));
                }else
                    {
                        edt_name.setError(null);
                    }

                if (TextUtils.isEmpty(m_email))
                {
                    edt_email.setError(getString(R.string.email_req));
                }else if (!Patterns.EMAIL_ADDRESS.matcher(m_email).matches())
                {
                    edt_email.setError(getString(R.string.inv_email));
                }else
                    {
                        edt_email.setError(null);
                    }

                if (TextUtils.isEmpty(m_phone))
                {
                    edt_phone.setError(getString(R.string.phone_req));
                }else
                {
                    edt_phone.setError(null);
                }

                if (TextUtils.isEmpty(m_address))
                {
                    edt_address.setError(getString(R.string.address_req));
                }else
                {
                    edt_address.setError(null);
                }

                if (TextUtils.isEmpty(country))
                {
                    Toast.makeText(this, R.string.country_req, Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(city))
                {
                    Toast.makeText(this, R.string.city_req, Toast.LENGTH_SHORT).show();
                }
                if (uri==null)
                {
                    Toast.makeText(this, R.string.img_req, Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(m_password))
                {
                    edt_password.setError(getString(R.string.pass_req));
                }else
                {
                    edt_password.setError(null);
                }

            }
    }
    private void SignUp(String m_name, String m_email, String m_phone, String m_address, String m_password, String country, String city, Uri uri)
    {
        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.signng_up));
        dialog.show();

        RequestBody name_part = Common.getRequestBodyText(m_name);
        RequestBody email_part = Common.getRequestBodyText(m_email);
        RequestBody phone_part = Common.getRequestBodyText(m_phone);
        RequestBody address_part = Common.getRequestBodyText(m_address);
        RequestBody password_part = Common.getRequestBodyText(m_password);
        RequestBody country_part = Common.getRequestBodyText(country);
        RequestBody city_part = Common.getRequestBodyText(city);
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(myLat));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(myLng));
        RequestBody token_part = Common.getRequestBodyText("");
        MultipartBody.Part image_part = Common.getMultiPart(this,uri,"user_photo");

        Api.getService()
                .SignUp(password_part,phone_part,country_part,email_part,name_part,token_part,lat_part,lng_part,city_part,address_part,image_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.body().getSuccess_signup()==1)
                        {
                            Preferences preferences = Preferences.getInstance();
                            preferences.create_update_userData(SignUpActivity.this,response.body());
                            Intent intent = new Intent(SignUpActivity.this,HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }else if (response.body().getSuccess_signup()==2){

                            Toast.makeText(SignUpActivity.this, R.string.em_ph_exist, Toast.LENGTH_SHORT).show();
                        }else
                            {
                                Toast.makeText(SignUpActivity.this, R.string.something, Toast.LENGTH_SHORT).show();

                            }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        dialog.dismiss();
                        Log.e("Error",t.getMessage());
                        Toast.makeText(SignUpActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });


    }
    private void CheckPermission()
    {

        if (ContextCompat.checkSelfPermission(this,read_permition)!= PackageManager.PERMISSION_GRANTED)
        {
            String [] per = {read_permition};
            ActivityCompat.requestPermissions(this,per,read_req);
        }else
            {
                SelectImage();
            }

    }
    private void SelectImage()
    {
        Intent intent;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
        {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }else
            {
                 intent = new Intent(Intent.ACTION_GET_CONTENT);

            }
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent,"Select Image"),img);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==read_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    SelectImage();
                }
            }
        }else if (requestCode==fine_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if (isGpsOpen())
                    {
                        StartLocationUpdate();
                    }else
                        {
                            openGps();
                        }
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==img && resultCode==RESULT_OK && data!=null)
        {
            uri = data.getData();
            Bitmap bitmap = BitmapFactory.decodeFile(Common.getImagePath(SignUpActivity.this,uri));
            image.setImageBitmap(bitmap);
        }else if (requestCode==gps_req)
        {
            if (isGpsOpen())
            {
                StartLocationUpdate();
            }else
                {
                    Toast.makeText(this, R.string.cnnot_open_gps, Toast.LENGTH_SHORT).show();
                }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenForLocationUpdate(LocationModel locationModel)
    {
        Log.e("lat1",locationModel.getLat()+"__");
        Log.e("lng",locationModel.getLng()+"__");
        myLat = locationModel.getLat();
        myLng = locationModel.getLng();

    }
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (intenService!=null)
        {
            StopLocationUpdate();
        }

        if (EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().unregister(this);
        }
    }
}
