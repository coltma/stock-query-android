package com.yuyangma.stockquery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
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
import com.yuyangma.stockquery.adapter.SortParamAdapter;
import com.yuyangma.stockquery.model.StockListItem;
import com.yuyangma.stockquery.support.FreqTerm;
import com.yuyangma.stockquery.adapter.StockListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.yuyangma.stockquery.support.FreqTerm.DESCENDING;
import static com.yuyangma.stockquery.support.FreqTerm.SYMBOL_SORT;

public class MainActivity extends AppCompatActivity {
    private static String AUTO_COMPLETE = "AUTO_COMPLETE";
    private static String NULL = "null";


    private String symbol = "";
    private int toRemovePos = -1;
    private final List<String> symbols = new ArrayList<>();
    private List<StockListItem> favorites = new ArrayList<>();
    private String sortBy = "";
    private String orderBy = "";

    private RequestQueue requestQueue;


    ListView listView;
    StockListAdapter stockListAdapter;


    private ProgressBar progressBar;
    private AutoCompleteTextView autoCompleteTextView;
    private StockSymbolAdapter stockSymbolAdapter;
    private TextView getQuoteViewBtn;
    private TextView clearViewBtn;
    private Spinner orderSpinner ;
    private Spinner sortbySpinner;


    // How could I forget it!!!
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

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ListView
        listView = (ListView) findViewById(R.id.favorites_list_view);
        registerForContextMenu(listView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                toRemovePos = i;
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String symbol = ((StockListItem)stockListAdapter.getItem(i)).getSymbol();
                startActivity(prepareIntentForDetail(symbol, true));
            }
        });

        // Volley
        requestQueue = Volley.newRequestQueue(this);


        // ProgressBar
        progressBar = (ProgressBar) findViewById(R.id.autocCompleteProgressBar);
        progressBar.setVisibility(View.GONE);

        // Spinners
        // sortBy
        sortbySpinner = (Spinner) findViewById(R.id.sortby_spinner);

        final SortParamAdapter sortBySpinnerAdapter = new SortParamAdapter(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.sortby_array));
//        ArrayAdapter<CharSequence> sortBySpinnerAdapter = ArrayAdapter.createFromResource(this,
//                        R.array.sortby_array, android.R.layout.simple_spinner_item);
        sortBySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortbySpinner.setAdapter(sortBySpinnerAdapter);

        sortbySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortBySpinnerAdapter.setSelected(i);
                String[] sorts = getResources().getStringArray(R.array.sortby_array);
                sortBy = sorts[i];
                sort(favorites, sortBy, orderBy);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // orderBy
        orderSpinner = (Spinner) findViewById(R.id.order_spinner);
        final SortParamAdapter orderBySpinnerAdapter = new SortParamAdapter(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.order_array));

