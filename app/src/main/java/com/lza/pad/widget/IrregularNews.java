package com.lza.pad.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lza.pad.R;
import com.lza.pad.support.utils.ToastUtils;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/16/15.
 */
public class IrregularNews extends View {

    private Paint mPaint;
    private TextPaint mTextPaint;

    private String mTitle = "我校开通人大复印报刊资料全文库试用的通知，我校开通人大复印报刊资料全文库试用的通知，我校开通人大复印报刊资料全文库试用的通知，我校开通人大复印报刊资料全文库试用的通知，我校开通人大复印报刊资料全文库试用的通知，我校开通人大复印报刊资料全文库试用的通知，我校开通人大复印报刊资料全文库试用的通知";
    private String mAbstract = "人大“复印报刊资料”全文数据库是在复印报刊资料纸质期刊基础上，进一步分类、编辑整理形成。数据库资源从当年公开出版的社科报纸、期刊，依循严谨的学术标准——论文或文献须“具有较高的学术价值、应用价值，含有新观点、新材料、新方法或具有一定的代表性，能及时反映学术研究或实际工作部门的现状、成就及其新发展动向”的原则，由专家、学者在全面审视人文社科报刊所有文章的基础上对海量学术信息进行精心整理、加工、分类、编辑，去芜存菁、优中选优，提供高质量的学术信息产品。";

    private int mNewsColor;
    private int mTitleTextSize = 32;
    private int mAbstractTextSize = 24;

    private int mAlpha = 80;

    public IrregularNews(Context context) {
        this(context, null);
    }

    public IrregularNews(Context context, AttributeSet attrs) {
        super(context, attrs);

        mNewsColor = context.getResources().getColor(R.color.common_blue);

        mPaint = new Paint();
        mPaint.setAntiAlias(false);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.WHITE);

        BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.news_test);
        //mBitmap = drawable.getBitmap();
        mBitmap2 = drawable.getBitmap();
        if (mBitmap != null) {
            mSrc = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        } else if (mBitmap2 != null) {
            mSrc = new Rect(0, 0, mBitmap2.getWidth(), mBitmap2.getHeight());
        }

        mCtx = context;
    }

    private Bitmap mBitmap, mBitmap2;
    private Rect mSrc, mDst1, mDst2, mDst3, mDst4, mDst5 ,mDst6;
    private int W, H;
    private Context mCtx;

    @Override
    protected void onDraw(Canvas canvas) {
        W = getWidth();
        H = getHeight();

        if (mDst1 == null) {
            mDst1 = new Rect(0, 0, W / 4, H);
        }
        if (mDst2 == null) {
            mDst2 = new Rect(W / 4 + 5, 0, W / 4 * 3, H / 5 * 3);
        }
        if (mDst3 == null) {
            mDst3 = new Rect(W / 4 + 5, H / 5 * 3 + 5, W / 2, H);
        }
        if (mDst4 == null) {
            mDst4 = new Rect(W / 2 + 5, H / 5 * 3 + 5, W / 4 * 3, H);
        }
        if (mDst5 == null) {
            mDst5 = new Rect(W / 4 * 3 + 5, 0, W, H / 2);
        }
        if (mDst6 == null) {
            mDst6 = new Rect(W / 4 * 3 + 5, H / 2 + 5, W, H);
        }

        String _title, _abstract;

        //绘制背景
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, W, H, mPaint);

        //绘制6块新闻
        mPaint.setColor(mNewsColor);
        canvas.drawRect(mDst1, mPaint);
        canvas.drawRect(mDst2, mPaint);
        canvas.drawRect(mDst3, mPaint);
        canvas.drawRect(mDst4, mPaint);
        canvas.drawRect(mDst5, mPaint);
        canvas.drawRect(mDst6, mPaint);

        if (mBitmap == null) {
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(mTitle), 35);
            _abstract = sub(replace(mAbstract), 54);
            drawText(_title, 7, 20, 55, 3, canvas, mTextPaint);
            mTextPaint.setTextSize(mAbstractTextSize);
            drawText(_abstract, 9, 20, 260, 2, canvas, mTextPaint);
        } else {
            canvas.drawBitmap(mBitmap, mSrc, mDst1, null);
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setAlpha(mAlpha);
            Shader shader = new LinearGradient(0, H - 100, 0, H,
                    new int[]{Color.GRAY, Color.DKGRAY, Color.BLACK},
                    null, Shader.TileMode.REPEAT);
            mPaint.setShader(shader);
            canvas.drawRect(0, H - 100, W / 4, H, mPaint);
            mPaint.setShader(null);
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(mTitle), 14);
            drawText(_title, 7, 20, H - 60, 3, canvas, mTextPaint);
        }
        //绘制第二块新闻
        if (mBitmap2 == null) {
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(mTitle), 45);
            _abstract = sub(replace(mAbstract), 60);
            drawText(_title, 15, W / 4 + 30, 55, 3, canvas, mTextPaint);
            mTextPaint.setTextSize(mAbstractTextSize);
            drawText(_abstract, 20, W / 4 + 30, 180, 2, canvas, mTextPaint);
        } else {
            canvas.drawBitmap(mBitmap2, mSrc, mDst2, null);
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setAlpha(mAlpha);
            Shader shader = new LinearGradient(0, H / 5 * 3 - 100, 0, H / 5 * 3,
                    new int[]{Color.GRAY, Color.DKGRAY, Color.BLACK},
                    null, Shader.TileMode.REPEAT);
            mPaint.setShader(shader);
            canvas.drawRect(W / 4 + 5, H / 5 * 3 - 100, W / 4 * 3, H / 5 * 3, mPaint);
            mPaint.setShader(null);
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(mTitle), 30);
            drawText(_title, 15, W / 4 + 30, H / 5 * 3 - 60, 3, canvas, mTextPaint);
        }

        //绘制第三块新闻
        if (mBitmap2 == null) {
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(mTitle), 28);
            drawText(_title, 7, W / 4 + 25, H / 5 * 3 + 45, 3, canvas, mTextPaint);
        } else {
            canvas.drawBitmap(mBitmap2, mSrc, mDst3, null);
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setAlpha(mAlpha);
            Shader shader = new LinearGradient(0, H - 100, 0, H,
                    new int[]{Color.GRAY, Color.DKGRAY, Color.BLACK},
                    null, Shader.TileMode.REPEAT);
            mPaint.setShader(shader);
            canvas.drawRect(W / 4 + 5, H - 100, W / 2, H, mPaint);
            mPaint.setShader(null);
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(mTitle), 14);
            drawText(_title, 7, W / 4 + 25, H - 60, 3, canvas, mTextPaint);
        }

        //绘制第四块新闻
        if (mBitmap == null) {
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(mTitle), 28);
            drawText(_title, 7, W / 2 + 25, H / 5 * 3 + 45, 3, canvas, mTextPaint);
        } else {
            canvas.drawBitmap(mBitmap, mSrc, mDst4, null);
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setAlpha(mAlpha);
            Shader shader = new LinearGradient(0, H - 100, 0, H,
                    new int[]{Color.GRAY, Color.DKGRAY, Color.BLACK},
                    null, Shader.TileMode.REPEAT);
            mPaint.setShader(shader);
            canvas.drawRect(W / 2 + 5, H - 100, W / 4 * 3, H, mPaint);
            mPaint.setShader(null);
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(mTitle), 14);
            drawText(_title, 7, W / 2 + 25, H - 60, 3, canvas, mTextPaint);
        }

        //绘制第五块新闻
        if (mBitmap == null) {
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(mTitle), 21);
            _abstract = sub(replace(mAbstract), 18);
            drawText(_title, 7, W / 4 * 3 + 25, 55, 3, canvas, mTextPaint);
            mTextPaint.setTextSize(mAbstractTextSize);
            drawText(_abstract, 9, W / 4 * 3 + 25, 175, 2, canvas, mTextPaint);
        } else {
            canvas.drawBitmap(mBitmap, mSrc, mDst5, null);
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setAlpha(mAlpha);
            Shader shader = new LinearGradient(0, H / 2 - 100, 0, H / 2,
                    new int[]{Color.GRAY, Color.DKGRAY, Color.BLACK},
                    null, Shader.TileMode.REPEAT);
            mPaint.setShader(shader);
            canvas.drawRect(W / 4 * 3 + 5, H / 2 - 100, W, H / 2, mPaint);
            mPaint.setShader(null);
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(mTitle), 14);
            drawText(_title, 7, W / 4 * 3 + 25, H / 2 - 60, 3, canvas, mTextPaint);
        }

        //绘制第六块新闻
        if (mBitmap2 == null) {
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(mTitle), 21);
            _abstract = sub(replace(mAbstract), 18);
            drawText(_title, 7, W / 4 * 3 + 25, H / 2 + 55, 3, canvas, mTextPaint);
            mTextPaint.setTextSize(mAbstractTextSize);
            drawText(_abstract, 9, W / 4 * 3 + 25, H / 2 + 175, 2, canvas, mTextPaint);
        } else {
            canvas.drawBitmap(mBitmap2, mSrc, mDst6, null);
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setAlpha(mAlpha);
            Shader shader = new LinearGradient(0, H - 100, 0, H,
                    new int[]{Color.GRAY, Color.DKGRAY, Color.BLACK},
                    null, Shader.TileMode.REPEAT);
            mPaint.setShader(shader);
            canvas.drawRect(W / 4 * 3 + 5, H - 100, W, H, mPaint);
            mPaint.setShader(null);
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(mTitle), 14);
            drawText(_title, 7, W / 4 * 3 + 25, H - 60, 3, canvas, mTextPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            if (x <= W / 4) {
                ToastUtils.showShort(mCtx, "区域1");
            } else if (x > W / 4 && x < W / 4 * 3 && y <= H / 5 * 3) {
                ToastUtils.showShort(mCtx, "区域2");
            } else if (x > W / 4 && x <= W / 2 && y > H / 5 * 3) {
                ToastUtils.showShort(mCtx, "区域3");
            } else if (x > W / 2 && x <= W / 4 * 3 && y > H / 5 * 3) {
                ToastUtils.showShort(mCtx, "区域4");
            } else if (x > W / 4 * 3 && y <= H / 2) {
                ToastUtils.showShort(mCtx, "区域5");
            } else if (x > W / 4 * 3 && y > H / 2) {
                ToastUtils.showShort(mCtx, "区域6");
            } else {

            }
        }
        return super.onTouchEvent(event);
    }

    private String sub(String text, int max) {
        if (text.length() > max) {
            return text.substring(0, max);
        }
        return text;
    }

    private String replace(String text) {
        return text.replace("“", "").replace("”", "").replace("—", "");
    }

    /**
     * 绘制文字
     *
     * @param defaultText 默认文字
     * @param everyLine 每行显示几个文字
     * @param left 左侧间距
     * @param top 顶部间距
     * @param textPadding 行距
     * @param canvas 画布
     * @param paint 画笔
     *
     */
    private void drawText(String defaultText, int everyLine, int left, int top, int textPadding, Canvas canvas, Paint paint) {
        int line;
        if (!TextUtils.isEmpty(defaultText)) {
            if (defaultText.length() % everyLine == 0) {
                line = defaultText.length() / everyLine;
            } else {
                line = defaultText.length() / everyLine + 1;
            }
            for (int i = 0; i < line; i++) {
                String text;
                if (i == line - 1) {
                    text = defaultText.substring(everyLine * i, defaultText.length());
                } else {
                    text = defaultText.substring(everyLine * i, everyLine * (i + 1));
                }
                int h = (int) (top + (paint.getTextSize() + textPadding) * i);
                canvas.drawText(text, left, h, paint);
            }
        }
    }
}
