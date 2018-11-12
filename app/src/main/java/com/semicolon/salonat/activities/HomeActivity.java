package com.semicolon.salonat.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.semicolon.salonat.R;
import com.semicolon.salonat.fragments.FragmentNotifications;
import com.semicolon.salonat.fragments.Fragment_Contactus;
import com.semicolon.salonat.fragments.Fragment_Home;
import com.semicolon.salonat.fragments.Fragment_MyOrders;
import com.semicolon.salonat.fragments.Fragment_MyReservations;
import com.semicolon.salonat.fragments.Fragment_Profile;
import com.semicolon.salonat.fragments.Fragment_Terms_Conditions;
import com.semicolon.salonat.models.ItemModel;
import com.semicolon.salonat.models.ResponsModel;
import com.semicolon.salonat.models.SalonModel;
import com.semicolon.salonat.models.ServiceModel;
import com.semicolon.salonat.models.UnReadModel;
import com.semicolon.salonat.models.UserModel;
import com.semicolon.salonat.preference.Preferences;
import com.semicolon.salonat.remote.Api;
import com.semicolon.salonat.share.Common;
import com.semicolon.salonat.singletone.ItemSingleTone;
import com.semicolon.salonat.singletone.UserSingleTone;
import com.semicolon.salonat.tags.Tags;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    public FragmentManager fragmentManager;
    private Fragment_Home fragment_home;
    private Fragment_MyReservations fragmentMyReservation;
    private Fragment_Profile fragment_profile;
    private Fragment_Terms_Conditions fragment_terms_conditions;
    private Fragment_Contactus fragment_contactus;
    private Fragment_MyOrders fragment_myOrders;
    private FragmentNotifications fragmentNotifications;
    private TextView tv_title;
    private Preferences preferences;
    //private ImageView image;
    private TextView tv_name;
    private Intent intentService;
    private FrameLayout fl_not, fl_cart_not;
    private TextView tv_not, tv_cart_not;
    private LinearLayout ll_total;
    private TextView tv_total;
    private Button btn_my_order;
    private ItemSingleTone itemSingleTone;
    private ItemModel itemModel;
    private int can_read = 1;
    private List<ServiceModel.Sub_Service> sub_serviceList;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private final String fineLoc = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int fine_req =1010,gps_req = 2020;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();

    }

    private void initView() {
        sub_serviceList = new ArrayList<>();
        itemSingleTone = ItemSingleTone.getInstance();
        preferences = Preferences.getInstance();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, R.color.icon_cart));
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        //image = view.findViewById(R.id.image);
        tv_name = view.findViewById(R.id.tv_name);
        navigationView.setNavigationItemSelectedListener(this);
        ////////////////////////////////////////////////////////
        fl_not = findViewById(R.id.fl_not);
        fl_cart_not = findViewById(R.id.fl_cart_not);
        tv_not = findViewById(R.id.tv_not);
        tv_cart_not = findViewById(R.id.tv_cart_not);

        ////////////////////////////////////////////////////////
        ll_total = findViewById(R.id.ll_total);
        tv_total = findViewById(R.id.tv_total);
        btn_my_order = findViewById(R.id.btn_my_order);

        btn_my_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_total.setVisibility(View.GONE);
                fl_cart_not.setVisibility(View.GONE);

               /* Fragment_Salon_details fragment_salon_details = (Fragment_Salon_details) fragmentManager.findFragmentById(R.id.fragment_home_container);
                fragmentManager.beginTransaction().hide(fragment_salon_details).commit();*/
                if (!(fragmentManager.findFragmentById(R.id.fragment_home_container) instanceof Fragment_MyOrders)) {
                    fragment_myOrders = Fragment_MyOrders.getInstance(itemModel);

                    fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_myOrders).addToBackStack("fragment_reservations").commit();

                }
            }
        });
        fl_cart_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_total.setVisibility(View.GONE);
                fl_cart_not.setVisibility(View.GONE);
                if (!(fragmentManager.findFragmentById(R.id.fragment_home_container) instanceof Fragment_MyOrders)) {

                    fragment_myOrders = Fragment_MyOrders.getInstance(itemModel);
                    fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_myOrders).addToBackStack("fragment_reservations").commit();

                }
            }
        });

        fl_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateTitle(getString(R.string.notification));
                if (can_read == 1) {
                    ReadNotification(userModel.getUser_id());
                }

                if (fragmentNotifications == null) {
                    fragmentNotifications = FragmentNotifications.getInstance();
                }

                if (!(fragmentManager.findFragmentById(R.id.fragment_home_container) instanceof FragmentNotifications)) {
                    if (!(fragmentManager.findFragmentById(R.id.fragment_home_container) instanceof Fragment_Home)) {
                        fragmentManager.popBackStack();

                    }
                }


                if (!fragmentNotifications.isAdded()) {
                    fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragmentNotifications).addToBackStack("fragmentNotifications").commit();
                }
            }
        });
        ////////////////////////////////////////////////////////
        tv_title = findViewById(R.id.tv_title);

        fragmentManager = getSupportFragmentManager();
        fragment_home = Fragment_Home.getInstance();
        fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_home).addToBackStack("fragment_home").commit();
        UpdateTitle(getString(R.string.home));

        String session = preferences.getSession(this);
        if (session.equals(Tags.session_login)) {
            this.userModel = preferences.getUserData(this);
            userSingleTone.setUserModel(this.userModel);
            checkGpsPermission();
            EventBus.getDefault().register(this);
            UpdateToken();
            UpdateUi(this.userModel);
            getUnReadNotificationCount(userModel.getUser_id());
        }
    }

    public void UpdateUi(UserModel userModel) {

        Log.e("Photo", userModel.getUser_photo());
        // Picasso.with(this).load(Tags.IMAGE_URL+userModel.getUser_photo()).into(image);
        tv_name.setText(userModel.getUser_full_name());
        fl_not.setVisibility(View.VISIBLE);
    }

    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private void initLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(6000 * 5);
        locationRequest.setFastestInterval(6000 * 5);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
                initGoogleApiClient();
            }else
            {
                CreateGpsDialog();
            }
        }
    }
    private void CreateGpsDialog()
    {
       final AlertDialog gps_dialog = new AlertDialog.Builder(this)
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
    private void UpdateToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult().getToken();
                            Api.getService()
                                    .UpdateTokenId(userModel.getUser_id(), token)
                                    .enqueue(new Callback<ResponsModel>() {
                                        @Override
                                        public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                                            if (response.isSuccessful()) {
                                                if (response.body().getSuccess_token_id() == 1) {
                                                    Log.e("Token updated", "Token updated successfully");
                                                } else {
                                                    Log.e("Token updated", "Token updated failed");

                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponsModel> call, Throwable t) {
                                            Log.e("Error", t.getMessage());

                                        }
                                    });
                        }
                    }
                });
    }

    public void UpdateCartNot(int not_count) {

        if (not_count > 0) {
            fl_cart_not.setVisibility(View.VISIBLE);
            tv_cart_not.setVisibility(View.VISIBLE);
            tv_cart_not.setText(String.valueOf(not_count));

        } else {
            tv_cart_not.setVisibility(View.GONE);

            fl_cart_not.setVisibility(View.GONE);
        }
    }

    public void UpdateReservationCostTotal(int total) {
        if (total > 0) {
            ll_total.setVisibility(View.VISIBLE);
            tv_total.setText(getString(R.string.total) + " " + total + " " + getString(R.string.sar));
        } else {
            ll_total.setVisibility(View.GONE);

        }
    }

    public void CreateAlertClearItemCart(String msg) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .create();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_clear_cart, null);
        TextView tv_msg = view.findViewById(R.id.tv_msg);
        Button doneBtn = view.findViewById(R.id.doneBtn);
        Button cancelBtn = view.findViewById(R.id.cancelBtn);
        tv_msg.setText(msg);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClearItemModel();
                alertDialog.dismiss();
                ClearDataCart();
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

    private void getUnReadNotificationCount(String user_id) {
        Api.getService()
                .getUnReadNotificationsCount(user_id)
                .enqueue(new Callback<UnReadModel>() {
                    @Override
                    public void onResponse(Call<UnReadModel> call, Response<UnReadModel> response) {
                        if (response.isSuccessful()) {
                            UpdateNotificationUi(response.body().getAlert_count());
                        }
                    }

                    @Override
                    public void onFailure(Call<UnReadModel> call, Throwable t) {
                        Log.e("Error", t.getMessage());
                        try {
                            Toast.makeText(HomeActivity.this,R.string.something, Toast.LENGTH_SHORT).show();

                        }catch (NullPointerException e){}
                    }
                });
    }

    private void ReadNotification(String user_id) {
        Api.getService()
                .ReadNotifications(user_id, "1")
                .enqueue(new Callback<ResponsModel>() {
                    @Override
                    public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getSuccess_read() == 1) {
                                UpdateNotificationUi(0);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponsModel> call, Throwable t) {
                        Log.e("Error", t.getMessage());
                        try {
                            Toast.makeText(HomeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();

                        }catch (NullPointerException e){}

                    }
                });
    }

    private void UpdateNotificationUi(int not_count) {
        if (not_count > 0) {
            tv_not.setVisibility(View.VISIBLE);
            tv_not.setText(String.valueOf(not_count));
        } else {
            can_read = 0;
            tv_not.setVisibility(View.GONE);

        }
    }

    private void ClearDataCart() {
        UpdateTitle(getString(R.string.home));
        navigationView.getMenu().getItem(0).setChecked(true);
        hideCartTotal();
        fragmentManager.popBackStack();
        /*HomeActivity.this.fragment_home = (Fragment_Home) fragmentManager.findFragmentByTag("fragment_home");

        if (fragment_home==null)
        {
            fragment_home = Fragment_Home.getInstance();
        }

        fragmentManager.beginTransaction().replace(R.id.fragment_home_container,fragment_home).commit();
*/

    }

    public void hideCartTotal() {
        UpdateCartNot(0);
        ll_total.setVisibility(View.GONE);
    }

    private void StartLocationUpdate() {
        Log.e("Connected","connected");
        initLocationRequest();
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };

        LocationServices.getFusedLocationProviderClient(HomeActivity.this).requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());

        /*intentService = new Intent(this, UpdateLocation.class);
        startService(intentService);*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateNotification(UnReadModel unReadModel)
    {
        if (fragmentManager.findFragmentById(R.id.fragment_home_container)instanceof FragmentNotifications)
        {
            fragmentNotifications.getNotifications(userModel.getUser_id());
        }else
            {
                can_read=1;
                getUnReadNotificationCount(userModel.getUser_id());
            }
    }
    public void UpdateTitle(String title)

    {
        tv_title.setText(title);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.home:
                if (fragmentManager.findFragmentById(R.id.fragment_home_container) instanceof Fragment_Home)
                {
                    Log.e("home","home1");


                }else
                    {
                        Log.e("home","home11");

                        UpdateTitle(getString(R.string.home));
                        /*if (fragment_home==null)
                        {
                            fragment_home = Fragment_Home.getInstance();
                        }
                        fragmentManager.beginTransaction().replace(R.id.fragment_home_container,fragment_home).commit();
             */
                        fragmentManager.popBackStack();
                    }

                break;
            case R.id.profile:

                if (userModel==null) {
                    new Handler()
                            .postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    navigationView.getMenu().getItem(0).setChecked(true);

                                }
                            },2000);
                    Common.CreateUserNotSignInAlertDialog(HomeActivity.this);

                }else
                    {
                        UpdateTitle(getString(R.string.profile));

                        if (fragment_profile==null)
                        {
                            fragment_profile = Fragment_Profile.getInstance(userModel);

                        }



                        if (!(fragmentManager.findFragmentById(R.id.fragment_home_container)instanceof Fragment_Profile))
                        {
                            if (!(fragmentManager.findFragmentById(R.id.fragment_home_container)instanceof Fragment_Home))
                            {
                                Log.e("profile","1");
                                fragmentManager.popBackStack();
                                if (!fragment_profile.isAdded())
                                {
                                    fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragment_profile).addToBackStack("fragment_profile").commit();

                                }
                            }else
                                {
                                    if (!fragment_profile.isAdded())
                                    {
                                        fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragment_profile).addToBackStack("fragment_profile").commit();

                                    }
                                }


                        }

                    }


                break;
            case R.id.my_reservations:
                if (userModel==null)
                {
                    new Handler()
                            .postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    navigationView.getMenu().getItem(0).setChecked(true);

                                }
                            },2000);
                    Common.CreateUserNotSignInAlertDialog(HomeActivity.this);

                }else
                {
                    UpdateTitle(getString(R.string.my_reserve));

                    if (fragmentMyReservation==null)
                    {
                        fragmentMyReservation = Fragment_MyReservations.getInstance();
                    }


                    if (!(fragmentManager.findFragmentById(R.id.fragment_home_container)instanceof Fragment_MyReservations))
                    {
                        if (!(fragmentManager.findFragmentById(R.id.fragment_home_container)instanceof Fragment_Home))
                        {
                            Log.e("my_reserve","2");

                            fragmentManager.popBackStack();
                            if(!fragmentMyReservation.isAdded())
                            {
                                fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragmentMyReservation).addToBackStack("fragmentmyReservation").commit();

                            }

                        }else
                            {
                                if(!fragmentMyReservation.isAdded())
                                {
                                    fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragmentMyReservation).addToBackStack("fragmentmyReservation").commit();

                                }

                            }


                    }

                }


                break;
            case R.id.contact_us:


                UpdateTitle(getString(R.string.contactus));


                if (fragment_contactus==null)
                {

                    fragment_contactus = Fragment_Contactus.getInstance();
                }

                if (!(fragmentManager.findFragmentById(R.id.fragment_home_container)instanceof Fragment_Contactus))
                {

                    if (!(fragmentManager.findFragmentById(R.id.fragment_home_container)instanceof Fragment_Home))
                    {
                        Log.e("contact_us","3");

                        fragmentManager.popBackStack();
                        if(!fragment_contactus.isAdded())
                        {
                            fragmentManager.beginTransaction().add(R.id.fragment_home_container, Fragment_Contactus.getInstance()).addToBackStack("fragment_contactus").commit();

                        }

                    }else
                        {
                            if(!fragment_contactus.isAdded())
                            {
                                fragmentManager.beginTransaction().add(R.id.fragment_home_container, Fragment_Contactus.getInstance()).addToBackStack("fragment_contactus").commit();

                            }
                        }

                }


                break;
            case R.id.terms:
                UpdateTitle(getString(R.string.terms_and_conditions));

                if (fragment_terms_conditions==null)
                {
                    fragment_terms_conditions = Fragment_Terms_Conditions.getInstance();

                }
                if (!(fragmentManager.findFragmentById(R.id.fragment_home_container)instanceof Fragment_Terms_Conditions))
                {
                    if (!(fragmentManager.findFragmentById(R.id.fragment_home_container)instanceof Fragment_Home))
                    {
                        Log.e("terms","4");

                        fragmentManager.popBackStack();
                        if(!fragmentMyReservation.isAdded())
                        {
                            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_terms_conditions).addToBackStack("fragment_terms_conditions").commit();

                        }

                    }else
                        {
                            if(!fragmentMyReservation.isAdded())
                            {
                                fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_terms_conditions).addToBackStack("fragment_terms_conditions").commit();

                            }
                        }


                }

                break;
            case R.id.logout:
                if (userModel==null)
                {
                    finish();
                }else
                {
                    logout();
                }
                break;


        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void logout()
    {
        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.logng_out));
        dialog.show();
        Api.getService().logout(userModel.getUser_id())
                .enqueue(new Callback<ResponsModel>() {
                    @Override
                    public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();

                            if (response.body().getSuccess_logout()==1)
                            {
                                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                manager.cancelAll();
                                userSingleTone.clear();
                                userModel = null;
                                preferences.create_update_session(HomeActivity.this,Tags.session_logout);
                                Intent intent = new Intent(HomeActivity.this,SignInActivity.class);
                                startActivity(intent);
                                finish();


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponsModel> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        dialog.dismiss();
                        try {
                            Toast.makeText(HomeActivity.this,R.string.something, Toast.LENGTH_SHORT).show();

                        }catch (NullPointerException e){}

                    }
                });
    }
    @Override
    public void onBackPressed()
    {

        Log.e("count1",fragmentManager.getBackStackEntryCount()+"_");

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            {
               Back();
            }
    }

    public void Back() {
        Log.e("back stack",fragmentManager.getBackStackEntryCount()+"_");
        for (int i=0;i<fragmentManager.getBackStackEntryCount();i++)
        {
            Log.e("stack_name",fragmentManager.getBackStackEntryAt(i).getName()+"_");
            Log.e("stack_id",fragmentManager.getBackStackEntryAt(i).getId()+"_");

        }
        if (fragmentManager.findFragmentById(R.id.fragment_home_container) instanceof Fragment_Home)
        {
            Log.e("1","1");
            fragmentManager.popBackStack();
            finish();

        }else
        {
            if (fragmentManager.findFragmentById(R.id.fragment_home_container) instanceof Fragment_MyOrders)
            {
                Log.e("hh","hh");
                fragmentManager.popBackStack();
                ll_total.setVisibility(View.VISIBLE);
                fl_cart_not.setVisibility(View.VISIBLE);
                //fragmentManager.beginTransaction().show(fragment_salon_details).commit();
            }else
                {


                   //// itemSingleTone = ItemSingleTone.getInstance();
                    //itemModel = itemSingleTone.getItemModel();

                    if (itemModel!=null)
                    {

                        String msg = getString(R.string.cart_contain)+" "+itemModel.getServiceModelList().size()+" "+getString(R.string.item)+"\n"+getString(R.string.do_del_item);
                        CreateAlertClearItemCart(msg);
                    }else
                    {
                        ClearDataCart();
                        Log.e("2","2");
                    }

                }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment :fragmentList)
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode==gps_req)
        {
            if (isGpsOpen())
            {
                initGoogleApiClient();
            }else
            {
                CreateGpsDialog();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment :fragmentList)
        {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (requestCode==fine_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    initGoogleApiClient();

                }
            }
        }


    }

    @Override
    protected void onDestroy()
    {
        if (googleApiClient!=null)
        {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
            googleApiClient.disconnect();
        }

        if (EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();

    }


    public void AddItemInSalon(ServiceModel.Sub_Service subService, SalonModel salonModel)
    {
        Log.e("costSalone",subService.getSalon_cost());

        if (itemModel==null)
        {
            Log.e("itemModel","null");
            sub_serviceList.add(subService);
            itemModel = new ItemModel(salonModel.getId_salon(),salonModel.getMain_photo(),salonModel.getTitle(),String.valueOf(salonModel.getSalon_stars_num()),salonModel.getGallary(),sub_serviceList,subService.getSalon_cost(),Tags.in_salon);
            UpdateCartNot(itemModel.getServiceModelList().size());
            UpdateReservationCostTotal(Integer.parseInt(itemModel.getReservation_cost()));
            itemSingleTone.setItemModel(itemModel);

        }else
        {
            Log.e("itemModel","!null");

            if (TextUtils.isEmpty(itemModel.getReservation_cost()))
            {
                itemModel.setReservation_cost(subService.getSalon_cost());
            }else
            {
                itemModel.setReservation_cost(String.valueOf(Integer.parseInt(itemModel.getReservation_cost())+Integer.parseInt(subService.getSalon_cost())));
            }


            itemModel.AddItem(itemModel,subService);
            UpdateCartNot(itemModel.getServiceModelList().size());
            UpdateReservationCostTotal(Integer.parseInt(itemModel.getReservation_cost()));
            itemSingleTone.setItemModel(itemModel);


        }

    }

    public void RemoveItemInSalon(ServiceModel.Sub_Service subService)
    {
        if (itemModel!=null)
        {
            itemModel.RemoveItem(itemModel,subService);
            int reservation_cost =Integer.parseInt(itemModel.getReservation_cost())-Integer.parseInt(subService.getSalon_cost());
            itemModel.setReservation_cost(String.valueOf(reservation_cost));
            UpdateCartNot(itemModel.getServiceModelList().size());
            if (itemModel.getServiceModelList().size()>0)
            {
                UpdateReservationCostTotal(Integer.parseInt(itemModel.getReservation_cost()));
            }else
            {
                ClearItemModel();

                UpdateReservationCostTotal(0);

            }
        }
    }

    public void AddItemInHome(ServiceModel.Sub_Service subService, SalonModel salonModel)
    {

        Log.e("cost",subService.getHome_cost());
        if (itemModel==null)
        {
            sub_serviceList.add(subService);
            itemModel = new ItemModel(salonModel.getId_salon(),salonModel.getMain_photo(),salonModel.getTitle(),String.valueOf(salonModel.getSalon_stars_num()),salonModel.getGallary(),sub_serviceList,subService.getHome_cost(),Tags.in_home);
            UpdateCartNot(itemModel.getServiceModelList().size());
            UpdateReservationCostTotal(Integer.parseInt(itemModel.getReservation_cost()));
            itemSingleTone.setItemModel(itemModel);

        }else
        {
            if (TextUtils.isEmpty(itemModel.getReservation_cost()))
            {
                itemModel.setReservation_cost(subService.getHome_cost());
            }else
            {
                itemModel.setReservation_cost(String.valueOf(Integer.parseInt(itemModel.getReservation_cost())+Integer.parseInt(subService.getHome_cost())));
            }


            itemModel.AddItem(itemModel,subService);
            UpdateCartNot(itemModel.getServiceModelList().size());
            UpdateReservationCostTotal(Integer.parseInt(itemModel.getReservation_cost()));
            itemSingleTone.setItemModel(itemModel);


        }


    }

    public void RemoveItemInHome(ServiceModel.Sub_Service subService)
    {
        if (itemModel!=null)
        {
            itemModel.RemoveItem(itemModel,subService);
            int reservation_cost =Integer.parseInt(itemModel.getReservation_cost())-Integer.parseInt(subService.getHome_cost());
            itemModel.setReservation_cost(String.valueOf(reservation_cost));
            UpdateCartNot(itemModel.getServiceModelList().size());
            if (itemModel.getServiceModelList().size()>0)
            {
                UpdateReservationCostTotal(Integer.parseInt(itemModel.getReservation_cost()));
            }else
            {
                ClearItemModel();
                UpdateReservationCostTotal(0);

            }
        }
    }

    public void RemoveItemFromCurrentOrders()
    {
        ClearItemModel();
        fragmentManager.popBackStack("fragment_salon_details", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void ClearDataAfterReservation()
    {
        fragmentManager.popBackStack("fragment_salon_details", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ClearItemModel();

    }

    //InReservationFragmentFromCurrentOrdersFragment
    public void UpdateItemModel(ItemModel itemModel)
    {
        fragment_myOrders.UpdateItemModel(itemModel);
    }

    public void ClearItemModel()
    {
        itemSingleTone.Clear(itemModel);
        itemModel = null;
        itemSingleTone.setItemModel(itemModel);
        hideCartTotal();
    }

    public void ClearDataAfterTransfer() {
        fragmentManager.popBackStack("fragmentNotifications", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ClearItemModel();
    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {
        StartLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {

        if (googleApiClient!=null)
        {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("Loc",location.getLongitude()+"_____");
        if (googleApiClient!=null)
        {
            googleApiClient.disconnect();
            UpdateLocation(location.getLatitude(),location.getLongitude());
            LocationServices.getFusedLocationProviderClient(this)
                    .removeLocationUpdates(new LocationCallback());
        }

    }

    private void UpdateLocation(double lat,double lng)
    {
        if (userModel!=null)
        {
            Api.getService()
                    .updateLocation(userModel.getUser_id(),lat,lng)
                    .enqueue(new Callback<ResponsModel>() {
                        @Override
                        public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                            if (response.isSuccessful())
                            {
                                if (response.body().getSuccess_location()==1)
                                {
                                    Log.e("location updated","location updated successfully");
                                }else
                                {
                                    Log.e("location updated","location updated failed");

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponsModel> call, Throwable t) {
                            Log.e("Error",t.getMessage());
                            try {
                                Toast.makeText(HomeActivity.this,R.string.something, Toast.LENGTH_SHORT).show();

                            }catch (NullPointerException e){}
                        }
                    });
        }

    }


}



