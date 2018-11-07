package com.semicolon.salonat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.semicolon.salonat.R;
import com.semicolon.salonat.preference.Preferences;
import com.semicolon.salonat.tags.Tags;

public class SplashActivity extends AppCompatActivity {

    private ImageView image;
    private Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferences = Preferences.getInstance();
        image = findViewById(R.id.image);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.fade);
        image.clearAnimation();
        image.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                String session = preferences.getSession(SplashActivity.this);

                if (session.equals(Tags.session_login))
                {
                    Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else
                {
                    Intent intent = new Intent(SplashActivity.this,SignInActivity.class);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}
