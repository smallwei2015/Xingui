package com.blue.xingui.fragment;


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
import com.blue.xingui.base.BaseFragment;
import com.blue.xingui.bean.NetData;
import com.blue.xingui.bean.Order;
import com.blue.xingui.manager.OrderAdapter;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.utils.AnimationUtils;
import com.blue.xingui.utils.UIUtils;
import com.blue.xingui.utils.UrlUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_order)
public class OrderFragment extends BaseFragment {


    @ViewInject(R.id.order_ptr)
    PtrClassicFrameLayout ptrFrame;
    @ViewInject(R.id.order_rec)
    RecyclerView rec;
    @ViewInject(R.id.order_progress)
    ContentLoadingProgressBar progress;
    @ViewInject(R.id.no_data)
    View no_data;

    private List<Order> datas;
    private OrderAdapter adapter;
    private boolean isLoading;

    private int cPage=2;
    public int orderType;

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = x.view().inject(this, inflater, container);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

        no_data.setVisibility(View.GONE);

        PtrClassicDefaultHeader header = new PtrClassicDefaultHeader(getContext());
        ptrFrame.setHeaderView(header);
        ptrFrame.addPtrUIHandler(header);

        header.setLastUpdateTimeKey(this.getClass().getName());

        ptrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                fresh();
            }
        });

        datas=new ArrayList<>();

        rec.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new OrderAdapter(datas,mActivity);
        rec.setAdapter(adapter);

        rec.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)){
                    if (!isLoading){
                        loadMore();
                    }else {
                        UIUtils.showToast("加载中...");
                    }
                }
            }
        });
    }

    private void fresh(){
        getData(0,1);
    }
    private void loadMore() {
        getData(1,cPage);
    }

    private void getData(final int type, int page){

        isLoading=true;

        RequestParams entity = new RequestParams(UrlUtils.getOrderInfoByAppuser);

        entity.addBodyParameter("appuserId", UserManager.getUser().getAppuserId()+"");
        entity.addBodyParameter("flag",orderType+"");
        entity.addBodyParameter("page",page+"");

        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {


                NetData netData = JSON.parseObject(result, NetData.class);
                if (netData.getResult()==200){

                    List<Order> orders = JSON.parseArray(netData.getInfo(), Order.class);

                    if (orders!=null) {
                        if (type == 0) {
                            datas.clear();
                            cPage=2;
                            if (orders.size()>0){
                                no_data.setVisibility(View.GONE);
                            }else {
                                if (no_data.getVisibility()!=View.VISIBLE) {

                                    no_data.setAnimation(AnimationUtils.scaleToSelfSize());

                                    no_data.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            if(orders.size()>0) {
                                cPage++;
                            }
                        }

                        datas.addAll(orders);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                no_data.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                ptrFrame.refreshComplete();
                isLoading=false;

                if (progress.isShown()) {
                    progress.hide();
                }
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        orderType = getArguments().getInt("type");
        fresh();
    }
}
