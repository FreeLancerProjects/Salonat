package com.semicolon.salonat.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.semicolon.salonat.models.ResponsModel;
import com.semicolon.salonat.remote.Api;
import com.semicolon.salonat.share.Common;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {
    private ImageView image_back;
    private EditText edt_email;
    private Button btn_send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initView();
    }

    private void initView() {
        image_back = findViewById(R.id.image_back);
        edt_email = findViewById(R.id.edt_email);
        btn_send = findViewById(R.id.btn_send);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });
    }

    private void CheckData() {
        String m_email = edt_email.getText().toString();

        if (TextUtils.isEmpty(m_email))
        {
            edt_email.setError(getString(R.string.email_req));
        }else if (!Patterns.EMAIL_ADDRESS.matcher(m_email).matches())
        {
            edt_email.setError(getString(R.string.inv_email));

        }else
            {
                edt_email.setError(null);
                Common.CloseKeyBoard(this,edt_email);

                ResetPassword(m_email);
            }
    }

    private void ResetPassword(String m_email) {
        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.sending));
        dialog.show();
        Api.getService()
                .resetPassword(m_email)
                .enqueue(new Callback<ResponsModel>() {
                    @Override
                    public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();

                            if (response.body().getSuccess_rest()==1)
                            {
                                CreateAlertDialog();
                            }else {
                                Toast.makeText(ForgetPasswordActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponsModel> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        dialog.dismiss();
                        Toast.makeText(ForgetPasswordActivity.this,R.string.something, Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void CreateAlertDialog()
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .create();
        View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog,null);
        TextView tv_msg = view.findViewById(R.id.tv_msg);
        Button done_btn = view.findViewById(R.id.doneBtn);
        tv_msg.setText(getString(R.string.recov_password_txt));
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });

        alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog;
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setView(view);
        alertDialog.show();
    }
}
