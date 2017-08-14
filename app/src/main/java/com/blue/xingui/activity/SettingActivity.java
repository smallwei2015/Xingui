package com.blue.xingui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.NetData;
import com.blue.xingui.bean.User;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.manager.xUtilsManager;
import com.blue.xingui.utils.FileUtils;
import com.blue.xingui.utils.ToastUtils;
import com.blue.xingui.utils.UIUtils;
import com.blue.xingui.utils.UrlUtils;
import com.blue.xingui.view.ptr.BasePopUpWindow;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

public class SettingActivity extends BaseActivity implements GalleryFinal.OnHanlderResultCallback {


    @ViewInject(R.id.user_icon)
    View icon;
    @ViewInject(R.id.user_nickname)
    View nickname;
    @ViewInject(R.id.user_gender)
    RadioGroup gender;
    @ViewInject(R.id.user_password)
    View password;
    @ViewInject(R.id.user_phone)
    View phone;

    @ViewInject(R.id.icon_image)
    ImageView icon_image;
    private AlertDialog dialog;
    public PopupWindow window;

    @Override
    public void initView() {
        initTop(R.mipmap.left_gray,"用户信息",-1);

        nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View inflate = LayoutInflater.from(mActivity).inflate(R.layout.interact_comment_pop, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setView(inflate);
                dialog = builder.create();

                ((TextView) inflate.findViewById(R.id.title)).setText("修改昵称：");
                final EditText editText = (EditText) inflate.findViewById(R.id.edit);

                inflate.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                inflate.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nickname = editText.getText().toString();
                        if (TextUtils.isEmpty(nickname)) {
                            ToastUtils.show(mActivity, "昵称不能为空");
                        } else if (nickname.length()>10){
                            ToastUtils.show(mActivity,"昵称长度不能超过10位");
                        }else {
                            changeNickname(nickname);
                        }
                    }
                });
                dialog.show();
            }
        });

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePass();
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePhone();
            }
        });
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==R.id.man){
                    changeGender(1);
                }else if (checkedId==R.id.woman){
                    changeGender(2);
                }else {
                    changeGender(3);
                }
            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window = new BasePopUpWindow(mActivity);

                View inflate = LayoutInflater.from(mActivity).inflate(R.layout.image_pic, null);

                inflate.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GalleryFinal.openCamera(100, SettingActivity.this);
                    }
                });

                inflate.findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GalleryFinal.openGallerySingle(200, SettingActivity.this);
                    }
                });


                window.setContentView(inflate);
                window.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

                window.setBackgroundDrawable(new ColorDrawable(0x00000000));
                window.setOutsideTouchable(false);
                window.setFocusable(true);

                window.showAsDropDown(v);
            }
        });
    }

    private void changeGender(final int gender) {

        if (UserManager.getUser().getSex()==gender){
            return;
        }
        RequestParams entity = new RequestParams(String.format(UrlUtils.UPDATE_GENDER, gender+"", UserManager.getUser().getAppuserId() + ""));

        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    int code = jsonObject.getInt("result");

                    if (code == 200) {
                        ToastUtils.show(mActivity, "修改成功");
                        UserManager.getUser().setSex(gender);
                        UserManager.sendChange();

                    } else if (code == 500) {
                        ToastUtils.show(mActivity, "服务器异常");
                    } else {
                        ToastUtils.show(mActivity, "程序异常");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtils.show(mActivity, "网络请求失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void changePhone() {
        Intent intent=new Intent(mActivity,CheckPhoneActivity.class);
        intent.putExtra("flag",2);
        startActivity(intent);
    }

    private void changePass() {
        Intent intent=new Intent(mActivity,CheckPhoneActivity.class);
        intent.putExtra("flag",1);
        startActivity(intent);
    }

    private void changeNickname(final String nickname) {
        RequestParams entity = new RequestParams(UrlUtils.UPDATE_NAME);
        entity.addBodyParameter("nickName",nickname);
        entity.addBodyParameter("appuserId", UserManager.getUser().getAppuserId() + "");
        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    int code = jsonObject.getInt("result");

                    if (code == 200) {
                        ToastUtils.show(mActivity, "修改成功");
                        UserManager.getUser().setNickName(nickname);
                        dialog.dismiss();

                    } else if (code == 500) {
                        ToastUtils.show(mActivity, "服务器异常");
                    } else {
                        ToastUtils.show(mActivity, "程序异常");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtils.show(mActivity, "网络请求失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void changeIcon(final String path){


        final ProgressDialog dialog=new ProgressDialog(mActivity);
        dialog.setMessage("上传中...");

        dialog.show();

        File file=new File(path);

        RequestParams entity = new RequestParams(UrlUtils.UPDATE_HEAD);
        entity.addBodyParameter("file",file);
        entity.addBodyParameter("appuserId",UserManager.getUser().getAppuserId()+"");
        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {


                NetData netData = JSON.parseObject(result, NetData.class);
                if (netData.getResult()==200) {
                    UIUtils.showToast("修改成功");
                    com.alibaba.fastjson.JSONObject object = JSON.parseObject(netData.getInfo());

                    String headIcon = object.getString("headIcon");

                    UserManager.getUser().setHeadIcon(headIcon);
                    UserManager.sendChange();

                    /*存储当前头像*/
                    FileUtils.copyFile(path,FileUtils.USERICON);

                    x.image().bind(icon_image,headIcon,xUtilsManager.getCircleImageOption());

                    //icon_image.setImageBitmap(BitmapFactory.decodeFile(path));
                }else {
                    UIUtils.showToast("修改失败");
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
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        x.view().inject(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (UserManager.isLogin()) {
            User user = UserManager.getUser();


            x.image().bind(icon_image,user.getHeadIcon(), xUtilsManager.getCircleImageOption());

            int sex = user.getSex();
            if (sex ==1) {
                gender.check(R.id.man);
            }else if (sex ==2){
                gender.check(R.id.woman);
            }
        }

    }

    /*图片选择回调*/
    @Override
    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {

        window.dismiss();

        String path = resultList.get(0).getPhotoPath();


        if (reqeustCode == 100) {
            /*拍照*/
            changeIcon(path);

        } else if (reqeustCode == 200) {
            /*相册*/
            changeIcon(path);
        }

    }

    @Override
    public void onHanlderFailure(int requestCode, String errorMsg) {

    }
}
