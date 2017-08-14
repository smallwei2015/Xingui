package com.blue.xingui.utils;


import android.util.Log;

import java.net.URLEncoder;


public class WeatherUtils {
    /**
     * �̶���KEYֵ
     */
    public static String regularKey = "temp123456_bpt2_barrel";
    public static String App_ID = "uc1b58cea86aa423gx";

    /**
     * ��ȡǩ��
     *
     * @param data
     * @param key
     * @return
     */
    public static String sign(String data, String key) {
        String encode = "";
        byte[] b = HMACSHA1.getHmacSHA1(data,key);
        String s = new BASE64Encoder().encode(b);

        Log.w("3333",s);
        try {
            encode = URLEncoder.encode(s, "UTF-8");
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return encode;
    }


}
