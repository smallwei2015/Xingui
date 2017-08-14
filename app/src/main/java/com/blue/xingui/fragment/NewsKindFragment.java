package com.blue.xingui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.blue.xingui.R;
import com.blue.xingui.activity.NewsKindDetailActivty;
import com.blue.xingui.activity.NewsKindImageActivity;
import com.blue.xingui.base.BaseFragment;
import com.blue.xingui.bean.Article;
import com.blue.xingui.bean.DataWrap;
import com.blue.xingui.manager.NewsAdapter;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.utils.AnimationUtils;
import com.blue.xingui.utils.ToastUtils;
import com.blue.xingui.utils.UrlUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsKindFragment extends BaseFragment {


    private RecyclerView recycler;
    private LinearLayoutManager manager;
    private NewsAdapter adapter;
    private List<DataWrap> items;

    private int curPage=1;
    private ContentLoadingProgressBar loading;
    private boolean isloading;
    private in.srain.cube.views.ptr.PtrFrameLayout ptrFra;
    public View no_data;


    public NewsKindFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_news_kind, container, false);

        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        recycler = ((RecyclerView) view.findViewById(R.id.recycler));

        loading = ((ContentLoadingProgressBar) view.findViewById(R.id.loading));

        no_data = view.findViewById(R.id.no_data);
        no_data.setVisibility(View.GONE);

        ptrFra = ((in.srain.cube.views.ptr.PtrFrameLayout) view.findViewById(R.id.framelayout));

        PtrClassicDefaultHeader header = new PtrClassicDefaultHeader(getContext());
        ptrFra.setHeaderView(header);
        ptrFra.addPtrUIHandler(header);

        header.setLastUpdateTimeKey(this.getClass().getName());

        ptrFra.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(in.srain.cube.views.ptr.PtrFrameLayout frame, View content, View header) {
                //return !checkContentCanPullDown(frame, content, header);

                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(in.srain.cube.views.ptr.PtrFrameLayout frame) {
                fresh(75);
            }
        });


        isloading=false;
        recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recycler.canScrollVertically(1)&&items.size()>0){

                    if (!isloading) {
                        if (UserManager.isLogin()) {
                            load(UserManager.getUser().getAppuserId(), curPage + 1);
                        }
                    }else {
                        ToastUtils.show(getActivity(),"加载中...");
                    }
                }
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        items = new ArrayList<>();

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
                        intent = new Intent(getActivity(), NewsKindDetailActivty.class);
                        intent.putExtra("data", ((Article) dataWrap.getData()));
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getActivity(), NewsKindDetailActivty.class);
                        intent.putExtra("data", ((Article) dataWrap.getData()));
                        startActivity(intent);
                        break;
                    case 2:
                        if (id==R.id.news_single_icon) {
                            intent=new Intent(getActivity(), NewsKindImageActivity.class);
                            startActivity(intent);
                        }else {
                            intent = new Intent(getActivity(), NewsKindDetailActivty.class);
                            intent.putExtra("data", ((Article) dataWrap.getData()));
                            startActivity(intent);
                        }
                        break;
                    case 3:
                        intent = new Intent(getActivity(), NewsKindDetailActivty.class);
                        intent.putExtra("data", ((Article) dataWrap.getData()));
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(getActivity(), NewsKindDetailActivty.class);
                        intent.putExtra("data", ((Article) dataWrap.getData()));
                        startActivity(intent);
                        break;
                    case 6:
                        if (id==R.id.news_triple_icon1){
                            ToastUtils.show(getActivity(),"picture1");
                        }else if (id==R.id.news_triple_icon2){
                            ToastUtils.show(getActivity(),"picture2");

                        }else if (id==R.id.news_triple_icon3){
                            ToastUtils.show(getActivity(),"picture3");
                        }else {
                            intent = new Intent(getActivity(), NewsKindDetailActivty.class);
                            intent.putExtra("data", ((Article) dataWrap.getData()));
                            startActivity(intent);
                        }
                        break;
                }
            }
        });
        recycler.setAdapter(adapter);

    }


    @Override
    public void initData() {
        super.initData();

        fresh(1);

        adapter.notifyDataSetChanged();
    }

    private void fresh(int UID){

        RequestParams params=new RequestParams(UrlUtils.NEWS+"?appuserId="+UID+"&page="+1);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                loading.setVisibility(View.GONE);
                ptrFra.refreshComplete();

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    int code = jsonObject.getInt("result");

                    if (code == 200) {
                        /*重置当前page*/
                        curPage=1;
                        //items.removeAll(items);
                        items.clear();


                        String hot = jsonObject.getString("hot");
                        String info = jsonObject.getString("info");
                        List<Article> hots = JSON.parseArray(hot, Article.class);
                        List<Article> articles = JSON.parseArray(info, Article.class);

                        if (hots!=null&&hots.size()>0) {
                            DataWrap e1 = new DataWrap();
                            e1.setType(0);
                            e1.setData(hots);
                            items.add(e1);
                        }
                        for (int i = 0; i < articles.size(); i++) {

                            DataWrap e = new DataWrap();
                            Article article = articles.get(i);
                            e.setData(article);
                            e.setType(article.getDisplayType());

                            items.add(e);
                            adapter.notifyDataSetChanged();
                        }

                        if (items.size()>0){
                            no_data.setVisibility(View.GONE);
                        }else {
                            if (no_data.getVisibility()!=View.VISIBLE){
                                no_data.setAnimation(AnimationUtils.scaleToSelfSize());
                                no_data.setVisibility(View.VISIBLE);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }else {
                        ToastUtils.show(getActivity(),"数据刷新失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (no_data.getVisibility()!=View.VISIBLE){
                    no_data.setAnimation(AnimationUtils.scaleToSelfSize());
                    no_data.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

                ptrFra.refreshComplete();
                loading.setVisibility(View.GONE);
            }
        });
    }
    private void load(long uid, final int page) {

        /*修改正在加载，这时就不可以上拉加载，避免网络较慢的情况下，多此加载相同数据*/
        //loading.setVisibility(View.VISIBLE);
        isloading=true;

        RequestParams params=new RequestParams(UrlUtils.NEWS+"?appuserId="+uid+"&page="+page);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    int code = jsonObject.getInt("result");

                    if (code == 200) {

                        String hot = jsonObject.getString("hot");
                        String info = jsonObject.getString("info");
                        List<Article> hots = JSON.parseArray(hot, Article.class);
                        List<Article> articles = JSON.parseArray(info, Article.class);


                        if (articles.size()==0){
                            //ToastUtils.show(getActivity(),"没有更多了");
                            return;
                        }

                        if (hots!=null&&hots.size()>0&&page==1) {
                            DataWrap e1 = new DataWrap();
                            e1.setType(0);
                            e1.setData(hots);
                            items.add(e1);
                        }

                        for (int i = 0; i < articles.size(); i++) {

                            DataWrap e = new DataWrap();
                            Article article = articles.get(i);
                            e.setData(article);
                            e.setType(article.getDisplayType());
                            items.add(e);
                        }


                        adapter.notifyDataSetChanged();
                        curPage++;
                    }else {
                        ToastUtils.show(getActivity(),"没有更多了");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
                /*上拉加载结束，隐藏掉*/
                loading.setVisibility(View.GONE);
                isloading=false;
            }
        });
    }


}
