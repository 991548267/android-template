package gy.android.ui.intent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class StartActivity {

    public static void startActivity(Object object, Class<?> cls) {
        startActivity(object, cls, null);
    }

    public static void startActivity(Object object, Class<?> cls, Bundle bundle) {
        startActivity(object, cls, bundle, 0);
    }

    public static void startActivity(Object object, Class<?> cls, int flags) {
        startActivity(object, cls, null, flags);
    }

    public static void startActivity(Object object, Class<?> cls, Bundle bundle, int flags) {
        Intent intent = new Intent();
        if (bundle != null)
            intent.putExtras(bundle);
        if (flags != 0)
            intent.addFlags(flags);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (object instanceof Activity) {
            Activity activity = (Activity) object;
            intent.setClass(activity, cls);
            activity.startActivity(intent);
        } else if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            intent.setClass(fragment.getContext(), cls);
            fragment.startActivity(intent);
        } else if (object instanceof Context) {
            Context context = (Context) object;
            intent.setClass(context, cls);
            context.startActivity(intent);
        }
    }

    public static void startActivityForResult(Object object, Class<?> cls, int requestCode) {
        startActivityForResult(object, cls, requestCode, null);
    }

    public static void startActivityForResult(Object object, Class<?> cls, int requestCode, Bundle bundle) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (object instanceof Activity) {
            Activity activity = (Activity) object;
            intent.setClass(activity, cls);
            activity.startActivityForResult(intent, requestCode, bundle);
        } else if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            intent.setClass(fragment.getContext(), cls);
            fragment.startActivityForResult(intent, requestCode, bundle);
        }
    }
}
