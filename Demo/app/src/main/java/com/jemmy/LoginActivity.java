package com.jemmy;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.jemmy.vo.UserVO;

import java.lang.ref.WeakReference;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editText, editText2;
    Button button, button2;
    ProgressBar progressBar;
    ImageView imageView;
    MyHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);

        progressBar.setVisibility(View.INVISIBLE);
        handler = new MyHandler(this);

        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        imageView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                String username = editText.getText().toString();
                String password = editText2.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                //请求接口 okhttp
                OkHttpUtils.get(Const.LOCAL+"/portal/user/login.do?username=" + username
                                + "&password=" + password,
                        new OkHttpCallback() {
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                if (status.equals("failure")) {
                                    handler.sendEmptyMessage(0);
                                    Looper.prepare();
                                    Toast.makeText(LoginActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                    return;
                                }
                                //数据在msg里(json格式的)
                                //解析数据
                                Gson gson = new Gson();
                                ServerResponse<User> serverResponse = gson.fromJson(msg,
                                        new TypeToken<ServerResponse<User>>() {
                                        }.getType());
                                int status1 = serverResponse.getStatus();

                                if (status1 == 0) {//登录成功
                                    //保存用户信息
                                    SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(getApplicationContext());
                                    util.clear();
                                    util.putBoolean("isLogin", true);
                                    util.putString("user", gson.toJson(serverResponse.getData()));
//                                    Boolean isLogin = util.readBoolean("isLogin");
                                    handler.sendEmptyMessage(0);
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                } else {
                                    handler.sendEmptyMessage(0);
                                    Looper.prepare();
                                    Toast.makeText(LoginActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }
                            }
                        });
                break;
            case R.id.imageView:
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                break;
            case R.id.button2:
                startActivity(new Intent(LoginActivity.this, SignInActivity.class));
                break;
        }
    }


    static class MyHandler extends Handler{
        private final WeakReference<LoginActivity> reference;
        public MyHandler(LoginActivity activity){
            this.reference = new WeakReference<LoginActivity>(activity);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 0:
//                    progressBar.setVisibility(View.INVISIBLE);
                    reference.get().progressBar.setVisibility(View.INVISIBLE);
                    break;
            }
            super.handleMessage(msg);
        }
    };

}
