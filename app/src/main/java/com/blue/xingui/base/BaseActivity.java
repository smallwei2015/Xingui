package com.blue.xingui.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.xingui.R;
import com.blue.xingui.utils.AnimationUtils;
import com.blue.xingui.utils.SPUtils;

import static com.blue.xingui.manager.UserManager.action_change;
import static com.blue.xingui.manager.UserManager.action_in;
import static com.blue.xingui.manager.UserManager.action_out;

/**
 * Created by cj on 2017/3/6.
 */

public abstract class BaseActivity extends AppCompatActivity implements BaseUIContainer{

    private TextView center;
    public BaseActivity mActivity;
    private View.OnClickListener ToolbarListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.left:
                    onLeftClick(v);
                    break;
                case R.id.right:
                    onRightClick(v);
                    break;
                case R.id.right_text:
                    onRightClick(v);
                    break;
                case R.id.left_icon:
                    onLeftIconClick(v);
                    break;
            }
        }
    };
    public BroadcastReceiver receiver;
    public View nodata;
    public BroadcastReceiver connectionReceiver;

    public void initView(){
        nodata = findViewById(R.id.no_data);
        if (nodata != null) {
            nodata.setVisibility(View.GONE);
        }
    }
    public void initData(){}
    public void initView(View view){

    }

    private int type;
    private String tag;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseApplication.push(this);
        type=TYPE_ACTIVITY;
        tag=getClass().getName();
        mActivity=this;

        //registerUserReceiver();

        //registerNetReceiver();

        int sizeType = SPUtils.getSP().getInt("sizeType", 0);



        if (sizeType==0){
            setTheme(R.style.AppTheme_middle);
        }else if (sizeType==1){
            setTheme(R.style.AppTheme_small);
        }else if (sizeType==2){
            setTheme(R.style.AppTheme_large);
        }else {
            setTheme(R.style.AppTheme_middle);
        }
    }

    protected  void registerNetReceiver(){
        // unconnect network
        // connect network
        connectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onUserReciver(intent);
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver, intentFilter);
    }

    public void registerUserReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(action_in);
        filter.addAction(action_out);
        filter.addAction(action_change);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onUserReciver(intent);
            }
        };
        registerReceiver(receiver, filter);
    }

    public void onUserReciver(Intent intent){

    }

    public void onLeftIconClick(View view){

    }
    public void onRightClick(View view){

    }

    public void onLeftClick(View view){
        UIfinish();
    }

    public void initTop(int left,String content,int right){
        initTop(left,content,right,-1);
    }

    public void initTop(int left,String content,String right){
        initTop(left,content,-1);
        setRightText(right);
    }
    public void initTop(int left,String content,int right,int bgColor){
        initTop(left,content,right,bgColor,-1);
    }

    public void initTop(int left,String content,int right,int bgColor,int textColor){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (bgColor!=-1) {
                toolbar.setBackgroundColor(getResources().getColor(bgColor));
                StatusBarUtils.setWindowStatusBarColor(this,bgColor);
            }else {
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                StatusBarUtils.setWindowStatusBarColor(this,R.color.colorPrimary);
            }

            /*如果全部为空，就把toolbar隐藏的*/
            if (left<=0&&content==null&&right<=0){
                toolbar.setVisibility(View.GONE);
                return;
            }else {

                toolbar.setVisibility(View.VISIBLE);

                ImageView leftI = (ImageView) findViewById(R.id.left);
                ImageView leftIcon = (ImageView) findViewById(R.id.left_icon);
                if (left <= 0) {
                    leftI.setVisibility(View.GONE);
                    leftIcon.setVisibility(View.GONE);
                } else {
                    leftI.setImageResource(left);
                    leftI.setOnClickListener(ToolbarListener);

                    leftIcon.setVisibility(View.GONE);
                }

                if (content != null) {
                    center = ((TextView) findViewById(R.id.center));
                    if (textColor !=-1) {
                        center.setTextColor(getResources().getColor(textColor));
                    }else {
                        center.setTextColor(getResources().getColor(R.color.dark_gray));
                    }
                    //center.setTextSize(18);
                    center.setText(content);

                }

                ImageView rightIcon = (ImageView) findViewById(R.id.right);
                TextView right_text = (TextView) findViewById(R.id.right_text);

                if (TextUtils.isEmpty(right_text.getText().toString())) {
                    if (right == -1) {
                        rightIcon.setVisibility(View.GONE);
                    } else {
                        rightIcon.setImageResource(right);
                        rightIcon.setOnClickListener(ToolbarListener);
                        rightIcon.setVisibility(View.VISIBLE);
                    }
                }else {
                    right_text.setOnClickListener(ToolbarListener);
                    if (bgColor== -1) {
                        right_text.setTextColor(getResources().getColor(R.color.white));
                    }else {
                        right_text.setTextColor(getResources().getColor(R.color.middleGray));
                    }
                }
            }
        }
    }

    private void setRightText(String text){
        TextView right_text = (TextView) findViewById(R.id.right_text);
        ImageView right = (ImageView) findViewById(R.id.right);

        if (right_text!=null&& !TextUtils.isEmpty(text)){
            right_text.setVisibility(View.VISIBLE);
            right_text.setText(text);
            right_text.setOnClickListener(ToolbarListener);
            right.setVisibility(View.GONE);
        }else {
            right_text.setText("");
            right_text.setVisibility(View.GONE);
        }

    }


    public void setLeftIcon(int res){

        /*要先init在设置*/
        ImageView leftIcon = (ImageView) findViewById(R.id.left_icon);
        ImageView left = (ImageView) findViewById(R.id.left);
        if (res>0){
            left.setVisibility(View.GONE);
            leftIcon.setVisibility(View.VISIBLE);
            leftIcon.setImageResource(res);
            leftIcon.setOnClickListener(ToolbarListener);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (connectionReceiver!=null){
            unregisterReceiver(connectionReceiver);
        }
        BaseApplication.pop(this);
    }

    @Override
    public void UIfinish(){
        /*处理页面的销毁*/
        finish();
    }


    @Override
    public int getType() {
        return type;
    }

    @Override
    public String getUITag() {
        return tag;
    }


    public void isNodata(boolean no){
        if (nodata != null) {
            if (no){
                if (nodata.getVisibility()!=View.VISIBLE) {
                    nodata.setVisibility(View.VISIBLE);
                    nodata.setAnimation(AnimationUtils.scaleToSelfSize());
                }
            }else {
                nodata.setVisibility(View.GONE);
            }
        }
    }
}
