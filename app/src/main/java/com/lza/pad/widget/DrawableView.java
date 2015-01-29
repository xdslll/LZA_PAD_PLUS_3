package com.lza.pad.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/28.
 */
public class DrawableView extends View {

    public DrawableView(Context context) {
        this(context, null);
    }

    public DrawableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6.0f);

    }

    Paint paint;
    float x = -1, y = -1;

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        paint.setColor(Color.RED);
        if (x > 0 && y > 0)
            canvas.drawCircle(x, y, 50, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }
}
