package com.blue.xingui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.BarrelNetInfo;
import com.blue.xingui.bean.NetData;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.utils.UIUtils;
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

public class BarrelInfoActivity extends BaseActivity {

    @ViewInject(R.id.barrel_rec)
    RecyclerView rec;
    @ViewInject(R.id.barrel_loading)
    ContentLoadingProgressBar bar;
    @ViewInject(R.id.ptr_frame)
    PtrClassicFrameLayout ptrFrame;

    private List<BarrelNetInfo> datas;
    private RecyclerView.Adapter<Holder> adapter;
    private boolean isLoading=false;

    @Override
    public void initView() {
        super.initView();
        initTop(R.mipmap.left_gray,"酒桶信息",-1);


        PtrClassicDefaultHeader header = new PtrClassicDefaultHeader(mActivity);
        ptrFrame.setHeaderView(header);
        ptrFrame.addPtrUIHandler(header);
        header.setLastUpdateTimeKey(getClass().getName());

        ptrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,content,header);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                fresh();
            }
        });

        datas=new ArrayList<>();

        rec.setLayoutManager(new LinearLayoutManager(mActivity));

        adapter = new RecyclerView.Adapter<Holder>() {

            public View.OnClickListener listener;

            @Override
            public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(mActivity).inflate(R.layout.barrel_item,parent,false);
                Holder holder = new Holder(view);


                x.view().inject(holder,view);
                return holder;
            }

            @Override
            public void onBindViewHolder(Holder holder, int position) {

                final BarrelNetInfo barrel = datas.get(position);
                holder.barrel_number.setText(barrel.getCaskNum());
                int capacityType = barrel.getCapacityType();
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int tag = (int) v.getTag();
                        BarrelNetInfo barrelNetInfo = datas.get(tag);
                        switch (v.getId()){
                            case R.id.barrel_title:
                            case R.id.bartender_name:
                                int type = barrelNetInfo.getCapacityType();
                                if (type==1){
                                    UIUtils.showToast("当前酒量充足，无需添加");
                                }else if (type==2){
                                    notifyAddWine(barrelNetInfo);
                                }else if (type==3){
                                    notifyAddWine(barrelNetInfo);
                                }else {
                                    UIUtils.showToast("当前酒桶异常，请联系相关人员");
                                }
                                break;

                        }
                    }
                };
                holder.title.setOnClickListener(listener);
                holder.title.setTag(position);
                if (capacityType ==1){
                    holder.barrel_state.setImageResource(R.mipmap.light_green);
                }else if (capacityType==2){
                    holder.barrel_state.setImageResource(R.mipmap.light_blue);
                }else if (capacityType==3){
                    holder.barrel_state.setImageResource(R.mipmap.light_red);
                }else {
                    holder.barrel_state.setImageResource(R.mipmap.exception);
                }

                holder.hotel_name.setText(barrel.getHotelName());
                holder.hotel_address.setText(barrel.getLocation());
                holder.hotel_phone.setText(barrel.getHotelPhone());
                holder.hotel_phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        call(barrel.getHotelPhone());
                    }
                });

                holder.bartender_name.setText(barrel.getWineMaster());
                holder.bartender_name.setTag(position);
                holder.bartender_name.setOnClickListener(listener);

                holder.bartender_phone.setText(barrel.getMasterPhone());
                holder.bartender_phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        call(barrel.getMasterPhone());
                    }
                });
                holder.capacity.setText(barrel.getCurrentCapacity()+"L/"+barrel.getTotalCapacity()+"L");


            }


            @Override
            public int getItemCount() {
                if (datas != null) {
                    return datas.size();
                }
                return 0;
            }
        };
        rec.setAdapter(adapter);
        rec.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)){
                    if (!isLoading) {
                        //loadMore();
                    }else {
                        UIUtils.showToast("加载中...");
                    }
                }
            }
        });



    }

    private void notifyAddWine(final BarrelNetInfo barrelNetInfo) {

        /*final View inflate = LayoutInflater.from(mActivity).inflate(R.layout.notify_add_wine, null);
        new AlertDialog.Builder(mActivity)
                .setView(inflate)
                .create().show();
        ((TextView) inflate.findViewById(R.id.barrel_number)).setText(barrelNetInfo.getCaskNum());

        ((TextView) inflate.findViewById(R.id.wine_tips)).setText("该酒桶当前酒量为"+barrelNetInfo.getCurrentCapacity()+
                "L,该酒桶总容量为"+barrelNetInfo.getTotalCapacity()+"L");
        inflate.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {

            public int number;

            @Override
            public void onClick(View v) {
                final ProgressDialog dialog=new ProgressDialog(mActivity);
                dialog.setMessage("网络加载中...");
                dialog.show();
                EditText et_number = (EditText) inflate.findViewById(R.id.wine_number);

                String s = et_number.getText().toString();

                if (s != null) {
                    number = Integer.parseInt(s);
                }else {
                    number= (int) (barrelNetInfo.getTotalCapacity()-barrelNetInfo.getCurrentCapacity());
                }

                Log.w("3333",number+"");

                RequestParams entity = new RequestParams(UrlUtils.notifyTheMasterOfBeer);

                entity.addBodyParameter("dataId",barrelNetInfo.getDataId()+"");
                entity.addBodyParameter("num",number+"");
                x.http().post(entity, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        NetData netData = JSON.parseObject(result, NetData.class);

                        if (netData.getResult()==200){
                            UIUtils.showToast("通知成功");
                        }else {
                            UIUtils.showToast("通知失败");
                        }

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        UIUtils.showToast("网络请求失败");
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {
                        dialog.dismiss();
                    }
                });
            }
        });*/



        Intent intent=new Intent(mActivity,SupplyActivity.class);
        intent.putExtra("data",barrelNetInfo);
        startActivity(intent);


    }

    private void fresh(){
        getInfo(0,1,0);
    }
    private void loadMore() {

        isLoading=true;

        isLoading=false;
    }

    private void  getInfo(final int type, int page, int state){
        String path = UrlUtils.achieveCaskInfoOfAgent;
        switch (UserManager.getUser().getUserType()){
            /*0普通用户1加酒师傅
2二级代理3一级代理*/

            case 0:

                break;
            case 1:
                path=UrlUtils.achieveCaskInfoOfWineMaster;
                break;
            case 2:
                path = UrlUtils.achieveCaskInfoOfAgent;
                break;
            case 3:
                path = UrlUtils.achieveCaskInfoOfAgent;
                break;
        }

        RequestParams entity = new RequestParams(path);

        entity.addBodyParameter("appuserId", UserManager.getUser().getAppuserId()+"");

        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                NetData netData = JSON.parseObject(result, NetData.class);

                if (netData.getResult()==200){
                    List<BarrelNetInfo> barrelNetInfos = JSON.parseArray(netData.getInfo(), BarrelNetInfo.class);

                    if (barrelNetInfos != null) {

                        if (type==0) {

                            if (barrelNetInfos.size()>0){
                                isNodata(false);
                            }else {
                                isNodata(true);
                            }
                            datas.clear();
                            datas.addAll(barrelNetInfos);
                        }
                        adapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                UIUtils.showToast("网络连接失败");
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
    public void initData() {
        super.initData();

        fresh();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barrel_info);
        x.view().inject(this);
        initView();
        initData();
    }

    private void call(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    class Holder extends RecyclerView.ViewHolder{

        @ViewInject(R.id.hotel_address)
        TextView hotel_address;
        @ViewInject(R.id.hotel_name)
        TextView hotel_name;
        @ViewInject(R.id.hotel_phone)
        TextView hotel_phone;
        @ViewInject(R.id.capacity)
        TextView capacity;
        @ViewInject(R.id.bartender_name)
        TextView bartender_name;
        @ViewInject(R.id.bartender_phone)
        TextView bartender_phone;
        @ViewInject(R.id.barrel_number)
        TextView barrel_number;
        @ViewInject(R.id.barrel_state)
        ImageView barrel_state;
        @ViewInject(R.id.barrel_title)
        LinearLayout title;

        public Holder(View itemView) {
            super(itemView);
        }
    }
}
