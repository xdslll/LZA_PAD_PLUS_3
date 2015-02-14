package com.lza.pad.app.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.support.utils.RuntimeUtility;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/21/15.
 */
public abstract class BaseContentActivity extends BaseActivity {

    private TextView mTxtClose, mTxtTitle;
    protected PadResource mPadResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_content_container);

        if (getIntent() != null) {
            mPadResource = getIntent().getParcelableExtra(KEY_PAD_RESOURCE_INFO);
        }

        mTxtClose = (TextView) findViewById(R.id.ebook_content_close);
        mTxtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTxtTitle = (TextView) findViewById(R.id.ebook_content_title);
        mTxtTitle.setText(getModName());

        Fragment frg = getFragment();
        frg.setArguments(getArgument());
        launchFragment(frg, R.id.ebook_content_container);
    }

    protected abstract String getModName();

    protected abstract Fragment getFragment();

    protected Bundle getArgument() {
        Bundle arg = new Bundle();
        arg.putInt(KEY_FRAGMENT_WIDTH, RuntimeUtility.getScreenWidth(this));
        arg.putInt(KEY_FRAGMENT_HEIGHT, RuntimeUtility.getScreenHeight(this));
        arg.putParcelable(KEY_PAD_RESOURCE_INFO, mPadResource);
        return arg;
    }
}
