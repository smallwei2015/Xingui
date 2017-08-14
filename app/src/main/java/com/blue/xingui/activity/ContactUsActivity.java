package com.blue.xingui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.utils.ToastUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class ContactUsActivity extends BaseActivity {

    @ViewInject(R.id.contact_commit)
    Button commit;

    @ViewInject(R.id.contact_content)
    EditText content;

    @ViewInject(R.id.contact_name)
    EditText name;

    @ViewInject(R.id.contact_phone)
    EditText phone;

    @ViewInject(R.id.contact_info)
    TextView info;
    @ViewInject(R.id.contact_info2)
    TextView info2;
    @Override
    public void initView() {
        initTop(R.mipmap.left_gray,"联系我们",-1);

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = name.getText().toString();
                String phoneStr = phone.getText().toString();
                String contentStr = content.getText().toString();

                if (TextUtils.isEmpty(nameStr)||TextUtils.isEmpty(phoneStr)||TextUtils.isEmpty(contentStr)){
                    ToastUtils.show(ContactUsActivity.this,"请填写相关信息");
                }else {
                    // TODO: 2017/3/20 upload
                }
            }
        });

        SpannableString spannableString = new SpannableString("电话400-820-0017 | 邮件sevice@nosuhwm.com");
        Drawable drawable = getResources().getDrawable(R.mipmap.phone);
        drawable.setBounds(0, 0, 40, 35);
        ImageSpan imageSpan = new ImageSpan(drawable);
        spannableString.setSpan(imageSpan, 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        drawable = getResources().getDrawable(R.drawable.ssdk_oks_classic_email);
        drawable.setBounds(0, 0, 40, 35);
        imageSpan = new ImageSpan(drawable);
        spannableString.setSpan(imageSpan, 17, 19, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);




        info.setText(spannableString);

        SpannableString spannableString1 = new SpannableString("地址北京市海淀区上地街道康德大厦6212A");
        drawable = getResources().getDrawable(R.mipmap.address);
        drawable.setBounds(0, 0, 40, 35);
        imageSpan = new ImageSpan(drawable);
        spannableString1.setSpan(imageSpan, 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        info2.setText(spannableString1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        x.view().inject(this);
        initView();
        initData();
    }
}
