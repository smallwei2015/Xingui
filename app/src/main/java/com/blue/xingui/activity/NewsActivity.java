package com.blue.xingui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.utils.SPUtils;
import com.blue.xingui.utils.UIUtils;

public class NewsActivity extends BaseActivity {

    public SharedPreferences sp;

    @Override
    public void initView() {
        //initTop(R.mipmap.arrow_left,"新闻",-1);

        sp = SPUtils.getSP();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*getWindow().getDecorView().setSystemUiVisibility(
                 View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );*/
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();


        long cPresstime = sp.getLong("cPresstime", -1);

        Log.w("33333",cPresstime+"="+(System.currentTimeMillis()-cPresstime)+"");
        if (cPresstime>0&&System.currentTimeMillis()-cPresstime<1000){
            finish();
        }else {
            UIUtils.showToast("再次点击退出程序");
        }
        sp.edit().putLong("cPresstime",System.currentTimeMillis()).apply();

    }
}
