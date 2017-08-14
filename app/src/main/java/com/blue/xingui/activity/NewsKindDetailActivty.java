package com.blue.xingui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.blue.xingui.bean.Article;
import com.blue.xingui.manager.UserManager;
import com.blue.xingui.utils.FileUtils;
import com.blue.xingui.utils.ToastUtils;
import com.blue.xingui.utils.UrlUtils;
import com.blue.xingui.view.ptr.ListViewForScrollView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import static com.blue.xingui.utils.UIUtils.getContext;


public class NewsKindDetailActivty extends BaseActivity {

    @ViewInject(R.id.news_detail_webview)
    WebView webView;

    @ViewInject(R.id.news_input)
    EditText edit;

    @ViewInject(R.id.news_send)
    TextView send;

    @ViewInject(R.id.news_comment)
    ImageView comment;
    @ViewInject(R.id.news_collect)
    ImageView collect;
    @ViewInject(R.id.news_share)
    ImageView share;

    @ViewInject(R.id.news_bottom)
    LinearLayout bottom;

    @ViewInject(R.id.news_count)
    TextView count;

    @ViewInject(R.id.listview)
    ListViewForScrollView listView;

    @ViewInject(R.id.news_detail_progress)
    ProgressBar progressBar;

    @ViewInject(R.id.news_detail_scroll)
    ScrollView scrollView;

    private Article data;

    private long contentId;

    private List<Comment> datas;
    private BaseAdapter adapter;
    private PtrFrameLayout ptrFrame;
    private WebSettings mSetting;

    @Override
    public void initView() {
        initTop(R.mipmap.left_gray, "新闻详情", -1);
        data = ((Article) getIntent().getSerializableExtra("data"));
        datas = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                // hide virtual keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);

                edit.clearFocus();
                //webView.requestFocus();
            }
        });


        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (datas != null) {
                    return datas.size();
                }
                return 0;
            }

            @Override
            public Object getItem(int position) {
                return datas.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = LayoutInflater.from(NewsKindDetailActivty.this).inflate(R.layout.news_comment, parent, false);
                }
                ImageView icon = (ImageView) convertView.findViewById(R.id.comment_icon);
                TextView name = (TextView) convertView.findViewById(R.id.comment_name);
                TextView content = (TextView) convertView.findViewById(R.id.comment_content);
                TextView date = (TextView) convertView.findViewById(R.id.comment_date);
                Comment comment = datas.get(position);

                /*ImageOptions imageOptions = new ImageOptions.Builder()
                        .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                        .setCircular(true)
                        .setCrop(true)
                        .setLoadingDrawableId(R.drawable.circle_50)
                        .setFailureDrawableId(R.drawable.circle_50)
                        .build();
                x.image().bind(icon, comment.getHeadIcon(), imageOptions);

                name.setText(comment.getNickName());
                content.setText(comment.getCommentContent());
                date.setText(comment.getDatetime());*/

                return convertView;
            }
        };
        listView.setAdapter(adapter);


        if (data.getCommentCount() > 0) {
            count.setVisibility(View.VISIBLE);
            if (data.getCommentCount() > 99) {
                count.setText("99+");
            } else {
                count.setText(data.getCommentCount() + "");
            }
        } else {
            count.setVisibility(View.INVISIBLE);
        }
        send.setVisibility(View.GONE);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = edit.getText().toString();
                if (TextUtils.isEmpty(string)) {
                    ToastUtils.show(NewsKindDetailActivty.this, "请填写评论内容");
                } else {
                        if (UserManager.isLogin()) {
                            comment(string);
                        }else {
                            Intent intent=new Intent(NewsKindDetailActivty.this,LoginActivity.class);
                            startActivity(intent);
                        }
                }
            }
        });
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    send.setVisibility(View.VISIBLE);
                    bottom.setVisibility(View.GONE);
                } else {
                    send.setVisibility(View.GONE);
                    bottom.setVisibility(View.VISIBLE);

                    edit.setText("");
                }
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.requestFocus();
                scrollView.scrollTo(0, (int) listView.getY());
            }
        });

        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (UserManager.isLogin()) {
                        collect(!v.isSelected());
                    }else {
                        Intent intent=new Intent(mActivity,LoginActivity.class);
                        startActivity(intent);
                    }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });


        mSetting = webView.getSettings();
        //mSetting.setJavaScriptEnabled(true);
        mSetting.setJavaScriptEnabled(true);
        // 自适应屏幕
        mSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mSetting.setLoadWithOverviewMode(true);
        mSetting.setBlockNetworkImage(false);


        mSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        mSetting.setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        // 开启 DOM storage API 功能
        mSetting.setDomStorageEnabled(true);
        //开启 database storage API 功能
        mSetting.setDatabaseEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath() + "/webcache";
