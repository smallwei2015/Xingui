package com.blue.xingui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.blue.xingui.activity.InfoActivity;
import com.blue.xingui.activity.LoginActivity;
import com.blue.xingui.activity.MallActivity;
import com.blue.xingui.activity.NearbyActivity;
import com.blue.xingui.activity.NewsActivity;
import com.blue.xingui.activity.UserCenterActivity;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.NetData;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.manager.xUtilsManager;
import com.blue.xingui.utils.FileUtils;
import com.blue.xingui.utils.SPUtils;
import com.blue.xingui.utils.UIDUtils;
import com.blue.xingui.utils.UrlUtils;
import com.caption.update.UpdateUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.finalteam.toolsfinal.BitmapUtils;

import static com.blue.xingui.manager.UserManager.action_in;
import static com.blue.xingui.manager.UserManager.action_out;

public class MainActivity extends BaseActivity {

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {

                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.getLatitude();//获取纬度
                    amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    amapLocation.getCountry();//国家信息
                    amapLocation.getProvince();//省信息
                    amapLocation.getCity();//城市信息
                    amapLocation.getDistrict();//城区信息
                    amapLocation.getStreet();//街道信息
                    amapLocation.getStreetNum();//街道门牌号信息
                    amapLocation.getCityCode();//城市编码
                    amapLocation.getAdCode();//地区编码
                    amapLocation.getAoiName();//获取当前定位点的AOI信息
                    amapLocation.getBuildingId();//获取当前室内定位的建筑物Id
                    amapLocation.getFloor();//获取当前室内定位的楼层
                    //获取GPS的当前状态
                    amapLocation.getGpsAccuracyStatus();
                    //获取定位时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    df.format(date);


                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("3333","location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };

    @ViewInject(R.id.left_icon)
    ImageView icon;

    @Override
    public void initView() {
        initTop(-1,getResources().getString(R.string.app_name),R.mipmap.message2);
        setLeftIcon(R.drawable.circle_gray);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Fade());//new Slide()  new Fade()
        }

        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        x.view().inject(this);

        registerUserReceiver();

        initView();
        locate();
        openApp();

        /*360genxing*/
        UpdateUtils.getInit().update(this, 1);


        /*Intent intent=new Intent(mActivity, Main2Activity.class);
        startActivity(intent);*/
    }

    private void openApp() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);

            RequestParams entity = new RequestParams(UrlUtils.OPEN_APP);
            if (UserManager.isLogin()) {
                entity.addBodyParameter("appuserId", UserManager.getUser().getAppuserId()+"");
            }else {
                entity.addBodyParameter("appuserId","");
            }
            entity.addBodyParameter("arg1", UIDUtils.getUID());
            entity.addBodyParameter("arg2","2");
            entity.addBodyParameter("arg3",info.versionName+"");

            boolean openApp = SPUtils.getCacheSp().getBoolean("openApp", false);
            if (!openApp) {
                entity.addBodyParameter("arg4", "1");
                SPUtils.getCacheSp().edit().putBoolean("openApp",true).apply();
            }else {
                entity.addBodyParameter("arg4","0");
            }
            x.http().post(entity, new Callback.CommonCallback<String>() {


                @Override
                public void onSuccess(String result) {

                    NetData netData = JSON.parseObject(result, NetData.class);

                    if (netData.getResult()==200){
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

    @Override
    protected void onResume() {
        super.onResume();

        if (UserManager.isLogin()) {
            x.image().bind(icon, UserManager.getUser().getHeadIcon(), xUtilsManager.getCircleImageOption(), new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable result) {
                    Bitmap bitmap = BitmapUtils.drawableToBitmap(result);

                    File file=new File(FileUtils.USERICON);
                    if (!file.exists()){
                        file.mkdirs();
                    }
                    BitmapUtils.saveBitmap(bitmap, file);
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
        }else {
            icon.setImageResource(R.mipmap.head);

        }

    }

    private void locate() {

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption=new AMapLocationClientOption();

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
        //mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置定位模式为AMapLocationMode.Device_Sensors，仅设备模式。
        //mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);

        //获取一次定位结果：
        //该方法默认为false。
        //mLocationOption.setOnceLocation(true);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
        // 如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);

        /*多次定位间隔*/
        mLocationOption.setInterval(2000);

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否强制刷新WIFI，默认为true，强制刷新。
        mLocationOption.setWifiActiveScan(false);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);


        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
    }

    @Override
    public void onLeftIconClick(View view) {
        //super.onLeftClick(view);

        if (!UserManager.isLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else {
            Intent intent=new Intent(this,UserCenterActivity.class);
            ActivityOptions options=
                    null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                options = ActivityOptions.makeSceneTransitionAnimation(this,icon,"usericon");
                startActivity(intent,options.toBundle());
            }else {
                startActivity(intent);
            }
        }

    }

    @Override
    public void onRightClick(View view) {
        super.onRightClick(view);

        if (UserManager.isLogin()) {
            Intent intent = new Intent(mActivity, InfoActivity.class);
            startActivity(intent);
        }else {
            Intent intent=new Intent(mActivity,LoginActivity.class);
            startActivity(intent);
        }

    }


    @Override
    public void onUserReciver(Intent intent) {
        super.onUserReciver(intent);

        if (intent.getAction().equalsIgnoreCase(action_in)) {
            if (UserManager.isLogin()) {
                x.image().bind(icon, UserManager.getUser().getHeadIcon(), xUtilsManager.getCircleImageOption());
            }
        }else if (intent.getAction().equalsIgnoreCase(action_out)){
            x.image().bind(icon, null, xUtilsManager.getCircleImageOption());
        }
    }

    public void btn_map(View view) {
        Intent intent=new Intent(this, NearbyActivity.class);
        startActivity(intent);

    }

    public void btn_news(View view) {
        Intent intent=new Intent(this, NewsActivity.class);
        startActivity(intent);
    }

    public void btn_mall(View view) {
        Intent intent=new Intent(this, MallActivity.class);
        startActivity(intent);
    }

    public void btn_mine(View view) {
        Intent intent=new Intent(this,UserCenterActivity.class);
        ActivityOptions options=
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(this,icon,"usericon");
            startActivity(intent,options.toBundle());
        }else {
            startActivity(intent);
        }
    }



}
