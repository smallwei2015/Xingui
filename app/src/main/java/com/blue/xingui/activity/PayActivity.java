package com.blue.xingui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.ItemData;
import com.blue.xingui.utils.UIUtils;
import com.blue.xingui.utils.UrlUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PayActivity extends BaseActivity {


    @ViewInject(R.id.pay_rec)
    RecyclerView rec;

    @ViewInject(R.id.pay_money)
    TextView tv_money;
    @ViewInject(R.id.pay_number)
    TextView tv_number;

    public static int SDK_PAY_FLAG = 100;
    public static final String WEIXID = "wx1e42833dacbc392e";
    public static final int PLUGIN_NOT_INSTALLED = -1;
    public static final int PLUGIN_NEED_UPGRADE = 2;

    private IWXAPI msgApi = null;
    /*支付结果*/
    private String result;
    /*支付订单*/
    public String idNumber;

    private List<ItemData> datas;
    public RecyclerView.Adapter<Holder> adapter;
    /*支付方式*/
    public int cPay = -1;
    public double money;


    @Override
    public void initView() {
        initTop(R.mipmap.left_gray, "支付", -1);


        rec.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new RecyclerView.Adapter<Holder>() {

            @Override
            public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
                View inflate = LayoutInflater.from(mActivity).inflate(R.layout.pay_item, parent, false);

                Holder holder = new Holder(inflate);
                x.view().inject(holder, inflate);
                return holder;
            }

            @Override
            public void onBindViewHolder(Holder holder, int position) {
                final ItemData data = datas.get(position);

                holder.name.setText(data.getName());
                holder.icon.setImageResource(data.getIcon());

                holder.check.setTag(position);
                holder.check.setChecked(data.isChecked());
                holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int pos = (int) buttonView.getTag();
                        if (isChecked) {
                            update(pos);
                        } else {
                            if (data.isChecked()) {
                                cPay = -1;
                            }
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
    }

    private void update(int pos) {
        cPay = pos;
        for (int i = 0; i < datas.size(); i++) {
            ItemData itemData = datas.get(i);
            if (pos == i) {
                itemData.setChecked(true);
            } else {
                itemData.setChecked(false);
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void initData() {
        super.initData();
        // 将该app注册到微信
        msgApi = WXAPIFactory.createWXAPI(mActivity, null);
        msgApi.registerApp(WEIXID);

        idNumber = getIntent().getStringExtra("id");
        money = getIntent().getDoubleExtra("money",0);


        tv_number.setText("订单号："+idNumber);
        tv_money.setText("￥"+String.format("%.2f",money));

        datas = new ArrayList<>();
        datas.add(new ItemData("微信", R.mipmap.weixin));
        datas.add(new ItemData("支付宝", R.mipmap.zhifubao));
        datas.add(new ItemData("银联", R.mipmap.yinglian));

        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        x.view().inject(this);

        initView();
        initData();

    }


    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*************************************************
         *
         * 步骤3：处理银联手机支付控件返回的支付结果
         *
         ************************************************/
        if (data == null) {
            return;
        }

        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
        String str = data.getExtras().getString("pay_result");
//        String oderresult = "http://202.85.214.120/xixi/login.do?method=updateOrder&orderno=20150519190244&state=success&type=1";
        if (str.equalsIgnoreCase("success")) {
            msg = "支付成功！";
            result = "success";
            //sendBroadcast(new Intent("XINGUI_PAY_SUCCESS"));

        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
            result = "fail";

        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
            result = "cancel";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("银联支付结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (result.equalsIgnoreCase("success")) {
                    finish();
                }
            }
        });
        builder.create().show();
    }

    private void payYinl(String singStr) {
        // “00” – 银联正式环境
        // “01” – 银联测试环境，该环境中不发生真实交易
        String serverMode = "00";

        try {
            String decodeRes = URLDecoder.decode(singStr, "utf-8");

            int ret = UPPayAssistEx.startPay(PayActivity.this, null, null, decodeRes, serverMode);
            if (ret == PLUGIN_NEED_UPGRADE || ret == PLUGIN_NOT_INSTALLED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PayActivity.this);
                builder.setTitle("提示");
                builder.setMessage("完成购买需要安装银联支付控件，是否安装？");

                builder.setNegativeButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                UPPayAssistEx
                                        .installUPPayPlugin(PayActivity.this);
                            }
                        });

                builder.setPositiveButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void payWeix(String signStr) {

        /*{
    "appid": "wxdccc9e48aecced55",
    "noncestr": "jy3mbgwngehcdmf8bw4790w8k0hp0o37",
    "package": "Sign=WX",
    "partnerid": "1399801802",
    "prepayid": "wx20170718144743b3b850e9f10334986270",
    "sign": "60FC603FDA3ED1EAEE900A8458ADD239",
    "timestamp": "1500360467"
}*/
        JSONObject object = JSON.parseObject(signStr);

        if (object != null) {
            //UIUtils.showToast("weixin");
            PayReq request = new PayReq();

            request.appId = object.getString("appid");

            request.partnerId = object.getString("partnerid");

            request.prepayId = object.getString("prepayid");

            request.packageValue = object.getString("package");

            request.nonceStr = object.getString("noncestr");

            request.timeStamp = object.getString("timestamp");

            request.sign = object.getString("sign");

            msgApi.sendReq(request);
        }


    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            if (msg.what==SDK_PAY_FLAG){
                Map<String, String> result = (Map<String, String>) msg.obj;

                String resultStatus = result.get("resultStatus");

                               /*9000 	订单支付成功
8000 	正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
4000 	订单支付失败
5000 	重复请求
6001 	用户中途取消
6002 	网络连接出错
6004*/

                String msgS="";

                if (resultStatus.equalsIgnoreCase("9000")) {
                    msgS = "支付成功！";
                    //sendBroadcast(new Intent("XINGUI_PAY_SUCCESS"));
                    //finish();
                } else if (resultStatus.equalsIgnoreCase("8000")) {
                    msgS = "支付处理中";

                } else if (resultStatus.equalsIgnoreCase("6001")) {
                    msgS = "用户取消了支付";
                }else {
                    msgS="支付失败";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("支付宝支付结果通知");
                builder.setMessage(msgS);
                builder.setInverseBackgroundForced(true);
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        finish();
                    }
                });
                builder.create().show();


            }
        }
    };

    private void payAli(final String sign) {
        // 订单信息

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(mActivity);
                Map<String, String> result = alipay.payV2(sign, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }

    private void pay(final int type, String orderNumer, int flag) {

        final ProgressDialog dialog=new ProgressDialog(mActivity);

        dialog.setMessage("正在加载订单信息...");
        dialog.show();


        RequestParams entity = new RequestParams(UrlUtils.pay);

        entity.addBodyParameter("dataId", orderNumer);
        entity.addBodyParameter("type", type + "");//0银联1微信2支付宝

        //entity.addBodyParameter("flag", flag + "");//0未使用1使用
        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                JSONObject object = JSON.parseObject(result);

                if (object.getInteger("result") == 200) {
                    String sign = object.getString("sign");

                    if (sign != null) {

                        if (type == 2) {
                            payAli(sign);
                        } else if (type == 1) {
                            payWeix(sign);
                        } else if (type == 0) {
                            payYinl(sign);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.w("33333",ex.getMessage());
                UIUtils.showToast("订单生成失败");
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

    public void btn_pay(View view) {
        if (cPay < 0) {
            UIUtils.showToast("请选择支付方式");
            return;
        } else if (cPay == 0) {
            pay(1, idNumber, 0);
        } else if (cPay == 1) {
            pay(2, idNumber, 0);
        } else if (cPay == 2) {
            pay(0, idNumber, 0);
        }
    }


    class Holder extends RecyclerView.ViewHolder {

        @ViewInject(R.id.pay_icon)
        ImageView icon;
        @ViewInject(R.id.pay_name)
        TextView name;
        @ViewInject(R.id.pay_check)
        CheckBox check;
        @ViewInject(R.id.pay_parent)
        View parent;

        public Holder(View itemView) {
            super(itemView);
        }
    }
}