//      String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
        //设置数据库缓存路径
        mSetting.setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        mSetting.setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        mSetting.setAppCacheEnabled(true);

        /*//支持javascript
        mSetting.setJavaScriptEnabled(true);
        // 设置可以支持缩放
        mSetting.setSupportZoom(true);
        // 设置出现缩放工具
        mSetting.setBuiltInZoomControls(true);
        //扩大比例的缩放
        mSetting.setUseWideViewPort(true);
        //自适应屏幕
        mSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mSetting.setLoadWithOverviewMode(true);*/

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    handler.sendEmptyMessage(0);// 如果全部载入,隐藏进度对话框
                }
                super.onProgressChanged(view, progress);
            }
        });

        if (data != null) {
            webView.loadUrl(data.getLinkUrl());
        }

        ptrFrame = ((PtrFrameLayout) findViewById(R.id.fresh_frame));
        PtrClassicDefaultHeader header = new PtrClassicDefaultHeader(getContext());
        ptrFrame.setHeaderView(header);
        ptrFrame.addPtrUIHandler(header);

        header.setLastUpdateTimeKey(this.getClass().getName());

        ptrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return !checkContentCanPullDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                fresh();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL && view.getLastVisiblePosition() == datas.size()) {
                    ToastUtils.show(NewsKindDetailActivty.this, "加载更多");
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    private void fresh() {

        RequestParams entity = new RequestParams(UrlUtils.LIST_ARTICLE_COMMENT);
        entity.addBodyParameter("dataId", contentId + "");
        //entity.addBodyParameter("appuserId",UserUtils.getCUser(this).getAppuserid()+"");

        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                com.alibaba.fastjson.JSONObject object = JSON.parseObject(result);
                Integer code = object.getInteger("result");
                if (code == 200) {
                    datas.clear();

                    List<Comment> comments = JSON.parseArray(object.getString("info"), Comment.class);
                    datas.addAll(comments);
                    adapter.notifyDataSetChanged();

                } else {
                    ToastUtils.show(NewsKindDetailActivty.this, "刷新失败");
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtils.show(NewsKindDetailActivty.this, "网络加载失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                ptrFrame.refreshComplete();
            }
        });
    }

    private void getComments(String id, String user) {
        RequestParams entity = new RequestParams(UrlUtils.LIST_ARTICLE_COMMENT);
        entity.addBodyParameter("dataId", id);
        entity.addBodyParameter("appuserId", user);

        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                com.alibaba.fastjson.JSONObject object = JSON.parseObject(result);
                Integer code = object.getInteger("result");
                if (code == 200) {
                    List<Comment> comments = JSON.parseArray(object.getString("info"), Comment.class);
                    datas.addAll(comments);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtils.show(NewsKindDetailActivty.this, "没有更多评论");
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtils.show(NewsKindDetailActivty.this, "网络加载失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void comment(String content) {
        RequestParams entity = new RequestParams(UrlUtils.COMMENT_ARTICLE);
        entity.addBodyParameter("dataId", contentId + "");
        entity.addBodyParameter("content", content);
        entity.addBodyParameter("appuserId", UserManager.getUser().getAppuserId() + "");
        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("message");

                    if (code == 200) {
                        ToastUtils.show(NewsKindDetailActivty.this, "评论成功,审核通过后可见");

                        edit.setText("");
                        edit.clearFocus();
                    } else {
                        ToastUtils.show(NewsKindDetailActivty.this, "服务器请求失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtils.show(NewsKindDetailActivty.this, "服务器请求失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void collect(boolean col) {

        if (col) {
            RequestParams entity = new RequestParams(String.format(UrlUtils.COLLECT_ARTICLE, contentId + "",
                    UserManager.getUser().getAppuserId()+""));
            x.http().get(entity, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        int code = jsonObject.getInt("result");

                        if (code == 200) {
                            ToastUtils.show(NewsKindDetailActivty.this, "收藏成功");
                            collect.setSelected(!collect.isSelected());
                        }else {
                            ToastUtils.show(NewsKindDetailActivty.this,"服务器请求失败");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    ToastUtils.show(NewsKindDetailActivty.this,"服务器请求失败");

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }else {
            RequestParams entity = new RequestParams(String.format(UrlUtils.CANCEL_COLLECT_ARTICLE, contentId + "",
                    UserManager.getUser().getAppuserId()+""));
            x.http().get(entity, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject jsonObject=new JSONObject(result);

                        int code = jsonObject.getInt("result");
                        if (code==200){
                            ToastUtils.show(NewsKindDetailActivty.this,"成功取消收藏");
                            collect.setSelected(!collect.isSelected());
                        }else {
                            ToastUtils.show(NewsKindDetailActivty.this,"服务器请求失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    ToastUtils.show(NewsKindDetailActivty.this,"服务器请求失败");

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
    }

    private void share() {

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用

        if (!TextUtils.isEmpty(data.getTitle()) && !data.getTitle().equals(data.getSummary())) {
            oks.setTitle(data.getTitle());
        } else {
            oks.setTitle("新龙岩");
        }

        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        if (TextUtils.isEmpty(data.getShareUrl())) {
            oks.setTitleUrl(UrlUtils.APP_URL);
        } else {
            oks.setTitleUrl(data.getShareUrl());
        }

        // text是分享文本，所有平台都需要这个字段
        if (!TextUtils.isEmpty(data.getSummary())) {
            oks.setText(data.getSummary());
        } else {
            oks.setText("新龙岩");
        }
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数


        final String path = data.getPicsrc().split(";")[0];

        if (TextUtils.isEmpty(path)) {
            oks.setImagePath(FileUtils.CACHEPATH + File.separator + "logo.png");
            //确保SDcard下面存在此张图片
            //oks.setImageUrl(path);
        } else {
            oks.setImageUrl(path);
        }
        // url仅在微信（包括好友和朋友圈）中使用
        if (TextUtils.isEmpty(data.getShareUrl())) {
            oks.setUrl(UrlUtils.APP_URL);
        } else {
            oks.setUrl(data.getShareUrl());
        }
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(UrlUtils.APP_URL);

        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                //分享成功后调用，分享成功的接口
                RequestParams entity = new RequestParams(String.format(UrlUtils.SHARE_ARTICLE,
                        UserManager.getUser().getAppuserId() + "", contentId + ""));
                x.http().get(entity, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int code = jsonObject.getInt("result");

                            if (code == 200) {
                                ToastUtils.show(NewsKindDetailActivty.this, "分享成功");
                            } else {
                                ToastUtils.show(NewsKindDetailActivty.this, "服务器异常");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastUtils.show(NewsKindDetailActivty.this, "服务器异常");
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                ToastUtils.show(NewsKindDetailActivty.this, "分享失败");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                ToastUtils.show(NewsKindDetailActivty.this, "取消分享");
            }
        });


        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {

                if (platform.getName().equalsIgnoreCase(Wechat.NAME)) {
                    // 微信必须要设置具体的方分享类型
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                } else if (platform.getName().equals(WechatMoments.NAME)) {
                    if (!TextUtils.isEmpty(data.getTitle())) {
                        paramsToShare.setTitle(data.getTitle());
                    }

                }

            }
        });
        // 启动分享GUI
        oks.show(this);

    }


    @Override
    public void initData() {
        super.initData();

        contentId = data.getContentId();
        if (UserManager.isLogin()) {
            getUserState();
            getComments(contentId + "", UserManager.getUser().getAppuserId() + "");
        }

    }

    private void getUserState() {
        /*获取当前用户关于这篇文章的状态*/
        RequestParams entity = new RequestParams(String.format(UrlUtils.USER_ARTICLE_STATE, contentId + "",
                UserManager.getUser().getAppuserId() + ""));
        x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("result");

                    if (code == 200) {
                        JSONObject info = jsonObject.getJSONObject("info");
                        int collectState = info.getInt("collectState");
                        int shareState = info.getInt("shareState");

                        if (collectState == 1) {
                            collect.setSelected(true);
                        } else {
                            collect.setSelected(false);
                        }

                        if (shareState == 1) {
                            share.setSelected(true);
                        } else {
                            share.setSelected(false);
                        }
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

            }
        });
    }

    private void readSuccess() {

        if (!isReading) {
            isReading = true;

            //成功阅读了一篇
            x.http().get(new RequestParams(String.format(UrlUtils.READ_ARTICLE,
                    UserManager.getUser().getAppuserId() + "", contentId + "")), new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        int code = jsonObject.getInt("result");

                        if (code == 200) {
                            // TODO: 2017/3/20 成功阅读一篇文章
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
                    isReading = false;
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_kind_detail_activty);
        x.view().inject(this);
        initView();
        initData();
    }

    private boolean isReading;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    if (!isReading) {
                        if (UserManager.isLogin()) {
                            readSuccess();
                        }
                    }
                    break;
                case 1:

                    break;
            }
        }
    };

    private boolean checkContentCanPullDown(PtrFrameLayout frame, View content, View header) {
        if (content instanceof RecyclerView) {
            /*这里就单独处理recycler*/
            RecyclerView content1 = (RecyclerView) content;
            return content1.canScrollVertically(-1);
        } else {
            return content.getScrollY() > 0;
        }
    }
}
