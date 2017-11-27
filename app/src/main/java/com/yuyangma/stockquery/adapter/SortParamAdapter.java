package com.yuyangma.stockquery.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yuyangma.stockquery.R;

/**
 * Created by Ma on 11/26/17.
 */

public class SortParamAdapter extends ArrayAdapter<CharSequence> {
    private Context context;
    private int selected = -1;

    public SortParamAdapter(@NonNull Context context, int resource, @NonNull CharSequence[] objects) {
        super(context, resource, objects);
        this.context = context;
    }

    public void setSelected(int position) {
        selected = position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public boolean isEnabled(int position) {
        // Disable first item and selected item.
        return position != 0 && position != selected;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = (TextView) view;
        if (position == 0 || position == selected) {
            textView.setTextColor(context.getResources().getColor(R.color.colorGrey, null));
        } else {
            textView.setTextColor(context.getResources().getColor(R.color.colorBlack, null));
        }
        return view;
    }
}
