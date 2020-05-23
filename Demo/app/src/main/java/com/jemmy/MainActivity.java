package com.jemmy;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jemmy.common.Const;
import com.jemmy.utils.OkHttpCallback;
import com.jemmy.utils.OkHttpUtils;
import com.jemmy.utils.SharedPreferencesUtil;
import com.jemmy.vo.User;

public class MainActivity extends AppCompatActivity{


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
        if (islogin) {
            User user = (User) sharedPreferences.readObject("user", User.class);
            String username = user.getUsername();
            String password = user.getPassword();
            OkHttpUtils.get(Const.LOCAL+"/portal/user/login.do?username=" + username
                            + "&password=" + password,
                    new OkHttpCallback());
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        } else {
            sharedPreferences.clear();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }
}
