package com.lza.pad.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

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

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/8.
 */
@Deprecated
public class __RequestHelper implements Consts {

    private String mOriginalResponseData;

    private String mRequestUrl;

    private Context mCtx;

    private ResponseReceiver mResponseReceiver = null;

    private __RequestHelper(Context c) {
        this(c, "");
    }

    private __RequestHelper(Context c, String requestUrl) {
        this.mCtx = c;
        this.mRequestUrl = requestUrl;

        mResponseReceiver = new ResponseReceiver();
        c.registerReceiver(mResponseReceiver, new IntentFilter(INTENT_ACTION_RESPONSE_RECEIVER));
    }

    private static __RequestHelper sInstance = null;

    public static __RequestHelper getInstance(Context c) {
        if (sInstance == null) {
            sInstance = new __RequestHelper(c);
        }
        return sInstance;
    }

    public static __RequestHelper getInstance(Context c, String requestUrl) {
        if (sInstance == null) {
            sInstance = new __RequestHelper(c, requestUrl);
        }
        return sInstance;
    }

    public static void sendRequest(Context c, String requestUrl) {
        AppLogger.e("请求地址：" + requestUrl);
        if (sInstance == null) {
            getInstance(c, requestUrl);
        } else {
            sInstance.mRequestUrl = requestUrl;
            sInstance.send(requestUrl);
        }
    }

    public static void releaseService() {
        if (sInstance != null) {
            sInstance.release();
        }
    }

    private void send(final String url) {
        AppLogger.e("Url --> " + url);
        if (TextUtils.isEmpty(url)) return;
        final MyStringRequest request = new MyStringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Intent intent = new Intent(INTENT_ACTION_RESPONSE_RECEIVER);
                        intent.putExtra(KEY_RESPONSE_CODE, INTENT_ACTION_RESPONSE_OK);
                        intent.putExtra(KEY_COMMON_RESPONSE, s);
                        intent.putExtra(KEY_COOKIE, mCookie);
                        intent.putExtra(KEY_URL, url);
                        mCtx.sendBroadcast(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg = error.getMessage();
                        Intent intent = new Intent(INTENT_ACTION_RESPONSE_RECEIVER);
                        intent.putExtra(KEY_RESPONSE_CODE, INTENT_ACTION_RESPONSE_ERROR);
                        intent.putExtra(KEY_COMMON_RESPONSE, msg);
                        intent.putExtra(KEY_URL, url);
                        mCtx.sendBroadcast(intent);
                    }
                });
        VolleySingleton.getInstance(mCtx).addToRequestQueue(request);
    }

    public void release() {
        try {
            if (mResponseReceiver != null) {
                mCtx.unregisterReceiver(mResponseReceiver);
            }
            sInstance = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String mCookie = "";

    private String getCookie(Map<String, String> header) {
        if (header != null && header.containsKey("Set-Cookie")) {
            return header.get("Set-Cookie");
        }
        return null;
    }

    private class MyStringRequest extends StringRequest {

        public MyStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        public MyStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(url, listener, errorListener);
        }

        @Override
        protected void deliverResponse(String response) {
            //AppLogger.e(response);
            super.deliverResponse(response);
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            Map<String, String> headers = response.headers;
            mCookie = getCookie(headers);
            //AppLogger.e(headers.toString());
            //AppLogger.e("Cookie-->" + mCookie);
            return super.parseNetworkResponse(response);
        }
    }

    private class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ResponseEventInfo responseInfo = new ResponseEventInfo();
            if (intent != null) {
                responseInfo.setUrl(intent.getStringExtra(KEY_URL));
                String responseCode = intent.getStringExtra(KEY_RESPONSE_CODE);
                if (!TextUtils.isEmpty(responseCode)) {
                    mOriginalResponseData = intent.getStringExtra(KEY_COMMON_RESPONSE);
                    AppLogger.e("RequestHelper --> " + mOriginalResponseData);
                    if (responseCode.equals(INTENT_ACTION_RESPONSE_OK)) {
                        responseInfo.setTag(ResponseEventTag.ON_RESONSE);
                        responseInfo.setResponseData(mOriginalResponseData);
                    } else {
                        responseInfo.setTag(ResponseEventTag.ON_ERROR);
                        responseInfo.setErrorMessage(mOriginalResponseData);
                    }
                } else {
                    responseInfo.setTag(ResponseEventTag.NO_RESPONSE);
                }
            } else {
                responseInfo.setTag(ResponseEventTag.NO_RESPONSE);
            }
            EventBus.getDefault().post(responseInfo);
        }
    }

}
