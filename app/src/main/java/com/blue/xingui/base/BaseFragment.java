package com.blue.xingui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by cj on 2017/3/6.
 */

public abstract class BaseFragment extends Fragment implements BaseUIContainer {
    public BaseActivity mActivity;
    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    private String tag;
    private int type;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.push(this);
        type=TYPE_FRAGMENT;
        tag=getClass().getName();
        mActivity= (BaseActivity) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaseApplication.pop(this);
    }

    @Override
    public void UIfinish() {
    }

    @Override
    public String getUITag() {
        return tag;
    }

    @Override
    public int getType() {
        return type;
    }
}
