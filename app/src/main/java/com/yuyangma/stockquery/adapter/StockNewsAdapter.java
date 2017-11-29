package com.yuyangma.stockquery.adapter;

/**
 * Created by Ma on 11/27/17.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuyangma.stockquery.R;
import com.yuyangma.stockquery.model.StockListItem;

import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuyangma.stockquery.R;
import com.yuyangma.stockquery.model.StockListItem;
import com.yuyangma.stockquery.model.StockNews;

import java.util.List;

/**
 * Created by Ma on 11/21/17.
 */

public class StockNewsAdapter extends ViewHolderAdapter {
    private Context context;
    private List<StockNews> data;

    public StockNewsAdapter(@NonNull Context context, List<StockNews> data) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        return new StockNewsViewHolder(view);
    }

    @Override
    protected void bindViewHolder(ViewHolderAdapter.ViewHolder viewHolder, int position) {
        StockNews item = (StockNews) getItem(position);
        ((StockNewsViewHolder) viewHolder).title.setText(item.getTitle());
        ((StockNewsViewHolder) viewHolder).author.setText(item.getAuthor());
        ((StockNewsViewHolder) viewHolder).date.setText(item.getDate());
    }
}

