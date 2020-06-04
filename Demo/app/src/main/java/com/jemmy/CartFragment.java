package com.jemmy;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jemmy.adapter.CartAdapter;
import com.jemmy.alipay.PayResult;
import com.jemmy.common.Const;
import com.jemmy.utils.CartListVODiffCallback;
import com.jemmy.utils.OkHttpCallback;
import com.jemmy.utils.OkHttpUtils;
import com.jemmy.viewmodel.CartViewModel;
import com.jemmy.vo.CartVO;
import com.jemmy.vo.ServerResponse;

import java.util.Map;

public class CartFragment extends Fragment {

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case Const.SET_VIEWMODEL:
                    mViewModel._cartVOLive.setValue((CartVO) msg.obj);
                    break;
                case Const.CHECK_FAIL:
                    Toast.makeText(requireContext(), (String)msg.obj, Toast.LENGTH_LONG).show();
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
                        showAlert(requireActivity(), getString(R.string.pay_success));

                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        showAlert(requireActivity(), getString(R.string.pay_failed) + payResult);
                        showAlert(requireActivity(), "订单创建成功，未完成支付");
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private CartViewModel mViewModel;
    private SwipeRefreshLayout cartRefreshLayout;
    private RecyclerView cartRecycler;
    private CheckBox allChecked;
    private TextView cartTotalPrice;
    private Button orderAdd;

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        View view = inflater.inflate(R.layout.cart_fragment, container, false);

        cartRefreshLayout = view.findViewById(R.id.cartRefreshLayout);
        cartRecycler = view.findViewById(R.id.cartRecycler);
        allChecked = view.findViewById(R.id.allChecked);
        cartTotalPrice = view.findViewById(R.id.cartTotalPrice);
        orderAdd = view.findViewById(R.id.orderAdd);

        cartRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.fetchData();
            }
        });

        final CartAdapter adapter = new CartAdapter(new CartListVODiffCallback(), mViewModel);
        cartRecycler.setAdapter(adapter);
        cartRecycler.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        mViewModel._cartVOLive.observe(getViewLifecycleOwner(), new Observer<CartVO>() {
            @Override
            public void onChanged(CartVO cartVO) {
                adapter.submitList(cartVO.getCartProductVOList());
                cartRefreshLayout.setRefreshing(false);
                if(cartVO.getAllChecked() != null) {
                    allChecked.setChecked(cartVO.getAllChecked());
                }
                if(cartVO.getCartTotalPrice() != null) {
                    cartTotalPrice.setText(String.valueOf(cartVO.getCartTotalPrice()));
                } else {
                    cartTotalPrice.setText("0");
                }
            }
        });

        allChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //全选
                OkHttpUtils.get(Const.IP_PORT + "/portal/cart/check.do?userid="
                                + mViewModel.userId,
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
                                    } else {
                                        Message message = Message.obtain();
                                        message.what = Const.CHECK_FAIL;
                                        message.obj = serverResponse.getMsg();
                                        myHandler.sendMessage(message);
                                    }
                                } else {
                                    Message message = Message.obtain();
                                    message.what = Const.CHECK_FAIL;
                                    message.obj = msg;
                                    myHandler.sendMessage(message);
                                }
                            }
                        });
            }
        });

        orderAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //结算，添加订单
                OkHttpUtils.get(Const.IP_PORT + "/portal/order/create.do?userid="
                                + mViewModel.userId,
                        new OkHttpCallback() {
                            @Override
                            public void onFinish(String status, String msg) {
                                super.onFinish(status, msg);
                                if (status.equals(Const.SUCCESS)) {

                                    Gson gson = new Gson();
                                    ServerResponse<Long> serverResponse =
                                            gson.fromJson(msg,
                                            new TypeToken<ServerResponse<Long>>() {
                                            }.getType());
                                    if (serverResponse.getStatus() == 0) {
                                        //订单添加成功

                                        Long orderNo = serverResponse.getData();
                                        OkHttpUtils.get(Const.IP_PORT + "/portal/pay/pay.do"
                                                        + "?userid=" + mViewModel.userId
                                                        + "&orderno=" + orderNo,
                                                new OkHttpCallback() {
                                                    @Override
                                                    public void onFinish(String status, String msg) {
                                                        super.onFinish(status, msg);
                                                        if (status.equals(Const.SUCCESS)) {

                                                            Gson gson = new Gson();
                                                            ServerResponse<String> serverResponse2 =
                                                                    gson.fromJson(msg,
                                                                            new TypeToken<ServerResponse<String>>() {
                                                                            }.getType());
                                                            if (serverResponse2.getStatus() == 0) {
                                                                //得到支付宝orderInfo
                                                                final String info = serverResponse2.getData();
                                                                final String orderInfo = info.replace("+", "%20");
                                                                final Runnable payRunnable = new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        PayTask alipay = new PayTask(requireActivity());
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

                                                            } else {
                                                                Message message = Message.obtain();
                                                                message.what = Const.CHECK_FAIL;
                                                                message.obj = serverResponse2.getMsg();
                                                                myHandler.sendMessage(message);
                                                            }
                                                        } else {
                                                            Message message = Message.obtain();
                                                            message.what = Const.CHECK_FAIL;
                                                            message.obj = msg;
                                                            myHandler.sendMessage(message);
                                                        }
                                                    }
                                                });

                                    } else {
                                        Message message = Message.obtain();
                                        message.what = Const.CHECK_FAIL;
                                        message.obj = serverResponse.getMsg();
                                        myHandler.sendMessage(message);
                                    }
                                } else {
                                    Message message = Message.obtain();
                                    message.what = Const.CHECK_FAIL;
                                    message.obj = msg;
                                    myHandler.sendMessage(message);
                                }
                            }
                        });
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = ViewModelProviders.of(this).get(CartViewModel.class);

        // TODO: Use the ViewModel

//        "cartProductVOList":null,"allChecked":null,"cartTotalPrice":null
        if (mViewModel._cartVOLive.getValue() == null ||
                mViewModel._cartVOLive.getValue().getCartProductVOList() == null ||
                mViewModel._cartVOLive.getValue().getAllChecked() == null ||
                mViewModel._cartVOLive.getValue().getCartTotalPrice() == null) {
            mViewModel.fetchData();
        }
    }

    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton(R.string.confirm, null)
                .setOnDismissListener(onDismiss)
                .show();
    }
}
