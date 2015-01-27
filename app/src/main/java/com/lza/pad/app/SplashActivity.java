package com.lza.pad.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lza.pad.R;
import com.lza.pad.app.base.BaseActivity;
import com.lza.pad.helper.UrlHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/4/15.
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);
        findViewById(R.id.text1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        });

        Map<String, String> par = new HashMap<String, String>();
        par.put("control", "get_pad_model");
        par.put("bh", "nju01");
        String url = UrlHelper.generateUrl(par);

        //RequestHelper.sendRequest();
    }
}
