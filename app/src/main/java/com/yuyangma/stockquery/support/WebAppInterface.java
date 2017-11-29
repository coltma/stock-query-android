package com.yuyangma.stockquery.support;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.yuyangma.stockquery.CurrentFragment;


/**
 * Created by Ma on 11/27/17.
 */

public class WebAppInterface {
    Context mContext;
    CurrentFragment fragment;
    Handler handler;

    /** Instantiate the interface and set the context */
    public WebAppInterface(Context context, CurrentFragment fragment, Handler handler) {
        this.mContext = context;
        this.handler = handler;
        this.fragment = fragment;
    }

    /** JS load data async and hide progress bar for webview in Android. */
    @JavascriptInterface
    public void hideWebViewProgressBar() {
        Log.d("JS-Android", "called");
        handler.sendEmptyMessage(FreqTerm.HIDE_WEBVIEW_PROGRESS_BAR);
    }

    /** JS load data async and show facebook share. */
    @JavascriptInterface
    public void facebookShare(String url) {
        Log.d("share", "receive:" + url);
//        ShareDialog shareDialog = new ShareDialog(this.fragment);
//        if (ShareDialog.canShow(ShareLinkContent.class)) {
//            ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                    .setContentUrl(Uri.parse(url))
//                    .build();
//            shareDialog.show(linkContent);
//        }
    }
}
