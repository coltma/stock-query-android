package com.yuyangma.stockquery.support;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;


/**
 * Created by Ma on 11/27/17.
 */

public class WebAppInterface {
    Context mContext;
    Handler handler;

    /** Instantiate the interface and set the context */
    public WebAppInterface(Context c, Handler handler) {
        mContext = c;
        this.handler = handler;
    }

    /** JS load data async and hide progress bar for webview in Android. */
    @JavascriptInterface
    public void hideWebViewProgressBar() {
        Log.d("JS-Android", "called");
        handler.sendEmptyMessage(FreqTerm.HIDE_WEBVIEW_PROGRESS_BAR);
    }
}
