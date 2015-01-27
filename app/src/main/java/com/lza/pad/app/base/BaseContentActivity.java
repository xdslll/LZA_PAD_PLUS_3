package com.lza.pad.app.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lza.pad.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/21/15.
 */
public abstract class BaseContentActivity extends BaseActivity {

    private TextView mTxtClose, mTxtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_content_container);

        mTxtClose = (TextView) findViewById(R.id.ebook_content_close);
        mTxtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTxtTitle = (TextView) findViewById(R.id.ebook_content_title);
        mTxtTitle.setText(getModName());

        launchFragment(getFragment(), R.id.ebook_content_container);
    }

    protected abstract String getModName();

    protected abstract Fragment getFragment();
}
