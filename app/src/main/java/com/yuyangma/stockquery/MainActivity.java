package com.yuyangma.stockquery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.yuyangma.stockquery.model.StockListItem;
import com.yuyangma.stockquery.view.StockListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static String AUTO_COMPLETE = "AUTO_COMPLETE";
    private static final int HIDE_PROGRESS_BAR = 0;
    private static final int SHOW_PROGRESS_BAR = 1;

    private String symbol = "";

    private StockSymbolAdapter stockSymbolAdapter;

    private RequestQueue requestQueue;

    private ProgressBar progressBar;
    private AutoCompleteTextView autoCompleteTextView;

    // How could I forget it!!!
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HIDE_PROGRESS_BAR:
                    progressBar.setVisibility(View.GONE);
                    break;
                case SHOW_PROGRESS_BAR:
                    // 隐藏ProcessBar。
                    progressBar.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Volley
        requestQueue = Volley.newRequestQueue(this);


        // ProgressBar
        progressBar = (ProgressBar) findViewById(R.id.autocCompleteProgressBar);
        progressBar.setVisibility(View.GONE);



        // Spinners
        Spinner sortbySpinner = (Spinner) findViewById(R.id.sortby_spinner);
        ArrayAdapter<CharSequence> sortBySpinnerAdapter = ArrayAdapter.createFromResource(this,
                        R.array.sortby_array, android.R.layout.simple_spinner_item);
        sortBySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortbySpinner.setAdapter(sortBySpinnerAdapter);

        Spinner orderSpinner = (Spinner) findViewById(R.id.order_spinner);
        ArrayAdapter<CharSequence> orderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.order_array, android.R.layout.simple_spinner_item);
        orderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(orderSpinnerAdapter);


        // Geoquote
        final TextView getQuoteView = (TextView) findViewById(R.id.getquote_view);
        getQuoteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (symbol == null || symbol.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.no_symbol_error, Toast.LENGTH_SHORT).show();;
                    return;
                }
                boolean isFavorited = false;
                for (StockListItem item : favorites) {
                    if (item.getSymbol().equals(symbol)) {
                        isFavorited = true;
                        break;
                    }
                }

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("symbol", symbol);
                intent.putExtra("isFavorited", isFavorited);
                startActivity(intent);
            }
        });

        // ListView + Databinding
        ListView listView = (ListView) findViewById(R.id.favorites_list_view);
        listView.setAdapter(new StockListAdapter(this, mockData()));


        // AutoComplete
        stockSymbolAdapter = new StockSymbolAdapter(this,
                android.R.layout.select_dialog_item, symbols, progressBar);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(stockSymbolAdapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // "AAPL - Apple inc. (NYS..)"
                symbol = stockSymbolAdapter.getItem(i).split("-")[0].trim();
                Log.i("symbol", symbol);
            }
        });





    }

    private final List<String> symbols = new ArrayList<>();
    private List<StockListItem> favorites = new ArrayList<>();

    private List<StockListItem> mockData() {
        favorites.add(new StockListItem("AAPL", 239.23423, 210.3432));
        favorites.add(new StockListItem("TSLA", 23.129, 20.987632));
        favorites.add(new StockListItem("AMZN", 139.423, 100.3432));
        return favorites;
    }

    public class StockSymbolAdapter extends ArrayAdapter<String> {
        private ArrayList<String> values;
        public StockSymbolAdapter(@NonNull Context context, int resource, List<String> items, ProgressBar progressBar) {
            super(context, resource, items);
            // Volley
            requestQueue = Volley.newRequestQueue(this.getContext());
            values = new ArrayList<>();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return symbolFilter;
        }

        Filter symbolFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
//                progressBar.setVisibility(View.VISIBLE);
                final FilterResults filterResults = new FilterResults();
                if (charSequence == null || charSequence.length() == 0) {
                    return filterResults;
                }
                String symbol = charSequence.toString().trim();
                Log.i("symbolFilter", symbol);
                getAutoCompleteData(symbol, stockSymbolAdapter, progressBar);
                filterResults.values = stockSymbolAdapter.values;
                filterResults.count = stockSymbolAdapter.values.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                List<String> filterList = (ArrayList<String>) filterResults.values;
                if (filterResults != null && filterResults.count > 0) {
                    clear();
                    for (String item : filterList) {
                        add(item);
                    }
                    notifyDataSetChanged();
                }
            }
        };
    }


    public void getAutoCompleteData(final String symbol, final MainActivity.StockSymbolAdapter adapter, final ProgressBar progressBar) {
        final String url = "http://cs571.us-east-1.elasticbeanstalk.com/autocomplete?symbol=" + symbol;
        // Intercept.
        requestQueue.cancelAll(AUTO_COMPLETE);
        handler.sendEmptyMessage(SHOW_PROGRESS_BAR);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.i("autoCompleteData", response.toString());
                        try {
                            adapter.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = response.getJSONObject(i);
                                String row = String.format(Locale.US, "%s - %s (%s)",
                                        item.getString("Symbol"),
                                        item.getString("Name"),
                                        item.getString("Exchange"));
                                adapter.add(row);
                            }
                            adapter.notifyDataSetChanged();
                            handler.sendEmptyMessage(HIDE_PROGRESS_BAR);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(HIDE_PROGRESS_BAR);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Failed for:" + url, Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(HIDE_PROGRESS_BAR);
                    }
                });
        jsonArrayRequest.setTag(AUTO_COMPLETE);
        requestQueue.add(jsonArrayRequest);
    }

}
