package com.blue.xingui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;

public class SplashActivity extends BaseActivity {

    public ImageView banner;

    @Override
    public void initView() {

        banner = ((ImageView) findViewById(R.id.banner));

        banner.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(mActivity,Main2Activity.class);
                startActivity(intent);

                finish();
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        initView();
    }
}
