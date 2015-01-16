package com.lza.pad.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.lza.pad.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/15/15.
 */
public class DefaultEbookCover extends View {

    private String mCoverTitle = "";
    private String mCoverAuthor = "";
    private float mCoverTitleSize, mCoverAuthorSize, mCoverTitleMargin, mCoverAuthorMargin;


    private BitmapDrawable mDrawable;
    private Paint mPaint;

    public DefaultEbookCover(Context context) {
        this(context, null, 0);
    }

    public DefaultEbookCover(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultEbookCover(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DefaultEbookCover);
        mCoverTitle = ta.getString(R.styleable.DefaultEbookCover_cover_title);
        mCoverAuthor = ta.getString(R.styleable.DefaultEbookCover_cover_author);
        mDrawable = (BitmapDrawable) ta.getDrawable(R.styleable.DefaultEbookCover_cover_img);
        mCoverTitleSize = ta.getDimension(R.styleable.DefaultEbookCover_cover_title_size, 35);
        mCoverAuthorSize = ta.getDimension(R.styleable.DefaultEbookCover_cover_author_size, 25);
        mCoverTitleMargin = ta.getDimension(R.styleable.DefaultEbookCover_cover_title_margin, 60);
        mCoverAuthorMargin = ta.getDimension(R.styleable.DefaultEbookCover_cover_author_margin, 30);
        ta.recycle();

        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int W = getWidth();
        final int H = getHeight();
        mPaint.setAntiAlias(true);

        Bitmap bitmap = mDrawable.getBitmap();
        final int BW = bitmap.getWidth();
        final int BH = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale((float) W / BW, (float) H / BH);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, BW, BH, matrix, true);
        canvas.drawBitmap(newBitmap, 0, 0, mPaint);

        mPaint.setTextSize(mCoverTitleSize);
        mPaint.setColor(Color.WHITE);
        float titleWidth = mPaint.measureText(mCoverTitle, 0, 5);
        float x = (W - titleWidth) / 2;
        canvas.drawText(mCoverTitle, 0, 5, x, mCoverTitleMargin, mPaint);

        mPaint.setTextSize(mCoverAuthorSize);
        float authorWidth = mPaint.measureText(mCoverAuthor);
        x = (W - authorWidth) / 2;
        canvas.drawText(mCoverAuthor, x, H - mCoverAuthorMargin, mPaint);
    }

}
