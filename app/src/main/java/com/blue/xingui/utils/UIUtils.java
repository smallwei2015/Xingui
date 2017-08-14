package com.blue.xingui.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.blue.xingui.base.BaseApplication;


public class UIUtils {

    private static Toast mToast;

    public static DisplayMetrics getWindowDisplay(Activity activity) {
        //获取屏幕宽高并保存到application中
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static int getWindowWidth(Activity activity) {
        return getWindowDisplay(activity).widthPixels;
    }

    public static int getWindowHeight(Activity activity) {
        return getWindowDisplay(activity).heightPixels;
    }

    /**
     * 吐司
     *
     * @param stringId
     */
    public static void showToast(final int stringId) {
        if ("main".equals(Thread.currentThread().getName())) {
            if (mToast != null)
                mToast.setText(stringId);
            else {
                mToast = Toast.makeText(getContext(), stringId, Toast.LENGTH_SHORT);
            }
            mToast.show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mToast != null)
                        mToast.setText(stringId);
                    else {
                        mToast = Toast.makeText(getContext(), stringId, Toast.LENGTH_SHORT);
                    }
                    mToast.show();
                }
            });
        }
    }

    /**
     * 吐司
     *
     * @param str
     */
    public static void showToast(final String str) {
        if ("main".equals(Thread.currentThread().getName())) {
            if (mToast != null)
                mToast.setText(str);
            else {
                mToast = Toast.makeText(getContext(), str, Toast.LENGTH_SHORT);
            }
            mToast.show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mToast != null)
                        mToast.setText(str);
                    else {
                        mToast = Toast.makeText(getContext(), str, Toast.LENGTH_SHORT);
                    }
                    mToast.show();
                }
            });
        }
    }

    /**
     * 在主程序执行一段逻辑
     *
     * @param runnable
     */
    public static void runOnUiThread(Runnable runnable) {
        if (android.os.Process.myTid() == BaseApplication.MainThreadId) {
            runnable.run();
        } else {
            /*Handler handler = BaseApplication.getHandler();
            handler.post(runnable);*/
        }
    }

    /**
     * 获得资源
     *
     * @return
     */
    public static Resources getResource() {
        return getContext().getResources();
    }

    /**
     * 获得上下文
     *
     * @return
     */
    public static Context getContext() {
        return BaseApplication.getInstance();
    }

    /**
     * 创建一个布局
     *
     * @param resource
     * @return
     */
    public static View createView(int resource) {
        return View.inflate(getContext(), resource, null);
    }

    /**
     * 获得string
     *
     * @param id
     * @return
     */
    public static String getString(int id) {
        return getResource().getString(id);
    }

    /**
     * 获得string数组
     *
     * @param id ��Դid
     * @return
     */
    public static String[] getStringArray(int id) {
        return getResource().getStringArray(id);
    }

    /**
     * dp转 px.
     *
     * @param value the value
     * @return the int
     */
    public static int dp2px(Context context, float value) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }

    /**
     * dp转 px.
     *
     * @param value the value
     * @return the int
     */
    public static int dp2px(float value) {
        final float scale = BaseApplication.getInstance().getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }

    /**
     * px转dp.
     *
     * @param value the value
     * @return the int
     */
    public static int px2dp(Context context, float value) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int) ((value * 160) / scale + 0.5f);
    }


    /**
     * sp转px.
     *
     * @param value the value
     * @return the int
     */
    public static int sp2px(Context context, float value) {
        Resources r;
        if (context == null) {
            r = Resources.getSystem();
        } else {
            r = context.getResources();
        }
        float spvalue = value * r.getDisplayMetrics().scaledDensity;
        return (int) (spvalue + 0.5f);
    }


    /**
     * px转sp.
     *
     * @param value the value
     * @return the int
     */
    public static int px2sp(Context context, float value) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (value / scale + 0.5f);
    }

    /**
     * 获取view的绘图镜像
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
