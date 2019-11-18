package gy.android.ui.util;

import android.annotation.SuppressLint;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;

import gy.android.ui.attach.Android2JS;

public class WebViewUtil {

    private WebView webView;
    private WebSettings webSettings;

    public WebViewUtil(WebView webView) {
        if (webView != null) {
            this.webView = webView;
            this.webSettings = webView.getSettings();
            initWebSettings(webSettings);
        }
    }

    private void initWebSettings(WebSettings webSettings) {
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);

        // WebView inside Browser doesn't want initial focus to be set.
        webSettings.setNeedInitialFocus(false);

        // Browser supports multiple windows
        webSettings.setSupportMultipleWindows(true);

        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    public void onResume() {
        if (webView != null) {
            webView.onResume();
        }
    }

    public void onPause() {
        if (webView != null) {
            webView.onPause();
        }
    }

    public boolean goForward() {
        if (webView != null && webView.canGoForward()) {
            webView.goForward();
            return true;
        }
        return false;
    }

    public boolean goBack() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

    public boolean goBackOrForward(int steps) {
        if (webView != null && webView.canGoBackOrForward(steps)) {
            webView.goBackOrForward(steps);
            return true;
        }
        return false;
    }

    public void destroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }
            webView.clearView();
            webView.removeAllViews();

            webView.destroy();
            webView = null;
        }
    }

    public void clearCacheHistoryFormData() {
        if (webView != null) {
            //清除网页访问留下的缓存
            //由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
            webView.clearCache(true);
            //清除当前webview访问的历史记录
            //只会webview访问历史记录里的所有记录除了当前访问记录
            webView.clearHistory();
            //这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
            webView.clearFormData();
        }
    }

    public void loadUrl(String url) {
        if (webView != null) {
            webView.loadUrl(url);
        }
    }

    public void reload() {
        if (webView != null) {
            webView.reload();
        }
    }

    public void setClient(WebViewClient client, WebChromeClient chromeClient) {
        if (webView != null) {
            webView.setWebViewClient(client);
            webView.setWebChromeClient(chromeClient);
        }
    }

    /**
     * have to call before @loadUrl
     */
    public void setCookie(String url, List<String> cookies) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookies(null);
        for (String cookie : cookies) {
            cookieManager.setCookie(url, cookie);
        }
        cookieManager.flush();
    }

    public String getCookie(String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        return cookieManager.getCookie(url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void addJavascriptInterface(Android2JS js, String name) {
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        //支持通过JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(js, name);
    }

    public void removeJavascriptInterface(String name) {
        webView.removeJavascriptInterface(name);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setJavaScriptEnabled(false);
    }
}
