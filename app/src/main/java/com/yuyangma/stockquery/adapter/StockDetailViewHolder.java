package com.yuyangma.stockquery.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyangma.stockquery.R;

/**
 * Created by Ma on 11/23/17.
 */
public class StockDetailViewHolder extends ViewHolderAdapter.ViewHolder {
    TextView key;
    TextView value;
    ImageView image;

    public StockDetailViewHolder(@NonNull View view) {
        super(view);
        key = (TextView) view.findViewById(R.id.detail_item_key);
        value = (TextView) view.findViewById(R.id.detail_item_value);
        image = (ImageView) view.findViewById(R.id.detail_item_image);
    }
}
