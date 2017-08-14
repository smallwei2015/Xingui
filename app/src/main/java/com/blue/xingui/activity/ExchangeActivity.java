package com.blue.xingui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class ExchangeActivity extends BaseActivity {
    @ViewInject(R.id.user)
    TextView user;

    @ViewInject(R.id.number)
    EditText number;

    @Override
    public void initView() {
        initTop(R.mipmap.left_gray,"礼包兑换",-1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        x.view().inject(this);
        initView();

    }

    public void btn_exchange(View view) {
        ProgressDialog dialog=new ProgressDialog(mActivity);
        dialog.setMessage("加载中...");

        dialog.show();
    }
}