//        ArrayAdapter<CharSequence> orderSpinnerAdapter = new ArrayAdapter<CharSequence>(this,
//                android.R.layout.simple_spinner_item,
//                getResources().getStringArray(R.array.order_array));
        orderBySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(orderBySpinnerAdapter);

        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                orderBySpinnerAdapter.setSelected(i);
                String[] orders = getResources().getStringArray(R.array.order_array);
                orderBy = orders[i];
                sort(favorites, sortBy, orderBy);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Geoquote
        getQuoteViewBtn = (TextView) findViewById(R.id.getquote_view);
        getQuoteViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (symbol == null || symbol.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.no_symbol_error, Toast.LENGTH_SHORT).show();;
                    return;
                }
                startActivity(prepareIntentForDetail(symbol, checkFavorite(symbol)));
            }
        });

        // Clear
        clearViewBtn = (TextView) findViewById(R.id.clear_view);
        clearViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                symbol = "";
                autoCompleteTextView.setText("");
            }
        });

        // AutoComplete
        stockSymbolAdapter = new StockSymbolAdapter(this,
                android.R.layout.select_dialog_item, symbols);

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

    @Override
    protected void onStart() {
        super.onStart();
        // ListView + Databinding
        stockListAdapter = new StockListAdapter(this, readFavoriteListData(favorites));
        listView.setAdapter(stockListAdapter);
        Log.d("favorite", "main activity onStart called.");
    }


    public class StockSymbolAdapter extends ArrayAdapter<String> {
        private ArrayList<String> values;
        public StockSymbolAdapter(@NonNull Context context, int resource, List<String> items) {
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
                String symbol = charSequence.toString().trim().toUpperCase();
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
        handler.sendEmptyMessage(FreqTerm.SHOW_PROGRESS_BAR);
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
                            handler.sendEmptyMessage(FreqTerm.HIDE_PROGRESS_BAR);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(FreqTerm.HIDE_PROGRESS_BAR);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Failed for:" + url, Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(FreqTerm.HIDE_PROGRESS_BAR);
                    }
                });
        jsonArrayRequest.setTag(AUTO_COMPLETE);
        requestQueue.add(jsonArrayRequest);
    }

    // 	Long press a row and display a Context Menu	to Delete list item.
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getString(R.string.long_click_menu_title));
        menu.add(Menu.NONE, FreqTerm.NO, menu.NONE, getString(R.string.no));
        menu.add(Menu.NONE, FreqTerm.YES, menu.NONE, getString(R.string.yes));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case FreqTerm.NO:
                Toast.makeText(getApplicationContext(),
                        getString(R.string.selected_no),
                        Toast.LENGTH_SHORT).show();;
                break;
            case FreqTerm.YES:
                String symbol = ((StockListItem)stockListAdapter.getItem(toRemovePos)).getSymbol();
                // First, Remove from SharedPreference.
                removeFromFavoriteList(symbol);
                // Second, Remove from ListView.
                stockListAdapter.remove(toRemovePos);
                stockListAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.selected_yes),
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private Intent prepareIntentForDetail(String symbol, boolean isFavorite) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(FreqTerm.SYMBOL_KEY, symbol);
        intent.putExtra(FreqTerm.IS_FAVORITE_KEY, isFavorite);
        return intent;
    }

    private boolean checkFavorite(String symbol) {
        boolean isFavorite = false;
        for (StockListItem item : favorites) {
            if (item.getSymbol().equals(symbol)) {
                isFavorite = true;
                break;
            }
        }
        return isFavorite;
    }

    private List<StockListItem> readFavoriteListData(List<StockListItem> favorites) {
        Log.d("favorite", "main activity before read:" + favorites.toString());
        favorites.clear();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        // check
        Set<String> storedFavorites = new HashSet<>();
        storedFavorites = sharedPref.getStringSet(getString(R.string.preference_symbols_key), storedFavorites);
        Log.d("favorite", "main activity read from shared:" + storedFavorites.toString());
        for (String key : storedFavorites) {
            String data = sharedPref.getString(key, NULL);
            Log.d("favorite", "main activity read shared ->:" + data);
            if (!data.equals(NULL)) {
                String[] arr = data.split(",");
                favorites.add(new StockListItem(arr[0],
                        Double.parseDouble(arr[1]),
                        Double.parseDouble(arr[2])));
            }
        }
        Log.d("favorite", "main activity after read:" + favorites.toString());

        return favorites;
    }

    private void removeFromFavoriteList(String symbol) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        // check
        Set<String> favorites = new HashSet<>();
        favorites = sharedPref.getStringSet(getString(R.string.preference_symbols_key), favorites);
        Log.d("favorite", "main activity before remove:" + favorites.toString());
        Log.d("favorite", "main activity before remove symbol:" + symbol);
        if (!favorites.contains(symbol)) {
            return ;
        }

        favorites.remove(symbol);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(getString(R.string.preference_symbols_key), favorites);
        editor.remove(symbol);
        editor.commit();
        Log.d("favorite", "main activity after remove:" + favorites.toString());
    }

    private void sort(List<StockListItem> favorites, final String sortBy, String orderBy) {
        // ascending or descending.
        Log.d("sort", "sortBy:" + sortBy + ", orderBy:" + orderBy);
        final int order = orderBy.equals(FreqTerm.ASCENDING) ? 1 : -1;
        Collections.sort(favorites, new Comparator<StockListItem>() {
            @Override
            public int compare(StockListItem left, StockListItem right) {
                int diff = 0;
                switch (sortBy) {
                    case FreqTerm.SYMBOL_SORT:
                        diff = left.getSymbol().compareTo(right.getSymbol());
                        break;
                    case FreqTerm.PRICE_SORT:
                        double l = left.getPrice();
                        double r = right.getPrice();
                        diff = l - r > 0 ? 1 : (l - r == 0 ? 0 : -1);
                        break;
                    // Use change percent.
                    case FreqTerm.CHANGE_SORT:
                        double lc = (left.getPrice() - left.getClose()) / left.getClose();
                        double rc = (right.getPrice() - right.getClose()) / right.getClose();
                        diff = lc - rc > 0 ? 1 : (lc - rc == 0 ? 0 : -1);
                        break;
                    default:
                        break;
                }
                return diff * order;
            }
        });
        stockListAdapter.notifyDataSetChanged();
    }

}
