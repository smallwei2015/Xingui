package com.blue.xingui.base;

import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import com.blue.xingui.R;
import com.blue.xingui.bean.Address;
import com.blue.xingui.bean.CartGoods;
import com.blue.xingui.bean.Goods;
import com.blue.xingui.bean.User;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.manager.xUtilsImageLoad;
import com.blue.xingui.utils.SPUtils;
import com.blue.xingui.utils.UIUtils;
import com.mob.MobApplication;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by cj on 2017/6/21.
 */

public class BaseApplication extends MobApplication {

    private static BaseApplication mApp=null;
    public static long MainThreadId;
    /*管理栈顶*/
    private static List<BaseUIContainer> containers;
    public static DbManager.DaoConfig daoConfig;

    public static BaseApplication getInstance(){
        return mApp;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mApp=this;

        initData();
        initThird();
        loginAuto();
    }

    private void loginAuto() {
        try {
            List<User> all = x.getDb(daoConfig).findAll(User.class);

            if (all != null&&all.size()>0) {

                if (all.size()>1){

                    x.getDb(BaseApplication.daoConfig).delete(User.class);

                    return;
                }
                User user = all.get(all.size() - 1);

                //Log.w("33333",all.size()+"size"+user.getExpiration()+"="+System.currentTimeMillis());
                if (user!=null&&user.getExpiration()>System.currentTimeMillis()) {

                    int userType = SPUtils.getSP().getInt("userFlag", -1);

                    //Log.w("33333",userType+"type");

                    if (userType==1) {

                        //Log.w("3333","out");
                        UserManager.loginOutWithoutDelay();
                        SPUtils.getSP().edit().putInt("userFlag",0).apply();
                    }else {
                        //Log.w("3333","in");
                        UserManager.setUser(user);
                    }
                }else {
                    UIUtils.showToast("登录超时请重新登录");
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    private void initThird() {

        //对xUtils进行初始化
        x.Ext.init(this);

        //是否是开发、调试模式
        //x.Ext.setDebug(BuildConfig.DEBUG);//是否输出debug日志，开启debug会影响性能


        File dbDir = new File(getFilesDir()+File.separator+"datebase");
        if (!dbDir.exists()) {
            //将数据库文件存储在database文件内
            dbDir.mkdirs();
        }

        daoConfig = new DbManager.DaoConfig()
                .setDbName("xingui_db.db")//创建数据库的名称
                .setDbVersion(3)//数据库版本号
                .setDbDir(dbDir)//设置数据库的存储位置
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {

                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {

                        // TODO: update
                        if (newVersion>oldVersion){
                            try {
                                db.delete(Address.class);
                                db.delete(CartGoods.class);
                                db.delete(User.class);

                                db.dropTable(User.class);
                                db.dropTable(Address.class);
                                db.dropTable(CartGoods.class);
                                db.dropTable(Goods.class);
                                /*db.execQuery("drop table address");
                                db.execNonQuery("drop table user");
                                db.execNonQuery("drop table cartGoods");*/

                            } catch (DbException e) {
                                Log.w("33333",e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                });

        /*初始化galleryfinal*/
        //配置主题
        ThemeConfig theme = null;
        theme = new ThemeConfig.Builder().setTitleBarTextColor(getResources().getColor(R.color.middleGray))
                .setTitleBarBgColor(getResources().getColor(R.color.colorPrimary))
                .setIconBack(R.drawable.left_gray_small)
                .setEditPhotoBgTexture(new ColorDrawable(getResources().getColor(R.color.transparent)))
                .setFabNornalColor(getResources().getColor(R.color.primaryColor))
                .setFabPressedColor(getResources().getColor(R.color.primaryColorPressed))//.setIconPreview()
                .setIconCamera(R.drawable.camera_gray)
                .setIconFolderArrow(R.drawable.arrow_down_gray)
                .setIconPreview(R.drawable.eye_gray)
                .setIconCrop(R.drawable.crop_gray)
                .setIconRotate(R.drawable.rotate_gray)
                .build();
        //配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .build();

        //配置imageloader
        ImageLoader imageloader = new xUtilsImageLoad();
        CoreConfig coreConfig = new CoreConfig.Builder(this, imageloader, theme)
                //.setDebug(BuildConfig.DEBUG)
                .setFunctionConfig(functionConfig)
                .build();
        GalleryFinal.init(coreConfig);


        /*初始化极光推送*/
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        /*初始化shareSDK*/
        //3.x不需要这一步
        //ShareSDK.initSDK(this);

    }

    private void initData() {
        MainThreadId = android.os.Process.myTid();
        containers=new ArrayList<>();
    }


    /*当前栈顶元素*/
    public static int push(BaseUIContainer container){
        containers.add(container);

        return containers.size();
    }
    public static int pop(BaseUIContainer container){

        if (containers.contains(container)) {
            containers.remove(container);
        }

        return containers.size();
    }

    public static void clearContainers(){
        if (containers!=null){

            for (int i = 0; i < containers.size(); i++) {
                containers.get(i).UIfinish();
            }
            containers.clear();
            containers=null;
        }
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        clearContainers();
    }
}
