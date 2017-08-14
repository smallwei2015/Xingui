package com.blue.xingui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.CookieManager;

import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.User;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.manager.UserManagerInterface;
import com.blue.xingui.utils.FileUtils;
import com.blue.xingui.utils.ToastUtils;
import com.caption.update.UpdateUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

import static com.blue.xingui.R.id.loginOut;


public class InfoSettingActivity extends BaseActivity {


    @ViewInject(R.id.setting_contact)
    View contact;

    @ViewInject(R.id.setting_size)
    View size;

    @ViewInject(R.id.setting_clear)
    View clear;

    @ViewInject(loginOut)
    View out;

    @ViewInject(R.id.setting_upload)
    View upload;

    @ViewInject(R.id.setting_about)
    View about;

    private Intent intent;


    @Override
    public void initView() {
        initTop(R.mipmap.left_gray,"用户设置",-1);

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(mActivity, ContactUsActivity.class);
                                startActivity(intent);
            }
        });

        size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mActivity,FeedbackActivity.class);
                startActivity(intent);
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File appCacheDir = new File(getFilesDir().getAbsolutePath()+"/webcache");
                    File webviewCacheDir = new File(getCacheDir().getAbsolutePath()+"/webviewCache");
                    long folderSize = FileUtils.getFolderSize(new File(FileUtils.CACHEPATH));

                    folderSize+=FileUtils.getFolderSize(appCacheDir);
                    folderSize+=FileUtils.getFolderSize(webviewCacheDir);

                    String formatSize = FileUtils.getFormatSize(folderSize);



                    AlertDialog.Builder builder1=new AlertDialog.Builder(mActivity);

                    builder1.setTitle("清除缓存：");
                    builder1.setMessage("现有缓存文件"+formatSize+",确认清除缓存文件？");
                    builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FileUtils.deleteFolderFile(FileUtils.CACHEPATH, true);
                            //FileUtils.cleanFiles(mActivity);

                            x.image().clearCacheFiles();
                            x.image().clearMemCache();

                            CookieManager cm = CookieManager.getInstance();
                            /*cm.removeSessionCookie();
                            cm.removeAllCookie();*/
                            /*webview自动创建的缓存数据库*/
                            boolean b = deleteDatabase("webview.db");
                            boolean b1 = deleteDatabase("webviewCache.db");

                            clearWebViewCache();

                            ToastUtils.show(mActivity, "缓存清理成功");
                        }
                    });

                    builder1.create().show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mActivity,UpdateActivity.class);
                startActivity(intent);

                UpdateUtils.getInit().update(mActivity,0);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mActivity,AboutActivity.class);
                startActivity(intent);
            }
        });

        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManager.loginOut(v, new UserManagerInterface() {
                    @Override
                    public void success(User user) {
                        finish();
                    }

                    @Override
                    public void faild(User user) {

                    }
                });

            }
        });


        if (UserManager.isLogin()){
            out.setVisibility(View.VISIBLE);
        }else {
            out.setVisibility(View.GONE);
        }
    }
    public void clearWebViewCache(){

        //清理Webview缓存数据库
        try {
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //WebView 缓存文件
        File appCacheDir = new File(getFilesDir().getAbsolutePath()+"/webcache");

        File webviewCacheDir = new File(getCacheDir().getAbsolutePath()+"/webviewCache");

        //删除webview 缓存目录
        if(webviewCacheDir.exists()){
            deleteFile(webviewCacheDir);
        }
        //删除webview 缓存 缓存目录
        if(appCacheDir.exists()){
            deleteFile(appCacheDir);
        }
    }
    public void deleteFile(File file) {

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_setting);
        x.view().inject(this);
        initView();
        initData();
    }
}
