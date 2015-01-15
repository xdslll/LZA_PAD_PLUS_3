package com.lza.pad.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lza.pad.R;
import com.lza.pad.app.base.BaseActivity;

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
            }
        });
    }
}
