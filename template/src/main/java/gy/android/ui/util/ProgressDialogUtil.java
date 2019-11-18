package gy.android.ui.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.text.TextUtils;

import java.util.concurrent.locks.ReentrantLock;

public class ProgressDialogUtil {

    private static ProgressDialog progressDialog;
    private static ReentrantLock progressDialogLock = new ReentrantLock();

    public static void synchronizedShow(Activity activity, String title, String message) {
        progressDialogLock.lock();
        show(activity, title, message);
        progressDialogLock.unlock();
    }

    public static void synchronizedDismiss(Activity activity) {
        progressDialogLock.lock();
        dismiss(activity);
        progressDialogLock.unlock();
    }

    private static void show(Activity activity, String title, String message) {
        dismiss(activity);
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        if (!TextUtils.isEmpty(title)) {
            progressDialog.setTitle(title);
        }
        if (!TextUtils.isEmpty(message)) {
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    private static void dismiss(Activity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = null;
        }
    }
}
