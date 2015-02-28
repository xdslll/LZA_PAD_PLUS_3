package com.lza.pad.app.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lza.pad.R;
import com.lza.pad.app.base.BaseActivity;
import com.lza.pad.support.network.VolleySingleton;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/23/15.
 */
@Deprecated
public class TestActivity3 extends BaseActivity {

    EditText mEdtUrl;
    TextView mTxtRequest, mTxtData;
    Button mBtnSend;

    String mTestUrl = "http://www.abced.com";

    public static final String TAG = "request";

    public static final int TIMEOUT = 10 * 1000;
    public static final int RETRY_COUNT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_dashboard);
        mEdtUrl = (EditText) findViewById(R.id.test_dashboard_edt);
        mTxtRequest = (TextView) findViewById(R.id.test_dashboard_request);
        mTxtData = (TextView) findViewById(R.id.test_dashboard_data);
        mBtnSend = (Button) findViewById(R.id.test_dashboard_send);

        mEdtUrl.setText(mTestUrl);

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTestUrl = mEdtUrl.getText().toString();
                mTxtRequest.setText("正在请求：[" + mTestUrl + "]");
                mTxtData.setText("");
                MyStringRequest request = new MyStringRequest(
                        mTestUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                mTxtRequest.setText("请求：[" + mTestUrl + "]成功！");
                                mTxtData.setText(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                mTxtData.setText("响应失败，错误原因：" + error.getMessage());
                            }
                        }
                );
                request.setTag(TAG);
                request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, RETRY_COUNT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleySingleton.getInstance(mCtx).addToRequestQueue(request);
            }
        });
    }

    class MyStringRequest extends StringRequest {

        public MyStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        public MyStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(url, listener, errorListener);
        }

        @Override
        protected void deliverResponse(String response) {
            super.deliverResponse(response);
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            return super.parseNetworkResponse(response);
        }
    }
}
