package com.blue.xingui.manager;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue.xingui.R;
import com.blue.xingui.activity.OrderDetailActivity;
import com.blue.xingui.activity.PayActivity;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.Order;
import com.blue.xingui.bean.OrderGoods;
import com.blue.xingui.view.ptr.ListViewForScrollView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import static com.blue.xingui.R.id.order_handler;

/**
 * Created by cj on 2017/7/10.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.Holder> {

    private List<Order> datas;
    private BaseActivity mActivity;
    private LayoutInflater inf;
    private View.OnClickListener listener;
    public BaseAdapter listAdapter;



    public OrderAdapter(final List<Order> datas, BaseActivity activity) {
        this.datas = datas;
        mActivity = activity;

        inf = LayoutInflater.from(mActivity);

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order order = datas.get(((int) v.getTag()));
                switch (v.getId()) {

                    case R.id.order_parent:

                        break;
                    case R.id.order_handler:

                        switch (order.getOrderFlag()) {
                            case 0:
                                pay(order);
                                break;
                            case 1:
                                notification();
                                break;
                            case 2:
                                sureGet(order);
                                break;
                            case 4:
                                buyAgain();
                                break;
                        }
                        break;
                }
            }


        };

    }

    private void buyAgain() {

    }

    private void sureGet(Order order) {
        Intent intent=new Intent(mActivity,OrderDetailActivity.class);
        intent.putExtra("order",order);
        mActivity.startActivity(intent);
    }

    private void notification() {

    }

    private void pay(Order order) {
        Intent intent=new Intent(mActivity, PayActivity.class);
        intent.putExtra("id",order.getOrderNo());
        intent.putExtra("money",order.getMoneySum());
        mActivity.startActivity(intent);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        Holder holder = null;
        View view = null;
        switch (viewType) {
            case 0:
                view = inf.inflate(R.layout.order_item, parent, false);
                holder = new Holder(view, viewType);
                break;

        }
        x.view().inject(holder, view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final Order order = datas.get(position);

        switch (order.getType()) {
            case 0:

                int state = order.getOrderFlag();
                holder.parent.setTag(position);
                holder.parent.setOnClickListener(listener);

                holder.handler.setVisibility(View.VISIBLE);

                if (state == 0) {
                    holder.state.setText("待付款");
                    holder.handler.setText("去付款");
                } else if (state == 1) {
                    holder.state.setText("待发货");
                    //holder.handler.setText("提醒发货");
                    holder.handler.setVisibility(View.GONE);
                } else if (state == 2) {
                    holder.state.setText("待收货");
                    holder.handler.setText("查看物流");
                } else if (state == 4) {
                    holder.state.setText("已完成");
                    holder.handler.setText("再次购买");
                }
                holder.handler.setTag(position);
                holder.handler.setOnClickListener(listener);


                holder.number.setText(order.getOrderNo());
                holder.totalPrice.setText("￥" + order.getMoneySum());

                holder.listView.setAdapter(new MyListAdapter(order.getGoodsInfo()));

                break;
            case 1:

                break;
            case 2:

                break;
        }
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
        return datas.get(position).getType();
    }

    class MyListAdapter extends BaseAdapter{

        private List<OrderGoods> goodsData;

        public MyListAdapter(List<OrderGoods> listdata) {
            goodsData=listdata;
        }

        @Override
        public int getCount() {
            if (goodsData != null) {
                return goodsData.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View inflate = LayoutInflater.from(mActivity).inflate(R.layout.order_goods_item, parent, false);

            OrderGoods goods = goodsData.get(position);


            ImageView icon = (ImageView) inflate.findViewById(R.id.order_img);

            x.image().bind(icon,goods.getPicsrc(),xUtilsManager.getRectangleImageOption());

            ((TextView) inflate.findViewById(R.id.order_des)).setText(goods.getDesc());

            ((TextView) inflate.findViewById(R.id.order_count)).setText("￥"+goods.getPrice()+"(x"+goods.getCount()+")");

            ((TextView) inflate.findViewById(R.id.order_price)).setText("￥"+String.format("%.2f",goods.getPrice()*goods.getCount()));


            return inflate;
        }
    };

    class Holder extends RecyclerView.ViewHolder {
        @ViewInject(R.id.order_parent)
        View parent;
        @ViewInject(R.id.order_count)
        TextView count;
        @ViewInject(R.id.order_number)
        TextView number;
        @ViewInject(R.id.order_state)
        TextView state;
        @ViewInject(R.id.order_des)
        TextView des;
        @ViewInject(R.id.order_price)
        TextView price;
        @ViewInject(R.id.order_totalPrice)
        TextView totalPrice;
        @ViewInject(order_handler)
        TextView handler;
        @ViewInject(R.id.order_img)
        ImageView img;
        @ViewInject(R.id.order_list)
        ListViewForScrollView listView;



        public Holder(View itemView, int type) {
            super(itemView);
        }
    }
}
