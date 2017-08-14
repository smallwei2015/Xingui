package com.blue.xingui.manager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.blue.xingui.activity.LoginActivity;
import com.blue.xingui.base.BaseApplication;
import com.blue.xingui.bean.NetData;
import com.blue.xingui.bean.User;
import com.blue.xingui.utils.MD5Utils;
import com.blue.xingui.utils.ToastUtils;
import com.blue.xingui.utils.UIUtils;
import com.blue.xingui.utils.UrlUtils;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static com.blue.xingui.base.BaseApplication.daoConfig;

/**
 * Created by cj on 2017/6/21.
 */

public class UserManager {

    public static final String action_in = "com.xingui.login";
    public static final String action_out = "com.xingui.login_out";
    public static final String action_change="com.xingui.change";
    private static User cUser;

    public static User getUser(){

        if (cUser!=null){
            return cUser;
        }else{
            return null;
        }
    }

    public static void setUser(User user){
        if (user != null) {
            cUser=user;
            sendLogin();

            setAlias();
        }
    }

    private static void setAlias() {
        JPushInterface.setAlias(BaseApplication.getInstance(), cUser.getUserName(), new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                Log.w("3333","设置alias成功"+i+"-"+s);

                if (i==6002){
                    /*超时就去重新设置*/
                    //setAlias();
                }
            }
        });
    }

    public static void login(String name, String pass, final UserManagerInterface manager){


        //RequestParams entity = new RequestParams(UrlUtils.LOGIN+"?arg1="+name+"&passWord="+MD5Utils.getMD5(pass));


        RequestParams entity = new RequestParams(UrlUtils.LOGIN);
        entity.addBodyParameter("arg1",name);
        entity.addBodyParameter("passWord", MD5Utils.getMD5(pass));

        /*entity.addQueryStringParameter("arg1",name);
        entity.addQueryStringParameter("passWord",MD5Utils.getMD5(pass));*/

        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                NetData netData = JSON.parseObject(result, NetData.class);

                if (netData.getResult()==200){

                    User user = JSON.parseObject(netData.getInfo(), User.class);
                    cUser=user;

                    manager.success(user);
                    saveUser(user);
                    sendLogin();

                    setAlias();
                }else {
                    manager.faild(null);
                    if (netData.getResult()==401){
                        UIUtils.showToast("登录名密码错误");
                    }
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                manager.faild(null);
                Log.w("3333",ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    public static void saveUser(User user) {
        try {
            /*登录过时时间为7天*/
            user.setExpiration(System.currentTimeMillis()+1000L*60*60*24*30);
            x.getDb(daoConfig).saveOrUpdate(user);

            //Log.w("3333","save success");
        } catch (DbException e) {
            e.printStackTrace();

            //Log.w("3333",e.getMessage());
        }
    }

    private static void sendLogin() {
        Intent intent = new Intent();
        intent.setAction(action_in);
        BaseApplication.getInstance().sendBroadcast(intent);
    }

    public static void loginOut(View view,final UserManagerInterface manager){

        final ProgressDialog dialog=new ProgressDialog(view.getContext());
        dialog.setMessage("正在退出登录...");
        dialog.show();

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                manager.success(cUser);


                JPushInterface.setAlias(BaseApplication.getInstance(), "", new TagAliasCallback() {
                    @Override
                    public void gotResult(int i, String s, Set<String> set) {
                        Log.w("3333","取消别名设置");
                    }
                });
                deleteUser();
                sendLoginOut();
            }
        }, 1000);


    }

    public static void loginOutWithoutDelay() {
        JPushInterface.setAlias(BaseApplication.getInstance(), "", new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                Log.w("3333","取消别名设置");
            }
        });
        //deleteUser();

        DbManager db = x.getDb(daoConfig);
        try {
            db.delete(User.class);
            //Log.w("3333","delete all");
        } catch (DbException e) {
            e.printStackTrace();

            ///Log.w("3333",e.getMessage());
        }

        ///Log.w("3333","login out not delay");
        cUser=null;
        sendLoginOut();
    }

    private static void deleteUser() {
        try {
            if (cUser != null) {
                DbManager db = x.getDb(daoConfig);
                db.delete(cUser);

                db.delete(User.class);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        cUser=null;
    }

    private static void sendLoginOut() {
        Intent intent = new Intent();
        intent.setAction(action_out);
        BaseApplication.getInstance().sendBroadcast(intent);
    }

    public static void register(String name,String pass,String nickname,String address,final UserManagerInterface manager){


        RequestParams entity = new RequestParams(UrlUtils.REGISTER);
        entity.addBodyParameter("phone",name);
        entity.addBodyParameter("passWord",MD5Utils.getMD5(pass));
        entity.addBodyParameter("nickName",nickname);
        entity.addBodyParameter("arg1",address);

        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                NetData netData = JSON.parseObject(result, NetData.class);

                if (netData.getResult()==200){
                    manager.success(null);
                }else {
                    manager.faild(null);
                    if (netData.getResult()==401){
                        UIUtils.showToast("该手机号已注册");
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                manager.faild(null);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                UIUtils.showToast("网络请求失败");
            }

            @Override
            public void onFinished() {

            }
        });
    }

    public static boolean isLogin() {
        return cUser!=null;
    }

    public static void loginThird(String userId, String nameStr, String userIcon,final UserManagerInterface manager) {

        RequestParams entity = new RequestParams(UrlUtils.THIRD_LOGIN);

        entity.addBodyParameter("thirdAuth",userId);
        entity.addBodyParameter("nickName",nameStr);
        entity.addBodyParameter("arg1",userIcon);


        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                /* {"info":{"headIcon":"http://q.qlogo.cn/qqapp/1106203045/E74972B512F8ABC98BC9AED5BC9F8A7C/100",
                "recommendCode":"b5a75","sex":3,"phone":"","nickName":"诗残莫续","userName":"6dbb848d64d","appuserId":99},
                "message":"登录成功","result":"200"}
*/

                NetData netData = JSON.parseObject(result, NetData.class);

                if (netData.getResult()==200){

                    User user = JSON.parseObject(netData.getInfo(), User.class);
                    cUser=user;
                    manager.success(user);

                    saveUser(user);
                    sendLogin();

                    setAlias();

                }else {
                    manager.faild(null);
                    UIUtils.showToast("登录成功");
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtils.showInner(BaseApplication.getInstance(),"网络连接失败");
                manager.faild(null);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    public static void sendChange(){

        try {
            x.getDb(daoConfig).saveOrUpdate(cUser);

            Intent intent = new Intent();
            intent.setAction(action_change);
            BaseApplication.getInstance().sendBroadcast(intent);
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    public static void toLogin() {
        BaseApplication instance = BaseApplication.getInstance();
        Intent intent=new Intent(instance, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        instance.startActivity(intent);

    }
}
