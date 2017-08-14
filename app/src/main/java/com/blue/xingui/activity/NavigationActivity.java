package com.blue.xingui.activity;

import android.os.Bundle;
import android.view.View;

import com.amap.api.navi.AMapHudView;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.model.NaviLatLng;
import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends BaseActivity {


    //算路终点坐标
    protected NaviLatLng mEndLatlng = new NaviLatLng(22.652, 113.966);
    //算路起点坐标
    protected NaviLatLng mStartLatlng = new NaviLatLng(22.540332, 113.939961);
    //存储算路起点的列表
    protected final List<NaviLatLng> sList = new ArrayList<NaviLatLng>();
    //存储算路终点的列表
    protected final List<NaviLatLng> eList = new ArrayList<NaviLatLng>();
    private AMapNavi mAMapNavi;
    private AMapNaviView mAMapNaviView;
    private List<NaviLatLng> mWayPointList = new ArrayList<>();
    private AMapHudView mAMapHudView;


    @Override

    public void initData() {
        super.initData();
        int marker = getIntent().getIntExtra("marker", -1);

    }

    @Override
    public void initView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);


        //获取 AMapNaviView 实例
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.setAMapNaviViewListener(new AMapNaviViewListener() {
            @Override
            public void onNaviSetting() {

            }

            @Override
            public void onNaviCancel() {

            }

            @Override
            public boolean onNaviBackClick() {
                return false;
            }

            @Override
            public void onNaviMapMode(int i) {

            }

            @Override
            public void onNaviTurnClick() {

            }

            @Override
            public void onNextRoadClick() {

            }

            @Override
            public void onScanViewButtonClick() {

            }

            @Override
            public void onLockMap(boolean b) {

            }

            @Override
            public void onNaviViewLoaded() {

            }
        });

        mAMapHudView=((AMapHudView) findViewById(R.id.hudview));

        //获取AMapNavi实例
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        //添加监听回调，用于处理算路成功
        /*mAMapNavi.addAMapNaviListener(new AMapNaviListener() {

*//*onCalculateRouteSuccess(int[])*//*
            @Override
            public void onInitNaviFailure() {

            }

            @Override
            public void onInitNaviSuccess() {
                *//**
                 * 方法:
                 *   int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute);
                 * 参数:
                 * @congestion 躲避拥堵
                 * @avoidhightspeed 不走高速
                 * @cost 避免收费
                 * @hightspeed 高速优先
                 * @multipleroute 多路径
                 *
                 * 说明:
                 *      以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
                 * 注意:
                 *      不走高速与高速优先不能同时为true
                 *      高速优先与避免收费不能同时为true
                 *//*
                int strategy = 0;
                try {
                    strategy = mAMapNavi.strategyConvert(true, false, false, false, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);
                mAMapNavi.calculateDriveRoute(sList,eList,strategy);
            }

            @Override
            public void onStartNavi(int i) {

            }

            @Override
            public void onTrafficStatusUpdate() {

            }

            @Override
            public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

            }

            @Override
            public void onGetNavigationText(int i, String s) {

            }

            @Override
            public void onEndEmulatorNavi() {

            }

            @Override
            public void onArriveDestination() {

            }

            @Override
            public void onCalculateRouteSuccess() {
                mAMapNavi.startNavi(NaviType.EMULATOR);
            }

            @Override
            public void onCalculateRouteFailure(int i) {

            }

            @Override
            public void onReCalculateRouteForYaw() {

            }

            @Override
            public void onReCalculateRouteForTrafficJam() {

            }

            @Override
            public void onArrivedWayPoint(int i) {

            }

            @Override
            public void onGpsOpenStatus(boolean b) {

            }

            @Override
            public void onNaviInfoUpdate(NaviInfo naviInfo) {

            }

            @Override
            public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

            }

            @Override
            public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

            }

            @Override
            public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

            }

            @Override
            public void showCross(AMapNaviCross aMapNaviCross) {

            }

            @Override
            public void hideCross() {

            }

            @Override
            public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

            }

            @Override
            public void hideLaneInfo() {

            }

            @Override
            public void onCalculateMultipleRoutesSuccess(int[] ints) {
                mAMapNavi.startNavi(NaviType.GPS);
            }

            @Override
            public void notifyParallelRoad(int i) {

            }

            @Override
            public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

            }

            @Override
            public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

            }

            @Override
            public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

            }

            @Override
            public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

            }

            @Override
            public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

            }

            @Override
            public void onPlayRing(int i) {

            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        mAMapNaviView.onResume();
        mAMapHudView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
        mAMapHudView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
        mAMapHudView.onDestroy();
    }

}
