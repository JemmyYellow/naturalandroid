package com.jemmy.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jemmy.R;
import com.jemmy.alipay.PayResult;
import com.jemmy.common.Const;
import com.jemmy.utils.OkHttpCallback;
import com.jemmy.utils.OkHttpUtils;
import com.jemmy.utils.ShowAlertUtil;
import com.jemmy.viewmodel.OrderViewModel;
import com.jemmy.vo.OrderVO;
import com.jemmy.vo.ServerResponse;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

public class OrderVOAdapter extends ListAdapter<OrderVO, OrderVOAdapter.MyViewHolder> {
    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case Const.SET_VIEWMODEL:
                    mViewModel._orderVOListLive.setValue((List<OrderVO>) msg.obj);
                    break;
                case Const.SDK_PAY_FLAG:
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        showAlert(requireActivity(), getString(R.string.pay_success) + payResult);
                        ShowAlertUtil.showAlert(reference.get(), "支付成功");

                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        showAlert(requireActivity(), getString(R.string.pay_failed) + payResult);
                        ShowAlertUtil.showAlert(reference.get(), "订单创建成功，未完成支付");
                    }
                    break;
                case Const.FETCH:
                    mViewModel.fetchData();
                    break;
                case Const.MAKE_FAIL_TOAST:
                    Toast.makeText(reference.get(), (String) msg.obj, Toast.LENGTH_LONG).show();
                default:
                    break;
            }
        }
    };

    private OrderViewModel mViewModel;
    private WeakReference<Activity> reference;

    public OrderVOAdapter(@NonNull DiffUtil.ItemCallback<OrderVO> diffCallback, OrderViewModel viewModel, Activity activity) {
        super(diffCallback);
        this.mViewModel = viewModel;
        this.reference = new WeakReference<Activity>(activity);
    }

    @NonNull
    @Override
    public OrderVOAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ordervo_cell, parent, false);
        final OrderVOAdapter.MyViewHolder holder = new OrderVOAdapter.MyViewHolder(view);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final OrderVO vo = getItem(holder.getAdapterPosition());
                AlertDialog.Builder builder = new AlertDialog.Builder(reference.get()).setMessage("是否关闭订单？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, final int which) {
                                OkHttpUtils.get(Const.IP_PORT + "/portal/order/cancel.do"
                                                + "?userid=" + vo.getUserId()
                                                + "&orderno=" + vo.getOrderNo(),
                                        new OkHttpCallback() {
                                            @Override
                                            public void onFinish(String status, String msg) {
                                                super.onFinish(status, msg);
                                                if (status.equals(Const.SUCCESS)) {
                                                    Gson gson = new Gson();
                                                    ServerResponse serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse>() {
                                                    }.getType());
                                                    if (serverResponse.getStatus() == 0) {
                                                        myHandler.sendEmptyMessage(Const.FETCH);
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
    public void onBindViewHolder(@NonNull final OrderVOAdapter.MyViewHolder holder, final int position) {
        final OrderVO vo = getItem(position);
        holder.createTime.setText(vo.getCreateTime().toString());
        holder.updateTime.setText(vo.getUpdateTime().toString());
        holder.orderNo.setText(String.valueOf(vo.getOrderNo()));
        holder.statusDesc.setText(vo.getStatusDesc());
        holder.totalPrice.setText(String.valueOf(vo.getTotalPrice()));
        /**
         * 订单状态： 20-未付款 30-已付款 40-交易成功 50-交易关闭
         */
        switch (vo.getStatus()) {
            case 20:
                holder.btn_order.setVisibility(View.VISIBLE);
                holder.btn_order.setText("去付款");
                holder.btn_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(holder.itemView.getContext(), "去付款", Toast.LENGTH_LONG).show();
                        OkHttpUtils.get(Const.IP_PORT + "/portal/pay/pay.do"
                                        + "?userid=" + vo.getUserId()
                                        + "&orderno=" + vo.getOrderNo(),
                                new OkHttpCallback() {
                                    @Override
                                    public void onFinish(String status, String msg) {
                                        super.onFinish(status, msg);
                                        if (status.equals(Const.SUCCESS)) {
                                            Gson gson = new Gson();
                                            ServerResponse<String> serverResponse = gson.fromJson(msg,
                                                    new TypeToken<ServerResponse<String>>() {
                                                    }.getType());
                                            if (serverResponse.getStatus() == 0) {
                                                //得到支付宝orderInfo
                                                final String info = serverResponse.getData();
                                                final String orderInfo = info.replace("+", "%20");
                                                final Runnable payRunnable = new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        PayTask alipay = new PayTask(reference.get());
                                                        Map<String, String> result = alipay.payV2(orderInfo, true);
                                                        Log.i("msp", result.toString());

                                                        Message msg = new Message();
                                                        msg.what = Const.SDK_PAY_FLAG;
                                                        msg.obj = result;
                                                        myHandler.sendMessage(msg);
                                                    }
                                                };

                                                // 必须异步调用
                                                Thread payThread = new Thread(payRunnable);
                                                payThread.start();
                                            }
                                        }
                                    }
                                });
                    }
                });
                break;
            case 30:
                holder.btn_order.setVisibility(View.VISIBLE);
                holder.btn_order.setText("确认收货");
                holder.btn_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(holder.itemView.getContext(), "确认收货", Toast.LENGTH_LONG).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(reference.get()).setMessage("是否确认收货？")
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        OkHttpUtils.get(Const.IP_PORT + "/portal/order/confirm.do"
                                                        + "?userid=" + vo.getUserId()
                                                        + "&orderno=" + vo.getOrderNo(),
                                                new OkHttpCallback() {
                                                    @Override
                                                    public void onFinish(String status, String msg) {
                                                        super.onFinish(status, msg);
                                                        if (status.equals(Const.SUCCESS)) {
                                                            Gson gson = new Gson();
                                                            ServerResponse serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse>() {
                                                            }.getType());
                                                            if (serverResponse.getStatus() == 0) {
                                                                myHandler.sendEmptyMessage(Const.FETCH);
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
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        builder.create().show();

                    }
                });
                break;
            case 40:
                holder.btn_order.setVisibility(View.VISIBLE);
                holder.btn_order.setText("关闭订单");
                holder.btn_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(holder.itemView.getContext(), "关闭订单", Toast.LENGTH_LONG).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(reference.get()).setMessage("是否关闭订单？")
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        OkHttpUtils.get(Const.IP_PORT + "/portal/order/cancel.do"
                                                        + "?userid=" + vo.getUserId()
                                                        + "&orderno=" + vo.getOrderNo(),
                                                new OkHttpCallback() {
                                                    @Override
                                                    public void onFinish(String status, String msg) {
                                                        super.onFinish(status, msg);
                                                        if (status.equals(Const.SUCCESS)) {
                                                            Gson gson = new Gson();
                                                            ServerResponse serverResponse = gson.fromJson(msg, new TypeToken<ServerResponse>() {
                                                            }.getType());
                                                            if (serverResponse.getStatus() == 0) {
                                                                myHandler.sendEmptyMessage(Const.FETCH);
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
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        builder.create().show();
                    }
                });
                break;
            case 50:
                holder.btn_order.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView createTime, updateTime, orderNo, statusDesc, totalPrice;
        private Button btn_order;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            createTime = itemView.findViewById(R.id.createTime);
            updateTime = itemView.findViewById(R.id.updateTime);
            orderNo = itemView.findViewById(R.id.orderNo);
            statusDesc = itemView.findViewById(R.id.statusDesc);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            btn_order = itemView.findViewById(R.id.btn_order);
        }
    }
}
