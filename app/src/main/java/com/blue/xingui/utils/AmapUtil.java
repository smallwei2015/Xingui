package com.blue.xingui.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import java.io.File;
import java.net.URISyntaxException;

/**
 * Created by Luyanli on 2016/11/16.
 * 使用第三方导航：高德、百度。。。。。。。。。。
 */
public class AmapUtil {
  /**
   * 启动高德App进行导航
   * <h3>Version</h3> 1.0
   * <h3>CreateTime</h3> 2016/6/27,13:58
   * <h3>UpdateTime</h3> 2016/6/27,13:58
   * <h3>CreateAuthor</h3>
   * <h3>UpdateAuthor</h3>
   * <h3>UpdateInfo</h3> (此处输入修改内容,若无修改可不写.)
   *
   * @param sourceApplication 必填 第三方调用应用名称。如 amap
   * @param poiname 非必填 POI 名称
   * @param lat 必填 纬度
   * @param lon 必填 经度
   * @param dev 必填 是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
   * @param style 必填 导航方式(0 速度快; 1 费用少; 2 路程短; 3 不走高速；4 躲避拥堵；5 不走高速且避免收费；6 不走高速且躲避拥堵；
   * 7 躲避收费和拥堵；8不走高速躲避收费和拥堵))
   */
  /** 高德导航 **/
  public static void goToGaodeNaviActivity(Context context, String sourceApplication,
      String poiname, String lat, String lon, String dev, String style) {
    StringBuffer stringBuffer =
        new StringBuffer("androidamap://navi?sourceApplication=").append(sourceApplication);
    if (!TextUtils.isEmpty(poiname)) {
      stringBuffer.append("&poiname=").append(poiname);
    }
    stringBuffer.append("&lat=")
        .append(lat)
        .append("&lon=")
        .append(lon)
        .append("&dev=")
        .append(dev)
        .append("&style=")
        .append(style);

    Intent intent =
        new Intent("android.intent.mall_action.VIEW", android.net.Uri.parse(stringBuffer.toString()));
    intent.setPackage("com.autonavi.minimap");
    context.startActivity(intent);
  }

  /**
   * @param slat 起点
   * @param dlat 终点
   * @param dev 必填 是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
   * @param t t = 1(公交) =2（驾车） =4(步行)
   * @param showType 高德导航
   * /*"androidamap://route?sourceApplication=changanchuxing"
   * + "&slat=36.2&slon=116.1&sname=abc&dlat=36.3"
   * + "&dlon=116.2&dname=def&dev=0&m=0&t=1&showType=1"
   * 高德开放平台 http://lbs.amap.com/api/uri-api/guide/android-uri-explain/route/
   */
  public static void goToGaodeNaviActivity(Context context, String sourceApplication, String slat,
      String slon, String sname, String dlat, String dlon, String dname, String dev, String m,
      String t, String showType) {
    String locationStr = "androidamap://route?sourceApplication="
        + sourceApplication
        +
        "&slat="
        + slat
        + "&slon="
        + slon
        +
        "&sname="
        + sname
        + "&dlat="
        + dlat
        + "&dlon="
        + dlon
        + "&dname="
        + dname
        + "&dev="
        + dev
        + "&m="
        + m
        + "&t="
        + t
        + "&showType="
        + showType;
    Intent intent = new Intent("android.intent.mall_action.VIEW",
        android.net.Uri.parse(/*stringBuffer.toString()*/locationStr));
    intent.setPackage("com.autonavi.minimap");
    context.startActivity(intent);
  }

