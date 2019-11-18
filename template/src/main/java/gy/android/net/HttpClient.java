package gy.android.net;

import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class HttpClient {

    // {
    // JSON数据格式
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    // 图片格式 gif png jpg
    public static final MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/*");
    // 二进制流数据
    public static final MediaType MEDIA_TYPE_BYTE = MediaType.parse("application/octet-stream");
    // MARKDOWN数据格式
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    // }

    public static final long DEFAULT_TIMEOUT = 60000;

    public abstract OkHttpClient getClient();

    public abstract Map<String, String> getHeaders();

    public Call doPostJson(String url, String json) {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, json);
        Request request = buildRequest(TYPE_POST, url, requestBody);
        return getClient().newCall(request);
    }

    public Call doPutJson(String url, String json) {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, json);
        Request request = buildRequest(TYPE_PUT, url, requestBody);
        return getClient().newCall(request);
    }

    public Call doGetJson(String url) {
        Request request = buildRequest(TYPE_GET, url, null);
        return getClient().newCall(request);
    }

    public Call doDeleteJson(String url, String json) {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, json);
        Request request = buildRequest(TYPE_DELETE, url, requestBody);
        return getClient().newCall(request);
    }

    protected static final int TYPE_GET = 0;
    protected static final int TYPE_POST = 1;
    protected static final int TYPE_PUT = 2;
    protected static final int TYPE_DELETE = 3;

    protected Request buildRequest(int type, String url, RequestBody requestBody) {
        Request.Builder builder = new Request.Builder().url(url);
        switch (type) {
            case TYPE_GET:
                builder = builder.get();
                break;
            case TYPE_POST:
                builder = builder.post(requestBody);
                break;
            case TYPE_PUT:
                builder = builder.put(requestBody);
                break;
            case TYPE_DELETE:
                builder = builder.delete(requestBody);
                break;
            default:
                return null;
        }
        if (!getHeaders().isEmpty()) {
            for (String key : getHeaders().keySet()) {
                builder = builder.addHeader(key, getHeaders().get(key));
            }
        }
        return builder.build();
    }
}
