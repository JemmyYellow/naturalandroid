package com.jemmy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jemmy.common.Const;
import com.jemmy.utils.OkHttpCallback;
import com.jemmy.utils.OkHttpUtils;
import com.jemmy.utils.SharedPreferencesUtil;
import com.jemmy.viewmodel.UserViewModel;
import com.jemmy.vo.ServerResponse;
import com.jemmy.vo.User;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView up_username,up_password,up_phone;
    private Button btnUpPassword,btnUpPhone,backtohome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        up_username = findViewById(R.id.up_username);
        up_phone = findViewById(R.id.up_phone);
        btnUpPassword = findViewById(R.id.btnUpPassword);
        btnUpPhone = findViewById(R.id.btnUpPhone);
        backtohome = findViewById(R.id.backtohome);

        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(getApplicationContext());
        User loginuser = (User) util.readObject("user", User.class);
        up_username.setText(loginuser.getUsername());
        up_phone.setText(loginuser.getPhone());

        btnUpPhone.setOnClickListener(this);
        btnUpPassword.setOnClickListener(this);
        backtohome.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnUpPassword:
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.update_password, null);
                final EditText oldpassword = view.findViewById(R.id.newphone);
                final EditText newpassword = view.findViewById(R.id.newpassword);
                final EditText confirmpassword = view.findViewById(R.id.confirmma);
                AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this).setView(view)
                        .setTitle("修改密码").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //确认修改密码 id,old,now
                                String old = oldpassword.getText().toString();
                                String now = newpassword.getText().toString();
                                String con = confirmpassword.getText().toString();
                                if(!now.equals(con)){
                                    Toast.makeText(UserInfoActivity.this,
                                            "两次输入不一致", Toast.LENGTH_LONG).show();
                                    dialog.cancel();
                                    return;
                                }
                                SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(getApplicationContext());
                                final User user = (User)util.readObject("user", User.class);
                                Integer id = user.getId();
                                OkHttpUtils.get("http://192.168.3.11:8080/portal/user/changePwd.do?id=" + id + ""
                                        + "&old=" + old
                                        + "&now=" + now,
                                        new OkHttpCallback(){
                                            @Override
                                            public void onFinish(String status, String msg) {
                                                super.onFinish(status, msg);
                                                Gson gson = new Gson();
                                                ServerResponse response = gson.fromJson(msg, ServerResponse.class);
                                                if(response.getStatus()==0){
                                                    Looper.prepare();
                                                    Toast.makeText(UserInfoActivity.this,
                                                            "修改成功", Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                }
                                            }
                                        });
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                break;
            case R.id.btnUpPhone:
                View view2 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.update_phone, null);
                final EditText newphone = view2.findViewById(R.id.newphone);
                final EditText confirmma = view2.findViewById(R.id.confirmma);
                final Button btn_send = view2.findViewById(R.id.btn_send);
                AlertDialog.Builder builder2 = new AlertDialog.Builder(UserInfoActivity.this).setView(view2)
                        .setTitle("修改手机").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //确认修改手机
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder2.create().show();
                break;
            case R.id.backtohome:
                UserInfoActivity.this.finish();
                break;
        }
    }
}