  /**
   * 启动BaiduApp进行导航
   * <h3>Version</h3> 1.0
   * <h3>CreateTime</h3> 2016/6/27,11:23
   * <h3>UpdateTime</h3> 2016/6/27,11:23
   * <h3>CreateAuthor</h3> luzhenbang
   * <h3>UpdateAuthor</h3>
   * <h3>UpdateInfo</h3> (此处输入修改内容,若无修改可不写.)
   *
   * @param context 上下文
   * @param origin 必选  起点名称或经纬度，或者可同时提供名称和经纬度，此时经纬度优先级高，将作为导航依据，名称只负责展示。例如：
   * latlng:34.264642646862,108.95108518068|name:我家
   * @param destination 必选 终点名称或经纬度，或者可同时提供名称和经纬度，此时经纬度优先级高，将作为导航依据，名称只负责展示。
   * @param mode 必选 导航模式，固定为transit、driving、walking，分别表示公交、驾车和步行
   * @param region 必选 城市名或县名 当给定region时，认为起点和终点都在同一城市，除非单独给定起点或终点的城市。
   * @param origin_region 必选  起点所在城市或县
   * @param destination_region 必选  终点所在城市或县
   * @param coord_type 可选 坐标类型，可选参数，默认为bd09经纬度坐标。
   * @param zoom 可选 展现地图的级别，默认为视觉最优级别。
   * @param src 必选 调用来源，规则：companyName|appName。
   */
  /** 百度导航 */
  public static void goToBaiduNaviActivity(Context context, String origin, String destination,
      String mode, String region, String origin_region, String destination_region,
      String coord_type, String zoom, String src) {
    StringBuffer stringBuffer = new StringBuffer("intent://map/direction?origin=");
    stringBuffer.append(origin)
        .append("&destination=")
        .append(destination)
        .append("&mode=")
        .append(mode);
    if (!TextUtils.isEmpty(region)) {
      stringBuffer.append("&region=").append(region);
    }
    if (!TextUtils.isEmpty(origin_region)) {
      stringBuffer.append("&origin_region=").append(origin_region);
    }
    if (!TextUtils.isEmpty(destination_region)) {
      stringBuffer.append("&destination_region=").append(destination_region);
    }
    if (!TextUtils.isEmpty(coord_type)) {
      stringBuffer.append("&coord_type=").append(coord_type);
    }

    if (!TextUtils.isEmpty(zoom)) {
      stringBuffer.append("&zoom=").append(zoom);
    }
    stringBuffer.append("&src=")
        .append(src)
        .append("#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
    String intentString = stringBuffer.toString();
    try {
      Intent intent = Intent.getIntent(intentString);
      context.startActivity(intent);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }

  /***
   * @param originlat 起点维度
   * @param originlon 起点经度
   * @param destinationlat 终点
   * @param mode 出行方式 导航模式，固定为transit（公交）、 driving（驾车）、walking（步行）和riding（骑行）. 默认:driving
   * 具体看百度地图开放者平台官网  http://lbsyun.baidu.com/index.php?title=uri/api/android
   */
  /*// 公交路线规划
  //i1.setData(Uri.parse("baidumap://map/direction?origin=name:对外经贸大学|latlng:39.98871,116.43234&destination=name:西直门&mode=transit&sy=3&index=0&target=1"));
  // 驾车路线规划
  i1.setData(Uri.parse("baidumap://map/direction?region=beijing&origin=39.98871,116.43234&destination=name:西直门&mode=driving"));
  // 步行路线规划
  i1.setData(Uri.parse("baidumap://map/direction?region=beijing&origin=39.98871,116.43234&destination=40.057406655722,116.2964407172&mode=walking"));
  startActivity(i1);*/
  public static void goToBaiduNaviActivity(Context context, double originlat, double originlon,
      double destinationlat, double destinationlon, String mode) {
    String locationStr = "baidumap://map/direction?origin="
        + GCJ02ToBD09(originlon, originlat)[1]
        + ","
        + GCJ02ToBD09(originlon, originlat)[0]
        + "&destination="
        + GCJ02ToBD09(destinationlon, destinationlat)[1]
        + ","
        + GCJ02ToBD09(destinationlon, destinationlat)[0]
        + "&mode="
        + mode;
    Intent intent = new Intent("android.intent.mall_action.VIEW",
        android.net.Uri.parse(/*stringBuffer.toString()*/locationStr));
    intent.setPackage("com.baidu.BaiduMap");
    context.startActivity(intent);
  }

  /**
   *  file:///E:/someDownCode/IntentMapGuide-master/%E8%85%BE%E8%AE%AF%E5%9C%B0%E5%9B%BE_URI%E8%A7%84%E8%8C%83%E6%96%87%E6%A1%A3_150417/URI%E8%A7%84%E8%8C%83%E6%96%87%E6%A1%A3.html
   * qqmap://map/routeplan?type=drive&from=天坛南门&fromcoord=39.873145,116.413306&to=国家大剧院&tocoord=39.907380,116.388501
   * coord=39.904956,116.389449	lat<纬度>,lng<经度>
   * //移动端启动腾讯地图App，并显示从出发点[天坛南门] 到 [目的地坐标(国家大剧院)] 的驾车路线规划
   * @param context
   * @param type 路线规划方式参数：公交 bus   驾车 drive    步行 walk
   * @param from
   * @param fromLat
   * @param fromLon
   * @param to
   * @param toLat
   * @param toLon
   */
  public static void goToTenCentNaviActivity(Context context, String type, String from,
      /*double fromLat, double fromLon,*/ String to, double toLat, double toLon) {

    if (!isInstallByRead("com.tencent.map")) {
      String locationStr = "http://apis.map.qq.com/uri/v1/routeplan?type=" + type + "&from=" + from + "&fromcoord="
              //+ fromLat
              + ","
              //+ fromLon
              + "&to=" + to + "&tocoord=" + toLat + "," + toLon + "&policy=0&referer=新贵";

      //Intent intent=new Intent("android.intent.mall_action.VIEW", Uri.parse(locationStr));
      Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(locationStr));

      context.startActivity(intent);
    }else {


      String locationStr = "qqmap://map/routeplan?type="
              + type
              + "&from="
              + from
              + "&fromcoord="
              //+ fromLat
              + ","
              //+ fromLon
              + "&to="
              + to
              + "&tocoord="
              + toLat
              + ","
              + toLon;
      System.out.println("tencent-location:" + locationStr);
      Intent intent = new Intent("android.intent.mall_action.VIEW", android.net.Uri.parse(locationStr));
      intent.setPackage("com.tencent.map");
      context.startActivity(intent);
    }
  }

  /**
   * 根据包名检测某个APP是否安装
   * <h3>Version</h3> 1.0
   * <h3>CreateTime</h3> 2016/6/27,13:02
   * <h3>UpdateTime</h3> 2016/6/27,13:02
   * <h3>CreateAuthor</h3>
   * <h3>UpdateAuthor</h3>
   * <h3>UpdateInfo</h3> (此处输入修改内容,若无修改可不写.)
   *
   * @param packageName 包名  百度的包名为 com.baidu.BaiduMap，高德com.autonavi.minimap,腾讯
   * @return true 安装 false 没有安装
   */
  public static boolean isInstallByRead(String packageName) {
    return new File("/data/data/" + packageName).exists();
  }

  /**
   * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
   * 即谷歌、高德 转 百度
   * 腾讯和高德经纬度一样
   **经纬度转换 http://blog.csdn.net/meegomeego/article/details/39927017
   * @param gcj_lon
   * @param gcj_lat
   * @return Double[lon, lat]
   *CoordtransformUtil文件坐标转换
   */
  private static double x_PI = 3.14159265358979324 * 3000.0 / 180.0;

  public static Double[] GCJ02ToBD09(Double gcj_lon, Double gcj_lat) {
    double z =
        Math.sqrt(gcj_lon * gcj_lon + gcj_lat * gcj_lat) + 0.00002 * Math.sin(gcj_lat * x_PI);
    double theta = Math.atan2(gcj_lat, gcj_lon) + 0.000003 * Math.cos(gcj_lon * x_PI);
    Double[] arr = new Double[2];
    arr[0] = z * Math.cos(theta) + 0.0065;
    arr[1] = z * Math.sin(theta) + 0.006;
    return arr;
  }
  /**
   * 调用高德导航
   */
  /*public void aMap(View view){
    if (AMapUtil.isInstallByRead("com.autonavi.minimap")){
      AMapUtil.goToNaviActivity(this,"test",null,"34.264642646862","108.95108518068","1","2");
    }
  }*/
}
