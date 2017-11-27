package com.yuyangma.stockquery.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Ma on 11/25/17.
 */

public class StockDetail {
    // for JSON.
    private static final String META_DATA = "Meta Data";
    private static final String SYMBOL = "2. Symbol";
    // for price.
    private static final String TIME_SERIES_DAILY = "Time Series (Daily)";
    private static final String CLOSE = "4. close";
    private static final String OPEN = "1. open";
    private static final String LOW = "3. low";
    private static final String HIGH = "2. high";
    private static final String VOLUME = "5. volume";
    private static final String LAST_REFRESHED = "3. Last Refreshed";
    private static final int TIMESTAMP_SHORT_LENGTH = 19;

    // for Android display;
    private static final String SYMBOL_KEY = "Stock Symbol";
    private static final String LAST_PRICE_KEY = "Last Price";
    private static final String CHANGE_KEY = "Change";
    private static final String TIMESTAMP_KEY = "Timestamp";
    private static final String OPEN_KEY = "Open";
    private static final String CLOSE_KEY = "Close";
    private static final String DAY_RANGE_KEY = "Day's Range";
    private static final String VOLUME_KEY = "Volume";

    private String symbol = "";
    private double lastPrice = 0.0;
    private double close = 0.0;
    private String timestamp = "";
    private double open = 0.0;
    private double low = 0.0;
    private double high = 0.0;
    private double change = 0.0;
    private double changePercent = 0.0;
    private int volume = 0;

    public StockDetail() {

    }

    public boolean loadJSON(JSONObject data) {
        try {
            JSONObject metaData = data.getJSONObject(META_DATA);
            this.symbol = metaData.getString(SYMBOL).toUpperCase();
            // During	 trading	 hours,	it	 should	 be
            // the	current	date	and	;me.	Aker	trading	hours,	it	should	be	16:00:00	with	the
            // appropriate	 date (no time, only date).
            // "2017-11-06 14:11:00" : 19.
            String timestamp = metaData.getString(LAST_REFRESHED);
            boolean closed = timestamp.length() != TIMESTAMP_SHORT_LENGTH;
            // Eastern daylight time or est.
            timestamp = timestamp + (closed ? " 16:00:00" : "");
            // Display EDT or EST according date.
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat ftz = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss z");
            ft.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            ftz.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            this.timestamp = ftz.format(ft.parse(timestamp));
            int i = 0;
            JSONObject seriesData = data.getJSONObject(TIME_SERIES_DAILY);
            Iterator<String> iterator = seriesData.keys();
            while (iterator.hasNext() && i < 2) {
                JSONObject oneDay = seriesData.getJSONObject(iterator.next());
                if (i == 0) {
                    this.open = oneDay.getDouble(OPEN);
                    this.low = oneDay.getDouble(LOW);
                    this.high = oneDay.getDouble(HIGH);
                    this.lastPrice = oneDay.getDouble(CLOSE);
                    this.volume = oneDay.getInt(VOLUME);
                    this.change = this.lastPrice;
                }
                if (i == 1) {
                    this.close = oneDay.getDouble(CLOSE);
                    // today_close - lastDay_lose = change;
                    this.change -= oneDay.getDouble(CLOSE);
                    this.changePercent = this.change * 100 / oneDay.getDouble(CLOSE);
                }
                i++;
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void createStockDetailItems(List<StockDetailItem> ans) {
        ans.add(new StockDetailItem(SYMBOL_KEY, this.getSymbol()));
        ans.add(new StockDetailItem(LAST_PRICE_KEY, this.getLastPriceStr()));
        ans.add(new StockDetailItem(CHANGE_KEY, this.getChangeStr()));
        ans.add(new StockDetailItem(TIMESTAMP_KEY, this.getTimestamp()));
        ans.add(new StockDetailItem(OPEN_KEY, this.getOpenStr()));
        ans.add(new StockDetailItem(CLOSE_KEY, this.getCloseStr()));
        ans.add(new StockDetailItem(DAY_RANGE_KEY, this.getDayRangeStr()));
        ans.add(new StockDetailItem(VOLUME_KEY, this.getVolumeStr()));
    }

    private String getLastPriceStr() {
        return String.format("%.2f", this.lastPrice);
    }

    private String getChangeStr() {
        return String.format("%.2f (%.2f%%)", this.change, this.changePercent);
    }

    private String getOpenStr() {
        return String.format("%.2f", this.open);
    }

    private String getCloseStr() {
        return String.format("%.2f", this.close);
    }

    private String getDayRangeStr() {
        return String.format("%.2f - %.2f", this.low, this.high);
    }

    private String getVolumeStr() {
        return String.valueOf(this.volume);
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
