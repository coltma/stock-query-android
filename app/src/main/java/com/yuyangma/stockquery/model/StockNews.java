package com.yuyangma.stockquery.model;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ma on 11/27/17.
 */

public class StockNews {


    private String title;
    private String author;
    private String date;
    private String link;

    public StockNews(String link, String title, String date, String author) {
        this.title = title.replaceAll("\"", "").
                replaceAll("\\[", "")
                .replaceAll("\\]","");
        this.author = author.replaceAll("\"", "").
                replaceAll("\\[", "")
                .replaceAll("\\]","");
        this.date = date.replaceAll("\"", "").
                replaceAll("\\[", "")
                .replaceAll("\\]","");
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
