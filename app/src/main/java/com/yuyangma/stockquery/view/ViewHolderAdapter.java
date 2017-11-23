package com.yuyangma.stockquery.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Ma on 11/21/17.
 */

public abstract class ViewHolderAdapter extends BaseAdapter {
    public static abstract class ViewHolder {
        protected View view;
        public ViewHolder(View view) {
            this.view = view;
        }
    }

    protected abstract ViewHolder generateViewHolder(ViewGroup parent, int position);
    protected abstract void bindViewHolder(ViewHolder viewHolder, int position);
}
