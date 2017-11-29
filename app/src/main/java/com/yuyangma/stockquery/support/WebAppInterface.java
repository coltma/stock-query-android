package com.yuyangma.stockquery.support;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.yuyangma.stockquery.CurrentFragment;
import com.yuyangma.stockquery.R;


/**
 * Created by Ma on 11/27/17.
 */

public class WebAppInterface {
    Context mContext;
    CurrentFragment fragment;
    Handler handler;
    CallbackManager callbackManager;

    /** Instantiate the interface and set the context */
    public WebAppInterface(Context context, CurrentFragment fragment, CallbackManager callbackManager, Handler handler) {
        this.mContext = context;
        this.handler = handler;
        this.fragment = fragment;
        this.callbackManager = callbackManager;
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
        handler.sendEmptyMessage(FreqTerm.HIDE_PROGRESS_BAR);
        ShareDialog shareDialog = new ShareDialog(this.fragment);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>(){
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d("share", "success");
                Toast.makeText(mContext.getApplicationContext(),
                        mContext.getResources().getString(R.string.facebook_share_success),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Log.d("share", "cancel");
                Toast.makeText(mContext.getApplicationContext(),
                        mContext.getResources().getString(R.string.facebook_share_cancel),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("share", "error");
                Toast.makeText(mContext.getApplicationContext(),
                        mContext.getResources().getString(R.string.facebook_share_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(url))
                    .build();
            shareDialog.show(linkContent);
        }
    }
}
