package com.lza.pad.helper;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.lza.pad.helper.event.model.ResponseEventInfo;
import com.lza.pad.helper.event.state.ResponseEventTag;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.network.VolleySingleton;
import com.lza.pad.support.utils.Consts;

import org.apache.http.Header;

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

    private OnRequestListener mListener = new SimpleRequestListener();

    private RequestHelper(Context c) {
        mCtx = c;
        setOnRequestListener(mListener);
        mHttpClient.setMaxRetriesAndTimeout(AsyncHttpClient.DEFAULT_MAX_RETRIES,
                AsyncHttpClient.DEFAULT_SOCKET_TIMEOUT);
    }

    private RequestHelper(Context c, OnRequestListener listener) {
        mCtx = c;
        setOnRequestListener(listener);
        mHttpClient.setMaxRetriesAndTimeout(AsyncHttpClient.DEFAULT_MAX_RETRIES,
                AsyncHttpClient.DEFAULT_SOCKET_TIMEOUT);
    }

    private RequestHelper(Context c, OnRequestListener listener, String url) {
        this(c, listener);
        this.mRequestUrl = url;
    }

    public synchronized static RequestHelper getInstance(Context c) {
        return new RequestHelper(c);
    }

    public synchronized static RequestHelper getInstance(Context c, OnRequestListener listener) {
        return new RequestHelper(c, listener);
    }

    public synchronized static RequestHelper getInstance(Context c, String url, OnRequestListener listener) {
        return new RequestHelper(c, listener, url);
    }

    public void send() {
        log("url-->" + mRequestUrl);
        if (!TextUtils.isEmpty(mRequestUrl)) {
            mHttpClient.get(mCtx, mRequestUrl, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String responseString = new String(responseBody);
                    handleResponse(responseString);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    handleError(error);
                }

                @Override
                public void onRetry(int retryNo) {
                    super.onRetry(retryNo);
                }
            });
        }
    }

    public void send(String url) {
        if (TextUtils.isEmpty(url)) return;
        mRequestUrl = url;
        send();
    }

    public void send(Context c, String url, OnRequestListener listener) {
        getInstance(c, url, listener).send();
    }

    public void sendByVolley() {
        log("url-->" + mRequestUrl);
        if (!TextUtils.isEmpty(mRequestUrl)) {
            StringRequest request = new StringRequest(mRequestUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            handleResponse(s);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                           handleError(volleyError);
                        }
                    });
            VolleySingleton.getInstance(mCtx).addToRequestQueue(request);
        }
    }

    public void sendByVolley(String url) {
        if (TextUtils.isEmpty(url)) return;
        mRequestUrl = url;
        sendByVolley();
    }

    public void sendByVolley(Context c, String url, OnRequestListener listener) {
        getInstance(c, url, listener).sendByVolley();
    }

    private void handleError(Throwable error) {
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

    public interface OnRequestListener {
        void onResponse(ResponseEventInfo response);
    }

    public void setOnRequestListener(OnRequestListener listener) {
        mListener = listener;
    }

    private void log(String msg) {
        AppLogger.e(">>>> " + msg);
    }

    private AsyncHttpClient mHttpClient = new AsyncHttpClient();

}
