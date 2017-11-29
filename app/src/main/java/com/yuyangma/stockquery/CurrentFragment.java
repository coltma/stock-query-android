package com.yuyangma.stockquery;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.yuyangma.stockquery.model.StockDetail;
import com.yuyangma.stockquery.model.StockDetailItem;
import com.yuyangma.stockquery.support.FreqTerm;
import com.yuyangma.stockquery.adapter.StockDetailAdapter;
import com.yuyangma.stockquery.support.WebAppInterface;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentFragment extends Fragment {
    private static final int POS_SYMBOL = 0;
    private static final int POS_PRICE = 1;
    private static final int POS_CHANGE = 2;
    private static final String MY_AWS = "http://cs571.us-east-1.elasticbeanstalk.com/";
    private static final String MY_URL = MY_AWS + "getquote?outputsize=compact&symbol=";

    private String symbol = "";
    private boolean isFavorited = false;
    // SMA, CCI, EMA...
    private String indicator = "";
    private String ret = "";

    private TextView textView;
    private Button fbBtn;
    private Button starBtn;
    private Spinner spinner;
    private WebView webView;
    private TextView changeBtn;
    private ListView listView;
    private ProgressBar progressBar;
    private ProgressBar webViewProgressBar;

    private StockDetailAdapter stockDetailAdapter;
    private List<StockDetailItem> detailItems = new ArrayList<>();;
    private StockDetail stockDetail = null;

    private RequestQueue requestQueue;
    private CallbackManager callbackManager;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FreqTerm.HIDE_PROGRESS_BAR:
                    progressBar.setVisibility(View.GONE);
                    break;
                case FreqTerm.SHOW_PROGRESS_BAR:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
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

    public CurrentFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static CurrentFragment newInstance(int page, String symbol, boolean isFavorited) {
        Bundle args = new Bundle();
        args.putInt(FreqTerm.PAGE_KEY, page);
        args.putString(FreqTerm.SYMBOL_KEY, symbol);
        args.putBoolean(FreqTerm.FAVORITE_KEY, isFavorited);
        CurrentFragment fragment = new CurrentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        symbol = getArguments().getString(FreqTerm.SYMBOL_KEY);
        Log.d("onCreate", symbol);
        isFavorited = getArguments().getBoolean(FreqTerm.FAVORITE_KEY);
        // Inflate the layout for this fragment
        Log.d("onCreateView", "" + getArguments().getInt(FreqTerm.PAGE_KEY));
        View view = inflater.inflate(R.layout.fragment_current, container, false);
        textView = (TextView) view.findViewById(R.id.fragment_stock_details);
        changeBtn = (TextView) view.findViewById(R.id.change_btn);
        fbBtn = (Button) view.findViewById(R.id.fragment_facebook_btn);
        starBtn = (Button) view.findViewById(R.id.fragment_star_btn);
        if (isFavorited) {
            starBtn.setBackground(getResources().getDrawable(R.mipmap.ic_star_filled, null));
        }

        // Progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.fragment_current_progressbar);

        // Webview Progress bar
        webViewProgressBar = (ProgressBar) view.findViewById(R.id.webview_progessbar);
        webViewProgressBar.setVisibility(View.GONE);

        // Spinner
        spinner = (Spinner) view.findViewById(R.id.crt_frg_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.indicators_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                final String nextIndicator = adapterView.getItemAtPosition(i).toString();
                Log.d("selected", nextIndicator);
                Log.d("indicator", indicator);
                if (!indicator.isEmpty() && indicator.equals(nextIndicator)) {
                    disableChangeClickListener();
                } else {
                    changeBtn.setTextColor(getContext().getColor(R.color.colorBlack));
                    enableChangeClickListener(nextIndicator);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinner.setAdapter(spinnerAdapter);


        //Facebook share.
        callbackManager = CallbackManager.Factory.create();
        fbBtn = (Button) view.findViewById(R.id.fragment_facebook_btn);
        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareSymbol = symbol;
                String shareIndicator = indicator;
                if (shareSymbol.isEmpty()) {
                    Toast.makeText(getContext().getApplicationContext(),
                            "Please select stock symbol first.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // Default use "Price".
                if (shareIndicator.isEmpty()) {
                    shareIndicator = FreqTerm.PRICE;
                }
                String type = "\"" + shareIndicator + "\"";;
                String tmpSymbol = "\"" + shareSymbol + "\"";
                String params = type + "," + tmpSymbol;
                handler.sendEmptyMessage(FreqTerm.SHOW_PROGRESS_BAR);
                webView.evaluateJavascript("javascript:exportChart(" + params.toUpperCase() + ");" , new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.d("eval-back", value);
                        // When JavaScript done, it calls facebook share.
                        ret = value;

                    }
                });
            }
        });

        // List View
        listView = (ListView) view.findViewById(R.id.stock_detail_list_view);
        stockDetailAdapter = new StockDetailAdapter(getContext(), detailItems);

        listView.setAdapter(stockDetailAdapter);

        // List View StockDetail
        handler.sendEmptyMessage(FreqTerm.SHOW_PROGRESS_BAR);
        requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                MY_URL + symbol,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

//                      adapter.clear();
                        Log.d("before", stockDetailAdapter.getCount() + "");
                        stockDetail = new StockDetail();
                        if (stockDetail.loadJSON(response)) {
                            detailItems.clear();
                            stockDetail.createStockDetailItems(detailItems);
                            Log.d("after", stockDetailAdapter.getCount() + "");
//                        detailItems.add(new StockDetailItem("abc", "dfg"));
                            stockDetailAdapter.notifyDataSetChanged();
                            // Should update the old data.
                            if (isFavorited) {
                                updateFavoriteList(stockDetail);
                            }
                            enableStarBtn();
                        } else {
                            // TODO
                            return;
                        }
                        handler.sendEmptyMessage(FreqTerm.HIDE_PROGRESS_BAR);
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handler.sendEmptyMessage(FreqTerm.HIDE_PROGRESS_BAR);
                    }
                }
        );
        // When switch tabs, should not reload data. (But spec reload webview.)
        Log.d("Current", "detailItems size:" + detailItems.size());
        if (detailItems.size() == 0) {
            requestQueue.add(request);
        } else {
            // Spec enables changeBtn and remove webView.
            changeBtn.setTextColor(getContext().getColor(R.color.colorBlack));
            enableChangeClickListener(indicator);
        }

        webView = (WebView) view.findViewById(R.id.webview_current);
        webView.setVisibility(View.GONE);
        // Enable JavaScript call Android.
        webView.addJavascriptInterface(new WebAppInterface(getContext(),
                this,
                callbackManager,
                handler), "Android");
        // Disable other broswer.
        webView.setWebViewClient(new MyWebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        webView.loadUrl("http://www-scf.usc.edu/~yuyangma/superchart.html");

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        int page = getArguments().getInt(FreqTerm.PAGE_KEY);

        Log.d("Created", symbol);
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
//            view.loadUrl("javascript:var data = document.documentElement.innerHTML; console.log(data);");
            Log.d("finished","test()");
        }
    }


    private void enableChangeClickListener(final String newIndicator) {
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("change", "clicked");

                indicator = newIndicator;
                disableChangeClickListener();
                handler.sendEmptyMessage(FreqTerm.SHOW_WEBVIEW_PROGRESS_BAR);
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        // Use newIndicator instead of indicator, in case of use switch a new indicator.
                        String type = "\"" + newIndicator + "\"";;
                        String tmpSymbol = "\"" + symbol + "\"";
                        //MUST UPPER CASE FOR KEY MATHC.
                        String params = type + "," + tmpSymbol;
                        webView.evaluateJavascript("javascript:createChart(" + params.toUpperCase() + ");" , new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.d("eval-back", value);
                                ret = value;

                            }
                        });
