package com.lza.pad.app;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import com.lza.pad.R;
import com.lza.pad.app.base.BaseActivity;
import com.lza.pad.widget.TestView;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/23.
 */
public class TestActivity extends BaseActivity {

    SeekBar mSeekBar;
    TestView mTestView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        mTestView = (TestView) findViewById(R.id.test_view);
        mSeekBar = (SeekBar) findViewById(R.id.test_seek_bar);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float scale = (float) progress / 50;
                mTestView.scaleImg(scale);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mTestView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return mTestView.onDragEvent(event);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mTestView.onTouchEvent(event);
    }
}
