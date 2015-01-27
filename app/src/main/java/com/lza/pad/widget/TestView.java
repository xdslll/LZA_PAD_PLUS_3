package com.lza.pad.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.lza.pad.R;
import com.lza.pad.support.debug.AppLogger;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/23.
 */
public class TestView extends View {

    private Bitmap bitmap;
    private Matrix matrix = new Matrix();
    private float sx = 0.0f;
    private int width, height;
    private float scale = 1.0f;
    private boolean isScale = true;

    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;

    private Paint paint;

    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.test_home_ilustracao_bg2)).getBitmap();
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        setFocusable(true);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setAlpha(150);

        gestureDetector = new GestureDetector(context, gestureListener);
        scaleDetector = new ScaleGestureDetector(context, scaleListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(canvasX, canvasY);
        matrix.reset();
        if (!isScale) {
            matrix.setSkew(sx, 0);
        } else {
            matrix.setScale(scale, scale);
        }
        if (scale > 0) {
            Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            if (mCurrentMode == MODE_DRAG) {
                paint.setAlpha(120);
            } else {
                paint.setAlpha(255);
            }
            canvas.drawBitmap(bitmap2, matrix, paint);
        }
    }

    public void skewImg(float newSkew) {
        isScale = false;
        sx = newSkew;
        postInvalidate();
    }

    public void scaleImg(float newScale) {
        isScale = true;
        if (newScale >2) scale = 2;
        else scale = newScale;
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointCount = event.getPointerCount();

        if (pointCount == 1) {
            return gestureDetector.onTouchEvent(event);
        } else {
            return scaleDetector.onTouchEvent(event);
        }
    }

    int MODE_NONE = 0;
    int MODE_DRAG = 1;
    int mCurrentMode = MODE_NONE;

    @Override
    public boolean onDragEvent(DragEvent event) {
        AppLogger.e("onDragEvent");
        return super.onDragEvent(event);
    }

    GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            AppLogger.e("onScroll");
            canvasX -= distanceX;
            canvasY -= distanceY;
            postInvalidate();
            return true;
        }
    };

    ScaleGestureDetector.OnScaleGestureListener scaleListener = new ScaleGestureDetector.OnScaleGestureListener() {

        private float oldDist;
        private float newDist;
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            AppLogger.e("onScale");
            newDist = detector.getCurrentSpan();

            AppLogger.e("old=" + oldDist + ",new=" + newDist);

            if (oldDist != 0) {
                scale = newDist / oldDist;
                scaleImg(scale);
            }
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            AppLogger.e("onScaleBegin");
            if (oldDist == 0) {
                oldDist = detector.getCurrentSpan();
            }
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            AppLogger.e("onScaleEnd");
            oldDist = 0;
            newDist = 0;
        }
    };

    private float canvasX = 0, canvasY = 0;
}
