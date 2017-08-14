package com.blue.xingui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.CouponDetailInfo;
import com.blue.xingui.bean.NetData;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.utils.UIUtils;
import com.blue.xingui.utils.UrlUtils;
import com.blue.xingui.utils.WeatherUtils;
import com.blue.xingui.view.ptr.BasePopUpWindow;

import net.wujingchao.android.view.SimpleTagImageView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class ValidateActivity extends BaseActivity {


    public EditText edit;
    public LinearLayout parent;

    @Override
    public void initView() {
        super.initView();
        initTop(R.mipmap.left_gray,"验证优惠券",-1);

        edit = ((EditText) findViewById(R.id.validate_code));
        parent = ((LinearLayout) findViewById(R.id.activity_validate));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);

        initView();
    }

    private void validate(String code){
        RequestParams entity = new RequestParams(UrlUtils.updateCouponState);

        String appuserIdStr = UserManager.getUser().getAppuserId()+"";
        entity.addBodyParameter("appuserId", appuserIdStr);
        entity.addBodyParameter("arg1",code);
        entity.addBodyParameter("appid", WeatherUtils.App_ID);

        String path=UrlUtils.updateCouponState+"?appuserId="+appuserIdStr+"&arg1="+code+"&appid="+WeatherUtils.App_ID;

        Log.w("3333",path);
        String sign = WeatherUtils.sign(path, WeatherUtils.regularKey);
        Log.w("3333",sign);

        entity.addBodyParameter("key", sign);

        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                NetData netData = JSON.parseObject(result, NetData.class);

                int result1 = netData.getResult();
                if (result1 ==200){
                    UIUtils.showToast("兑换码兑换成功");
                }else if (result1==300){
                    UIUtils.showToast("优惠券不可用");
                }else{
                    UIUtils.showToast("服务器错误");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                UIUtils.showToast("网络连接错误");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void getInfo(final String code){


        RequestParams entity = new RequestParams(UrlUtils.getCouponInfoByRedeemCode);


        entity.addBodyParameter("arg1",code);


        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Log.w("3333",result);
                NetData netData = JSON.parseObject(result, NetData.class);

                int result1 = netData.getResult();
                if (result1 ==200){

                    CouponDetailInfo info = JSON.parseObject(netData.getInfo(), CouponDetailInfo.class);


                    showPop(info,code);

                }else if (result1==300){
                    UIUtils.showToast("没有查询到此优惠券信息");
                }else {
                    UIUtils.showToast("服务器异常");
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

    private void showPop(CouponDetailInfo info, final String code) {
        final PopupWindow popupWindow=new BasePopUpWindow(mActivity);
        popupWindow.setWidth((int) getResources().getDimension(R.dimen.dp300));
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        View inflate = LayoutInflater.from(mActivity).inflate(R.layout.validate_pop, null);

        ((TextView) inflate.findViewById(R.id.money)).setText(info.getCoupon()+"");
        popupWindow.setContentView(inflate);


        ((TextView) inflate.findViewById(R.id.deadline)).setText(".有效期至" + info.getDeathDate());
        ((TextView) inflate.findViewById(R.id.condition1)).setText(".兑换码："+info.getRedeemCode());
        ((TextView) inflate.findViewById(R.id.condition2)).setText(".满" + info.getUseCondition() + "可以使用");

                /*0未使用1已使用
                2已过期*/
        final int state = info.getState();
        SimpleTagImageView coupon_tag = (SimpleTagImageView) inflate.findViewById(R.id.coupon_tag);

        if (state == 0) {
            coupon_tag.setTagText("未使用");
            coupon_tag.setTagBackgroundColor(Color.parseColor("#27CDC0"));
        } else if (state == 1) {
            coupon_tag.setTagText("已使用");
            coupon_tag.setTagBackgroundColor(Color.parseColor("#cccccc"));
        } else {
            coupon_tag.setTagText("已过期");
            coupon_tag.setTagBackgroundColor(Color.parseColor("#999999"));
        }

        ((TextView) inflate.findViewById(R.id.sure)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state==0) {
                    validate(code);
                    popupWindow.dismiss();
                }else {
                    UIUtils.showToast("该优惠券不能使用");
                }
            }
        });

        popupWindow.showAtLocation(parent, Gravity.CENTER,0,0);
    }

    public void btn_sure(View view) {

        String code = edit.getText().toString();

        if (TextUtils.isEmpty(code)){
            UIUtils.showToast("请输入兑换码");
            return;
        }

        getInfo(code);
    }
}
