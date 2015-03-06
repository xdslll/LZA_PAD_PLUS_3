package com.lza.pad.app;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app.base._BaseActivity;
import com.lza.pad.fragment.SubjectFragment;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/21/15.
 */
@Deprecated
public class SubjectActivity extends _BaseActivity {

    private TextView mTxtClose, mTxtTitle;
    private int mCurrentSubject = 0;
    private String[] mData;

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
        mTxtTitle.setText("选择学科");

        mData = getIntent().getStringArrayExtra(KEY_SUBJECT_DATA);
        mCurrentSubject = getIntent().getIntExtra(KEY_CURRENT_SUBJECT, 0);

        SubjectFragment fragment = new SubjectFragment();
        Bundle arg = new Bundle();
        arg.putStringArray(KEY_SUBJECT_DATA, mData);
        arg.putInt(KEY_CURRENT_SUBJECT, mCurrentSubject);
        fragment.setArguments(arg);
        launchFragment(fragment, R.id.ebook_content_container);
    }
}
