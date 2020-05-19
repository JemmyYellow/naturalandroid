package com.jemmy.utils;

import android.util.Log;

import java.io.IOException;

import okhttp3.*;

public class OkHttpCallback implements Callback {

    private final String TAG = OkHttpCallback.class.getSimpleName();

    String url;
    private String result;
//    private Headers headers;

    @Override
    public void onFailure(Call call, IOException e) {
        Log.d(TAG, "url:"+url);
        Log.d(TAG, "请求失败:"+e.toString());
        onFinish("failure", e.toString());
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
//        headers = response.headers();
//        String session = headers.get("Set-Cookie");
//        String JSESSIONID = session.substring(0, session.indexOf(";"));
//        Log.e("aaaaaaaaaa", JSESSIONID);
        Log.d(TAG, "url:"+url);
        result = response.body().string();
        Log.d(TAG, "请求成功:"+result);
        onFinish("success", result);
    }

    public void onFinish(String status, String msg){
        Log.d(TAG, "url:"+url+" status: "+status);
    }

    public String getResult(){
        return result;
    }
}
