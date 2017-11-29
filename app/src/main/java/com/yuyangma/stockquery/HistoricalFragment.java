package com.yuyangma.stockquery;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.yuyangma.stockquery.support.FreqTerm;
import com.yuyangma.stockquery.support.WebAppInterface;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoricalFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class HistoricalFragment extends Fragment {
    private String symbol;
    private int page;
    private WebView webView;
    private String indicator;
    private String ret;


    private ProgressBar webViewProgressBar;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FreqTerm.HIDE_WEBVIEW_PROGRESS_BAR:
                    webViewProgressBar.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    break;
                case FreqTerm.SHOW_WEBVIEW_PROGRESS_BAR:
                    webView.setVisibility(View.GONE);
                    webViewProgressBar.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param page Parameter 1.
     * @param symbol Parameter 2.
     * @return A new instance of fragment HistoricalFragment.
     */
    public static HistoricalFragment newInstance(int page, String symbol) {
        HistoricalFragment fragment = new HistoricalFragment();
        Bundle args = new Bundle();
        args.putInt(FreqTerm.PAGE_KEY, page);
        args.putString(FreqTerm.SYMBOL_KEY, symbol);
        fragment.setArguments(args);
        return fragment;
    }
    public HistoricalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page = getArguments().getInt(FreqTerm.PAGE_KEY);
            symbol = getArguments().getString(FreqTerm.SYMBOL_KEY);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historical, container, false);

        // Progress bar
        webViewProgressBar = (ProgressBar) view.findViewById(R.id.webview_progessbar_historical);

        webView = (WebView) view.findViewById(R.id.webview_historical);
        webView.setVisibility(View.GONE);
        // Enable JavaScript call Android.
        webView.addJavascriptInterface(new WebAppInterface(getContext(), handler), "Android");
        // Disable other broswer.
        webView.setWebViewClient(new HistoricalFragment.MyWebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);

        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        webView.loadUrl("http://www-scf.usc.edu/~yuyangma/superchart.html");

        indicator = FreqTerm.HISTORICAL;
        handler.sendEmptyMessage(FreqTerm.SHOW_WEBVIEW_PROGRESS_BAR);

        return view;
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
//            view.loadUrl("javascript:var data = document.documentElement.innerHTML; console.log(data);");
            webView.post(new Runnable() {
                @Override
                public void run() {
                    // Use newIndicator instead of indicator, in case of use switch a new indicator.
                    String type = "\"" + indicator + "\"";;
                    String tmpSymbol = "\"" + symbol + "\"";
                    //MUST UPPER CASE FOR KEY MATHC.
                    String params = type + "," + tmpSymbol;
                    webView.evaluateJavascript("javascript:createChart(" + params.toUpperCase() + ");" , new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            Log.d("historical-eval-back", value);
                            ret = value;

                        }
                    });
//                        webView.loadUrl("javascript:test()");
                }
            });
            Log.d("finished","test()");
        }
    }

}
