package com.example.yangliang.myweatherapp.util;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 创建日期：2017/9/25 on 上午10:21
 * 描述:网络请求框架：和服务器进行交互的代码封装
 * 由于OKhttp出色的的封装，z合理和服务器进行交互的代码非常简单，发起i条HTTP请求只需调用sendOkHttpRequest方法
 * 作者:yangliang
 */
public class HttpUtil {

    /**
     * @param address  传入请求地址
     * @param callback 注册一个回调来处理服务器响应
     */
    public static void sendOkHttpRequest(String address, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
