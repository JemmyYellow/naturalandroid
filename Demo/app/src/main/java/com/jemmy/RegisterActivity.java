package com.jemmy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jemmy.common.Const;
import com.jemmy.utils.OkHttpCallback;
import com.jemmy.utils.OkHttpUtils;
import com.jemmy.vo.ServerResponse;
import com.jemmy.vo.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_username,et_password,et_phone;
    private Button btn_commit;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case Const.START_LOGIN_IN_REGISTER:
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                    finish();
                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                    i.putExtra("usernameAndPassword",msg.getData());
                    startActivity(i);
                    break;
                case Const.MAKE_FAIL_TOAST:
                    Toast.makeText(RegisterActivity.this, (String)msg.obj, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setTitle("注册");
        this.initView();

        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = et_username.getText().toString();
                final String password = et_password.getText().toString();
                final String phone = et_phone.getText().toString();

                OkHttpUtils.get(Const.IP_PORT+"/portal/user/register.do?username="
                +username+"&password="+password+"&phone="+phone,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                if(status.equals("success")){
                                    Gson gson = new Gson();
                                    ServerResponse<User> serverResponse = gson.fromJson(msg,
                                            new TypeToken<ServerResponse<User>>() {
                                            }.getType());
                                    int status1 = serverResponse.getStatus();
                                    if(status1 == 0){
                                        //注册成功
                                        Message message = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("username",serverResponse.getData().getUsername());
                                        bundle.putString("password",serverResponse.getData().getPassword());
                                        message.what = Const.START_LOGIN_IN_REGISTER;
                                        message.setData(bundle);
                                        handler.sendMessage(message);
                                    }else {
                                        //注册失败
                                        Message message = new Message();
                                        message.what = Const.MAKE_FAIL_TOAST;
                                        message.obj = serverResponse.getMsg();
                                        handler.sendMessage(message);
                                    }
                                } else {
                                    Message message = new Message();
                                    message.what = Const.MAKE_FAIL_TOAST;
                                    message.obj = msg;
                                    handler.sendMessage(message);
                                }
                            }
                        });
            }
        });
    }

    private void initView() {
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_phone = findViewById(R.id.et_phone);
        btn_commit = findViewById(R.id.commit);
    }
}
