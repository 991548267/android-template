package gy.android.ui.attach;

import android.webkit.JavascriptInterface;

public class Android2JS {

    @JavascriptInterface
    public void println(String msg) {
        System.out.println(msg);
    }
}
