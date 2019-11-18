package gy.android.ui.util;

import android.content.Context;

import androidx.appcompat.app.AppCompatDialog;

public class AppCompatDialogUtil {

    private static AppCompatDialog dialog;

    public static void showDialog(Context context) {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
        dialog = new AppCompatDialog(context);
    }
}
