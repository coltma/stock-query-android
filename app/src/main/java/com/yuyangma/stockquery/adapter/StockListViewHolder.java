package com.yuyangma.stockquery.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.yuyangma.stockquery.R;

/**
 * Created by Ma on 11/23/17.
 */
public class StockListViewHolder extends ViewHolderAdapter.ViewHolder {
    TextView symbolTxView;
    TextView priceTxView;
    TextView changeTxView;

    public StockListViewHolder(@NonNull View view) {
        super(view);
        symbolTxView = (TextView) view.findViewById(R.id.list_item_symbol);
        priceTxView = (TextView) view.findViewById(R.id.list_item_price);
        changeTxView = (TextView) view.findViewById(R.id.list_item_change);
    }
}
