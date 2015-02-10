package com.lza.pad.helper;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lza.pad.event.model.ResponseEventInfo;
import com.lza.pad.event.state.ResponseEventTag;
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
public class RequestHelper implements Consts {

    private String mOriginalResponseData = "";

    private String mRequestUrl = "";

    private String mCookie = "";

    private int mStatusCode;

    private Context mCtx;

    private MyStringRequest mRequest;

    private OnRequestListener mListener = new SimpleRequestListener();

    private RequestHelper(Context c) {
        mCtx = c;
        setOnRequestListener(mListener);
    }

    private RequestHelper(Context c, OnRequestListener listener) {
        mCtx = c;
        setOnRequestListener(listener);
    }

    private RequestHelper(Context c, OnRequestListener listener, String url) {
        this(c, listener);
        this.mRequestUrl = url;
        this.mRequest = createRequest(mRequestUrl);
    }

    public synchronized static RequestHelper getInstance(Context c) {
        return new RequestHelper(c);
    }

    public synchronized static RequestHelper getInstance(Context c, OnRequestListener listener) {
        return new RequestHelper(c, listener);
    }

    public synchronized static RequestHelper getInstance(Context c, OnRequestListener listener, String url) {
        return new RequestHelper(c, listener, url);
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

        int timeout = 2500;

        public MyStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        public MyStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(url, listener, errorListener);
        }

        public MyStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener, int timeout) {
            this(url, listener, errorListener);
            this.timeout = timeout;
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

        @Override
        public RetryPolicy getRetryPolicy() {
            log("正在获取重试规则...");
            return new DefaultRetryPolicy(timeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
    }

    private void log(String msg) {
        AppLogger.e(">>>> " + msg);
    }

}
