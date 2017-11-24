package com.yuyangma.stockquery.model;

import java.util.Locale;



/**
 * Created by Ma on 11/21/17.
 */

public class StockListItem {
    private String symbol;
    private double price;
    private double close;

    public StockListItem(String s, double p, double c) {
        this.symbol = s;
        this.price = p;
        this.close = c;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public boolean isGain() {
        return price - close >= 0;
    }

    public String priceToStr() {
        String tmp = String.format(Locale.US, "%.2f", this.price);
        return tmp;
    }

    public String changeToStr() {
        double change = this.price - this.close;
        double changePercent = change / this.close;
        String tmp = String.format(Locale.US, "%.2f(%.2f%%)", change, changePercent * 100);

        return tmp;
    }
}
