package com.yuyangma.stockquery.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuyangma.stockquery.R;
import com.yuyangma.stockquery.model.StockDetailItem;
import com.yuyangma.stockquery.model.StockListItem;

import java.util.List;

/**
 * Created by Ma on 11/21/17.
 */

public class StockDetailAdapter extends ViewHolderAdapter {
    private static final int CHANGE_POS = 2;
    private Context context;
    private List<StockDetailItem> data;

    public StockDetailAdapter(@NonNull Context context, List<StockDetailItem> data) {
        this.context = context;
        this.data = data;
    }



    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        Log.i("detailAdapterData", data.get(position).getKey());
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected ViewHolder createViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.detail_item, parent, false);
        return new StockDetailViewHolder(view);
    }

    @Override
    protected void bindViewHolder(ViewHolder viewHolder, int position) {
        StockDetailItem item = (StockDetailItem) getItem(position);
        ((StockDetailViewHolder) viewHolder).key.setText(item.getKey());
        ((StockDetailViewHolder) viewHolder).value.setText(item.getValue());
        if (position == CHANGE_POS) {
            if (item.getValue().contains("-")) {
                ((StockDetailViewHolder) viewHolder).image.setImageResource(R.mipmap.ic_price_down);
//                ((StockDetailViewHolder) viewHolder).value.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_price_down,0);
            } else {
                ((StockDetailViewHolder) viewHolder).image.setImageResource(R.mipmap.ic_price_up);
//                ((StockDetailViewHolder) viewHolder).value.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_price_up,0);
            }
            ((StockDetailViewHolder) viewHolder).image.setVisibility(View.VISIBLE);
        }
    }
}
