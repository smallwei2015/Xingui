package com.blue.xingui.activity;

import android.os.Bundle;

import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;

public class AppInfoActivity extends BaseActivity {

    @Override
    public void initView() {
        initTop(R.mipmap.left_gray,"应用信息",-1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        initView();
    }
}
