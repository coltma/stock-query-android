package com.yuyangma.stockquery;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentFragment extends Fragment {

    public static final String KEY_PAGE = "page";

    private TextView textView;
    private ImageButton fbBtn;
    private ImageButton starBtn;
    private WebView webView;

    public CurrentFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static CurrentFragment newInstance(int page) {

        Bundle args = new Bundle();
        args.putInt(KEY_PAGE, page);

        CurrentFragment fragment = new CurrentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("onCreateView", "" + getArguments().getInt(KEY_PAGE));
        View view = inflater.inflate(R.layout.fragment_current, container, false);
        textView = (TextView) view.findViewById(R.id.fragment_stock_details);
        fbBtn = (ImageButton) view.findViewById(R.id.fragment_facebook_btn);
        starBtn = (ImageButton) view.findViewById(R.id.fragment_star_btn);

        starBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                webView.evaluateJavascript("alert(\"aaa\");", null);
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("starBtn", "clicked");
                        String symbol = "\"老马最Man\"";
                        webView.evaluateJavascript("javascript:test(" + symbol + ");" , new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("eval-back", value);
                            }
                        });
//                        webView.loadUrl("javascript:test()");
                    }
                });

            }
        });

        webView = (WebView) view.findViewById(R.id.webview_current);
        // Disable other broswer.
        webView.setWebViewClient(new MyWebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();


        webView.loadUrl("http://www-scf.usc.edu/~yuyangma/52whyOoTAT4QnM7.html");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        int page = getArguments().getInt(KEY_PAGE);
//        textView.setText("page -> " + page);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("www-scf.usc.edu")) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.loadUrl("javascript:var data = document.documentElement.innerHTML; console.log(data);");
            Log.i("finished","test()");
        }
    }




}
