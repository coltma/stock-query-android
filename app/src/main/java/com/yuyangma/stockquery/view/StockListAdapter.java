package com.yuyangma.stockquery.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuyangma.stockquery.R;
import com.yuyangma.stockquery.model.StockListItem;

import java.util.List;

/**
 * Created by Ma on 11/21/17.
 */

public class StockListAdapter extends ViewHolderAdapter {
    private Context context;
    private List<StockListItem> data;

    public StockListAdapter(@NonNull Context context, List<StockListItem> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected ViewHolderAdapter.ViewHolder createViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorites_list_view_item, parent, false);
        return new StockListViewHolder(view);
    }

    @Override
    protected void bindViewHolder(ViewHolderAdapter.ViewHolder viewHolder, int position) {
        StockListItem item = (StockListItem) getItem(position);
        ((StockListViewHolder) viewHolder).symbolTxView.setText(item.getSymbol());
        ((StockListViewHolder) viewHolder).priceTxView.setText(item.priceToStr());
        ((StockListViewHolder) viewHolder).changeTxView.setText(item.changeToStr());
        if (item.isGain()) {
            ((StockListViewHolder) viewHolder).changeTxView.setTextColor(context.getResources().getColor(R.color.colorGreen,null));
        } else {
            ((StockListViewHolder) viewHolder).changeTxView.setTextColor(context.getResources().getColor(R.color.colorRed, null));
        }
    }
}
