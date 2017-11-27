package com.yuyangma.stockquery.view;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Ma on 11/21/17.
 */

public abstract class ViewHolderAdapter extends BaseAdapter {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = createViewHolder(parent, position);
            convertView = vh.view;
            vh.view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        bindViewHolder(vh, position);
        return convertView;
    }

    protected abstract ViewHolder createViewHolder(ViewGroup parent, int position);

    protected abstract void bindViewHolder(ViewHolder viewHolder, int position);

    public static abstract class ViewHolder {
        protected View view;
        public ViewHolder(@NonNull View view) {
            this.view = view;
        }
    }
}