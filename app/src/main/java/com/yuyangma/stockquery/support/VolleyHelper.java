package com.yuyangma.stockquery.support;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

/**
 * Created by Ma on 11/23/17.
 */

public class VolleyHelper {
    private Context context;

    public VolleyHelper(Context context) {
        this.context = context;
    }


    public static JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {

        }
    })
}
