package com.jemmy.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.jemmy.R;

public class ShowAlertUtil {
    public static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    public static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton(R.string.confirm, null)
                .setOnDismissListener(onDismiss)
                .show();
    }
}
