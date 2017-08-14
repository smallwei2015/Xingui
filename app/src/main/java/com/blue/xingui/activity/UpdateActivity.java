package com.blue.xingui.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class UpdateActivity extends BaseActivity {

    @ViewInject(R.id.update_detail)
    TextView detail;

    @ViewInject(R.id.update_version)
    TextView version;

    @ViewInject(R.id.update_qrcode)
    ImageView imageView;

    private String appVersion;

    @Override
    public void initView() {
        initTop(R.mipmap.left_gray,"检测更新",-1);
    }

    public void initData() {
        // 获取当前版本号

        try {
            PackageManager manager =getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            appVersion = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version.setText("版本号：" + appVersion);

        //启动服务
        /*Intent service = new Intent(this,UpdateService.class);
        startService(service);*/
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        x.view().inject(this);
        initView();
        initData();
    }
}
