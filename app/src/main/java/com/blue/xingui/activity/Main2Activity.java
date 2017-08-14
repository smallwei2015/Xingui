package com.blue.xingui.activity;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blue.xingui.R;
import com.blue.xingui.bean.NetData;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.utils.FileUtils;
import com.blue.xingui.utils.SPUtils;
import com.blue.xingui.utils.ToastUtils;
import com.blue.xingui.utils.UIDUtils;
import com.blue.xingui.utils.UrlUtils;
import com.caption.update.UpdateUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;

import cn.finalteam.toolsfinal.BitmapUtils;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Main2Activity extends TabActivity implements TabHost.OnTabChangeListener {
    private static final int TAB_INDEX_DIALER = 0;
    private static final int TAB_INDEX_CALL_LOG = 1;
    private static final int TAB_INDEX_CONTACTS = 2;
    private static final int TAB_INDEX_FAVORITES = 3;

    private TabHost mTabHost;
    public Intent intent;
    public SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = SPUtils.getSP();
        //intent = getIntent();

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main2);

        mTabHost = getTabHost();
        mTabHost.setOnTabChangedListener(this);

        // Setup the tabs
        setupCallLogTab();
        setupDialerTab();
        setupContactsTab();
        //setupFavoritesTab();
        setupMineTab();

        //setCurrentTab(intent);

        /*确定权限是有的*/
        permissionRequest();

        openApp();
        /*360genxing*/
        UpdateUtils.getInit().update(this, 1);


        File file = new File(FileUtils.SHARE_APP);


        try {
            file.delete();

            file.createNewFile();
            BitmapUtils.saveBitmap(BitmapFactory.decodeResource(getResources(), R.raw.logo), file);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void openApp() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);

            RequestParams entity = new RequestParams(UrlUtils.OPEN_APP);
            if (UserManager.isLogin()) {
                entity.addBodyParameter("appuserId", UserManager.getUser().getAppuserId() + "");
            } else {
                entity.addBodyParameter("appuserId", "");
            }
            entity.addBodyParameter("arg1", UIDUtils.getUID());
            entity.addBodyParameter("arg2", "2");
            entity.addBodyParameter("arg3", info.versionName + "");

            boolean openApp = SPUtils.getCacheSp().getBoolean("openApp", false);
            if (!openApp) {
                entity.addBodyParameter("arg4", "1");
                SPUtils.getCacheSp().edit().putBoolean("openApp", true).apply();
            } else {
                entity.addBodyParameter("arg4", "0");
            }
            x.http().post(entity, new Callback.CommonCallback<String>() {


                @Override
                public void onSuccess(String result) {

                    NetData netData = JSON.parseObject(result, NetData.class);

                    if (netData.getResult() == 200) {
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onTabChanged(String tabId) {
        Activity activity = getLocalActivityManager().getActivity(tabId);
        if (activity != null) {
            activity.onWindowFocusChanged(true);
        }

        /*TabWidget tabWidget = getTabWidget();
        View inflate = tabWidget.getChildTabViewAt(0);
        ((ImageView) inflate.findViewById(R.id.img)).setSelected(true);
        ((TextView) inflate.findViewById(R.id.text)).setSelected(true);*/
    }

    private void setupMineTab() {
        Intent intent = new Intent(this, UserCenterActivity.class);

        TabHost.TabSpec mine = mTabHost.newTabSpec("mine");
        View inflate = LayoutInflater.from(this).inflate(R.layout.tab_mine, null);
        ((ImageView) inflate.findViewById(R.id.img)).setImageResource(R.drawable.mine);
        ((TextView) inflate.findViewById(R.id.text)).setText("我的");

        mine.setIndicator(inflate);
        mTabHost.addTab(mine.setContent(intent));
    }


    private void setupCallLogTab() {
        // Force the class since overriding tab entries doesn't work
        Intent intent = new Intent(this, MallActivity.class);
        TabHost.TabSpec mall = mTabHost.newTabSpec("mall");
        View inflate = LayoutInflater.from(this).inflate(R.layout.tab_mine, null);
        ((ImageView) inflate.findViewById(R.id.img)).setImageResource(R.drawable.mall);
        ((TextView) inflate.findViewById(R.id.text)).setText("商城");

        mall.setIndicator(inflate);

        mTabHost.addTab(mall.setContent(intent));
    }

    private void setupDialerTab() {
        Intent intent = new Intent(this, NewsActivity.class);

        TabHost.TabSpec news = mTabHost.newTabSpec("news");
        View inflate = LayoutInflater.from(this).inflate(R.layout.tab_mine, null);
        ((ImageView) inflate.findViewById(R.id.img)).setImageResource(R.drawable.news);
        ((TextView) inflate.findViewById(R.id.text)).setText("新闻");

        news.setIndicator(inflate);
        mTabHost.addTab(news.setContent(intent));
    }

    private void setupContactsTab() {
        Intent intent = new Intent(this, NearbyActivity.class);

        TabHost.TabSpec near = mTabHost.newTabSpec("near");

        View inflate = LayoutInflater.from(this).inflate(R.layout.tab_mine, null);
        ((ImageView) inflate.findViewById(R.id.img)).setImageResource(R.drawable.near);
        ((TextView) inflate.findViewById(R.id.text)).setText("附近");

        near.setIndicator(inflate);
        mTabHost.addTab(near.setContent(intent));
    }

    private void setupFavoritesTab() {
        Intent intent = new Intent(this, InfoActivity.class);

        TabHost.TabSpec message = mTabHost.newTabSpec("message");
        View inflate = LayoutInflater.from(this).inflate(R.layout.tab_mine, null);
        ((ImageView) inflate.findViewById(R.id.img)).setImageResource(R.drawable.message);
        ((TextView) inflate.findViewById(R.id.text)).setText("消息");

        message.setIndicator(inflate);
        mTabHost.addTab(message.setContent(intent));
    }


    private void setCurrentTab(Intent intent) {
        Activity activity = getLocalActivityManager().
                getActivity(mTabHost.getCurrentTabTag());
        if (activity != null) {
            activity.closeOptionsMenu();
        }

        intent.putExtra("", true);

        String componentName = intent.getComponent().getClassName();
        if (getClass().getName().equals(componentName)) {
            if (false) {
                mTabHost.setCurrentTab(TAB_INDEX_DIALER);
            } else if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
                mTabHost.setCurrentTab(TAB_INDEX_DIALER);
            } else if (true) {
                mTabHost.setCurrentTab(TAB_INDEX_DIALER);
            }
        }
    }


    @Override

    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {

        switch (permsRequestCode) {

            case 200:
                boolean hasRight = true;
                for (int i = 0; i < grantResults.length; i++) {
                    hasRight = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                }
                if (hasRight) {
                    //授权成功之后，调用系统相机进行拍照操作等
                    //locate();

                    //UIUtils.showToast("授权成功");
                } else {
                    //用户授权拒绝之后，友情提示一下就可以了
                    ToastUtils.show(this, "拒绝该权限后系统将不能正常使用");
                }
                break;

        }

    }

    @Override
    public boolean shouldShowRequestPermissionRationale(String permission) {
        return super.shouldShowRequestPermissionRationale(permission);
    }

    public void permissionRequest() {

        if (!(hasPermission(ACCESS_COARSE_LOCATION) && hasPermission(WRITE_EXTERNAL_STORAGE))) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                Log.w("33333", "申请权限");
                requestPermissions(new String[]{ACCESS_COARSE_LOCATION, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, 200);
            }
        } else {
            Log.w("3333", "权限都具有");
        }
    }

    private boolean hasPermission(String permission) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }
        return false;

    }


}
