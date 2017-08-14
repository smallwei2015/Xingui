package com.blue.xingui.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.BarrelNetInfo;
import com.blue.xingui.bean.NetData;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.utils.UIUtils;
import com.blue.xingui.utils.UrlUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Calendar;


public class SupplyActivity extends BaseActivity {

    public BarrelNetInfo data;


    @ViewInject(R.id.supply_name)
    TextView name;
    @ViewInject(R.id.supply_capacity)
    TextView capacity;
    @ViewInject(R.id.supply_date)
    TextView date;
    @ViewInject(R.id.supply_number)
    EditText number;
    @ViewInject(R.id.supply_tips)
    EditText tips;


    @Override
    public void initView() {
        initTop(R.mipmap.left_gray,"补充酒水",R.drawable.finish_gray);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar instance = Calendar.getInstance();
                final int day = instance.get(Calendar.DAY_OF_MONTH);
                int month=instance.get(Calendar.MONTH);
                int year = instance.get(Calendar.YEAR);

                DatePickerDialog datePicker = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                    }
                },year, month, day);

                datePicker.show();

            }
        });
    }

    @Override
    public void initData() {
        super.initData();

        data = ((BarrelNetInfo) getIntent().getSerializableExtra("data"));

        capacity.setText(String.format("当前酒量："+"%.1f"+"L/"+"%.1f"+"L",+data.getCurrentCapacity(),data.getTotalCapacity()));
        name.setText(data.getHotelName());
    }

    @Override
    public void onRightClick(View view) {
        super.onRightClick(view);


        String numberStr = number.getText().toString();
        String tipsStr = tips.getText().toString();
        String dateStr = date.getText().toString();

        if (TextUtils.isEmpty(numberStr)) {

            UIUtils.showToast("请填写加酒量");
            return;
        }else {

            try {
                double v = Double.parseDouble(numberStr);
            }catch (Exception e){
                UIUtils.showToast("请填写正确的加酒量");
                return;
            }
        }

        final ProgressDialog dialog=new ProgressDialog(mActivity);
        dialog.setMessage("网络加载中...");
        dialog.show();


        RequestParams entity = new RequestParams(UrlUtils.notifyTheMasterOfBeer);

        entity.addBodyParameter("dataId",data.getDataId()+"");
        entity.addBodyParameter("num",numberStr);
        entity.addBodyParameter("appuserId", UserManager.getUser().getAppuserId()+"");
        entity.addBodyParameter("arg1",dateStr);
        entity.addBodyParameter("arg2",tipsStr);

        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                NetData netData = JSON.parseObject(result, NetData.class);

                if (netData.getResult()==200){
                    UIUtils.showToast("通知成功");
                }else {
                    UIUtils.showToast("通知失败");
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                UIUtils.showToast("网络请求失败");
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
        setContentView(R.layout.activity_supply);
        x.view().inject(this);
        initView();
        initData();
    }
}
