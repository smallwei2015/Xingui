package com.blue.xingui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.Article;
import com.blue.xingui.bean.DataWrap;
import com.blue.xingui.bean.NetData;
import com.blue.xingui.manager.NewsAdapter;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.utils.ToastUtils;
import com.blue.xingui.utils.UrlUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class CollectActivity extends BaseActivity {

    @ViewInject(R.id.framelayout)
    PtrClassicFrameLayout ptrFrame;
    @ViewInject(R.id.recycler)
    RecyclerView rec;
    @ViewInject(R.id.progress)
    ContentLoadingProgressBar bar;


    private boolean isLoading;
    private int cPage=2;
    private List<DataWrap> items;
    public NewsAdapter adapter;

    @Override
    public void initView() {
        super.initView();

        initTop(R.mipmap.left_gray,"我的收藏",-1);


        PtrClassicDefaultHeader ptrUIHandler = new PtrClassicDefaultHeader(mActivity);
        ptrFrame.addPtrUIHandler(ptrUIHandler);
        ptrFrame.setHeaderView(ptrUIHandler);

        ptrUIHandler.setLastUpdateTimeKey(getClass().getName());

        ptrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,content,header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                fresh();
            }
        });

        items=new ArrayList<>();

        adapter = new NewsAdapter(items);
        adapter.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                DataWrap dataWrap = items.get(position);
                Intent intent=null;
                int id = v.getId();
                switch (dataWrap.getType()) {
                    case 0:

                        break;
                    case 1:
                        if (id==R.id.news_single_icon) {
                            intent=new Intent(CollectActivity.this, NewsKindImageActivity.class);
                            startActivity(intent);
                        }else {
                            intent = new Intent(CollectActivity.this, NewsKindDetailActivty.class);
                            intent.putExtra("data", ((Article) dataWrap.getData()));
                            startActivity(intent);
                        }
                        break;
                    case 2:
                        intent = new Intent(CollectActivity.this, NewsKindDetailActivty.class);
                        intent.putExtra("data", ((Article) dataWrap.getData()));
                        startActivity(intent);
                        break;
                    case 3:
                        if (id==R.id.news_triple_icon1){
                            ToastUtils.show(CollectActivity.this,"picture1");
                        }else if (id==R.id.news_triple_icon2){
                            ToastUtils.show(CollectActivity.this,"picture2");

                        }else if (id==R.id.news_triple_icon3){
                            ToastUtils.show(CollectActivity.this,"picture3");

                        }else {
                            intent = new Intent(CollectActivity.this, NewsKindDetailActivty.class);
                            intent.putExtra("data", dataWrap);
                            startActivity(intent);
                        }
                        break;
                    case 5:

                        intent = new Intent(CollectActivity.this, NewsKindDetailActivty.class);
                        intent.putExtra("data", ((Article) dataWrap.getData()));
                        startActivity(intent);
                        break;
                    case 6:
                        intent = new Intent(CollectActivity.this, NewsKindDetailActivty.class);
                        intent.putExtra("data", ((Article) dataWrap.getData()));
                        startActivity(intent);
                        break;
                }
            }
        });
        rec.setLayoutManager(new LinearLayoutManager(mActivity));
        rec.setAdapter(adapter);

    }

    @Override
    public void initData() {
        super.initData();
        fresh();
    }

    private void fresh() {
        loadData(0,1);
    }

    private void loadMore(){
        loadData(1,cPage);
    }

    private void loadData(final int type, final int page){
        RequestParams entity = new RequestParams(UrlUtils.LIST_USER_COLLECT);

        entity.addBodyParameter("appuserId", UserManager.getUser().getAppuserId()+"");
        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {


                NetData netData = JSON.parseObject(result, NetData.class);

                if (netData.getResult()==200){
                    List<Article> articles = JSON.parseArray(netData.getInfo(), Article.class);

                    if (articles != null) {

                        if (articles.size()>0) {
                            isNodata(false);
                            if (type == 0) {
                                items.clear();
                            }else {
                                cPage++;
                            }
                            for (int i = 0; i < articles.size(); i++) {

                                DataWrap e = new DataWrap();
                                Article article = articles.get(i);
                                e.setData(article);
                                e.setType(article.getDisplayType());

                                items.add(e);
                            }
                            adapter.notifyDataSetChanged();
                        }else {

                            if (type==0) {
                                isNodata(true);
                                //UIUtils.showToast("暂无数据");
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                isNodata(true);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                ptrFrame.refreshComplete();
                if (bar.isShown()) {
                    bar.hide();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        x.view().inject(this);

        initView();
        initData();
    }
}
