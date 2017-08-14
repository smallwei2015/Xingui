package com.blue.xingui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.Goods;
import com.blue.xingui.utils.xUtilsImageUtils;
import com.blue.xingui.view.ptr.PinchImageView;
import com.blue.xingui.view.ptr.PinchImageViewPager;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class NewsKindImageActivity extends BaseActivity {

    @ViewInject(R.id.news_image_container)
    FrameLayout container;
    private PinchImageView pinchImageView;
    private PinchImageViewPager pager;
    private List<ImageView> mImages;
    private Goods report;
    public static String TRA_NAME="goods_name";

    @Override
    public void initView() {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        pager=new PinchImageViewPager(this);
        mImages=new ArrayList<>();
        /*for (int i = 0; i < 3; i++) {
            pinchImageView = new PinchImageView(this);
            pinchImageView.setImageResource(R.mipmap.demo);
            pinchImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            pager.addView(pinchImageView);
        }*/

        Intent intent = getIntent();
        report = (Goods) intent.getSerializableExtra("data");
        int position = intent.getIntExtra("position", -1);

        List<String> manyPic = new ArrayList<>();
        manyPic.add(report.getPicsrc());
        for (int i = 0; i < manyPic.size(); i++) {
            ImageView imageView = new PinchImageView(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.setTransitionName(TRA_NAME);
            }
            xUtilsImageUtils.display(imageView,manyPic.get(i));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAfterTransition();
                    }else {
                        finish();
                    }
                }
            });
            mImages.add(i, imageView);
        }
        pager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(final ViewGroup container,
                                          final int position) {
                container.addView(mImages.get(position));
                return mImages.get(position);
            }

            @Override
            public int getCount() {
                return mImages.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mImages.get(position));
            }
        });
        if (position!=-1) {
            pager.setCurrentItem(position);
        }
        container.addView(pager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_kind_image);
        x.view().inject(this);
        initView();
        initData();
    }
}
