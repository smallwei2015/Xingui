package com.blue.xingui.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cj on 2017/6/27.
 */

public class ThirdMapUtils {


    public static void toBaidu(Context context,double[] location) {
        if (isAvilible(context, "com.baidu.BaiduMap")) {//传入指定应用包名
            try {
//              intent = Intent.getIntent("intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving®ion=西安&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                Intent intent = Intent.getIntent("intent://map/direction?" +
                        //"origin=latlng:"+"34.264642646862,108.95108518068&" +   //起点  此处不传值默认选择当前位置
                        "destination=latlng:" + location[0] + "," + location[1] + "|name:我的目的地" +        //终点
                        "&mode=driving&" +          //导航路线方式
                        "region=" +           //
                        "&src=新贵#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                context.startActivity(intent); //启动调用
            } catch (URISyntaxException e) {
                Log.e("intent", e.getMessage());
            }
        } else {//未安装
            //market为路径，id为包名
            //显示手机上所有的market商店
            Toast.makeText(context, "您尚未安装百度地图", Toast.LENGTH_LONG).show();
            Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
    }

    public static void toGaode(Context context,double[] location){
        Intent intent;
        if (isAvilible(context, "com.autonavi.minimap")) {
            try{
                intent = Intent.getIntent("androidamap://navi?sourceApplication=新贵&poiname=我的目的地&lat="+location[0]+"&lon="+location[1]+"&dev=0");
                context.startActivity(intent);
            } catch (URISyntaxException e)
            {e.printStackTrace(); }
        }else{
            Toast.makeText(context, "您尚未安装高德地图", Toast.LENGTH_LONG).show();
            Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
            intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
    }

    public static void toTenxun(Context context,String pos,double[] location){

        String url = "http://apis.map.qq.com/uri/v1/routeplan?type=drive&from="+pos+"&fromcoord="
                + "&to=&tocoord=" + location[0] + "," + location[1] + "&policy=0&referer=myapp";

        Intent intentOther = new Intent("android.intent.mall_action.VIEW", Uri.parse(url));
        context.startActivity(intentOther);

    }

    public static void toGoogle(Context context,double[] location){
        if (isAvilible(context,"com.google.android.apps.maps")) {
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+location[0]+","+location[1] +", + Sydney +Australia");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        }else {
            Toast.makeText(context, "您尚未安装谷歌地图", Toast.LENGTH_LONG).show();

            Uri uri = Uri.parse("market://details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);  }
    }

    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }
}
