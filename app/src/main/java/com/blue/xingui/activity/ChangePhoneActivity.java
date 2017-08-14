package com.blue.xingui.activity;

import android.os.Bundle;

import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;

public class ChangePhoneActivity extends BaseActivity {


    @Override
    public void initView() {
        initTop(R.mipmap.left_gray,"修改密码",-1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
    }
}
