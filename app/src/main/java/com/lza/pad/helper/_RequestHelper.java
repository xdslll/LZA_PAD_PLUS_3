package com.lza.pad.helper;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lza.pad.helper.event.model.ResponseEventInfo;
import com.lza.pad.helper.event.state.ResponseEventTag;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.network.VolleySingleton;
import com.lza.pad.support.utils.Consts;

import java.util.Map;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/8.
 */
@Deprecated
public class _RequestHelper implements Consts {

    private String mOriginalResponseData = "";

    private String mRequestUrl = "";

    private String mCookie = "";

    private int mStatusCode;

    private Context mCtx;

    private MyStringRequest mRequest;

    private OnRequestListener mListener = new SimpleRequestListener();

    private _RequestHelper(Context c) {
        mCtx = c;
        setOnRequestListener(mListener);
    }

    private _RequestHelper(Context c, OnRequestListener listener) {
        mCtx = c;
        setOnRequestListener(listener);
    }

    private _RequestHelper(Context c, OnRequestListener listener, String url) {
        this(c, listener);
        this.mRequestUrl = url;
        //this.mRequest = createRequest(mRequestUrl);
    }

    public synchronized static _RequestHelper getInstance(Context c) {
        return new _RequestHelper(c);
    }

    public synchronized static _RequestHelper getInstance(Context c, OnRequestListener listener) {
        return new _RequestHelper(c, listener);
    }

    public synchronized static _RequestHelper getInstance(Context c, String url, OnRequestListener listener) {
        return new _RequestHelper(c, listener, url);
    }

    private MyStringRequest createRequest(String url) {
        return new MyStringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String json) {
                        handleResponse(json);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleError(error);
                    }
                });
    }

    public void send() {
        log("url-->" + mRequestUrl);
        if (mRequest != null) {
            VolleySingleton.getInstance(mCtx).addToRequestQueue(mRequest);
        } else {
            if (!TextUtils.isEmpty(mRequestUrl)) {
                mRequest = createRequest(mRequestUrl);
                VolleySingleton.getInstance(mCtx).addToRequestQueue(mRequest);
            } else {
                log("url为空，不能发送请求！");
            }
        }
    }

    public void send(String url) {
        if (TextUtils.isEmpty(url)) return;
        mRequestUrl = url;
        mRequest = createRequest(url);
        send();
    }

    public void send(Context c, String url, OnRequestListener listener) {
        getInstance(c, url, listener).send();
    }

    private void handleError(VolleyError error) {
        AppLogger.e("error-->" + error.getCause() + "," + error.getMessage());
        ResponseEventInfo response = new ResponseEventInfo();
        response.setUrl(mRequestUrl);
        response.setTag(ResponseEventTag.ON_ERROR);
        response.setStatusCode(mStatusCode);
        response.setError(error);
        mListener.onResponse(response);
    }

    private void handleResponse(String json) {
        mOriginalResponseData = json;
        AppLogger.e("response-->" + mOriginalResponseData);
        ResponseEventInfo response = new ResponseEventInfo();
        response.setUrl(mRequestUrl);
        response.setTag(ResponseEventTag.ON_RESONSE);
        response.setResponseData(mOriginalResponseData);
        response.setStatusCode(mStatusCode);
        response.setCookie(mCookie);
        mListener.onResponse(response);
    }

    private String getCookie(Map<String, String> header) {
        if (header != null && header.containsKey("Set-Cookie")) {
            return header.get("Set-Cookie");
        }
        return null;
    }

    public interface OnRequestListener {
        void onResponse(ResponseEventInfo response);
    }

    public class SimpleRequestListener implements OnRequestListener {

        @Override
        public void onResponse(ResponseEventInfo response) {

        }
    }

    public void setOnRequestListener(OnRequestListener listener) {
        mListener = listener;
    }

    private class MyStringRequest extends StringRequest {

        public static final int DEFAULT_TIMEOUT = 10 * 1000;
        public static final int DEFAULT_RETRY_COUNT = 3;

        public MyStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
            setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DEFAULT_RETRY_COUNT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }

        public MyStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(url, listener, errorListener);
            setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DEFAULT_RETRY_COUNT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }

        public MyStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener, int timeout, int maxRetryCount) {
            super(url, listener, errorListener);
            setRetryPolicy(new DefaultRetryPolicy(timeout, maxRetryCount, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }

        @Override
        protected void deliverResponse(String response) {
            super.deliverResponse(response);
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            mStatusCode = response.statusCode;
            Map<String, String> headers = response.headers;
            mCookie = getCookie(headers);
            return super.parseNetworkResponse(response);
        }
    }

    private void log(String msg) {
        AppLogger.e(">>>> " + msg);
    }

}
