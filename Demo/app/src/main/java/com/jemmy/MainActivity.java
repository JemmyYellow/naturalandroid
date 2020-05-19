package com.jemmy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jemmy.utils.OkHttpCallback;
import com.jemmy.utils.OkHttpUtils;
import com.jemmy.utils.SharedPreferencesUtil;
import com.jemmy.vo.ServerResponse;
import com.jemmy.vo.User;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferencesUtil sharedPreferences = SharedPreferencesUtil.getInstance(getApplicationContext());
        boolean islogin = sharedPreferences.readBoolean("isLogin");
//        Log.e("eeeeeeeee", islogin+"");
        if(islogin){
            User user = (User)sharedPreferences.readObject("user", User.class);
            String username = user.getUsername();
            String password = user.getPassword();
            OkHttpUtils.get("http://192.168.3.11:8080/portal/user/login.do?username=" + username
                            + "&password=" + password,
                    new OkHttpCallback());
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }
        else {
            sharedPreferences.clear();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }
}