//                        webView.loadUrl("javascript:test()");
                    }
                });

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("share", "onActivityResult");
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void disableChangeClickListener() {
        changeBtn.setTextColor(getContext().getColor(R.color.colorGrey));
        changeBtn.setOnClickListener(null);
    }

    private void enableStarBtn() {
        // Star Button
        starBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorited) {
                    starBtn.setBackground(getResources().getDrawable(R.mipmap.ic_star_empty, null));
                    removeFromFavoriteList(symbol);
                } else {
                    starBtn.setBackground(getResources().getDrawable(R.mipmap.ic_star_filled, null));
                    updateFavoriteList(stockDetail);
                }
                isFavorited = !isFavorited;
            }
        });
    }

    private void removeFromFavoriteList(String symbol) {
        SharedPreferences sharedPref = getActivity().getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        // check
        Set<String> favorites = new HashSet<>();
        favorites = sharedPref.getStringSet(getString(R.string.preference_symbols_key), favorites);
        Log.d("favorite", "current fragment before remove:" + favorites.toString());
        Log.d("favorite", "current fragment before remove symbol:" + symbol);
        if (!favorites.contains(symbol)) {
            return ;
        }

        favorites.remove(symbol);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(getString(R.string.preference_symbols_key), favorites);
        editor.remove(symbol);
        editor.commit();
        Log.d("favorite", "current fragment after remove:" + favorites.toString());

    }

    private void updateFavoriteList(StockDetail stockDetail) {
        SharedPreferences sharedPref = getActivity().getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key),Context.MODE_PRIVATE);

        // check
        Set<String> favorites = new HashSet<>();
        Log.d("favorite", "current fragment before update:" + favorites.toString());
        favorites = sharedPref.getStringSet(getString(R.string.preference_symbols_key), favorites);
        favorites.add(stockDetail.getSymbol());

        String data = stockDetail.getSymbol()
                + "," + stockDetail.getLastPrice()
                + "," + stockDetail.getClose();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(getString(R.string.preference_symbols_key), favorites);
        editor.putString(stockDetail.getSymbol(), data);
        editor.commit();
        Log.d("favorite", "current fragment after update:" + favorites.toString());
    }
}
