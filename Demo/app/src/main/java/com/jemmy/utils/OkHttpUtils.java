package com.jemmy.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.*;

public class OkHttpUtils {

    private OkHttpUtils(){}

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(6,TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();
    /**
     * get请求
     */
    public static void get(String url, OkHttpCallback callback){
        callback.url = url;
        Request request = new Request.Builder().url(url).build();
        CLIENT.newCall(request).enqueue(callback);
    }

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * post请求
     */
    public static void post(String url, String json, OkHttpCallback callback){
        callback.url = url;
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        CLIENT.newCall(request).enqueue(callback);
    }


    public static void downFile(String url, final String saveDir, OkHttpCallback callback){
        callback.url = url;
        Request request = new Request.Builder().url(url).build();
        CLIENT.newCall(request).enqueue(callback);
    }
}
