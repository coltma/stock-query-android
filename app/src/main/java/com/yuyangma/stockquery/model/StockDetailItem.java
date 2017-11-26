package com.yuyangma.stockquery.model;

import java.util.Date;

/**
 * Created by Ma on 11/25/17.
 */

public class StockDetailItem {
    private String key;
    private String value;

    public StockDetailItem(String k, String v) {
        this.key = k;
        this.value = v;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
