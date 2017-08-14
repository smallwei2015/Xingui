package com.blue.xingui.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.Goods;
import com.blue.xingui.bean.NetData;
import com.blue.xingui.manager.UltraPagerAdapter;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.manager.xUtilsManager;
import com.blue.xingui.utils.SPUtils;
import com.blue.xingui.utils.UIUtils;
import com.blue.xingui.utils.UrlUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import static com.blue.xingui.manager.UserManager.action_in;
import static com.blue.xingui.manager.UserManager.action_out;
import static com.blue.xingui.utils.UIUtils.getContext;

public class MallActivity extends BaseActivity {


    @ViewInject(R.id.mall_rec)
    RecyclerView rec;
    @ViewInject(R.id.loading)
    ContentLoadingProgressBar loading;

    @ViewInject(R.id.count)
    TextView goods_count;

    @ViewInject(R.id.count)
    TextView right_count;

    private List<Goods> datas;
    private List<Goods> hotGoods;
    private StaggeredGridLayoutManager manager;
    private PtrFrameLayout mPtrFrameLayout;

    private boolean isLoading;
    public static String mall_action ="com.xingui.addgoods";
    private int goodsCount;
    //private DbManager db;
    private int cPage=2;
    public RecyclerView.Adapter<Holder> adapter;
    public int netState;
    private SharedPreferences sp;
    public List<View> contains;
    public PagerAdapter adapter1;
    public UltraPagerAdapter ultraPagerAdapter;


