package com.blue.xingui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.Address;
import com.blue.xingui.bean.CartGoods;
import com.blue.xingui.bean.Coupon;
import com.blue.xingui.bean.NetData;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.manager.xUtilsManager;
import com.blue.xingui.utils.UIUtils;
import com.blue.xingui.utils.UrlUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import static com.blue.xingui.activity.MallActivity.mall_action;

public class ShoppingCartActivity extends BaseActivity {

    @ViewInject(R.id.cart_ptr)
    PtrClassicFrameLayout ptrFrame;
    @ViewInject(R.id.cart_rec)
    RecyclerView rec;
    @ViewInject(R.id.cart_progress)
    ContentLoadingProgressBar progress;
    @ViewInject(R.id.cart_buy)
    TextView buy;
    @ViewInject(R.id.cart_clear)
    TextView clear;
    @ViewInject(R.id.cart_goods_count)
    TextView goods_count;
    @ViewInject(R.id.cart_price)
    TextView price;


    @ViewInject(R.id.address)
    TextView address;
    @ViewInject(R.id.name)
    TextView name;
    @ViewInject(R.id.phone)
    TextView phone;

    @ViewInject(R.id.cart_address)
    View cart_address;

    @ViewInject(R.id.cart_coupon)
    TextView cart_coupon;
    @ViewInject(R.id.cart_all_coupon)
    LinearLayout cart_all_coupon;

    @ViewInject(R.id.cart_no_address)
    LinearLayout cart_no_address;


    //private DbManager db;


    private List<CartGoods> datas;
    /*记录选择的id*/
    private List<Long> selectedData;
    private RecyclerView.Adapter<Holder> adapter;
    private boolean isLoading;
    private int cPage = 2;

    private int cSelectedCount = 0;
    public static final int SELECT_ADDRESS = 200;
    public static final int SELECT_COUPON = 100;
    public Address addressEx;
    public Coupon coupon;

