package com.blue.xingui.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.blue.xingui.R;
import com.blue.xingui.activity.PayActivity;
import com.blue.xingui.utils.UIUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxpay_entry);

        api = WXAPIFactory.createWXAPI(this, PayActivity.WEIXID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.w("3333","req");
    }

    @Override
    public void onResp(BaseResp baseResp) {

        Log.w("3333","resp");
        if(baseResp.getType()== ConstantsAPI.COMMAND_PAY_BY_WX){
            Log.d("3333","onPayFinish,errCode="+baseResp.errCode);

            if (baseResp.errCode == 0) { //支付成功
                sendBroadcast(new Intent().setAction("XINGUI_PAY_SUCCESS"));
                UIUtils.showToast("支付成功");
            } else {
                sendBroadcast(new Intent().setAction("XINGUI_PAY_FAILD"));
                UIUtils.showToast("支付失败");
            }
        }
    }
}
