package com.jemmy.alipay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.jemmy.R;
import com.jemmy.alipay.util.OrderInfoUtil2_0;

import java.util.Map;

/**
 * 重要说明：
 * <p>
 * 本 Demo 只是为了方便直接向商户展示支付宝的整个支付流程，所以将加签过程直接放在客户端完成
 * （包括 OrderInfoUtil2_0_HK 和 OrderInfoUtil2_0）。
 * <p>
 * 在真实 App 中，私钥（如 RSA_PRIVATE 等）数据严禁放在客户端，同时加签过程务必要放在服务端完成，
 * 否则可能造成商户私密数据泄露或被盗用，造成不必要的资金损失，面临各种安全风险。
 * <p>
 * Warning:
 * <p>
 * For demonstration purpose, the assembling and signing of the request parameters are done on
 * the client side in this demo application.
 * <p>
 * However, in practice, both assembling and signing must be carried out on the server side.
 */
public class PayDemoActivity extends AppCompatActivity {

    /**
     * 用于支付宝支付业务的入参 app_id。
     */
    public static final String APPID = "2016102400748346";

    /**
     * 用于支付宝账户登录授权业务的入参 pid。
     */
    public static final String PID = "2088102180830201";

    /**
     * 用于支付宝账户登录授权业务的入参 target_id。
     */
    public static final String TARGET_ID = "";

    /**
     * pkcs8 格式的商户私钥。
     * <p>
     * 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个，如果两个都设置了，本 Demo 将优先
     * 使用 RSA2_PRIVATE。RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议商户使用
     * RSA2_PRIVATE。
     * <p>
     * 建议使用支付宝提供的公私钥生成工具生成和获取 RSA2_PRIVATE。
     * 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    public static final String RSA2_PRIVATE = "MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQDLJob8IFeEq6Gs6lpf1VeQ9iYX0/reHgFrGF4NP1UB/0qYLDHw2ow1IY0gfMKpGOWtI2xR7oavyrM2PFCOWuE1ivxxW4VR7r5/uBxErXNx7VHR3RzzFAfpJbiQABjc4pC50336jAOG/FyaFtspPJje8Cf1uwVUX1/6DPtiCX27kHGvIkckOXwfjMwqG7D5cRQxTfBfawLJ9Ykw4IxCmfrEhCdffQn1RkmzYyH2a8wy486pRPMY47YK5Mzgktl4aDKa00GuXiFDig5+fyeAzj+kB6Ea25iqudL+97Dg+oOnNqkgqtrOHj5ukVbfDXwlNx6dEEoAEvJyEO4ENNcy1U41AgMBAAECggEBAJPB4v4wu9cYSpCNJ8x6TKV4Ez7ThKzXYCq333h9LuQz/dpukqhfHQlr7MzLUX5SSINEt31kIvxrTyvMdL8FpLYf7/q3GWH9ZvtYOt9M1SHw/cjJrUuzrwlJ0VKTb4IBHEi8Ze28trgQh33fz+nPVFUakcXuEDNM527k0FZ/Fr87RPtkxuKPax3+/5Rr/zKV1ghtQPMNpOzXurSNVH9Y8FuLkP/uvVqhEUyGwvPRDZ2UqQ/ArqSw4f/M7TgMCh6QDJX1GdnKlUeQTQL46WfE3hYp7DW8y0EZ/Sva2pGDtH428f/g/RNLxcFI3bkMRbo1yCYbH6AreokLgoj9PO5kwUUCgYEA79f5Gu7UDtdnwN9RXLo5MsuIEE2j4u1Hrqf32Dji7YOWdM05Vlfdhs5RwCugIgbT/JxrTrdp1xN3C4cbfd68w5qklPSSrzO4So4aNxQNRFEuhCHfCIRmLF+uAK+0fpTQSO6Jd/fCd6m3z0wKuf8mc8gaQFUHH1yR5VIRC2pTqU8CgYEA2NXK6XSnnwRCgmucwDxVhzaRvEy2Jew9VNk4e6+hu2tYiYQPhlgcNvM5dczFnBkG5YFxFHaIBM3VejdUUbW0B/dDcZ4bYu06y4KWa/i8oeBR9zE7lkcy56fSUxdeo3j4cagUVJZu493TM/chHpswijNlZ19mnbEtvzfWKMAJ5zsCgYEA7Kf0emjSAVr2W09/Hrn5mm9N8JhLb6yloGbInSd0ctv2Z4XgX5wPT+bgr3fcZ/bI0WceUuuTmc7xFgTYUZioNXGvl/LDkfC8lYQlxdcZSCG6flJVmjfaAUrfjHnS5Vu9MVl9d0yXLC/g2MC1jNWAgZoenP/JKV7T7qzNeLAPoO0CgYEAsm3TKKKm4HKX7ExzU7kSRE7CQ+n4JWKt4WlSUm1nMBuYYqi6Qbm1lf41tVsI7Xemtir7C3SyXwml9okbKJXWDIwLAq6xKInATQErmLO5PZeiuz/XIcV6/SebKmbbqLd98cD24dhcbcP27RcaON5JZh/+/L6DaZYH6AchgqzsOUsCgYEAiIvKb1UaYntBNwMgRsMv0H6jFdCVCuneKb/F0wDM6agipIZeBlDO5D1THI+d4Z5LKW8/2RAaX8R1NKo9sUgA+lTrKez/NFEM29qX9dl/YZGBPqXB40M9orre09ekoZlqBk/6nMEFDBD67R0Y6B6qbJKXH2RR6VBAKWdhsDX175c=";
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
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
                        showAlert(PayDemoActivity.this, getString(R.string.pay_success) + payResult);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showAlert(PayDemoActivity.this, getString(R.string.pay_failed) + payResult);
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        showAlert(PayDemoActivity.this, getString(R.string.auth_success) + authResult);
                    } else {
                        // 其他状态值则为授权失败
                        showAlert(PayDemoActivity.this, getString(R.string.auth_failed) + authResult);
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_main);
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
    }

    /**
     * 支付宝支付业务示例
     */
    public void payV2(View v) {
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            showAlert(this, getString(R.string.error_missing_appid_rsa_private));
            return;
        }
        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo 的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
//        final String orderInfo = orderParam + "&" + sign;

