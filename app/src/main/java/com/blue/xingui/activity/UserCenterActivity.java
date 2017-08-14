package com.blue.xingui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.NetData;
import com.blue.xingui.bean.User;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.manager.UserManagerInterface;
import com.blue.xingui.manager.xUtilsManager;
import com.blue.xingui.utils.FileUtils;
import com.blue.xingui.utils.SPUtils;
import com.blue.xingui.utils.ToastUtils;
import com.blue.xingui.utils.UIUtils;
import com.blue.xingui.utils.UrlUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.blue.xingui.manager.UserManager.action_change;
import static com.blue.xingui.manager.UserManager.action_in;
import static com.blue.xingui.manager.UserManager.action_out;

public class UserCenterActivity extends BaseActivity {

    @ViewInject(R.id.usercenter_rec)
    RecyclerView rec;
    @ViewInject(R.id.usercenter_icon)
    ImageView icon;

    private List<String> datas;
    private RecyclerView.Adapter<Holder> adapter;
    public SharedPreferences sp;


    @Override
    public void initData() {
        super.initData();

        sp = SPUtils.getSP();

        datas = new ArrayList<>();

        /*datas.add("我的订单");
        datas.add("我的收藏");
        datas.add("我的优惠券");
        datas.add("邀请好友");


        *//*if (UserManager.getUser().getUserType()==0){

        }else {
            datas.add("酒桶信息");
        }*//*
        //datas.add("补充酒水");
        datas.add("系统设置");*/


    }

    @Override
    public void initView() {

        rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerView.Adapter<Holder>() {
            @Override
            public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mActivity).inflate(R.layout.usercenter_item, parent, false);

                return new Holder(view);
            }