    @Override
    public void initView() {
        initTop(R.mipmap.left_gray, "购物车", R.drawable.delete_gray);


        cart_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, MyAddressActivity.class);
                startActivityForResult(intent, SELECT_ADDRESS);
            }
        });

        cart_no_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, MyAddressActivity.class);
                startActivityForResult(intent, SELECT_ADDRESS);
            }
        });

        cart_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CouponActivity.class);
                intent.putExtra("flag",1);
                startActivityForResult(intent, SELECT_COUPON);
            }
        });

        PtrClassicDefaultHeader ptrUIHandler = new PtrClassicDefaultHeader(mActivity);
        ptrFrame.setHeaderView(ptrUIHandler);
        ptrFrame.addPtrUIHandler(ptrUIHandler);

        ptrUIHandler.setLastUpdateTimeKey(getClass().getName());

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

        datas = new ArrayList<>();
        selectedData = new ArrayList<>();

        rec.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new RecyclerView.Adapter<Holder>() {
            @Override
            public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(mActivity).inflate(R.layout.goods_item, parent, false);

                Holder holder = new Holder(view);

                x.view().inject(holder, view);

                return holder;
            }

            @Override
            public void onBindViewHolder(final Holder holder, int position) {

                final CartGoods goods = datas.get(position);

                holder.goods_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean selected = holder.goods_select.isSelected();
                        goods.setSelect(!selected);
                        holder.goods_select.setSelected(!selected);


                        if (selected) {
                            selectedData.remove(goods.getShopId());
                            cSelectedCount--;
                        } else {
                            selectedData.add(goods.getShopId());
                            cSelectedCount++;
                        }

                        updateBottom();

                    }
                });

                holder.goods_select.setSelected(goods.isSelect());

                x.image().bind(holder.goods_img, goods.getPicsrc(), xUtilsManager.getRectangleImageOption());

                /*holder.goods_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });*/

                holder.goods_des.setText(goods.getDesc());
                holder.goods_price.setText("￥" + goods.getPrice() + " x " + goods.getCount());
                holder.total_price.setText("￥" + String.format("%.2f", goods.getPrice() * goods.getCount()));

                holder.goods_count.setText(goods.getCount() + "");

                holder.goods_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modifyGoods(goods, 0, goods.getCount() + 1);
                    }
                });

                holder.goods_minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (goods.getCount() == 1) {
                            UIUtils.showToast("不能再减少了");
                        } else {
                            modifyGoods(goods, 0, goods.getCount() - 1);
                        }
                    }
                });

                holder.goods_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(mActivity)
                                .setMessage("确认要删除该商品？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        boolean selected = holder.goods_select.isSelected();

                                        if (selected) {
                                            holder.goods_select.setSelected(false);
                                            selectedData.remove(goods.getShopId());
                                            updateBottom();
                                        }

                                        modifyGoods(goods, 1, 0);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create().show();
                    }
                });
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

            /*滑动到4dp才算滑动了*/
            private int mScrollThreshold = 4;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if (!isLoading) {
                        //getMore();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                boolean isSignificantDelta = Math.abs(dy) > mScrollThreshold;
                if (isSignificantDelta) {
                    if (dy > 0) {
                        //onScrollUp();
                        /*if (cart_all_coupon.getVisibility() == View.GONE) {
                            cart_all_coupon.setVisibility(View.VISIBLE);
                            cart_all_coupon.setAnimation(moveToViewLocation());
                        }*/

                    } else {
                        //onScrollDown();

                        /*if (cart_all_coupon.getVisibility() == View.VISIBLE) {
                            cart_all_coupon.setVisibility(View.GONE);
                            cart_all_coupon.setAnimation(moveToViewBottom());
                        }*/
                    }
                }

            }

        });
    }

    private void updateData(int type){
        if (type==1){
            selectedData.clear();
        }else {
            for (int i = 0; i < selectedData.size() ; i++) {
                long id = selectedData.get(i);
                boolean isIndata=false;

                for (int j = 0; j < datas.size(); j++) {
                    CartGoods cartGoods = datas.get(j);
                    if (cartGoods.getShopId()==id){
                        cartGoods.setSelect(true);

                        isIndata=true;
                    }
                }

                if (!isIndata){
                    /*如果商品不在了就删除掉*/
                    selectedData.remove(id);
                }
            }
        }
    }
    private void updateBottom() {

        double tPrice = 0;

        for (int i = 0; i < datas.size(); i++) {
            CartGoods cartGoods = datas.get(i);

            for (int j = 0; j < selectedData.size(); j++) {
                if (cartGoods.getShopId() == selectedData.get(j)) {
                    double v = cartGoods.getPrice() * cartGoods.getCount();
                    tPrice += v;
                }
            }
        }

        if (coupon != null) {
            double v = tPrice - coupon.getCoupon();
            if (v < 0) {
                v = 0;
            }
            price.setText("￥" + String.format("%.2f", v));
        } else {
            price.setText("￥" + String.format("%.2f", tPrice));
        }

        cSelectedCount = selectedData.size();

        if (cSelectedCount < 99) {
            goods_count.setText(cSelectedCount + "");
        } else {
            goods_count.setText("99");
        }
    }

    private void fresh() {
        getGoods(1, 1);
    }

    private void getMore() {
        getGoods(cPage, 2);
    }

    @Override
    public void initData() {
        super.initData();

        //db = x.getDb(daoConfig);


        if (UserManager.isLogin()) {
            getGoods(1, 1);
        } else {
            /*try {
                List<Goods> all = db.findAll(Goods.class);
                if (all != null&&all.size()>0) {
                    datas.addAll(all);
                    adapter.notifyDataSetChanged();
                }else {
                    UIUtils.showToast("暂无商品");
                }

            } catch (DbException e) {
                e.printStackTrace();
            }*/
        }
    }

    private void getGoods(int page, final int type) {
        isLoading = true;

        RequestParams entity = new RequestParams(UrlUtils.getShopCartInfo);

        entity.addBodyParameter("appuserId", UserManager.getUser().getAppuserId() + "");
        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {


                JSONObject netData = JSON.parseObject(result);

                if (netData.getInteger("result") == 200) {

                    List<Address> addresses = JSON.parseArray(netData.getString("list"), Address.class);

                    if (addresses == null||addresses.size()==0) {
                        if (addressEx != null) {
                            cart_address.setVisibility(View.GONE);
                            cart_no_address.setVisibility(View.VISIBLE);
                        }
                    }else {

                        if (addressEx == null) {
                            cart_address.setVisibility(View.VISIBLE);
                            cart_no_address.setVisibility(View.GONE);
                        }

                        for (int i = 0; i < addresses.size(); i++) {
                            if (addresses.get(i).getIsDefault() == 1) {
                                addressEx = addresses.get(i);

                                address.setText(addressEx.getDistrict() + "-" + addressEx.getReceiveAddress());
                                name.setText("收件人：" + addressEx.getReceiveName());
                                phone.setText("电话：" + addressEx.getReceivePhone());
                            }
                        }
                    }

                    List<CartGoods> goodses = JSON.parseArray(netData.getString("info"), CartGoods.class);

                    if (goodses != null ) {

                        if (type == 1) {
                            datas.clear();
                            cPage = 2;
                            datas.addAll(goodses);
                            updateData(1);

                            updateBottom();
                        } else if (type == 2) {
                            if (goodses.size() > 0) {
                                cPage++;
                                datas.addAll(goodses);
                            }else {
                                UIUtils.showToast("没有更多了");
                            }

                        }
                        adapter.notifyDataSetChanged();
                    } else {

                    }


                } else {
                    UIUtils.showToast("服务器错误");
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
                ptrFrame.refreshComplete();
                isLoading = false;

                if (progress.isShown()) {
                    progress.hide();
                }
            }
        });
    }

    @Override
    public void onRightClick(View view) {
        super.onRightClick(view);

        new AlertDialog.Builder(mActivity)
                .setMessage("确定清空购物车？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        clearGoods();
                    }
                }).create().show();

        /*Intent intent = new Intent(mActivity, MyAddressActivity.class);
        startActivity(intent);*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        x.view().inject(this);

        initView();
        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        progress.show();

        fresh();

        /*if (true) {
            try {
                List<Address> all = db.findAll(Address.class);


                if (all != null && all.size() > 0) {

                    for (int i = 0; i < all.size(); i++) {
                        Address address1 = all.get(i);

                        if (address1.getIsDefault() == 1) {
                            addressEx = address1;
                            address.setText(addressEx.getDistrict() + "-" + addressEx.getReceiveAddress());
                            name.setText("收件人：" + addressEx.getReceiveName());
                            phone.setText("电话：" + addressEx.getReceivePhone());
                            break;
                        }
                    }

                }

            } catch (DbException e) {
                e.printStackTrace();
            }
        }*/
    }

    private void clearGoods() {

        final ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setMessage("清空中...");
        dialog.show();


        RequestParams entity = new RequestParams(UrlUtils.emptyShopCart);

        entity.addBodyParameter("appuserId", UserManager.getUser().getAppuserId() + "");
        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                NetData netData = JSON.parseObject(result, NetData.class);

                if (netData.getResult() == 200) {
                    UIUtils.showToast("清空成功");

                    datas.clear();
                    updateData(1);
                    cSelectedCount = 0;
                    updateBottom();

                    /*传个大的清空购物车*/
                    changeMall(0);
                    adapter.notifyDataSetChanged();
                    /*try {
                        db.dropTable(Goods.class);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }*/
                } else {
                    UIUtils.showToast("请求失败");
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

    private void changeMall(int i) {
        /**/
        Intent intent = new Intent();
        intent.setAction(mall_action);
        intent.putExtra("count", i);
        sendBroadcast(intent);

    }

    private void modifyGoods(final CartGoods goods, final int flag, int num) {

        final ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setMessage("加载中...");
        dialog.show();

        RequestParams entity = new RequestParams(UrlUtils.updateShopCart);

        entity.addBodyParameter("appuserId", UserManager.getUser().getAppuserId() + "");
        entity.addBodyParameter("dataId", "" + goods.getShopId());
        /*0修改1删除*/
        entity.addBodyParameter("flag", "" + flag);
        if (flag == 0) {
            entity.addBodyParameter("num", "" + num);
        }

        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                NetData netData = JSON.parseObject(result, NetData.class);

                if (netData.getResult() == 200) {


                    List<CartGoods> cartGoodses = JSON.parseArray(netData.getInfo(), CartGoods.class);

                    if (cartGoodses != null) {

                        /*按商品种类*/
                        int totalCount = cartGoodses.size();
                        /*for (int i = 0; i < cartGoodses.size(); i++) {
                            totalCount+=cartGoodses.get(i).getCount();
                        }*/
                        changeMall(totalCount);

                        datas.clear();
                        datas.addAll(cartGoodses);
                        updateData(2);
                        updateBottom();
                        adapter.notifyDataSetChanged();

                        /*if (flag == 1) {
                            try {
                                //db.delete(Goods.class, WhereBuilder.b("goodsId","=",goods.getGoodsId()));
                                db.execNonQuery("delete from goods where goodsId = " + goods.getGoodsId());
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }*/
                    } else {

                    }

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

    public void btn_clear(View view) {

        new AlertDialog.Builder(mActivity)
                .setMessage("确认要清除购物车？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearGoods();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();

    }

    public void btn_buy(View view) {
        if (datas != null && datas.size() > 0) {
            if (addressEx == null) {
                UIUtils.showToast("请选择收货地址");
                return;
            }

            if (selectedData.size() == 0) {

                UIUtils.showToast("请选择要购买的商品");
                return;
            }

            List<CartGoods> buyGoods = new ArrayList<>();
            for (int i = 0; i < datas.size(); i++) {
                CartGoods cartGoods = datas.get(i);

                for (int j = 0; j < selectedData.size(); j++) {
                    if (cartGoods.getShopId() == selectedData.get(j)) {
                        buyGoods.add(cartGoods);
                    }
                }
            }

            Intent intent=new Intent(mActivity,OrderGenerateActivity.class);
            intent.putExtra("goods", (Serializable) buyGoods);
            intent.putExtra("address",addressEx);
            startActivity(intent);

            //generateOrder(buyGoods);
        } else {
            UIUtils.showToast("请选择要购买的物品");
        }
    }

    private void generateOrder(List<CartGoods> goodses) {

        final ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setMessage("正在生成订单...");
        dialog.show();

        StringBuilder arg = new StringBuilder();

        arg.append("[");

        for (int i = 0; i < goodses.size(); i++) {
            arg.append("{");
            arg.append("\"" + "id" + "\"" + ":" + "\"" + goodses.get(i).getGoodsId() + "\"");
            arg.append("}");

            if (i != goodses.size() - 1) {
                arg.append(",");
            }
        }

        arg.append("]");
        RequestParams entity = new RequestParams(UrlUtils.getOrderId);

        String value = arg.toString();

        entity.addBodyParameter("appuserId", UserManager.getUser().getAppuserId() + "");
        entity.addBodyParameter("arg1", value);

        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                JSONObject object = JSON.parseObject(result);

                Integer code = object.getInteger("result");
                if (code == 200) {

                    String shopcartNum = object.getString("shopcartNum");

                    Intent intent = new Intent(mActivity, PayActivity.class);
                    intent.putExtra("id", object.getString("orderNo"));
                    startActivity(intent);
                } else if (code == 300) {
                    UIUtils.showToast("请先设置收货信息");
                    /*200:操作成功
                    300:请先设置收货信息
                    301:请先选择商品
                    400:参数错误
                    401:请先登录
                    500:服务器异常*/

                } else if (code == 301) {
                    UIUtils.showToast("请先选择商品");
                } else {
                    UIUtils.showToast("生成订单失败");
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

    /**
     * 从控件所在位置移动到控件的底部
     *
     * @return
     */
    public TranslateAnimation moveToViewBottom() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        mHiddenAction.setDuration(500);
        return mHiddenAction;
    }

    /**
     * 从控件的底部移动到控件所在位置
     *
     * @return
     */
    public TranslateAnimation moveToViewLocation() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(500);
        return mHiddenAction;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_ADDRESS) {
            if (data != null) {

                if (addressEx == null) {
                    cart_address.setVisibility(View.VISIBLE);
                    cart_no_address.setVisibility(View.GONE);
                }

                addressEx = (Address) data.getSerializableExtra("address");
                address.setText(addressEx.getDistrict() + "-" + addressEx.getReceiveAddress());
                name.setText("收件人：" + addressEx.getReceiveName());
                phone.setText("电话：" + addressEx.getReceivePhone());
            }else {

                if (addressEx==null) {
                    cart_address.setVisibility(View.GONE);
                    cart_no_address.setVisibility(View.VISIBLE);
                }
            }
        } else if (requestCode == SELECT_COUPON) {
            if (data != null) {

                int flag = data.getIntExtra("flag", 0);

                if (flag == 1) {
                    cart_coupon.setText("不使用优惠券");
                    cart_coupon.setTextColor(getResources().getColor(R.color.xxlight_gray));
                } else {
                    coupon = (Coupon) data.getSerializableExtra("coupon");

                    if (true) {
                        cart_coupon.setText("￥" + coupon.getCoupon() + "优惠券");
                        cart_coupon.setTextColor(getResources().getColor(R.color.price_red));
                    }
                }
                updateBottom();
            }
        }
    }

    class Holder extends RecyclerView.ViewHolder {

        @ViewInject(R.id.goods_select)
        ImageView goods_select;
        @ViewInject(R.id.goods_add)
        ImageView goods_add;
        @ViewInject(R.id.goods_minus)
        ImageView goods_minus;
        @ViewInject(R.id.order_img)
        ImageView goods_img;

        @ViewInject(R.id.goods_count)
        TextView goods_count;
        @ViewInject(R.id.order_des)
        TextView goods_des;
        @ViewInject(R.id.goods_price)
        TextView goods_price;
        @ViewInject(R.id.goods_total_price)
        TextView total_price;

        @ViewInject(R.id.goods_delete)
        TextView goods_delete;

        public Holder(View itemView) {
            super(itemView);
        }
    }
}