        final String orderInfo = "charset=utf-8&biz_content=%7B%22timeout_express%22%3A%2230m%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22total_amount%22%3A99999.0%2C%22subject%22%3A%221%22%2C%22body%22%3A%22%E6%88%91%E6%98%AF%E6%B5%8B%E8%AF%95%E6%95%B0%E6%8D%AE%22%2C%22out_trade_no%22%3A%221591204017220%22%7D&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fu3g7ik.natappfree.cc%2Fportal%2Fpay%2Fcallback.do&app_id=2016102400748346&sign_type=RSA2&version=1.0&timestamp=2020-06-04%2001%3A41%3A42&sign=PbUzWyzIuhUxxbroangC4gzNoKIila6cSV2rb%2Fjbgn4lSrSBY5Ob4skCzCVXrrXig93e5Vw78m26FF3f6n%2FDuPRCZPD8yv77d774O3AvxRFNihTkv1HYQ1a78ducb3fEYKat5Br4x%2FzNfdexuesdqiBgoeR5m4%2FofaiY9w7xly8TuattGjezCCHAW93byT7GiJxLKLacUHtaqPjYjSNvrZaKhCNTs2CzbibyS91eo2vV94No6s8u2AKdmsVi8CpJgPG0P%2BT3trK537ruzbTnLTaRrpqDIbdrHOVhT6N9iEN%2Fes2HOM%2B%2BStxPbGcyIe9f2Qry0Su4vNj%2F3dWYYjwuPw%3D%3D";



        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(PayDemoActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 支付宝账户授权业务示例
     */
    public void authV2(View v) {
        if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APPID)
                || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))
                || TextUtils.isEmpty(TARGET_ID)) {
            showAlert(this, getString(R.string.error_auth_missing_partner_appid_rsa_private_target_id));
            return;
        }
        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * authInfo 的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(PID, APPID, TARGET_ID, rsa2);
        String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
        final String authInfo = info + "&" + sign;
        Runnable authRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(PayDemoActivity.this);
                // 调用授权接口，获取授权结果
                Map<String, String> result = authTask.authV2(authInfo, true);

                Message msg = new Message();
                msg.what = SDK_AUTH_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread authThread = new Thread(authRunnable);
        authThread.start();
    }

    /**
     * 获取支付宝 SDK 版本号。
     */
    public void showSdkVersion(View v) {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        showAlert(this, getString(R.string.alipay_sdk_version_is) + version);
    }

    /**
     * 将 H5 网页版支付转换成支付宝 App 支付的示例
     */
    public void h5Pay(View v) {
        WebView.setWebContentsDebuggingEnabled(true);
        Intent intent = new Intent(this, H5PayDemoActivity.class);
        Bundle extras = new Bundle();

        /*
         * URL 是要测试的网站，在 Demo App 中会使用 H5PayDemoActivity 内的 WebView 打开。
         *
         * 可以填写任一支持支付宝支付的网站（如淘宝或一号店），在网站中下订单并唤起支付宝；
         * 或者直接填写由支付宝文档提供的“网站 Demo”生成的订单地址
         * （如 https://mclient.alipay.com/h5Continue.htm?h5_route_token=303ff0894cd4dccf591b089761dexxxx）
         * 进行测试。
         *
         * H5PayDemoActivity 中的 MyWebViewClient.shouldOverrideUrlLoading() 实现了拦截 URL 唤起支付宝，
         * 可以参考它实现自定义的 URL 拦截逻辑。
         */
        String url = "https://m.taobao.com";
        extras.putString("url", url);
        intent.putExtras(extras);
        startActivity(intent);
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

    private static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    private static String bundleToString(Bundle bundle) {
        if (bundle == null) {
            return "null";
        }
        final StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            sb.append(key).append("=>").append(bundle.get(key)).append("\n");
        }
        return sb.toString();
    }
}
