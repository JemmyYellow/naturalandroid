package com.jemmy.utils;

import android.content.Context;

import com.android.volley.RequestQueue;

public class VolleySingleton {

    private static volatile VolleySingleton instance = null;
    private Context context;
    private RequestQueue requestQueue;

    private VolleySingleton(Context context){
        this.context = context;
    }

    public VolleySingleton getInstance(Context context){

        if(instance == null){
            synchronized (this){
                if(instance == null){
                    instance = new VolleySingleton(context);
                }
            }
        }
        return instance;
    }


}
