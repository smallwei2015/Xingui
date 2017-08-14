package com.blue.xingui.base;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.blue.xingui.R;
import com.blue.xingui.activity.InfoActivity;

/**
 * Created by cj on 2017/7/24.
 */

public class BaseTabActivity extends TabActivity implements TabHost.OnTabChangeListener {

    private static final int TAB_INDEX_ALL = 0;
    private static final int TAB_INDEX_MISSED = 1;
    private static final int TAB_INDEX_OUTGOING = 2;
    private static final int TAB_INDEX_RECEIVED = 3;

    private TabHost mTabHost;
    private TabWidget mTabWidget;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.inner);

        mTabHost = getTabHost();
        mTabHost.setOnTabChangedListener(this);

        setupTabs();
        mTabWidget = mTabHost.getTabWidget();
        mTabWidget.setStripEnabled(false);

        for (int i = 0; i < mTabWidget.getChildCount(); i++) {

            TextView tv = (TextView) mTabWidget.getChildAt(i).findViewById(
                    android.R.id.title);
            tv.setTextColor(this.getResources().getColorStateList(
                    android.R.color.white));

            tv.setPadding(0, 0, 0, (int) tv.getTextSize());
            tv.setText("Tab" + i);
            mTabWidget.getChildAt(i).getLayoutParams().height = (int) (3 * tv.getTextSize());

            mTabWidget.getChildAt(i).setBackgroundResource(R.drawable.ic_delete_photo);
        }
    }

    public void onTabChanged(String tabId) {

    }

    private void setupTabs() {
        mTabHost.addTab(mTabHost.newTabSpec("all").setIndicator(
                getString(R.string.inner)).setContent(
                new Intent(this, InfoActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec("Missed").setIndicator(
                getString(R.string.inner)).setContent(
                new Intent(this, InfoActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec("Outgoing").setIndicator(
                getString(R.string.inner)).setContent(
                new Intent(this, InfoActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec("Received").setIndicator(
                getString(R.string.inner)).setContent(
                new Intent(this, InfoActivity.class)));

    }

}

