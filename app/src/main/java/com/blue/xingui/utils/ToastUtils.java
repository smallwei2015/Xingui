package com.blue.xingui.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by cj on 2017/3/6.
 */

public class ToastUtils  {

    private static Toast toast;

    public static void showInner(Context context,String content){
        if (content==null)
            return;

        if (toast != null) {
            toast.setText(content);
        }else {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);

        }

        toast.show();
    }

    public static void show(final Activity activity, final String content){
        if (activity==null)
            return;

        if ("main".equals(Thread.currentThread().getName()))
            showInner(activity, content);
        else {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showInner(activity, content);
                }
            });
        }

    }
}
