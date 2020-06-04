package com.jemmy;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jemmy.common.Const;
import com.jemmy.utils.OkHttpCallback;
import com.jemmy.utils.OkHttpUtils;
import com.jemmy.utils.SharedPreferencesUtil;
import com.jemmy.vo.CartVO;
import com.jemmy.vo.Product;
import com.jemmy.vo.ServerResponse;
import com.jemmy.vo.User;

import io.supercharge.shimmerlayout.ShimmerLayout;
import uk.co.senab.photoview.PhotoView;

public class DetailActivity extends AppCompatActivity {

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case Const.SET_PROGRESSBAR_VISIBLE:
                    cartAddProgressBar.setVisibility(View.VISIBLE);
                    break;
                case Const.SET_PROGRESSBAR_INVISIBLE:
                    cartAddProgressBar.setVisibility(View.INVISIBLE);
                    break;
                case Const.MAKE_FAIL_TOAST:
                    Toast.makeText(DetailActivity.this, (String)msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case Const.MAKE_SUCCESS_TOAST:
                    Toast.makeText(DetailActivity.this, "操作成功", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    private ShimmerLayout shimmerLayoutDetail;
    private PhotoView photoView;
    private Button OrderAdd;
    private TextView productName, productDetail, productPrice;
    private Product product;
    private ProgressBar cartAddProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle("商品信息");

        findView();

        cartAddProgressBar.setVisibility(View.INVISIBLE);

        shimmerLayoutDetail.setShimmerAngle(0);
        shimmerLayoutDetail.setShimmerColor(0x55FFFFFF);
        shimmerLayoutDetail.startShimmerAnimation();

        Glide.with(this).load(product.getMainImage()).placeholder(R.drawable.ic_photo_gray_24dp)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (shimmerLayoutDetail != null) {
                            shimmerLayoutDetail.stopShimmerAnimation();
                        }
                        return false;
                    }
                })
                .into(photoView);

        productName.setText(product.getName());
        productDetail.setText(product.getDetail());
        productPrice.setText(String.valueOf(product.getPrice()));

        setOnClick();
    }

    private void setOnClick() {

        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(DetailActivity.this);
        final User user = (User) util.readObject("user", User.class);
        OrderAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myHandler.sendEmptyMessage(Const.SET_PROGRESSBAR_VISIBLE);

                OkHttpUtils.get(Const.IP_PORT+"/portal/cart/add.do?userid="+user.getId()+"&productid="+product.getId(),
                        new OkHttpCallback(){
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                myHandler.sendEmptyMessage(Const.SET_PROGRESSBAR_INVISIBLE);
                                if(status.equals(Const.SUCCESS)){
                                    Gson gson = new Gson();
                                    ServerResponse<CartVO> serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse<CartVO>>(){}.getType());
                                    int status1 = serverResponse.getStatus();
                                    if(status1 == 0){
                                        myHandler.sendEmptyMessage(Const.MAKE_SUCCESS_TOAST);
                                    } else {
                                        Message message = Message.obtain();
                                        message.what = Const.MAKE_FAIL_TOAST;
                                        message.obj = serverResponse.getMsg();
                                        myHandler.sendMessage(message);
                                    }
                                } else {
                                    Message message = Message.obtain();
                                    message.what = Const.MAKE_FAIL_TOAST;
                                    message.obj = msg;
                                    myHandler.sendMessage(message);
                                }
                            }
                        });
            }
        });

    }

    private void findView(){
        product = (Product) getIntent().getBundleExtra("ProductData").getSerializable("Product");
        photoView = findViewById(R.id.photoView);
        shimmerLayoutDetail = findViewById(R.id.detail_shimmer);
        OrderAdd = findViewById(R.id.CartAdd);
        productName = findViewById(R.id.productName);
        productDetail = findViewById(R.id.productDetail);
        productPrice = findViewById(R.id.productPrice);
        cartAddProgressBar = findViewById(R.id.cartAddProgressBar);
    }

}
