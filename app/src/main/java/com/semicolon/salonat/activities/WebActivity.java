package com.semicolon.salonat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.semicolon.salonat.R;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class WebActivity extends AppCompatActivity {
    private SmoothProgressBar smoothProgressBar;
    private WebView webView;
    private String url="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        getDataFromIntent();
        initView();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null)
        {
            url = intent.getStringExtra("url");
        }
    }

    private void initView() {
        smoothProgressBar = findViewById(R.id.smoothProgress);
        webView = findViewById(R.id.webView);
        webView.loadUrl(url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                smoothProgressBar.setVisibility(View.GONE);
            }
        });
    }
}
