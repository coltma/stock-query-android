package com.yuyangma.stockquery.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyangma.stockquery.R;

/**
 * Created by Ma on 11/27/17.
 */

public class StockNewsViewHolder extends ViewHolderAdapter.ViewHolder {
    TextView title;
    TextView author;
    TextView date;

    public StockNewsViewHolder(@NonNull View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.news_title);
        author = (TextView) view.findViewById(R.id.news_author);
        date = (TextView) view.findViewById(R.id.news_date);
    }
}
