package com.yuyangma.stockquery.support;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Ma on 11/27/17.
 */


public class XMLRequest extends Request<XmlPullParser> {

    private final Response.Listener<XmlPullParser> listener;

    public XMLRequest(int method, String url, Response.ErrorListener errorListener, Response.Listener<XmlPullParser> listener) {
        super(method, url, errorListener);
        this.listener = listener;
    }

    @Override
    protected Response<XmlPullParser> parseNetworkResponse(NetworkResponse response) {
        try {
            String xmlStr = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlStr));
            return Response.success(xmlPullParser, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (XmlPullParserException e) {
            return Response.error(new ParseError((e)));
        }
    }

    @Override
    protected void deliverResponse(XmlPullParser response) {
        listener.onResponse(response);
    }
}
