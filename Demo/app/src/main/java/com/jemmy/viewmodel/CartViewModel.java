package com.jemmy.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jemmy.common.Const;
import com.jemmy.utils.OkHttpCallback;
import com.jemmy.utils.OkHttpUtils;
import com.jemmy.utils.SharedPreferencesUtil;
import com.jemmy.vo.CartVO;
import com.jemmy.vo.ServerResponse;
import com.jemmy.vo.User;

public class CartViewModel extends AndroidViewModel {

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case Const.FETCH:
                    CartVO cartVO = (CartVO) msg.obj;
                    _cartVOLive.setValue(cartVO);
                    break;
                case Const.FETCH_FAIL:
                    break;
                default:
                    break;
            }
        }
    };

    public MutableLiveData<CartVO> _cartVOLive = new MutableLiveData<>();
    public Integer userId;


    // TODO: Implement the ViewModel
    public CartViewModel(@NonNull Application application) {
        super(application);
    }

    //fetchData
    public void fetchData() {

        User user = (User) SharedPreferencesUtil.getInstance(getApplication().getApplicationContext()).readObject("user", User.class);
        userId=user.getId();

        OkHttpUtils.get(Const.IP_PORT + "/portal/cart/list.do?userid=" + userId,
                new OkHttpCallback() {
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        if (status.equals("success")) {

                            Gson gson = new Gson();
                            ServerResponse<CartVO> serverResponse = gson.fromJson(msg,
                                    new TypeToken<ServerResponse<CartVO>>() {
                                    }.getType());

                            if (serverResponse.getStatus() == 0) {
                                Bundle bundle = new Bundle();
                                Message message = Message.obtain();
                                message.what = Const.FETCH;
                                message.obj = serverResponse.getData();
                                bundle.putString("CartVO", msg);
                                message.setData(bundle);
                                myHandler.sendMessage(message);
                            } else {
                                myHandler.sendEmptyMessage(Const.FETCH_FAIL);
                            }

                        } else {
                            myHandler.sendEmptyMessage(Const.FETCH_FAIL);
                        }
                    }
                });
    }
}