    @Override
    public void initData() {
        super.initData();

        sp=SPUtils.getSP();
        //db = x.getDb(daoConfig);

        /*try {
            List<Goods> all = db.findAll(Goods.class);
            if (all != null) {
                UIUtils.showToast("hhh"+all.size());
            }

        } catch (DbException e) {
            e.printStackTrace();
        }*/


        IntentFilter filter = new IntentFilter();
        filter.addAction(mall_action);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int count = intent.getIntExtra("count",-1);

                if (goods_count != null) {


                    if (count != -1) {
                        goodsCount = count;

                        if (goodsCount < 99) {
                            if (goodsCount < 0) {
                                goodsCount = 0;
                            }

                            if (goodsCount == 0) {
                                goods_count.setVisibility(View.GONE);
                            } else {
                                goods_count.setVisibility(View.VISIBLE);
                            }
                            goods_count.setText("" + goodsCount);
                        } else {
                            goods_count.setText("99");
                        }
                    }

                }

            }
        }, filter);


        datas=new ArrayList<>();

        getGoods(1,1);
    }

    private void getGoods(int page, final int type) {
        isLoading=true;

        RequestParams entity = new RequestParams(UrlUtils.getGoodsInfo);
        if (UserManager.isLogin()){
            entity.addBodyParameter("appuserId",UserManager.getUser().getAppuserId()+"");
        }
        entity.addBodyParameter("page",page+"");
        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Log.w("3333",result);

                NetData netData = JSON.parseObject(result, NetData.class);

                Integer shopcarNum = JSON.parseObject(result).getInteger("shopcarNum");
                /*初始化购物车数量*/

                Intent intent = new Intent();
                intent.setAction(mall_action);
                intent.putExtra("count",shopcarNum);
                sendBroadcast(intent);

                if (netData.getResult()==200){
                    List<Goods> hotGoods1 = JSON.parseArray(netData.getHot(), Goods.class);

                    /*if (hotGoods1 != null&&hotGoods1.size()>0) {
                        hotGoods.clear();
                        hotGoods.addAll(hotGoods1);
                    }*/
                    List<Goods> goodses = JSON.parseArray(netData.getInfo(), Goods.class);

                    if (goodses != null&&goodses.size()>0) {
                        isNodata(false);
                        if (type==1){
                            datas.clear();
                            cPage=2;
                            datas.addAll(goodses);
                        }else if (type==2){
                            cPage++;
                            datas.addAll(goodses);
                        }
                        adapter.notifyDataSetChanged();
                    }else {

                        if (type==1) {
                            datas.clear();

                            if (datas.size()==0) {
                                isNodata(true);
                            }else {
                                isNodata(false);
                            }
                        }
                    }

                }else {
                    UIUtils.showToast("服务器错误");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                UIUtils.showToast("网络请求失败");
                if (datas.size()==0) {
                    isNodata(true);
                }else {
                    isNodata(false);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                mPtrFrameLayout.refreshComplete();
                isLoading=false;

                if (loading.isShown()) {
                    loading.hide();
                }
            }
        });
    }



    @Override
    public void initView() {
        super.initView();
        initTop(R.drawable.message_red,null,R.drawable.mall_red);

        goods_count.setText(goodsCount+"");
        hotGoods=new ArrayList<>();
        contains = new ArrayList<>();


        //manager=new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);


        /*ViewGroup.LayoutParams layoutParams = cycleView.getLayoutParams();
        layoutParams.height= (int) (UIUtils.getWindowWidth(mActivity)*0.5);
        cycleView.setLayoutParams(layoutParams);



        adapter1 = new PagerAdapter() {
            @Override
            public int getCount() {

                if (hotGoods != null&&hotGoods.size()>0) {
                    return Integer.MAX_VALUE;
                }
                return 0;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {

                int t=position%hotGoods.size();
                View child = contains.get(t);

                ViewParent parent = child.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeView(child);
                }
                container.addView(child);
                return child;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {

                *//*position%=hotGoods.size();
                container.removeView(container.getChildAt(position));*//*
            }
        };
        cycleView.setAdapter(adapter1);*/


        /*UltraViewPager ultraViewPager = (UltraViewPager)findViewById(R.id.ultra_viewpager);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
//UltraPagerAdapter 绑定子view到UltraViewPager


        ultraPagerAdapter = new UltraPagerAdapter(false,hotGoods);
        ultraViewPager.setAdapter(ultraPagerAdapter);

//内置indicator初始化
        ultraViewPager.initIndicator();
//设置indicator样式
        ultraViewPager.getIndicator()
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(Color.GREEN)
                .setNormalColor(Color.WHITE)
                .setRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
//设置indicator对齐方式
        ultraViewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
//构造indicator,绑定到UltraViewPager
        ultraViewPager.getIndicator().build();

//设定页面循环播放
        ultraViewPager.setInfiniteLoop(true);
//设定页面自动切换  间隔2秒
        ultraViewPager.setAutoScroll(2000);*/


        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 2);


        rec.setLayoutManager(gridLayoutManager);
        this.adapter = new RecyclerView.Adapter<Holder>() {
            @Override
            public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(mActivity).inflate(R.layout.mall_item,parent,false);

                return new Holder(view);
            }

            @Override
            public void onBindViewHolder(Holder holder, int position) {

                final Goods goods = datas.get(position);


                holder.parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mActivity,MallDetailActivity.class);
                        intent.putExtra("data",goods);
                        startActivity(intent);
                    }
                });

                holder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserManager.isLogin()) {

                            if (goods.getStockCount()>0) {
                                addGoods(goods);
                            }else {
                                UIUtils.showToast("当前库存量为0");
                            }
                        }else {
                            UserManager.toLogin();
                        }
                    }
                });
                if (!TextUtils.isEmpty(goods.getPicsrc())) {
                    x.image().bind(holder.img, goods.getPicsrc(), xUtilsManager.getRectangleImageOption());
                }


                holder.title.setText(goods.getTitle());

                holder.price.setText("￥"+goods.getPrice());

                holder.count.setText("库存量"+goods.getStockCount()+"件");
            }

            @Override
            public int getItemCount() {
                if (datas != null) {
                    return datas.size();
                }
                return 0;
            }

            @Override
            public int getItemViewType(int position) {
                return super.getItemViewType(position);
            }
        };
        rec.setAdapter(this.adapter);
