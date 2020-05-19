package com.jemmy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
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

public class SignInActivity extends AppCompatActivity {

    private EditText et_username,et_password,et_phone;
    private Button btn_commit;
    private int sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        this.initView();

        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = et_username.getText().toString();
                final String password = et_password.getText().toString();
                final String phone = et_phone.getText().toString();

                OkHttpUtils.get(Const.LOCAL+"/portal/user/register.do?username="
                +username+"&password="+password+"&phone="+phone,
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                Gson gson = new Gson();
                                ServerResponse<User> serverResponse = gson.fromJson(msg,
                                        new TypeToken<ServerResponse<User>>() {
                                        }.getType());
                                int status1 = serverResponse.getStatus();
                                if(status1 == 0){
                                    //注册成功
                                    sign = 0;
                                    Looper.prepare();
                                    Toast.makeText(SignInActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }else {
                                    Looper.prepare();
                                    Toast.makeText(SignInActivity.this, serverResponse.getMsg(), Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }
                            }
                        });
            }
        });
    }

    private void initView() {
        sign = -1;
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_phone = findViewById(R.id.et_phone);
        btn_commit = findViewById(R.id.commit);
    }
}
