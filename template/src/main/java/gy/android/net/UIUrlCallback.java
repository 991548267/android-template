package gy.android.net;

import android.os.Handler;
import android.os.Looper;

public abstract class UIUrlCallback extends UrlCallback {

    protected Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onFailureCallback() {
        handler.post(() -> UIUrlCallback.super.onFailureCallback());
    }

    @Override
    protected void onSuccessCallback(String body) {
        handler.post(() -> UIUrlCallback.super.onSuccessCallback(body));
    }
}
