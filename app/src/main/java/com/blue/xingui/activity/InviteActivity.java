package com.blue.xingui.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.NetData;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.utils.FileUtils;
import com.blue.xingui.utils.UIUtils;
import com.blue.xingui.utils.UrlUtils;
import com.blue.xingui.view.ptr.BasePopUpWindow;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import cn.finalteam.toolsfinal.BitmapUtils;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class InviteActivity extends BaseActivity {


    public RelativeLayout parent;

    @Override
    public void initView() {
        super.initView();
        //initTop(R.mipmap.left_gray,"邀请好友",-1);
        parent = ((RelativeLayout) findViewById(R.id.activity_invite));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        x.view().inject(this);
        initView();
        initData();
    }

    public void btn_invite(View view) {

        String title="礼包疯狂派送中！";
        String content="下载安装\"活啤\"App，免费得礼包，机会难得，限时优惠哦。";
        String shareUrl="http://202.85.214.46:8090/blueapp/plug-in-ui/project/coupon/index.html?appuserId="+UserManager.getUser().getAppuserId();
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(shareUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数

        File file=new File(FileUtils.SHARE_APP);

        if (!file.exists()) {
            try {
                file.createNewFile();

                BitmapUtils.saveBitmap(BitmapFactory.decodeResource(getResources(),R.raw.qrcode),file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        oks.setImagePath(FileUtils.SHARE_APP);//确保SDcard下面存在此张图片



        ///oks.setImageUrl("http://static.firefoxchina.cn/img/201706/5_595455746bdc10.jpg");
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(shareUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(content);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(shareUrl);

        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

                if (UserManager.isLogin()) {
                    share();
                }
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });

        /*oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {

                *//*先拿到分享能力在设置为空*//*
                if (platform.getName().equalsIgnoreCase(QQ.NAME)) {
                    paramsToShare.setText(null);
                    paramsToShare.setTitle(null);
                    paramsToShare.setTitleUrl(null);
                    paramsToShare.setImagePath(FileUtils.SHARE_APP);
                } else if (platform.getName().equalsIgnoreCase(QZone.NAME)) {
                    paramsToShare.setText(null);
                    paramsToShare.setTitle(null);
                    paramsToShare.setTitleUrl(null);
                    paramsToShare.setImagePath(FileUtils.SHARE_APP);
                }else if (platform.getName().equalsIgnoreCase(Wechat.NAME)){

                *//*微信必须要设置具体的方分享类型*//*
                    paramsToShare.setShareType(Platform.SHARE_IMAGE);
                }else if (platform.getName().equals(WechatMoments.NAME)) {

                }

            }

        });*/

        // 启动分享GUI
        oks.show(this);
    }

    private void share() {
        RequestParams entity = new RequestParams(UrlUtils.INVITE_FRIEND);

        entity.addBodyParameter("appuserId", UserManager.getUser().getAppuserId() + "");
        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                NetData object = JSON.parseObject(result, NetData.class);
                if (object.getResult() == 200) {
                    UIUtils.showToast("分享成功");
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
    }

    public void btn_back(View view) {
        finish();
    }

    public void btn_tips(View view) {
        PopupWindow popupWindow=new BasePopUpWindow(mActivity);

        View inf= LayoutInflater.from(mActivity).inflate(R.layout.invite_tips,null);
        popupWindow.setContentView(inf);

        popupWindow.showAtLocation(parent, Gravity.CENTER,0,0);
    }
}
