package gy.android.ui.util;

import android.app.Activity;
import android.widget.Toast;

public class ToastUtil {
    private static Toast toast;

    public static synchronized void show(Activity activity, String message) {
        dismiss(activity);
        toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void dismiss(Activity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
    }
}
