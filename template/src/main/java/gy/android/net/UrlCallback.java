package gy.android.net;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.annotations.EverythingIsNonNull;

public abstract class UrlCallback implements Callback {

    public static final String MSG_NETWORK_ERROR = "网络错误，请重试！";
    public static final String MSG_JSON_ERROR = "JSON解析错误，请重试！";
    public static final String MSG_DATA_LOAD_ERROR = "加载数据失败，请重试！";

    protected boolean succeed = false;

    @Override
    public void onFailure(Call call, IOException e) {
        if (call.isCanceled()) {
            return;
        }
        onFailureCallback();
    }

    @Override
    @EverythingIsNonNull
    public void onResponse(Call call, Response response) {
        if (call.isCanceled()) {
            return;
        }
        if (response.isSuccessful()) {
            try {
                onSuccessCallback(response.body().string());
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        onFailureCallback();
    }

    protected void onFailureCallback() {
        succeed = false;
        onFailure(MSG_NETWORK_ERROR);
        onFinally();
    }

    protected void onSuccessCallback(String body) {
        succeed = true;
        onSuccess(body);
        onFinally();
    }

    public abstract void onFailure(String message);

    public abstract void onSuccess(String body);

    public abstract void onFinally();
}