//        rec.addItemDecoration(new DividerItemDecoration(mActivity, LinearLayout.VERTICAL));
//        rec.addItemDecoration(new DividerItemDecoration(mActivity,LinearLayout.HORIZONTAL));

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

        mPtrFrameLayout = ((PtrFrameLayout) findViewById(R.id.framelayout));

        PtrClassicDefaultHeader header = new PtrClassicDefaultHeader(getContext());
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);

        header.setLastUpdateTimeKey(this.getClass().getName());

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(in.srain.cube.views.ptr.PtrFrameLayout frame, View content, View header) {
                //return !checkContentCanPullDown(frame, content, header);

                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(final in.srain.cube.views.ptr.PtrFrameLayout frame) {
                fresh();
            }
        });

    }

    private void addGoods(final Goods goods) {


        final ProgressDialog dialog=new ProgressDialog(mActivity);
        dialog.setMessage("加载中...");

        dialog.show();

        RequestParams entity = new RequestParams(UrlUtils.joinShopCart);

        entity.addBodyParameter("appuserId", UserManager.getUser().getAppuserId()+"");
        entity.addBodyParameter("dataId", goods.getGoodsId()+"");
        entity.addBodyParameter("num","1");

        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                NetData netData = JSON.parseObject(result, NetData.class);

                Integer shopcarNum = JSON.parseObject(result).getInteger("shopcarNum");

                if (netData.getResult()==200){
                    Intent intent = new Intent();
                    intent.setAction(mall_action);
                    intent.putExtra("count",shopcarNum);
                    sendBroadcast(intent);



                    UIUtils.showToast("成功添加到购物车");
                    /*try {
                        db.saveOrUpdate(goods);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }*/
                }else {
                    UIUtils.showToast("添加失败");
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

    private void fresh(){
        getGoods(1,1);
    }
    private void loadMore() {
        getGoods(cPage,2);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall);

        registerUserReceiver();
        registerNetReceiver();
        x.view().inject(this);

        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );*/

        fresh();
    }

    @Override
    public void onUserReciver(Intent intent) {
        super.onUserReciver(intent);

        String action = intent.getAction();
        if (action.equals(action_in)){

            fresh();
        }else if (action.equalsIgnoreCase(action_out)){
            fresh();
        }else if (action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)){
            ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                // unconnect network
                UIUtils.showToast("网络已经断开");
                netState =-1;

            }else {
                // connect network
                if (netState!=0) {
                    fresh();
                }
                if (mobNetInfo.isConnected()) {
                    UIUtils.showToast("当前为移动网络，请注意资费");

                    netState = 1;
                }else if (wifiNetInfo.isConnected()){
                    if (netState==1) {
                        UIUtils.showToast("已经切换到wifi，尽情玩耍吧");
                    }
                    netState = 2;
                }


                //UIUtils.showToast("网络连接成功");
            }
        }
    }

    @Override
    public void onRightClick(View view) {
        super.onRightClick(view);

        if (UserManager.isLogin()) {
            Intent intent = new Intent(mActivity, ShoppingCartActivity.class);
            startActivity(intent);
        }else {
            UserManager.toLogin();
        }
    }

    @Override
    public void onLeftClick(View view) {
        //super.onLeftClick(view);

        if (UserManager.isLogin()) {
            Intent intent = new Intent(mActivity, InfoActivity.class);
            startActivity(intent);
        }else {
            UserManager.toLogin();
        }
    }

    @Override
    public void onLeftIconClick(View view) {
        //super.onLeftIconClick(view);
        Intent intent = new Intent(mActivity, InfoActivity.class);
        startActivity(intent);
    }

    class Holder extends RecyclerView.ViewHolder{

        View parent;
        TextView title,price,count;
        ImageView img,add;
        public Holder(View itemView) {
            super(itemView);

            parent=itemView;

            img= (ImageView) itemView.findViewById(R.id.goods_img);
            add= (ImageView) itemView.findViewById(R.id.mall_add);
            title= (TextView) itemView.findViewById(R.id.goods_title);
            price= (TextView) itemView.findViewById(R.id.mall_goods_price);
            count= (TextView) itemView.findViewById(R.id.mall_sale_count);

            int windowWidth = UIUtils.getWindowWidth(mActivity);

            ViewGroup.LayoutParams params = img.getLayoutParams();
            //设置图片的相对于屏幕的宽高比
            //params.width = windowWidth/2;
            params.height= (int) (windowWidth/2.2);
            img.setLayoutParams(params);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();


        long cPresstime = sp.getLong("cPresstime", -1);

        if (cPresstime>0&&System.currentTimeMillis()-cPresstime<1000){
            finish();
        }else {
            UIUtils.showToast("再次点击退出程序");
        }
        sp.edit().putLong("cPresstime",System.currentTimeMillis()).apply();

    }

}
