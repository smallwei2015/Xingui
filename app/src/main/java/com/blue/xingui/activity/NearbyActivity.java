package com.blue.xingui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.Barrel;
import com.blue.xingui.bean.Hotel;
import com.blue.xingui.bean.NetData;
import com.blue.xingui.manager.xUtilsManager;
import com.blue.xingui.utils.AmapUtil;
import com.blue.xingui.utils.FileUtils;
import com.blue.xingui.utils.SPUtils;
import com.blue.xingui.utils.ThirdMapUtils;
import com.blue.xingui.utils.UIUtils;
import com.blue.xingui.utils.UrlUtils;
import com.blue.xingui.view.ptr.BasePopUpWindow;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class NearbyActivity extends BaseActivity implements AMapLocationListener {


    private MapView map;
    private AMap aMap;
    private List<Marker> markers;
    private MyLocationStyle myLocationStyle;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private LocationSource.OnLocationChangedListener mListener;
    private UiSettings uiSettings;
    private AMapLocation aMapLocation;
    private boolean isFirstOpenMap = true;
    private GeocodeSearch geocoderSearch;
    public CountDownTimer timer;
    public PopupWindow popupWindow;
    public RelativeLayout parent;
    public TextView posText;
    public SharedPreferences sp;

    @Override
    public void initView() {

        aMap = map.getMap();

        initPos();
        initUserSetting();

        /*for (int i = 0; i < barrels.size(); i++) {
            Barrel barrel = barrels.get(i);
            addMarks(barrel);
        }*/

        // 定义 Marker 点击事件监听
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                //ToastUtils.show(mActivity,marker.getTitle());
                //lightoff();
                showPop(marker);
                /*用来接受点击事件，确认是否再被其他的view处理*/
                return true;
            }
        };
        // 绑定 Marker 被点击事件
        aMap.setOnMarkerClickListener(markerClickListener);

    }

    private void showPop(final Marker marker) {

        final Hotel hotel = (Hotel) marker.getObject();
        popupWindow = new BasePopUpWindow(this);

        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);

        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_pop_nearby, null);

        inflate.findViewById(R.id.pop_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();


                /*Intent intent=new Intent(mActivity, NavigationActivity.class);
                intent.putExtra("marker",100);
                startActivity(intent);*/

                LatLng position = marker.getPosition();

                if (position != null) {

                    double[] des = {hotel.getLat(), hotel.getLon()};
                    if (ThirdMapUtils.isAvilible(mActivity, "com.autonavi.minimap")) {
                        ThirdMapUtils.toGaode(mActivity, des);
                    } else if (ThirdMapUtils.isAvilible(mActivity, "com.baidu.BaiduMap")) {
                        ThirdMapUtils.toBaidu(mActivity, des);
                    } else if (ThirdMapUtils.isAvilible(mActivity, "com.google.android.apps.maps")) {
                        ThirdMapUtils.toGoogle(mActivity, des);
                    } else {
                        if (aMapLocation != null) {
                            //ThirdMapUtils.toTenxun(mActivity, aMapLocation.getAddress(), des);

                            AmapUtil.goToTenCentNaviActivity(mActivity,"drive",aMapLocation.getAddress(),hotel.getLocation(),hotel.getLat(),hotel.getLon());
                        }
                    }

                }else {
                    UIUtils.showToast("获取位置失败");
                }
                /*AmapUtil.goToTenCentNaviActivity(mActivity,"drive","",aMapLocation.getLatitude(),aMapLocation.getLongitude(),
                        "destination",marker.getPosition().latitude,marker.getPosition().longitude);*/

            }
        });



        ((TextView) inflate.findViewById(R.id.pop_title)).setText(hotel.getTitle());

        ((TextView) inflate.findViewById(R.id.pop_content)).setText(hotel.getDesc());
        ImageView img = (ImageView) inflate.findViewById(R.id.pop_img);

        List<String> manyPic = hotel.getManyPic();
        if (manyPic != null && manyPic.size() > 0) {
            x.image().bind(img, manyPic.get(0), xUtilsManager.getRectangleImageOption());
        }


        inflate.findViewById(R.id.pop_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.setContentView(inflate);
        /*popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);*/


        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }


    @Override
    public void initData() {
        super.initData();

        sp = SPUtils.getSP();
        initGPS();
        markers = new ArrayList<>();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nearby);
        parent = ((RelativeLayout) findViewById(R.id.activity_nearby));
        posText = ((TextView) findViewById(R.id.pos));
        map = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        map.onCreate(savedInstanceState);

        initData();
        initView();

        lightoff();

    }

    private void addMarks(Hotel barrel) {

        //绘制marker
        Marker marker = aMap.addMarker(new MarkerOptions()
                .position(new LatLng(barrel.getLat(), barrel.getLon()))
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), Barrel.iconSrc)))
                .title(barrel.getTitle())
                .autoOverturnInfoWindow(true)
                .draggable(true));

        Animation animation = new AlphaAnimation(0, 1);
        long duration = 1000L;
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());

        marker.setAnimation(animation);
        marker.startAnimation();

        /*存储当前的点数据*/
        marker.setObject(barrel);

        // 绘制曲线
        /*存储当前的标记*/
        markers.add(marker);
    }

    private void initPos() {
        myLocationStyle = new MyLocationStyle();

        /*myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) ;//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);//连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
        //以下三种模式从5.1.0版本开始提供
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。
        */
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);/
        // /连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。


        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
        //myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        myLocationStyle.interval(2000);
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);
        myLocationStyle.strokeWidth(0);
        myLocationStyle.strokeColor(Color.TRANSPARENT);

        /*设置蓝点在地图中心*/
        //myLocationStyle.anchor(0.5f,0.5f);


        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);

        //设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。

        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {

                mListener = onLocationChangedListener;
                if (mlocationClient == null) {
                    //初始化定位
                    mlocationClient = new AMapLocationClient(mActivity);
                    //初始化定位参数
                    mLocationOption = new AMapLocationClientOption();

                    mLocationOption.setNeedAddress(true);
                    //设置定位回调监听
                    mlocationClient.setLocationListener(NearbyActivity.this);

                    //设置为高精度定位模式
                    mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                    //设置定位参数
                    mlocationClient.setLocationOption(mLocationOption);
                    // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
                    // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
                    // 在定位结束后，在合适的生命周期调用onDestroy()方法
                    // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
                    mlocationClient.startLocation();//启动定位
                }
            }

            @Override
            public void deactivate() {
                mListener = null;
                if (mlocationClient != null) {
                    mlocationClient.stopLocation();
                    mlocationClient.onDestroy();
                }
                mlocationClient = null;
            }
        });
        aMap.setMyLocationEnabled(true);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

    }

    /*获取当前位置*/
    private void showPos(double lan, double lon) {
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                //Log.w("3333",regeocodeResult.getRegeocodeAddress().getFormatAddress());
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });


        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(lan, lon), 200, GeocodeSearch.AMAP);

        geocoderSearch.getFromLocationAsyn(query);
    }

    private void initUserSetting() {
        uiSettings = aMap.getUiSettings();
        /*uiSettings.setZoomControlsEnabled(true);
        uiSettings.isScaleControlsEnabled();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        map.onDestroy();

        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );*/
        /*Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
        }*/

        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        map.onResume();

        isFirstOpenMap=true;
        /*if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(1000 * 5, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                isFirstOpenMap = true;
            }
        };
        timer.start();*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        map.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        map.onSaveInstanceState(outState);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点

                if (isFirstOpenMap) {
                    /*移动到中心*/
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    /*放大*/
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                    isFirstOpenMap = false;

                    getNearbyHotel(aMapLocation.getLatitude(), aMapLocation.getLongitude());


                    posText.setText(aMapLocation.getAddress());
                }
                this.aMapLocation = aMapLocation;

                showPos(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            } else {
                /*定位失败重新定位*/
                isFirstOpenMap = true;

                AlertDialog dialog=new AlertDialog.Builder(mActivity)
                        .setMessage("当前定位失败，请检查网络情况是否正常或者权限是否被强制关闭！")
                        .setPositiveButton("前往查看", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                FileUtils.getAppDetailSettingIntent(mActivity);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();


                dialog.show();

                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("3333", errText);

            }
        }
    }

    /*获取附近酒店信息*/
    private void getNearbyHotel(double latitude, double longitude) {
        RequestParams entity = new RequestParams(UrlUtils.NEARBYHOTEL);
        entity.addBodyParameter("arg1", longitude + "");
        entity.addBodyParameter("arg2", latitude + "");
        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                NetData netData = JSON.parseObject(result, NetData.class);

                if (netData.getResult() == 200) {

                    List<Hotel> hotels = JSON.parseArray(netData.getInfo(), Hotel.class);

                    if (hotels != null && hotels.size() > 0) {
                        /*清除老的标记*/
                        if (markers != null && markers.size() > 0) {
                            for (int i = 0; i < markers.size(); i++) {
                                markers.get(i).destroy();
                            }
                        }
                        markers.clear();

                        for (int i = 0; i < hotels.size(); i++) {
                            addMarks(hotels.get(i));
                        }
                        //UIUtils.showToast("附近有"+hotels.size()+"家酒店");

                    } else {
                        UIUtils.showToast("附近没有查询到酒店");
                    }
                } else {
                    UIUtils.showToast("网络请求失败");
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                UIUtils.showToast("网络连接失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    private void initGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("请打开GPS");
            dialog.setPositiveButton("确定",
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面

                        }
                    });
            dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {
            // 弹出Toast
//          Toast.makeText(TrainDetailsActivity.this, "GPS is ready",
//                  Toast.LENGTH_LONG).show();
//          // 弹出对话框
//          new AlertDialog.Builder(this).setMessage("GPS is ready")
//                  .setPositiveButton("OK", null).show();
        }
    }


    private void lightoff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.6f;
        getWindow().setAttributes(lp);
    }

    //PopupWindow消失时，使屏幕恢复正常
    private void lighton() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }


    public void btn_cpos(View view) {

        /*回到当前位置*/
        isFirstOpenMap = true;
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
