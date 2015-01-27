package com.lza.pad.app;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app.base.BaseActivity;
import com.lza.pad.fragment.EbookContentFragment;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/18/15.
 */
public class EbookContentActivity extends BaseActivity {

    private TextView mTxtClose;

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

        launchFragment(new EbookContentFragment(), R.id.ebook_content_container);
    }
}
