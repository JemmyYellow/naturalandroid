package com.jemmy.utils;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OkHttpCallback implements Callback {

    private final String TAG = OkHttpCallback.class.getSimpleName();
    private final String ON_RESPONSE = "ON_RESPONSE";
    private final String ON_FAILURE = "ON_FAILURE";

    String url;
    private String result;

    @Override
    public void onFailure(Call call, IOException e) {
        Log.d(TAG, "url:"+url);
        Log.d(TAG, "请求失败:"+e.toString());
//        OkHttpUtils.initClient();
        onFinish("failure", e.toString());
        call.cancel();
        Log.d(TAG, ON_FAILURE);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Log.d(TAG, "url:"+url);
        result = response.body().string();
        Log.d(TAG, "请求成功:"+result);
        onFinish("success", result);
        call.cancel();
        Log.d(TAG, ON_RESPONSE);
    }

    public void onFinish(String status, String msg){
        Log.d(TAG, "url:"+url+" status: "+status);
    }

    public String getResult(){
        return result;
    }
}
