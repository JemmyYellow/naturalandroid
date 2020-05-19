package com.jemmy.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class SharedPreferencesUtil {
    private static volatile SharedPreferencesUtil instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final static String FILENAME = "business";

    private SharedPreferencesUtil(Context context) {
        this.sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();
    }

    public static SharedPreferencesUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (SharedPreferencesUtil.class) {
                if (instance == null) {
                    instance = new SharedPreferencesUtil(context);
                }
            }
        }
        return instance;
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public boolean readBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public String readString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public Object readObject(String key, Class clazz) {
        String str = sharedPreferences.getString(key, "");
        Gson gson = new Gson();
        return gson.fromJson(str, clazz);
    }

    public void delete(String key) {
        editor.remove(key).commit();
    }

    public void clear() {
        editor.clear().commit();
    }

}
