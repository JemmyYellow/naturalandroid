package com.jemmy.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
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
import com.jemmy.vo.OrderVO;
import com.jemmy.vo.ServerResponse;
import com.jemmy.vo.User;

import java.util.List;

public class OrderViewModel extends AndroidViewModel {

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Const.FETCH:
                    List<OrderVO> list = (List<OrderVO>) msg.obj;
                    _orderVOListLive.setValue(list);
                    break;
                case Const.FETCH_FAIL:
                    FETCH_FAIL = true;
                    break;
                default:
                    break;
            }
        }
    };
    // TODO: Implement the ViewModel

    public MutableLiveData<List<OrderVO>> _orderVOListLive = new MutableLiveData<>();
    public Boolean FETCH_FAIL = false;
    public Integer userId;

    public OrderViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean fetchData() {

        final boolean[] result = {true};

        User user = (User) SharedPreferencesUtil.getInstance(getApplication().getApplicationContext()).readObject("user", User.class);
        userId = user.getId();

        OkHttpUtils.get(Const.IP_PORT + "/portal/order/list.do"
                        + "?userid=" + userId
                , new OkHttpCallback() {
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);
                        if (status.equals("success")) {

                            Gson gson = new Gson();
                            ServerResponse<List<OrderVO>> serverResponse = gson.fromJson(msg,
                                    new TypeToken<ServerResponse<List<OrderVO>>>() {
                                    }.getType());
                            if (serverResponse.getStatus() == 0) {
                                result[0] = false;
                                Message message = Message.obtain();
                                message.what = Const.FETCH;
                                message.obj = serverResponse.getData();
                                handler.sendMessage(message);
                            } else {
                                handler.sendEmptyMessage(Const.FETCH_FAIL);
                            }

                        } else {
                            handler.sendEmptyMessage(Const.FETCH_FAIL);
                        }
                    }
                });
        return result[0];
    }

}
