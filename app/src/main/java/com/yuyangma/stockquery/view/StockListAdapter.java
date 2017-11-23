package com.yuyangma.stockquery.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuyangma.stockquery.R;
import com.yuyangma.stockquery.model.StockListItem;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Ma on 11/21/17.
 */

public class StockListAdapter extends ViewHolderAdapter {
    private static class StockListViewHolder extends ViewHolderAdapter.ViewHolder {
        TextView symbolView;
        TextView priceView;
        TextView changeView;

        public StockListViewHolder(View view) {
            super(view);
            symbolView = (TextView) view.findViewById(R.id.list_item_symbol);
            priceView = (TextView) view.findViewById(R.id.list_item_price);
            changeView = (TextView) view.findViewById(R.id.list_item_change);
        }
    }

    private List<StockListItem> items;
    private Context context;

    public StockListAdapter(Context context, List<StockListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    protected ViewHolder generateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.favorites_list_view_item, parent, false);
        return new view;
    }

    @Override
    protected void bindViewHolder(ViewHolder viewHolder, int position) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        View newView;
        if (view == null) {
            viewHolder = generateViewHolder(viewGroup, i);
            newView = viewHolder.view;
            // save ViewHolder in View.
            viewHolder.view.setTag(viewHolder);
        } else {
            newView = view;
            viewHolder = (ViewHolder) view.getTag();
        }
        bindViewHolder(viewHolder, i);
        return newView;
    }
}
