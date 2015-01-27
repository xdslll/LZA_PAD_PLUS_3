package com.lza.pad.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lza.pad.R;
import com.lza.pad.support.debug.AppLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/23.
 */
public class WritableView extends View {

    float preX, preY;
    private Path path;
    public Paint paint = null;
    int VIEW_WIDTH = 0;
    int VIEW_HEIGHT = 0;
    Bitmap cacheBitmap = null;
    Bitmap restoreBitmap = null;
    Canvas cacheCanvas = null;
    PathEffect effect;
    int bgColor, pathColor;

    public WritableView(Context context) {
        this(context, null);
    }

    public WritableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        cacheCanvas = new Canvas();
        path = new Path();
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        paint.setDither(true);

        //effect = new DiscretePathEffect(3.0f, 5.0f);
        effect = new CornerPathEffect(10f);
        paint.setPathEffect(effect);

        bgColor = getResources().getColor(R.color.white);
        pathColor = Color.RED;
        paint.setColor(pathColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        VIEW_WIDTH = getMeasuredWidth();
        VIEW_HEIGHT = getMeasuredHeight();
        cacheBitmap = Bitmap.createBitmap(VIEW_WIDTH, VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
        cacheCanvas.setBitmap(cacheBitmap);

        AppLogger.e("w=" + VIEW_WIDTH + ",h=" + VIEW_HEIGHT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(cacheBitmap, 0, 0, null);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                preX = x;
                preY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                path.quadTo(preX, preY, x, y);
                preX = x;
                preY = y;
                break;
            case MotionEvent.ACTION_UP:
                cacheCanvas.drawPath(path, paint);
                restoreBitmap = Bitmap.createBitmap(cacheBitmap);
                path.reset();
                break;
        }
        invalidate();
        return true;
    }

    public void save() {
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, "img.png");
        try {
            FileOutputStream out = new FileOutputStream(file);
            cacheBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restore() {
        cacheBitmap = restoreBitmap;
        cacheCanvas.setBitmap(cacheBitmap);
        invalidate();
    }

    public void clear() {
        cacheBitmap = Bitmap.createBitmap(VIEW_WIDTH, VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
        cacheCanvas.setBitmap(cacheBitmap);
        invalidate();
    }
}
