package com.blue.xingui.activity;

import android.os.Bundle;

import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.NoticeInfo;

public class InfoDetailActivity extends BaseActivity {

    public NoticeInfo notice;

    @Override
    public void initView() {
        super.initView();
        initTop(R.mipmap.left_gray,"消息详情",-1);
    }

    @Override
    public void initData() {
        super.initData();

        notice = ((NoticeInfo) getIntent().getSerializableExtra("data"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_detail);


        initView();
        initData();
    }
}
