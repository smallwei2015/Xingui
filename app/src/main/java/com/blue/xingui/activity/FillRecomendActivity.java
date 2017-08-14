package com.blue.xingui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.NetData;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.utils.UIUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class FillRecomendActivity extends BaseActivity {

    @ViewInject(R.id.code)
    EditText code;
    @Override
    public void initView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_recomend);
        x.view().inject(this);
        initView();
    }

    public void btn_sure(View view) {

        String s = code.getText().toString();

        if (s != null) {
            RequestParams entity = new RequestParams();

            entity.addBodyParameter("arg1","");
            entity.addBodyParameter("arg2",s);
            entity.addBodyParameter("arg3", UserManager.getUser().getAppuserId()+"");
            x.http().post(entity, new Callback.CommonCallback<NetData>() {
                @Override
                public void onSuccess(NetData result) {


                    int code = result.getResult();
                    if(code ==200){
                        UIUtils.showToast("填写成功");
                        finish();
                    }else if (code==301){
                        UIUtils.showToast("该设备已填写过推荐码");
                    }else if (code==302){
                        UIUtils.showToast("不能填写自己的推荐码");
                    }else if (code==303){
                        UIUtils.showToast("推荐码不存在");
                    }else{
                        UIUtils.showToast("未知错误");
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
        }else {
            UIUtils.showToast("推荐码不能为空");
        }
    }
}
