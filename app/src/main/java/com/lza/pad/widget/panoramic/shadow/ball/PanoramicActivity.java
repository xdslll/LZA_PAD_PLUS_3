package com.lza.pad.widget.panoramic.shadow.ball;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lza.pad.R;
import com.lza.pad.app.base.BaseActivity;

public class PanoramicActivity extends BaseActivity {

	GLSurfaceView mGlSurfaceView;
	PanoramicBallView mBall;
	private float mPreviousY;
	private float mPreviousX;

    RelativeLayout mLayoutTool;
    TextView mTxtBack, mTxtTitle, mTxtOrder, mTxtShowText;

    int mCurrentIndex;
    String mCurrentMapTitle = "地图";
    String mCurrentMapFunc = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.panoramic_view);

        if (getIntent() != null) {
            mCurrentIndex = getIntent().getIntExtra(KEY_MAP_INDEX, 0);
            mCurrentMapTitle = getIntent().getStringExtra(KEY_MAP_TITLE);
            mCurrentMapFunc = getIntent().getStringExtra(KEY_MAP_FUNC_TEXT);
        }

        mLayoutTool = (RelativeLayout) findViewById(R.id.panoramic_view_tool);
        mTxtBack = (TextView) findViewById(R.id.panoramic_view_back);
        mTxtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTxtTitle = (TextView) findViewById(R.id.panoramic_view_title);
        mTxtOrder = (TextView) findViewById(R.id.panoramic_view_order);

        mTxtTitle.setText(mCurrentMapTitle);
        if (TextUtils.isEmpty(mCurrentMapFunc))
            mTxtOrder.setVisibility(View.GONE);
        else
            mTxtOrder.setText(mCurrentMapFunc);

        mTxtShowText = (TextView) findViewById(R.id.panoramic_view_show_text);
        mTxtShowText.setText(mCurrentMapTitle);

        mGlSurfaceView = (GLSurfaceView) findViewById(R.id.panoramic_view_gl);
        mGlSurfaceView.setEGLContextClientVersion(2);
        mBall = new PanoramicBallView(this, mCurrentIndex);
        mGlSurfaceView.setRenderer(mBall);
	}

    @Override
	public boolean onTouchEvent(MotionEvent e) {
		float y = e.getY();
		float x = e.getX();
		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
			float dy = y - mPreviousY;// 计算触控笔y的位移
			float dx = x - mPreviousX;// 计算触控笔x的位移
			mBall.yAngle += dx * 0.3f;// 设置填充椭圆绕y轴旋转的角度
			mBall.xAngle += dy * 0.3f;// 设置填充椭圆绕x轴旋转的角度
		}
		mPreviousY = y;//记录触控笔位置
		mPreviousX = x;//记录触控笔位置
        return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (null != mGlSurfaceView) {
			mGlSurfaceView.onResume();
		}
    }

	@Override
	protected void onPause() {
		super.onPause();
		if (null != mGlSurfaceView) {
			mGlSurfaceView.onPause();
		}
	}

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mTxtShowText.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .playOn(mTxtShowText);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.FadeOut)
                        .duration(1000)
                        .playOn(mTxtShowText);

                mLayoutTool.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeIn)
                        .duration(1000)
                        .playOn(mLayoutTool);
            }
        }, 3000);
    }

    Handler mHandler = new Handler(Looper.getMainLooper());
}
