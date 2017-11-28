package com.yuyangma.stockquery;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.yuyangma.stockquery.adapter.StockNewsAdapter;
import com.yuyangma.stockquery.model.StockNews;
import com.yuyangma.stockquery.support.FreqTerm;
import com.yuyangma.stockquery.support.XMLRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment {
    private static final String RSS = "rss";
    private static final String CHANNEL = "channel";
    private static final String ITEM = "item";
    private static final String LINK = "link";
    private static final String TITLE = "title";
    private static final String PUB_DATE = "pubDate";
    private static final String AUTHOR =  "sa:author_name";
    private static final String ARTICLE = "article";

    private int page;
    private String symbol;

    private ListView listView;
    private ProgressBar progressBar;
    private StockNewsAdapter stockNewsAdapter;

    private RequestQueue requestQueue;


    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param page   Parameter 1.
     * @param symbol Parameter 2.
     * @return A new instance of fragment NewsFragment.
     */
    public static NewsFragment newInstance(int page, String symbol) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putInt(FreqTerm.PAGE_KEY, page);
        args.putString(FreqTerm.SYMBOL_KEY, symbol);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        listView = (ListView) view.findViewById(R.id.news_list_view);
        final List<StockNews> newsList = new ArrayList<StockNews>();
        stockNewsAdapter = new StockNewsAdapter(getContext(), newsList);


        requestQueue = Volley.newRequestQueue(getContext());
//        "http://cs571.us-east-1.elasticbeanstalk.com/getxml?symbol=" + "AAPL",

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "http://cs571.us-east-1.elasticbeanstalk.com/getxml?symbol=" + "AAPL",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        readData(response, newsList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                }

        );
        requestQueue.add(request);
        return view;
    }



    private void readData(JSONObject data, List<StockNews> arr) {
        int size = 5;
        int i = 0;
        try {
            JSONArray channels = data.getJSONObject(RSS).getJSONArray(CHANNEL);
            JSONArray items = channels.getJSONObject(0).getJSONArray(ITEM);
            int total = items.length();
            while (arr.size() < size && i < total) {
                JSONObject item = items.getJSONObject(i);
                if (item.toString().indexOf(ARTICLE) == -1) {
                    i++;
                    continue;
                }
// moment.tz(items[i].pubDate.toString(), 'America/New_York').format('ddd, D MMM YYYY HH:mm:ss z')
                arr.add(
                        new StockNews(item.getString(LINK),
                                item.getString(TITLE),
                                item.getString(PUB_DATE),
                                item.getString(AUTHOR))
                );
                Log.d("News",item.getString(LINK)
                                + "," + item.getString(TITLE)
                                + "," + item.getString(PUB_DATE)
                                + "," + item.getString(AUTHOR) );
                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // console.log('indexOf' + (items[.indexOf('article') === -1));
    }


}