            @Override
            public void onBindViewHolder(Holder holder, final int position) {
                final String s = datas.get(position);

                holder.text.setText(s);

                if (s.equals("我的订单")){
                    holder.img.setImageResource(R.mipmap.order);
                }else if (s.equals("我的收藏")){
                    holder.img.setImageResource(R.mipmap.collect);
                }else if (s.equals("我的优惠券")){
                    holder.img.setImageResource(R.mipmap.coupon_icon);
                }else if (s.equals("邀请好友")){
                    holder.img.setImageResource(R.mipmap.invite);
                }else if (s.equals("验证优惠券")){
                    holder.img.setImageResource(R.mipmap.validate);
                }else if (s.equals("酒桶信息")){
                    holder.img.setImageResource(R.mipmap.barrel);
                }else if (s.equals("系统设置")){
                    holder.img.setImageResource(R.mipmap.setting_icon);
                }

                if (position==datas.size()-1){
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.parent.getLayoutParams();
                    layoutParams.setMargins(0, (int) getResources().getDimension(R.dimen.dp20),0,0);
                }else {
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.parent.getLayoutParams();
                    layoutParams.setMargins(0,
                            (int) getResources().getDimension(R.dimen.dp1),0,0);
                }
                holder.parent.setTag(s);
                holder.parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = (String) v.getTag();
                        if (s.equals("我的订单")){
                            order();
                        }else if (s.equals("我的收藏")){
                            collect();
                        }else if (s.equals("我的优惠券")){
                            coupon();
                        }else if (s.equals("邀请好友")){
                            invite();
                        }else if (s.equals("验证优惠券")){
                            validate();
                        }else if (s.equals("酒桶信息")){
                            barrel();
                        }else if (s.equals("系统设置")){
                            setting();
                        }

                    }
                });

            }

            @Override
            public int getItemCount() {
                if (datas != null) {
                    return datas.size();
                }
                return 0;
            }
        };
        rec.setAdapter(adapter);


        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserManager.isLogin()) {
                    Intent intent = new Intent(mActivity, SettingActivity.class);

                    /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions options  = ActivityOptions.makeSceneTransitionAnimation(mActivity, icon, "usericon");

                        startActivity(intent, options.toBundle());
                    }else {*/
                    startActivity(intent);


                } else {
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void validate() {
        Intent intent=new Intent(mActivity,ValidateActivity.class);
        startActivity(intent);
    }

    private void setting() {
        Intent intent=new Intent(mActivity,InfoSettingActivity.class);
        startActivity(intent);
    }

    private void collect() {
        if (UserManager.isLogin()) {
            Intent intent = new Intent(mActivity, CollectActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(mActivity, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void modify() {
        Intent intent = new Intent(mActivity, SettingActivity.class);
        startActivity(intent);
    }

    private void wine() {
        Intent intent = new Intent(mActivity, SupplyActivity.class);
        startActivity(intent);
    }

    private void barrel() {
        if (UserManager.isLogin()) {
            Intent intent = new Intent(mActivity, BarrelInfoActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(mActivity, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void invite() {

        if (UserManager.isLogin()) {
            Intent intent = new Intent(mActivity, InviteActivity.class);
            startActivity(intent);
        } else {
            UserManager.toLogin();
        }


        /*OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.app_name));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        ///oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl("http://static.firefoxchina.cn/img/201706/5_595455746bdc10.jpg");
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

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

        // 启动分享GUI
        oks.show(this);*/
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

    private void coupon() {
        if (UserManager.isLogin()) {
            Intent intent = new Intent(mActivity, CouponActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(mActivity, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void order() {
        if (UserManager.isLogin()) {
            Intent intent = new Intent(mActivity, OrderActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(mActivity, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Fade());//new Slide()  new Fade()
        }
        setContentView(R.layout.activity_user_center);
        registerUserReceiver();

        x.view().inject(this);
        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initStatus();

        File file = new File(FileUtils.USERICON);

        if (UserManager.isLogin()) {

            datas.clear();

            datas.add("我的订单");
            datas.add("我的收藏");
            datas.add("我的优惠券");
            datas.add("邀请好友");


            int userType = UserManager.getUser().getUserType();
            if (userType < 2) {

            } else {
                if (userType==2){
                    datas.add("验证优惠券");
                }
                datas.add("酒桶信息");

            }

            datas.add("系统设置");
            adapter.notifyDataSetChanged();

            x.image().bind(icon, UserManager.getUser().getHeadIcon(), xUtilsManager.getCircleImageOption());

            /*这里是处理过渡元素，中断网络加载的问题*/
            icon.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (UserManager.isLogin()) {
                        x.image().bind(icon, UserManager.getUser().getHeadIcon(), xUtilsManager.getCircleImageOption());
                    }
                }
            }, 500);
        } else {
            icon.setImageResource(R.mipmap.head);

            datas.clear();

            datas.add("我的订单");
            datas.add("我的收藏");
            datas.add("我的优惠券");
            datas.add("邀请好友");

            datas.add("系统设置");
            adapter.notifyDataSetChanged();
        }


    }


    @Override
    public void onUserReciver(final Intent intent) {
        super.onUserReciver(intent);

        if (intent.getAction().equalsIgnoreCase(action_in)) {
            if (UserManager.isLogin()) {
                String path = UserManager.getUser().getHeadIcon();
                x.image().bind(icon, path, xUtilsManager.getCircleImageOption());
            }
        } else if (intent.getAction().equalsIgnoreCase(action_out)) {
            //x.image().bind(icon, null, xUtilsManager.getCircleImageOption());

            icon.setImageResource(R.mipmap.head);
        } else if (intent.getAction().equalsIgnoreCase(action_change)) {

            x.image().bind(icon, UserManager.getUser().getHeadIcon(), xUtilsManager.getCircleImageOption(), new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable result) {

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    //icon.setImageBitmap(BitmapFactory.decodeFile(FileUtils.USERICON));
                    x.image().bind(icon, FileUtils.USERICON, xUtilsManager.getCircleImageOption());
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
    }

    private void initStatus() {
        /*getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );*/

        /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/
    }

    public void btn_loginOut(View view) {
        UserManager.loginOut(view, new UserManagerInterface() {
            @Override
            public void success(User user) {
                ToastUtils.show(mActivity, "退出成功");
            }

            @Override
            public void faild(User user) {

            }
        });
    }

    public void btn_back(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /*过渡动画完成后退出*/
            finishAfterTransition();
        } else {
            finish();
        }
    }


    public void btn_setting(View view) {
        Intent intent = new Intent(mActivity, InfoSettingActivity.class);
        startActivity(intent);
    }


    class Holder extends RecyclerView.ViewHolder {
        View parent;
        TextView text;
        ImageView img;

        public Holder(View itemView) {
            super(itemView);
            parent = itemView;
            text = (TextView) itemView.findViewById(R.id.text);
            img = (ImageView) itemView.findViewById(R.id.img);

        }
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
