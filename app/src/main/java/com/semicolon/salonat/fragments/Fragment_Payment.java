package com.semicolon.salonat.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lamudi.phonefield.PhoneInputLayout;
import com.semicolon.salonat.R;
import com.semicolon.salonat.activities.HomeActivity;
import com.semicolon.salonat.models.MyReservationModel;
import com.semicolon.salonat.models.ResponsModel;
import com.semicolon.salonat.models.UserModel;
import com.semicolon.salonat.remote.Api;
import com.semicolon.salonat.share.Common;
import com.semicolon.salonat.singletone.UserSingleTone;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Payment extends Fragment {
    private static final String TAG="DATA";
    private ImageView image_back,image;
    private EditText edt_name,edt_phone,edt_amount;
    private PhoneInputLayout edt_check_phone;
    private LinearLayout ll_upload_image;
    private Button btn_send;
    private final String read_per = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final int read_req = 1023;
    private Uri uri=null;
    private final int img_req = 2022;
    private HomeActivity homeActivity;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private MyReservationModel myReservationModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Payment getInstance(MyReservationModel myReservationModel)
    {
        Fragment_Payment fragment_payment = new Fragment_Payment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,myReservationModel);
        fragment_payment.setArguments(bundle);
        return fragment_payment;
    }
    private void initView(View view) {
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        homeActivity = (HomeActivity) getActivity();
        image_back = view.findViewById(R.id.image_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.Back();
            }
        });
        image = view.findViewById(R.id.image);
        edt_name = view.findViewById(R.id.edt_name);
        edt_phone = view.findViewById(R.id.edt_phone);
        edt_amount = view.findViewById(R.id.edt_amount);
        edt_check_phone = view.findViewById(R.id.edt_check_phone);
        ll_upload_image = view.findViewById(R.id.ll_upload_image);
        btn_send = view.findViewById(R.id.btn_send);

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            myReservationModel = (MyReservationModel) bundle.getSerializable(TAG);
        }
        UpdateUI(userModel);
        ll_upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission();
            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckTransferData();
            }
        });


    }

    private void UpdateUI(UserModel userModel) {
        edt_name.setText(userModel.getUser_full_name());
        edt_phone.setText(userModel.getUser_phone());
    }

    private void SelectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent,"Select Image"),img_req);
    }


    private void CheckTransferData() {
        String m_name = edt_name.getText().toString();
        String m_phone = edt_phone.getText().toString();
        String m_amount = edt_amount.getText().toString();

        if (!TextUtils.isEmpty(m_phone))
        {
            if (!m_phone.startsWith("+"))
            {
                m_phone ="+"+m_phone;
            }

            edt_check_phone.setPhoneNumber(m_phone);
        }

        if (!TextUtils.isEmpty(m_name)&&!TextUtils.isEmpty(m_phone)&&edt_check_phone.isValid()&&!TextUtils.isEmpty(m_amount)&&uri!=null)
        {
            edt_phone.setError(null);
            edt_name.setError(null);
            edt_amount.setError(null);
            Book(m_name,m_phone,m_amount);
        }else
        {
            if (!TextUtils.isEmpty(m_name))
            {
                edt_name.setError(null);

            }else
            {
                edt_name.setError(getString(R.string.name_req));
            }

            if (TextUtils.isEmpty(m_phone))
            {

                edt_phone.setError(getString(R.string.phone_req));
            }else if (!edt_check_phone.isValid())
            {
                edt_phone.setError(getString(R.string.inv_phone));
            }else
            {
                edt_phone.setError(null);

            }

            if (!TextUtils.isEmpty(m_amount))
            {
                edt_amount.setError(null);

            }else
            {
                edt_amount.setError(getString(R.string.amo_req));
            }

            if (uri==null)
            {
                Toast.makeText(getActivity(), R.string.sel_trans_img, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void Book(String m_name, String m_phone, String m_amount) {

        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.bokng));
        dialog.show();


        RequestBody name_part = Common.getRequestBodyText(m_name);
        RequestBody phone_part = Common.getRequestBodyText(m_phone);
        RequestBody amount_part = Common.getRequestBodyText(m_amount);
        MultipartBody.Part image_part = Common.getMultiPart(getActivity(),uri,"transformation_image");

        Api.getService()
                .payment(myReservationModel.getId_reservation(),
                        name_part,phone_part,amount_part,image_part
                        )
                .enqueue(new Callback<ResponsModel>() {
                    @Override
                    public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();
                            if (response.body().getSuccess_transformation()==1)
                            {
                                homeActivity.ClearDataAfterTransfer();
                            }else
                                {
                                    Toast.makeText(homeActivity,R.string.something, Toast.LENGTH_SHORT).show();

                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponsModel> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        dialog.dismiss();
                        Toast.makeText(homeActivity,R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void CheckPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),read_per)!= PackageManager.PERMISSION_GRANTED)
        {
            String [] per = {read_per};
            ActivityCompat.requestPermissions(getActivity(),per,read_req);
        }else
        {
           SelectImage();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==read_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    SelectImage();

                }else
                {
                    Toast.makeText(getActivity(), R.string.per_den, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == img_req && resultCode== Activity.RESULT_OK && data!=null)
        {
            uri = data.getData();
            Bitmap bitmap = BitmapFactory.decodeFile(Common.getImagePath(getActivity(),uri));
            image.setImageBitmap(bitmap);
        }
    }

}
