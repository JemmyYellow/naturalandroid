package com.jemmy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jemmy.R;
import com.jemmy.common.Const;
import com.jemmy.utils.OkHttpCallback;
import com.jemmy.utils.OkHttpUtils;
import com.jemmy.viewmodel.CartViewModel;
import com.jemmy.vo.CartProductVO;
import com.jemmy.vo.CartVO;
import com.jemmy.vo.ServerResponse;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class CartAdapter extends ListAdapter<CartProductVO, CartAdapter.MyViewHolder> {

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case Const.SET_VIEWMODEL:
                    mViewModel._cartVOLive.setValue((CartVO) msg.obj);
                    break;
                case Const.MAKE_FAIL_TOAST:
                    Toast.makeText((Context) msg.obj, msg.getData().getString("ERROR"), Toast.LENGTH_LONG).show();
                default:
                    break;
            }
        }
    };
    private CartViewModel mViewModel;

    public CartAdapter(@NonNull DiffUtil.ItemCallback<CartProductVO> diffCallback, CartViewModel cartViewModel) {
        super(diffCallback);
        this.mViewModel = cartViewModel;
    }

    @NonNull
    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_cell, parent, false);
        final CartAdapter.MyViewHolder holder = new CartAdapter.MyViewHolder(view);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext()).setTitle("注意").setMessage("是否删除该条内容")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CartProductVO vo = getItem(holder.getAdapterPosition());
                                OkHttpUtils.get(Const.IP_PORT + "/portal/cart/delete.do?userid=" + vo.getUserId()
                                        + "&productid=" + vo.getProductId(),new OkHttpCallback(){
                                    @Override
                                    public void onFinish(String status, String msg) {
                                        super.onFinish(status, msg);
                                        if (status.equals("success")) {

                                            Gson gson = new Gson();
                                            ServerResponse<CartVO> serverResponse = gson.fromJson(msg,
                                                    new TypeToken<ServerResponse<CartVO>>() {
                                                    }.getType());
                                            if (serverResponse.getStatus() == 0) {
                                                Message message = Message.obtain();
                                                message.what = Const.SET_VIEWMODEL;
                                                message.obj = serverResponse.getData();
                                                myHandler.sendMessage(message);
//                                        mViewModel._cartVOLive.setValue(serverResponse.getData());
                                            } else {
                                                Message message = Message.obtain();
                                                message.what = Const.MAKE_FAIL_TOAST;
                                                message.obj = holder.itemView.getContext();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("ERROR",serverResponse.getMsg());
                                                message.setData(bundle);
                                                myHandler.sendMessage(message);
                                            }
                                        } else {
                                            Message message = Message.obtain();
                                            message.what = Const.MAKE_FAIL_TOAST;
                                            message.obj = holder.itemView.getContext();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("ERROR",msg);
                                            message.setData(bundle);
                                            myHandler.sendMessage(message);
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        ShimmerLayout shimmerLayout = holder.shimmerLayout;
        shimmerLayout.setShimmerColor(0x55FFFFFF);
        shimmerLayout.setShimmerAngle(0);
        shimmerLayout.startShimmerAnimation();

        Glide.with(holder.itemView).load(getItem(position).getProductMainImage()).placeholder(R.drawable.ic_photo_gray_24dp)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (holder.shimmerLayout != null) {
                            holder.shimmerLayout.stopShimmerAnimation();
                        }
                        return false;
                    }
                })
                .into(holder.productMainImage);

        CartProductVO cartProductVO = getItem(position);

        holder.productName.setText(cartProductVO.getProductName());
        holder.productPrice.setText(String.valueOf(cartProductVO.getProductPrice()));
        holder.productChecked.setChecked(cartProductVO.getProductChecked() == 1);
        holder.productTotalPrice.setText(String.valueOf(cartProductVO.getProductTotalPrice()));
        holder.quantity.setText(String.valueOf(cartProductVO.getQuantity()));

        final Integer q = getItem(position).getQuantity();
//        if (q == 1) {
//            holder.imageButton_minus.setClickable(false);
//            holder.imageButton_minus.setVisibility(View.INVISIBLE);
//            holder.imageButton_minus.setImageResource(R.drawable.ic_minus_unclickable);
//        }

        holder.imageButton_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //减
                int count = q - 1;
                if(count <= 0){
                    return;
                }
                CartProductVO vo = getItem(position);
                OkHttpUtils.get(Const.IP_PORT + "/portal/cart/update.do?userid=" + vo.getUserId()
                                + "&productid=" + vo.getProductId() + "&count=" + count,
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
                                        Message message = Message.obtain();
                                        message.what = Const.SET_VIEWMODEL;
                                        message.obj = serverResponse.getData();
                                        myHandler.sendMessage(message);
//                                        mViewModel._cartVOLive.setValue(serverResponse.getData());
                                    } else {
                                        Message message = Message.obtain();
                                        message.what = Const.MAKE_FAIL_TOAST;
                                        message.obj = holder.itemView.getContext();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("ERROR",serverResponse.getMsg());
                                        message.setData(bundle);
                                        myHandler.sendMessage(message);
                                    }
                                } else {
                                    Message message = Message.obtain();
                                    message.what = Const.MAKE_FAIL_TOAST;
                                    message.obj = holder.itemView.getContext();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("ERROR",msg);
                                    message.setData(bundle);
                                    myHandler.sendMessage(message);
                                }
                            }
                        });
            }
        });

        holder.imageButton_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //加
                int count = q + 1;
                if(count > 20){
                    return;
                }
                CartProductVO vo = getItem(position);
                OkHttpUtils.get(Const.IP_PORT + "/portal/cart/update.do?userid=" + vo.getUserId()
                                + "&productid=" + vo.getProductId() + "&count=" + count,
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
                                        Message message = Message.obtain();
                                        message.what = Const.SET_VIEWMODEL;
                                        message.obj = serverResponse.getData();
                                        myHandler.sendMessage(message);
//                                        mViewModel._cartVOLive.setValue(serverResponse.getData());
                                    } else {
                                        Message message = Message.obtain();
                                        message.what = Const.MAKE_FAIL_TOAST;
                                        message.obj = holder.itemView.getContext();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("ERROR",serverResponse.getMsg());
                                        message.setData(bundle);
                                        myHandler.sendMessage(message);
                                    }
                                } else {
                                    Message message = Message.obtain();
                                    message.what = Const.MAKE_FAIL_TOAST;
                                    message.obj = holder.itemView.getContext();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("ERROR",msg);
                                    message.setData(bundle);
                                    myHandler.sendMessage(message);
                                }
                            }
                        });
            }
        });

        holder.productChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选中
                int count = q + 1;
                CartProductVO vo = getItem(position);
                OkHttpUtils.get(Const.IP_PORT + "/portal/cart/check.do?userid=" + vo.getUserId()
                                + "&productid=" + vo.getProductId(),
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
                                        Message message = Message.obtain();
                                        message.what = Const.SET_VIEWMODEL;
                                        message.obj = serverResponse.getData();
                                        myHandler.sendMessage(message);
//                                        mViewModel._cartVOLive.setValue(serverResponse.getData());
                                    } else {
                                        Message message = Message.obtain();
                                        message.what = Const.MAKE_FAIL_TOAST;
                                        message.obj = holder.itemView.getContext();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("ERROR",serverResponse.getMsg());
                                        message.setData(bundle);
                                        myHandler.sendMessage(message);
                                    }
                                } else {
                                    Message message = Message.obtain();
                                    message.what = Const.MAKE_FAIL_TOAST;
                                    message.obj = holder.itemView.getContext();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("ERROR",msg);
                                    message.setData(bundle);
                                    myHandler.sendMessage(message);
                                }
                            }
                        });
            }
        });

    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        private CheckBox productChecked;
        private ImageView productMainImage;
        private CardView cartCardView;
        private TextView productName, productPrice, productTotalPrice, quantity;
        private ImageButton imageButton_minus, imageButton_add;
        private ShimmerLayout shimmerLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            shimmerLayout = itemView.findViewById(R.id.shimmerLayout);
            productChecked = itemView.findViewById(R.id.productChecked);
            productMainImage = itemView.findViewById(R.id.productMainImage);
            cartCardView = itemView.findViewById(R.id.cartCardView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productTotalPrice = itemView.findViewById(R.id.productTotalPrice);
            quantity = itemView.findViewById(R.id.quantity);
            imageButton_minus = itemView.findViewById(R.id.imageButton_minus);
            imageButton_add = itemView.findViewById(R.id.imageButton_add);
        }
    }
}
