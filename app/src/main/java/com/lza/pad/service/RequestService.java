package com.lza.pad.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lza.pad.helper.IRequest;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.network.VolleySingleton;
import com.lza.pad.support.utils.Consts;

import java.util.Map;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 11/3/14.
 */
public class RequestService extends Service implements Consts {

    @Override
    public IBinder onBind(Intent intent) {
        return new RequestBinder();
    }

    private class RequestBinder extends IRequest.Stub {

        @Override
        public void doRequest(String url) throws RemoteException {
            if (!TextUtils.isEmpty(url)) {
                final MyStringRequest request = new MyStringRequest(url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                AppLogger.e("I'm working!");
                                Intent intent = new Intent(INTENT_ACTION_RESPONSE_RECEIVER);
                                intent.putExtra(KEY_RESPONSE_CODE, INTENT_ACTION_RESPONSE_OK);
                                intent.putExtra(KEY_COMMON_RESPONSE, s);
                                intent.putExtra(KEY_COOKIE, mCookie);
                                sendBroadcast(intent);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String msg = error.getMessage();
                                Intent intent = new Intent(INTENT_ACTION_RESPONSE_RECEIVER);
                                intent.putExtra(KEY_RESPONSE_CODE, INTENT_ACTION_RESPONSE_ERROR);
                                intent.putExtra(KEY_COMMON_RESPONSE, msg);
                                sendBroadcast(intent);
                            }
                        });
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
            }
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
            AppLogger.e(response);
            super.deliverResponse(response);
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            Map<String, String> headers = response.headers;
            AppLogger.e(headers.toString());
            mCookie = getCookie(headers);
            AppLogger.e("Cookie-->" + mCookie);
            return super.parseNetworkResponse(response);
        }
    }

}
