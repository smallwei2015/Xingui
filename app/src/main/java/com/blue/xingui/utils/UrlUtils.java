package com.blue.xingui.utils;

/**
 * Created by cj on 2017/3/9.
 */

public class UrlUtils {

    /*项目路径*/
    public static final String BASEURL="http://202.85.214.46:8090/blueapp/frontAPI/";

    //public static final String BASEURL="http://192.168.1.12:8090/blueapp/frontAPI/";

    //public static final String BASEURL="http://longyan.blueapp.com.cn:8080/dwtt/frontAPI/";


    public static final String NEWS=BASEURL+"achieveChannelData";

    public static final String CHANEL_STRUCTURE=BASEURL+"achieveChannelData?channelId=%s&page=%s";

    public static final String CHANNEL_LIST=BASEURL+"achieveNewsList";
    /*获取token*/
    public static final String TOKEN=BASEURL+"achieveApplicationToken";
    /*登录接口*/
    public static final String LOGIN=BASEURL+"login";
    /*注册接口*/
    public static final String REGISTER=BASEURL+"regiWithText";
    /*验证码*/
    public static final String VERIFY=BASEURL+"achieveSMSCode";

    public static final String VALIDATE_VERIFY=BASEURL+"validateSMSCode";

    /*上传头像*/
    public static final String UPDATE_HEAD=BASEURL+"updatePersonalHeadIcon";
    /*修改用户名*/
    public static final String UPDATE_NAME=BASEURL+"updatePersonalNickName";
    /*修改性别*/
    public static final String UPDATE_GENDER=BASEURL+"updatePersonalSex?arg1=%s&appuserId=%s";
    /*修改手机号*/
    public static final String UPDATE_PHONE=BASEURL+"updatePersonalPhone";


    /*获取附近酒店*/

    public static final String NEARBYHOTEL=BASEURL+"achieveNearbyHotel";

    /*上传酒桶信息*/
    public static final String UPLOADBARREL=BASEURL+"uploadCaskInfo";


    /**天气*/
    public final static String WEATHER="http://weather.51wnl.com/weatherinfo/GetMoreWeather?cityCode=%s&weatherType=0";
    public static final String BWEATHER="http://api.map.baidu.com/telematics/v3/weather?location=%s&output=json&ak=qQTjMuGGYt9VlOYnpE0EeqIENimTW0nS&mcode=2D:52:C3:2B:88:44:A4:1D:92:2B:43:C9:03:ED:A3:A4:B5:55:73:20;com.blue.rchina";


    public static final String  UPDATE_PASSWORD =BASEURL+"resetPassword";

    /*第三方登录接口*/
    public static final String THIRD_LOGIN=BASEURL+"thirdPartyLogin";


    public static final String READ_ARTICLE=BASEURL+"readArticle?appuserId=%s&dataId=%s";

    public static final String SHARE_ARTICLE=BASEURL+"shareArticle?appuserId=%s&dataId=%s";

    public static final String COMMENT_ARTICLE=BASEURL+"commentArticle";

    public static final String COLLECT_ARTICLE=BASEURL+"collectArticle?dataId=%s&appuserId=%s";

    public static final String CANCEL_COLLECT_ARTICLE=BASEURL+"cancelCollectArticle?dataId=%s&appuserId=%s";

    public static final String UP_ARTICLE=BASEURL+"thumbsUpArticle?dataId=%s&appuserId=%s";
    public static final String DOWN_ARTICLE=BASEURL+"thumbsDownArticle?dataId=%s&appuserId=%s";

    public static final String USER_ARTICLE_STATE=BASEURL+"getStateAboutAppuserToArticle?dataId=%s&appuserId=%s";


    /*文章id（即contentId）
  appuserId手机用户id*/
    public static final String LIST_ARTICLE_COMMENT=BASEURL+"findArticleCommentList";

    public static final String LIST_USER_COLLECT=BASEURL+"findAppuserArticleCollectList?appuserId=%s";
    public static final String LIST_USER_COMMENT=BASEURL+"findAppuserArticleCommentList";

    public static final String USER_SCORE=BASEURL+"achievePersonalScore?appuserId=%s";


    public static final String REPORT =BASEURL+"report";

    public static final String LIST_REPORT =BASEURL+"achieveReport" ;

    public static final String List_COMMENT_REPORT=BASEURL+"achieveCommentReport";
    public static final String REPORT_COMMENT = BASEURL + "commentReport";

    public static final String REPORT_HANDLE=BASEURL+"handleReport";
    public static final String FEEDBACK = BASEURL+"feedBack";


    public final  static String SHAREAPP=BASEURL+"smartchina/saomiaowangye/recommendAPP.html";

    public static final String CONTRIBUTE=BASEURL+"contribute";
    public static final String OPEN_APP = BASEURL+"openAPP";

    public static final String INVITE_FRIEND=BASEURL+"inviteFriend";


    public static final String APP_URL="http://www.bluepacific.com.cn";
    public static final String RECOMMEND = BASEURL+"fillRecommendCode";




    /*商城相关*/

    /*查看收货信息*/
    public static final String getReceiveInfo=BASEURL+"getReceiveInfo";

    public static final String addReceiveInfo=BASEURL+"addReceiveInfo";

    public static final String updateReceiveInfo=BASEURL+"updateReceiveInfo";

    public static final String setDefaultReceiveInfo=BASEURL+"setDefaultReceiveInfo";

    public static final String getGoodsInfo=BASEURL+"getGoodsInfo";

    public static final String getShopCartInfo=BASEURL+"getShopCartInfo";

    public static final String joinShopCart=BASEURL+"joinShopCart";

    public static final String emptyShopCart=BASEURL+"emptyShopCart";

    public static final String updateShopCart=BASEURL+"updateShopCart";

    public static final String getOrderId=BASEURL+"getOrderId";

    public static final String pay=BASEURL+"pay";

    public static final String getOrderInfoByAppuser=BASEURL+"getOrderInfoByAppuser";

    public static final String getCouponInfoByAppuser=BASEURL+"getCouponInfoByAppuser";

    public static final String achieveCaskInfoOfAgent=BASEURL+"achieveCaskInfoOfAgent";

    public static final String notifyTheMasterOfBeer=BASEURL+"notifyTheMasterOfBeer";

    public static final String achieveCaskInfoOfWineMaster=BASEURL+"achieveCaskInfoOfWineMaster";

    public static final String perfectPersonalInfo=BASEURL+"perfectPersonalInfo";

    public static final String achieveNoticeList=BASEURL+"achieveNoticeList";

    public static final String getCouponInfoByRedeemCode=BASEURL+"getCouponInfoByRedeemCode";
    public static final String updateCouponState=BASEURL+"updateCouponState";
}
