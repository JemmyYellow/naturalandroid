package com.jemmy;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.jemmy.common.Const;
import com.jemmy.utils.OkHttpCallback;
import com.jemmy.utils.OkHttpUtils;
import com.jemmy.utils.SharedPreferencesUtil;
import com.jemmy.vo.ServerResponse;
import com.jemmy.vo.User;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView up_username, up_password, up_phone;
    private Button btnUpPassword, btnUpPhone, backtohome;
    private SharedPreferencesUtil util;
    private User loginuser;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case Const.REFRESH_USER_INFO:
                    setText();
                    Toast.makeText(UserInfoActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                    break;
                case Const.FETCH_FAIL:
                    Toast.makeText(UserInfoActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        setTitle("用户信息");
        up_username = findViewById(R.id.up_username);
        up_phone = findViewById(R.id.up_phone);
        up_password = findViewById(R.id.up_password);
        btnUpPassword = findViewById(R.id.btnUpPassword);
        btnUpPhone = findViewById(R.id.btnUpPhone);
        backtohome = findViewById(R.id.backtohome);

        util = SharedPreferencesUtil.getInstance(getApplicationContext());
        setText();
        setListener();
    }

    private void setText() {
        loginuser = (User) util.readObject("user", User.class);
        up_username.setText(loginuser.getUsername());
        up_phone.setText(loginuser.getPhone());
    }

    private void setListener() {
        btnUpPhone.setOnClickListener(this);
        btnUpPassword.setOnClickListener(this);
        backtohome.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        final User user = (User) util.readObject("user", User.class);
        final Integer userId = user.getId();
        switch (v.getId()) {
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
                                final String now = newpassword.getText().toString();
                                String con = confirmpassword.getText().toString();
                                if (!now.equals(con)) {
                                    Toast.makeText(UserInfoActivity.this,
                                            "两次输入不一致", Toast.LENGTH_LONG).show();
                                    dialog.cancel();
                                    return;
                                }
                                OkHttpUtils.get(Const.IP_PORT + "/portal/user/changepwd.do?userid=" + userId
                                                + "&old=" + old
                                                + "&now=" + now,
                                        new OkHttpCallback() {
                                            @Override
                                            public void onFinish(String status, String msg) {
                                                super.onFinish(status, msg);
                                                if (status.equals(Const.SUCCESS)) {
                                                    Gson gson = new Gson();
                                                    ServerResponse response = gson.fromJson(msg, ServerResponse.class);
                                                    if (response.getStatus() == 0) {
                                                        User user1 = (User) util.readObject("user", User.class);
                                                        user1.setPassword(now);
                                                        util.putString("user", new Gson().toJson(user1));
                                                        handler.sendEmptyMessage(Const.REFRESH_USER_INFO);
                                                    } else {
                                                        Message message = Message.obtain();
                                                        message.what = Const.FETCH_FAIL;
                                                        message.obj = response.getMsg();
                                                        handler.sendMessage(message);
                                                    }
                                                } else {
                                                    Message message = Message.obtain();
                                                    message.what = Const.FETCH_FAIL;
                                                    message.obj = msg;
                                                    handler.sendMessage(message);
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
                final TextView confirmCode = view2.findViewById(R.id.confirmCode);
                final int[] confirm = {0};
                final Button btn_send = view2.findViewById(R.id.btn_send);
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Random random = new Random();
                        int rand = random.nextInt(9000) + 1000;
                        confirm[0] = rand;
                        confirmCode.setText(String.valueOf(rand));
                    }
                });
                AlertDialog.Builder builder2 = new AlertDialog.Builder(UserInfoActivity.this).setView(view2)
                        .setTitle("修改手机").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String newPhone = newphone.getText().toString();
                                final String inputConfirm = confirmma.getText().toString();
                                if (newPhone.equals("") || inputConfirm.equals("")) {
                                    Toast.makeText(UserInfoActivity.this,
                                            "输入框为空", Toast.LENGTH_LONG).show();
                                } else {
                                    //修改手机
                                    if (String.valueOf(confirm[0]).equals(inputConfirm)) {
                                        //验证码正确
                                        OkHttpUtils.get(Const.IP_PORT + "/portal/user/changephone.do?userid="
                                                + loginuser.getId() + "&newphone="
                                                + newPhone, new OkHttpCallback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                super.onFailure(call, e);
                                                Message message = Message.obtain();
                                                message.what = Const.FETCH_FAIL;
                                                message.obj = e.toString();
                                                handler.sendMessage(message);
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                super.onResponse(call, response);
                                                Gson gson = new Gson();
                                                ServerResponse phoneResponse = gson.fromJson(super.getResult(), ServerResponse.class);
                                                if (phoneResponse.getStatus() == 0) {
                                                    //修改成功
                                                    User user2 = (User) util.readObject("user", User.class);
                                                    user2.setPhone(newPhone);
                                                    util.putString("user", new Gson().toJson(user2));
                                                    handler.sendEmptyMessage(Const.REFRESH_USER_INFO);
                                                } else {
                                                    Message message = Message.obtain();
                                                    message.what = Const.FETCH_FAIL;
                                                    message.obj = phoneResponse.getMsg();
                                                    handler.sendMessage(message);
                                                }
                                            }
                                        });
                                    } else {//验证码不正确
                                        Toast.makeText(UserInfoActivity.this,
                                                "验证码不正确", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog2 = builder2.create();
                dialog2.show();
                break;
            case R.id.backtohome:
                finish();
                break;
        }
    }

}
