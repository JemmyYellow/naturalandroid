package com.jemmy.viewmodel;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.jemmy.common.Const;
import com.jemmy.utils.OkHttpCallback;
import com.jemmy.utils.OkHttpUtils;
import com.jemmy.vo.PhotoItem;
import com.jemmy.vo.Pixabay;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Const.FETCH:
                    Gson gson = new Gson();
                    ArrayList<PhotoItem> list = gson.fromJson(msg.getData().getString("PhotoData"), Pixabay.class).getHits();
                    _photoListLive.setValue(list);
                    break;
            }
        }
    };

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }
    // TODO: Implement the ViewModel

    public MutableLiveData<List<PhotoItem>> _photoListLive = new MutableLiveData<>();

    public void fetchData() {
        OkHttpUtils.get(Const.PIXABAYURL, new OkHttpCallback() {
            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);
                if (status.equals("success")) {
                    Bundle bundle = new Bundle();
                    Message message = Message.obtain();
                    message.what = Const.FETCH;
                    bundle.putString("PhotoData", msg);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        });
    }

}
