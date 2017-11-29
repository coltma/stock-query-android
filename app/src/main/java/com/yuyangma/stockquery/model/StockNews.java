package com.yuyangma.stockquery.model;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Ma on 11/27/17.
 */

public class StockNews {


    private String title = "";
    private String author = "";
    private String date = "";
    private String link = "";

    public StockNews(String link, String title, String date, String author) {
        this.title = title.replaceAll("\"", "").
                replaceAll("\\[", "")
                .replaceAll("\\]","");
        this.author = author.replaceAll("\"", "").
                replaceAll("\\[", "")
                .replaceAll("\\]","");
        String timestamp = date.replaceAll("\"", "").
                replaceAll("\\[", "")
                .replaceAll("\\]","");
        // Display PST according date. Tue, 21 Nov 2017 15:37:32 -0500;
        SimpleDateFormat ft = new SimpleDateFormat ("EEE, dd MMM yyyy HH:mm:ss Z");
        //Tue, 21 Nov 2017 15:37:32 PST;
        SimpleDateFormat ftz = new SimpleDateFormat ("EEE, dd MMM yyyy HH:mm:ss z");
        ft.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        ftz.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        try {
            this.date = ftz.format(ft.parse(timestamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.link = link.replaceAll("\"", "").
                replaceAll("\\[", "")
                .replaceAll("\\]","")
                .replaceAll("\\\\","");
        Log.d("news", this.link);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
