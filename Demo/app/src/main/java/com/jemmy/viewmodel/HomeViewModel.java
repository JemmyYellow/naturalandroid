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
import com.jemmy.vo.PhotoItem;
import com.jemmy.vo.Product;
import com.jemmy.vo.ServerResponse;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Const.FETCH:
//                    Gson gson = new Gson();
//                    ArrayList<PhotoItem> list = gson.fromJson(msg.getData().getString("PhotoData"), Product.class).getList();
//                    ServerResponse<List<Product>> serverResponse = gson.fromJson(msg.getData().getString("ProductList"),
//                            new TypeToken<ServerResponse<List<Product>>>(){}.getType());
                    List<Product> list = (List<Product>) msg.obj;
                    _productListLive.setValue(list);
                    break;
                case Const.FETCH_FAIL:
                    break;
                default:
                    break;
            }
        }
    };

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }
    // TODO: Implement the ViewModel

    public MutableLiveData<List<PhotoItem>> _photoListLive = new MutableLiveData<>();
    public MutableLiveData<List<Product>> _productListLive = new MutableLiveData<>();

    public void fetchData() {
//        OkHttpUtils.get(Const.PIXABAYURL, new OkHttpCallback() {
//            @Override
//            public void onFinish(String status, String msg) {
//                super.onFinish(status, msg);
//                if (status.equals("success")) {
//                    Bundle bundle = new Bundle();
//                    Message message = Message.obtain();
//                    message.what = Const.FETCH;
//                    bundle.putString("PhotoData", msg);
//                    message.setData(bundle);
//                    handler.sendMessage(message);
//                } else {
//                    handler.sendEmptyMessage(Const.FETCH_FAIL);
//                }
//            }
//        });
        OkHttpUtils.get(Const.IP_PORT+"/portal/product/list.do", new OkHttpCallback() {
            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);
                if (status.equals("success")) {

                    Gson gson = new Gson();
                    ServerResponse<List<Product>> serverResponse = gson.fromJson(msg,
                            new TypeToken<ServerResponse<List<Product>>>(){}.getType());
                    if(serverResponse.getStatus() == 0){

                        Message message = Message.obtain();
                        message.what = Const.FETCH;
                        message.obj = serverResponse.getData();

                        handler.sendMessage(message);
                    }

                } else {
                    handler.sendEmptyMessage(Const.FETCH_FAIL);
                }
            }
        });
    }

}
