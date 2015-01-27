package com.lza.pad.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lza.pad.R;
import com.lza.pad.support.debug.AppLogger;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/22.
 */
public class LibraryMap extends View {

    private Paint mPaint;
    private Bitmap mBmpBg, mBmpPin;

    private float[][] mCor = new float[][] {
            new float[]{410.0f, 300.0f},
            new float[]{900.0f, 450.0f},
            new float[]{1600.0f, 400.0f},
            new float[]{960.0f, 0.0f}
    };

    private float[][] mCorReal;

    private int mOrgBmpW, mOrgBmpH;

    public LibraryMap(Context context) {
        this(context, null);
    }

    public LibraryMap(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LibraryMap(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        mBmpBg = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.test_home_ilustracao_bg)).getBitmap();
        mBmpPin = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.test_home_ilustracao_pin)).getBitmap();

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.test_home_ilustracao_bg2, opt);
        mOrgBmpW = opt.outWidth;
        mOrgBmpH = opt.outHeight;
        AppLogger.e("mOrgBmpW=" + mOrgBmpW + ",mOrgBmpH=" + mOrgBmpH);

        mCorReal = new float[mCor.length][];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    Bitmap mNewBmp = null;
    Matrix mMatrix = new Matrix();
    float mScale = 0;
    int mPadding = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        if (mScale == 0) {
            int w = getWidth();
            int h = getHeight();

            int bw = mBmpBg.getWidth();
            int bh = mBmpBg.getHeight();

            AppLogger.e("w=" + w + ",h=" + h);
            AppLogger.e("bw=" + bw + ",bh=" + bh);

            float sx, sy;
            sx = (float) w / bw;
            sy = (float) h / bh;

            if (sx < sy) {
                mScale = sx;
            } else {
                mScale = sy;
            }
            AppLogger.e("sx=" + sx + ",sy=" + sy + ",scale=" + mScale);
            mMatrix.postScale(mScale, mScale);
        }

        if (mNewBmp == null) {
            mNewBmp = mBmpBg.createBitmap(mBmpBg, 0, 0, mBmpBg.getWidth(), mBmpBg.getHeight(), mMatrix, true);
        }

        if (mPadding == 0) {
            int h = getHeight();
            int bh = mNewBmp.getHeight();
            if (bh > h) {
                mPadding = (bh - h) / 2;
            } else {
                mPadding = (h - bh) / 2;
            }
        }

        canvas.drawBitmap(mNewBmp, 0, mPadding, mPaint);

        if (!mIfCorAnimAll) {
            for (int i = 0; i < mCor.length; i++) {
                if (mCorReal[i] == null) {
                    int bw = mNewBmp.getWidth();
                    int bh = mNewBmp.getHeight();

                    float x = mCor[i][0] / mOrgBmpW * bw;
                    float y = mCor[i][1] / mOrgBmpH * bh + mPadding;

                    mCorReal[i] = new float[4];
                    mCorReal[i][0] = x;
                    mCorReal[i][1] = x + mBmpPin.getWidth();
                    mCorReal[i][2] = y;
                    mCorReal[i][3] = y + mBmpPin.getHeight();

                    AppLogger.e("x=" + x + ",y=" + y);
                }
                if (i == mCorClickIndex) continue;
                canvas.drawBitmap(mBmpPin, mCorReal[i][0], mCorReal[i][2], mPaint);
            }
        }

        if (mIfCorClick && mCorClickIndex != -1) {
            float x = mCorReal[mCorClickIndex][0];
            float y = mCorReal[mCorClickIndex][2];
            if (mCorClickCount < COR_Y_MAX_COUNT) {
                if (mIfAsc) {
                    y -= COR_Y_OFFSET * mCorClickCount;
                    mCorClickCount++;
                } else {
                    y -= COR_Y_OFFSET * mCorClickCount;
                    mCorClickCount--;
                    if (mCorClickCount == 0) {
                        mIfCorClick = false;
                        mCorClickIndex = -1;
                    }
                }
                canvas.drawBitmap(mBmpPin, x, y, mPaint);
            } else if (mCorClickCount == COR_Y_MAX_COUNT) {
                y -= COR_Y_OFFSET * mCorClickCount;
                mCorClickCount--;
                canvas.drawBitmap(mBmpPin, x, y, mPaint);
                mIfAsc = false;
            }
            invalidate();
        }

        if (mIfCorAnimAll) {
            for (int i = 0; i < mCorReal.length; i++) {
                float x = mCorReal[i][0];
                float y = mCorReal[i][2];
                if (mCorClickCount < COR_Y_MAX_COUNT) {
                    if (mIfAsc) {
                        y -= COR_Y_OFFSET * mCorClickCount;
                        if (i == mCorReal.length - 1)
                            mCorClickCount++;
                    } else {
                        y -= COR_Y_OFFSET * mCorClickCount;
                        if (i == mCorReal.length - 1)
                            mCorClickCount--;
                        if (mCorClickCount == 0) {
                            mIfCorAnimAll = false;
                        }
                    }
                    canvas.drawBitmap(mBmpPin, x, y, mPaint);
                } else if (mCorClickCount == COR_Y_MAX_COUNT) {
                    y -= COR_Y_OFFSET * mCorClickCount;
                    if (i == mCorReal.length - 1)
                        mCorClickCount--;
                    canvas.drawBitmap(mBmpPin, x, y, mPaint);
                    mIfAsc = false;
                }
            }
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            for (int i = 0; i < mCorReal.length; i++) {
                if (x >= mCorReal[i][0] && x <= mCorReal[i][1] &&
                        y >= mCorReal[i][2] && y <= mCorReal[i][3]) {
                    perfromCorClickAnim(i);
                    return true;
                }
            }
            mListener.onNoneClick();
        }
        return super.onTouchEvent(event);
    }

    boolean mIfCorClick = false;//是否被点击
    int mCorClickIndex = -1;//被点击控件的序号
    final int COR_Y_OFFSET = 1;//每次移动像素
    int mCorClickCount = 0;//已经移动的次数
    final int COR_Y_MAX_COUNT = 8;//最多移动的次数
    boolean mIfAsc = true;//是否处于上升

    boolean mIfCorAnimAll = false;//控件是否全部运动

    public void corAnimAll() {
        mIfCorAnimAll = true;
        mIfCorClick = false;
        mCorClickCount = 1;
        mIfAsc = true;
        invalidate();
    }

    public void perfromCorClickAnim(int index) {
        mIfCorClick = true;
        mCorClickIndex = index;
        mCorClickCount = 1;
        mIfAsc = true;
        mIfCorAnimAll = false;

        invalidate();

        mListener.onCorClick(index, mCorReal[index][0], mCorReal[index][2]);
    }

    private OnCorClickListener mListener = new OnCorClickListener() {
        @Override
        public void onCorClick(int index, float x, float y) {

        }

        @Override
        public void onNoneClick() {

        }
    };

    public void setOnCorClickListener(OnCorClickListener listener) {
        this.mListener = listener;
    }

    public interface OnCorClickListener {
        void onCorClick(int index, float x, float y);
        void onNoneClick();
    }
}
