package com.blue.xingui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.utils.SPUtils;

public class SizeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.AppTheme_large);
        setContentView(R.layout.activity_size);


        boolean sizeType = SPUtils.getSP().edit().putInt("sizeType", 1).commit();


        int sizeType1 = SPUtils.getSP().getInt("sizeType", 0);
        Log.w("3333",sizeType+""+sizeType1);
    }

    public void btn_size(View view) {

        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = 1.2f;    //1为标准字体，multiple为放大的倍数
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        displayMetrics.scaledDensity = configuration.fontScale * displayMetrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, displayMetrics);

    }
}
