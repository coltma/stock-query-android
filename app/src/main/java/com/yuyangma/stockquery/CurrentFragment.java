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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.yuyangma.stockquery.model.StockDetail;
import com.yuyangma.stockquery.model.StockDetailItem;
import com.yuyangma.stockquery.view.StockDetailAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentFragment extends Fragment {
    private static final String KEY_PAGE = "page";
    private static final String KEY_SYMBOL = "symbol";
    private static final String MY_URL = "http://cs571.us-east-1.elasticbeanstalk.com/" +
            "getquote?outputsize=compact&symbol=";


    private String symbol = "";
    // SMA, CCI, EMA...
    private String indicator = "";
    // private String clickedIndicator = "";
    private String ret = "";

    private TextView textView;
    private Button fbBtn;
    private Button starBtn;
    private Spinner spinner;
    private WebView webView;
    private TextView changeBtn;
    private ListView listView;

    private StockDetailAdapter stockDetailAdapter;
    private List<StockDetailItem> detailItems;

    private RequestQueue requestQueue;

    public CurrentFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static CurrentFragment newInstance(int page, String symbol) {
        Bundle args = new Bundle();
        args.putInt(KEY_PAGE, page);
        args.putString(KEY_SYMBOL, symbol);
        CurrentFragment fragment = new CurrentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        symbol = getArguments().getString(KEY_SYMBOL);
        Log.i("onCreate", symbol);

        // Inflate the layout for this fragment
        Log.i("onCreateView", "" + getArguments().getInt(KEY_PAGE));
        View view = inflater.inflate(R.layout.fragment_current, container, false);
        textView = (TextView) view.findViewById(R.id.fragment_stock_details);
        changeBtn = (TextView) view.findViewById(R.id.change_btn);
        fbBtn = (Button) view.findViewById(R.id.fragment_facebook_btn);
        starBtn = (Button) view.findViewById(R.id.fragment_star_btn);


        // Spinner
        spinner = (Spinner) view.findViewById(R.id.crt_frg_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.indicators_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                final String nextIndicator = adapterView.getItemAtPosition(i).toString();
                Log.i("selected", nextIndicator);
                Log.i("indicator", indicator);
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

        // Star Button
        starBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // List View
        listView = (ListView) view.findViewById(R.id.stock_detail_list_view);
        detailItems = new ArrayList<>();
        stockDetailAdapter = new StockDetailAdapter(getContext(), detailItems);
//        detailItems.add(new StockDetailItem("aa","bb"));
//        detailItems.add(new StockDetailItem("aa","bb"));
//        detailItems.add(new StockDetailItem("aa","bb"));
        listView.setAdapter(stockDetailAdapter);

        // List View StockDetail
        requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                MY_URL + symbol,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

//                      adapter.clear();
                        Log.i("before", stockDetailAdapter.getCount() + "");

                        StockDetail tmp = new StockDetail(response);
                        tmp.createStockDetailItems(detailItems);
                        Log.i("after", stockDetailAdapter.getCount() + "");
//                        detailItems.add(new StockDetailItem("abc", "dfg"));
                        stockDetailAdapter.notifyDataSetChanged();

//                      adapter.add(row);
//                      adapter.notifyDataSetChanged();
//                      handler.sendEmptyMessage(HIDE_PROGRESS_BAR);
//                        handler.sendEmptyMessage(HIDE_PROGRESS_BAR);
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(request);

        webView = (WebView) view.findViewById(R.id.webview_current);
        webView.setVisibility(View.GONE);
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
        int page = getArguments().getInt(KEY_PAGE);

        Log.i("Created", symbol);
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
            Log.i("finished","test()");
        }
    }


    private void enableChangeClickListener(final String newIndicator) {
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("change", "clicked");
                webView.setVisibility(View.VISIBLE);
                indicator = newIndicator;
                disableChangeClickListener();
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
                                Log.i("eval-back", value);
                                ret = value;

                            }
                        });
//                        webView.loadUrl("javascript:test()");
                    }
                });

            }
        });
    }

    private void disableChangeClickListener() {
        changeBtn.setTextColor(getContext().getColor(R.color.colorGrey));
        changeBtn.setOnClickListener(null);
    }
}
