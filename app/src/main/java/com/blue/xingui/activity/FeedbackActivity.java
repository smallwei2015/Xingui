package com.blue.xingui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.utils.ToastUtils;
import com.blue.xingui.utils.UrlUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class FeedbackActivity extends BaseActivity {

    private EditText content;

    @Override
    public void initView() {
        initTop(R.mipmap.left_gray,"意见反馈",-1);

        content = (EditText) findViewById(R.id.feedback_content);

        findViewById(R.id.feedback_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentStr = content.getText().toString();
                if (TextUtils.isEmpty(contentStr)) {
                    ToastUtils.show(FeedbackActivity.this,"请填写相关内容");
                }else {
                    if (UserManager.isLogin()) {
                        feedback(contentStr);
                    }else {
                        Intent intent=new Intent(FeedbackActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void feedback(String content) {
        RequestParams entity = new RequestParams(UrlUtils.FEEDBACK);
        entity.addBodyParameter("appuserId", UserManager.getUser().getAppuserId()+"");
        entity.addBodyParameter("content",content);

        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject object = JSON.parseObject(result);
                Integer code = object.getInteger("result");

                if (code==200){
                    ToastUtils.show(FeedbackActivity.this,"您的意见我们意见收到，感谢您的建议");
                    finish();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initView();
        initData();
    }
}
