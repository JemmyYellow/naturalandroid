package com.jemmy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jemmy.common.Const;
import com.jemmy.utils.OkHttpCallback;
import com.jemmy.utils.OkHttpUtils;
import com.jemmy.utils.SharedPreferencesUtil;
import com.jemmy.vo.ServerResponse;
import com.jemmy.vo.User;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case Const.START_HOME:
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    break;
                case Const.START_LOGIN:
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferencesUtil sharedPreferences = SharedPreferencesUtil.getInstance(getApplicationContext());
        boolean isLogin = sharedPreferences.readBoolean("isLogin");
        if (isLogin) {
            User user = (User) sharedPreferences.readObject("user", User.class);
            String username = user.getUsername();
            String password = user.getPassword();
            OkHttpUtils.get(Const.IP_PORT+"/portal/user/login.do?username=" + username
                            + "&password=" + password,
                    new OkHttpCallback(){
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            super.onResponse(call, response);
                            String result = super.getResult();
                            Gson gson = new Gson();
                            ServerResponse<User> serverResponse = gson.fromJson(result,
                                    new TypeToken<ServerResponse<User>>() {
                                    }.getType());
                            int status1 = serverResponse.getStatus();
                            if(status1 == 0){
                                SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(getApplicationContext());
                                util.clear();
                                util.putBoolean("isLogin", true);
                                util.putString("user", gson.toJson(serverResponse.getData()));
//                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                handler.sendEmptyMessage(Const.START_HOME);
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            super.onFailure(call, e);
                            handler.sendEmptyMessage(Const.START_LOGIN);
                        }
                    });
        } else {
            sharedPreferences.clear();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }
}
