package com.semicolon.salonat.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lamudi.phonefield.PhoneInputLayout;
import com.semicolon.salonat.R;
import com.semicolon.salonat.models.UserModel;
import com.semicolon.salonat.preference.Preferences;
import com.semicolon.salonat.remote.Api;
import com.semicolon.salonat.share.Common;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private EditText edt_phone,edt_password;
    private TextView tv_forget_password;
    private Button btn_signin,btn_signup,btn_skip;
    private PhoneInputLayout edt_check_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initView();
    }

    private void initView() {
        edt_phone = findViewById(R.id.edt_phone);
        edt_password = findViewById(R.id.edt_password);
        tv_forget_password = findViewById(R.id.tv_forget_password);
        edt_check_phone = findViewById(R.id.edt_check_phone);
        btn_signin = findViewById(R.id.btn_signin);
        btn_signup = findViewById(R.id.btn_signup);
        btn_skip = findViewById(R.id.btn_skip);

        tv_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });

    }

    private void CheckData() {
        String m_phone = edt_phone.getText().toString();
        String m_password = edt_password.getText().toString();
        if (!TextUtils.isEmpty(m_phone))
        {
            if (!m_phone.startsWith("+"))
            {
                m_phone ="+"+m_phone;
            }
        }
        edt_check_phone.setPhoneNumber(m_phone);

        if (!TextUtils.isEmpty(m_phone)&&edt_check_phone.isValid()&&!TextUtils.isEmpty(m_password))
        {
            edt_phone.setError(null);
            edt_password.setError(null);
            Common.CloseKeyBoard(this,edt_phone);
            SignIn(m_phone,m_password);
        }else
            {
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

                if (TextUtils.isEmpty(m_password))
                {
                    edt_password.setError(getString(R.string.pass_req));
                }else
                {
                    edt_password.setError(null);

                }
            }
    }

    private void SignIn(String m_phone, String m_password) {
        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.loging));
        dialog.show();
        Api.getService()
                .SignIn(m_phone,m_password)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();
                            if (response.body().getSuccess_login()==1)
                            {
                                Preferences preferences = Preferences.getInstance();
                                preferences.create_update_userData(SignInActivity.this,response.body());

                                Intent intent = new Intent(SignInActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }else if (response.body().getSuccess_login()==0)
                                {
                                    Toast.makeText(SignInActivity.this, R.string.ph_pas_incorr, Toast.LENGTH_SHORT).show();
                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        Toast.makeText(SignInActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

    }
}
