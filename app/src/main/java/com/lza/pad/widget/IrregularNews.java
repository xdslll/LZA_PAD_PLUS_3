package com.lza.pad.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.text.Html;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lza.pad.R;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.support.debug.AppLogger;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/16/15.
 */
public class IrregularNews extends View {

    private Paint mPaint;
    private TextPaint mTextPaint;

    //private String mTitle = "我校开通人大复印报刊资料全文库试用的通知，我校开通人大复印报刊资料全文库试用的通知，我校开通人大复印报刊资料全文库试用的通知，我校开通人大复印报刊资料全文库试用的通知，我校开通人大复印报刊资料全文库试用的通知，我校开通人大复印报刊资料全文库试用的通知，我校开通人大复印报刊资料全文库试用的通知";
    //private String mAbstract = "人大“复印报刊资料”全文数据库是在复印报刊资料纸质期刊基础上，进一步分类、编辑整理形成。数据库资源从当年公开出版的社科报纸、期刊，依循严谨的学术标准——论文或文献须“具有较高的学术价值、应用价值，含有新观点、新材料、新方法或具有一定的代表性，能及时反映学术研究或实际工作部门的现状、成就及其新发展动向”的原则，由专家、学者在全面审视人文社科报刊所有文章的基础上对海量学术信息进行精心整理、加工、分类、编辑，去芜存菁、优中选优，提供高质量的学术信息产品。";

    private int mNewsColor;
    private float mTitleTextSize = 22.0f;
    private float mAbstractTextSize = 18.0f;

    private int mAlpha = 255;

    private List<PadResource> mPadResources;

    private List<Bitmap> mBitmaps;
    private Canvas mCanvas;

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

        /*BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.test_news);
        mBitmap = drawable.getBitmap();
        mBitmap2 = drawable.getBitmap();
        if (mBitmap != null) {
            mSrc = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        } else if (mBitmap2 != null) {
            mSrc = new Rect(0, 0, mBitmap2.getWidth(), mBitmap2.getHeight());
        }
        mBitmaps = new ArrayList<Bitmap>();
        mBitmaps.add(mBitmap);*/

        mCtx = context;

        mTitleTextSize = getResources().getDimension(R.dimen.irregular_news_title_size);
        mAbstractTextSize = getResources().getDimension(R.dimen.irregular_news_abstract_size);
        AppLogger.e("新闻标题文字大小：" + mTitleTextSize);
        AppLogger.e("新闻摘要文字大小：" + mTitleTextSize);
    }

    //private Bitmap mBitmap, mBitmap2;
    private Rect mSrc, mDst1, mDst2, mDst3, mDst4, mDst5 ,mDst6;
    private NewsInfo mNewsInfo1, mNewsInfo2, mNewsInfo3, mNewsInfo4, mNewsInfo5, mNewsInfo6;
    private int W, H;
    private Context mCtx;

    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;

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

        //绘制背景
        mPaint.setColor(Color.TRANSPARENT);
        canvas.drawRect(0, 0, W, H, mPaint);

        //绘制6块新闻
        mPaint.setColor(mNewsColor);
        canvas.drawRect(mDst1, mPaint);
        canvas.drawRect(mDst2, mPaint);
        canvas.drawRect(mDst3, mPaint);
        canvas.drawRect(mDst4, mPaint);
        canvas.drawRect(mDst5, mPaint);
        canvas.drawRect(mDst6, mPaint);

        /**
         * 绘制第一块新闻
         */
        if (mBitmaps != null && mBitmaps.size() >= 1 && mBitmaps.get(0) != null) {
            if (mPadResources != null && mPadResources.size() >= 1) {
                mNewsInfo1 = new NewsInfo(14, 7, 20, H - 60, 3);
                mNewsInfo1.data = mPadResources.get(0);
                mNewsInfo1.bitmap = mBitmaps.get(0);
                mNewsInfo1.dest = mDst1;
                mNewsInfo1.shadowY0 = H - 200;
                mNewsInfo1.shadowY1 = H;
                mNewsInfo1.shadowRect = new Rect(0, H - 200, W / 4, H);
                drawBitmapNews(mNewsInfo1);
            }
        } else if (mPadResources != null && mPadResources.size() >= 1) {
            mNewsInfo1 = new NewsInfo(35, 48, 7, 20, 55, 3, 8, 20, 260, 2);
            mNewsInfo1.data = mPadResources.get(0);
            drawTextNews(mNewsInfo1);
        }

        /**
         * 绘制第二块新闻
         */
        if (mBitmaps != null && mBitmaps.size() >= 2 && mBitmaps.get(1) != null) {
            if (mPadResources != null && mPadResources.size() >= 2) {
                mNewsInfo2 = new NewsInfo(30, 15, W / 4 + 30, H / 5 * 3 - 60, 3);
                mNewsInfo2.data = mPadResources.get(1);
                mNewsInfo2.bitmap = mBitmaps.get(1);
                mNewsInfo2.dest = mDst2;
                mNewsInfo2.shadowY0 = H / 5 * 3 - 200;
                mNewsInfo2.shadowY1 = H / 5 * 3;
                mNewsInfo2.shadowRect = new Rect(W / 4 + 5, H / 5 * 3 - 200, W / 4 * 3, H / 5 * 3);
                drawBitmapNews(mNewsInfo2);
            }
        } else if (mPadResources != null && mPadResources.size() >= 2) {
            mNewsInfo2 = new NewsInfo(45, 60, 15, W / 4 + 30, 55, 3, 20, W / 4 + 30, 180, 2);
            mNewsInfo2.data = mPadResources.get(1);
            drawTextNews(mNewsInfo2);
        }

        /**
         * 绘制第三块新闻
         */
        if (mBitmaps != null && mBitmaps.size() >= 3 && mBitmaps.get(2) != null) {
            if (mPadResources != null && mPadResources.size() >= 3) {
                mNewsInfo3 = new NewsInfo(14, 7, W / 4 + 25, H - 60, 3);
                mNewsInfo3.data = mPadResources.get(2);
                mNewsInfo3.bitmap = mBitmaps.get(2);
                mNewsInfo3.dest = mDst3;
                mNewsInfo3.shadowY0 = H - 200;
                mNewsInfo3.shadowY1 = H;
                mNewsInfo3.shadowRect = new Rect(W / 4 + 5, H - 200, W / 2, H);
                drawBitmapNews(mNewsInfo3);
            }
        } else if (mPadResources != null && mPadResources.size() >= 3) {
            mNewsInfo3 = new NewsInfo(28, 7, W / 4 + 25, H / 5 * 3 + 45, 3);
            mNewsInfo3.data = mPadResources.get(2);
            drawTextNews(mNewsInfo3);
        }

        /**
         * 绘制第四块新闻
         */
        if (mBitmaps != null && mBitmaps.size() >= 4 && mBitmaps.get(3) != null) {
            if (mPadResources != null && mPadResources.size() >= 4) {
                mNewsInfo4 = new NewsInfo(14, 7, W / 2 + 25, H - 60, 3);
                mNewsInfo4.data = mPadResources.get(3);
                mNewsInfo4.bitmap = mBitmaps.get(3);
                mNewsInfo4.dest = mDst4;
                mNewsInfo4.shadowY0 = H - 200;
                mNewsInfo4.shadowY1 = H;
                mNewsInfo4.shadowRect = new Rect(W / 2 + 5, H - 200, W / 4 * 3, H);
                drawBitmapNews(mNewsInfo4);
            }
        } else if (mPadResources != null && mPadResources.size() >= 4) {
            mNewsInfo4 = new NewsInfo(28, 7, W / 2 + 25, H / 5 * 3 + 45, 3);
            mNewsInfo4.data = mPadResources.get(3);
            drawTextNews(mNewsInfo4);
        }

        /**
         * 绘制第五块新闻
         */
        if (mBitmaps != null && mBitmaps.size() >= 5 && mBitmaps.get(4) != null) {
            if (mPadResources != null && mPadResources.size() >= 5) {
                mNewsInfo5 = new NewsInfo(14, 7, W / 4 * 3 + 25, H / 2 - 60, 3);
                mNewsInfo5.data = mPadResources.get(4);
                mNewsInfo5.bitmap = mBitmaps.get(4);
                mNewsInfo5.dest = mDst5;
                mNewsInfo5.shadowY0 = H / 2 - 200;
                mNewsInfo5.shadowY1 = H / 2;
                mNewsInfo5.shadowRect = new Rect(W / 4 * 3 + 5, H / 2 - 200, W, H / 2);
                drawBitmapNews(mNewsInfo5);
            }
        } else if (mPadResources != null && mPadResources.size() >= 5) {
            mNewsInfo5 = new NewsInfo(21, 7, W / 4 * 3 + 25, 70, 3);
            mNewsInfo5.data = mPadResources.get(4);
            drawTextNews(mNewsInfo5);
        }

        /**
         * 绘制第六块新闻
         */
        if (mBitmaps != null && mBitmaps.size() >= 6 && mBitmaps.get(5) != null) {
            if (mPadResources != null && mPadResources.size() >= 6) {
                mNewsInfo6 = new NewsInfo(14, 7, W / 4 * 3 + 25, H - 60, 3);
                mNewsInfo6.data = mPadResources.get(5);
                mNewsInfo6.bitmap = mBitmaps.get(5);
                mNewsInfo6.dest = mDst6;
                mNewsInfo6.shadowY0 = H - 200;
                mNewsInfo6.shadowY1 = H;
                mNewsInfo6.shadowRect = new Rect(W / 4 * 3 + 5, H - 200, W, H);
                drawBitmapNews(mNewsInfo6);
            }
        } else if (mPadResources != null && mPadResources.size() >= 6) {
            mNewsInfo6 = new NewsInfo(21, 7, W / 4 * 3 + 25, H / 2 + 70, 3);
            mNewsInfo6.data = mPadResources.get(5);
            drawTextNews(mNewsInfo6);
        }
    }

    private class NewsInfo {

        public NewsInfo() {}

        private NewsInfo(int titleTextAmount, int titleEveryLine, int titlePaddingLeft, int titlePaddingTop, int titleTextPadding) {
            this.titleTextAmount = titleTextAmount;
            this.titleEveryLine = titleEveryLine;
            this.titlePaddingLeft = titlePaddingLeft;
            this.titlePaddingTop = titlePaddingTop;
            this.titleTextPadding = titleTextPadding;
        }

        private NewsInfo(int titleTextAmount, int abstractTextAmount, int titleEveryLine, int titlePaddingLeft,
                         int titlePaddingTop, int titleTextPadding, int abstractEveryLine, int abstractPaddingLeft,
                         int abstractPaddingTop, int abstractTextPadding) {
            this.titleTextAmount = titleTextAmount;
            this.abstractTextAmount = abstractTextAmount;
            this.titleEveryLine = titleEveryLine;
            this.titlePaddingLeft = titlePaddingLeft;
            this.titlePaddingTop = titlePaddingTop;
            this.titleTextPadding = titleTextPadding;
            this.abstractEveryLine = abstractEveryLine;
            this.abstractPaddingLeft = abstractPaddingLeft;
            this.abstractPaddingTop = abstractPaddingTop;
            this.abstractTextPadding = abstractTextPadding;
        }

        PadResource data;
        int titleTextAmount;
        int abstractTextAmount;
        int titleEveryLine;
        int titlePaddingLeft;
        int titlePaddingTop;
        int titleTextPadding;
        int abstractEveryLine;
        int abstractPaddingLeft;
        int abstractPaddingTop;
        int abstractTextPadding;

        Bitmap bitmap;
        Rect dest;
        int shadowY0;
        int shadowY1;
        Rect shadowRect;
    }

    /**
     * 绘制纯文本新闻
     *
     * @param news
     */
    public void drawTextNews(NewsInfo news) {
        if (news.data == null || mCanvas == null) return;
        //设置标题文本的颜色
        mTextPaint.setTextSize(mTitleTextSize);
        //截取标题
        String _title = sub(replace(news.data.getTitle()), news.titleTextAmount);
        //去掉摘要中的HTML标签
        String _abstract = Html.fromHtml(news.data.getContents()).toString();
        //截取摘要
        _abstract = sub(replace(_abstract), news.abstractTextAmount);
        //绘制标题
        drawText(_title, news.titleEveryLine, news.titlePaddingLeft, news.titlePaddingTop,
                news.titleTextPadding, mCanvas, mTextPaint);
        //设置摘要文本的颜色
        mTextPaint.setTextSize(mAbstractTextSize);
        //绘制摘要
        drawText(_abstract, news.abstractEveryLine, news.abstractPaddingLeft, news.abstractPaddingTop,
                news.abstractTextPadding, mCanvas, mTextPaint);
    }

    public void drawBitmapNews(NewsInfo news) {
        if (news.data == null || mCanvas == null || news.bitmap == null) return;
        //绘制背景图片
        mCanvas.drawBitmap(news.bitmap, mSrc, news.dest, null);
        //设置画笔
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setAlpha(mAlpha);
        //设置渐变效果
        Shader shader = new LinearGradient(0, news.shadowY0, 0, news.shadowY1,
                new int[]{Color.TRANSPARENT, Color.BLACK},
                null, Shader.TileMode.REPEAT);
        mPaint.setShader(shader);
        //绘制标题的背景矩形
        mCanvas.drawRect(news.shadowRect, mPaint);
        mPaint.setShader(null);
        mTextPaint.setTextSize(mTitleTextSize);
        //截取标题
        String _title = sub(replace(news.data.getTitle()), 14);
        //绘制标题
        drawText(_title, news.titleEveryLine, news.titlePaddingLeft, news.titlePaddingTop,
                news.titleTextPadding, mCanvas, mTextPaint);
    }

    public interface OnNewsClickListener {
        void onClick(View v, int position);
    }
    private OnNewsClickListener mListener;

    public void setOnNewsClickListener(OnNewsClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            if (x <= W / 4) {
                if (mListener != null) {
                    mListener.onClick(this, 1);
                }
            } else if (x > W / 4 && x < W / 4 * 3 && y <= H / 5 * 3) {
                if (mListener != null) {
                    mListener.onClick(this, 2);
                }
            } else if (x > W / 4 && x <= W / 2 && y > H / 5 * 3) {
                if (mListener != null) {
                    mListener.onClick(this, 3);
                }
            } else if (x > W / 2 && x <= W / 4 * 3 && y > H / 5 * 3) {
                if (mListener != null) {
                    mListener.onClick(this, 4);
                }
            } else if (x > W / 4 * 3 && y <= H / 2) {
                if (mListener != null) {
                    mListener.onClick(this, 5);
                }
            } else if (x > W / 4 * 3 && y > H / 2) {
                if (mListener != null) {
                    mListener.onClick(this, 6);
                }
            } else {
                if (mListener != null) {
                    mListener.onClick(this, -1);
                }
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

    public void setPadResources(List<PadResource> resources) {
        mPadResources = resources;
        invalidate();
    }

    public void setBitmaps(List<Bitmap> bitmaps) {
        mBitmaps = bitmaps;
        invalidate();
    }
}

/*mTextPaint.setTextSize(mTitleTextSize);
                _title = sub(replace(r1.getTitle()), 35);
                _abstract = sub(replace(r1.getContents()), 54);
                drawText(_title, 7, 20, 55, 3, canvas, mTextPaint);
                mTextPaint.setTextSize(mAbstractTextSize);
                drawText(_abstract, 9, 20, 260, 2, canvas, mTextPaint);*/

/*if (mBitmaps != null && mBitmaps.size() >= 1) {
            Bitmap bmp = mBitmaps.get(0);
            //绘制第一块新闻内容
            if (bmp == null) {
                drawTextNews(mTextNews1);
            } else {
                canvas.drawBitmap(bmp, mSrc, mDst1, null);
                mPaint.setColor(Color.TRANSPARENT);
                mPaint.setAlpha(mAlpha);
                Shader shader = new LinearGradient(0, H - 200, 0, H,
                        new int[]{Color.TRANSPARENT, Color.BLACK},
                        null, Shader.TileMode.REPEAT);
                mPaint.setShader(shader);
                canvas.drawRect(0, H - 200, W / 4, H, mPaint);
                mPaint.setShader(null);
                mTextPaint.setTextSize(mTitleTextSize);
                _title = sub(replace(r1.getTitle()), 14);
                drawText(_title, 7, 20, H - 60, 3, canvas, mTextPaint);
            }
        } else {
            drawTextNews(mTextNews1);
        }
        //绘制第二块新闻
        if (size < 2) return;
        PadResource r2 = mPadResources.get(1);

        if (mBitmap2 == null) {
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(r2.getTitle()), 45);
            _abstract = sub(replace(r2.getContents()), 60);
            drawText(_title, 15, W / 4 + 30, 55, 3, canvas, mTextPaint);
            mTextPaint.setTextSize(mAbstractTextSize);
            drawText(_abstract, 20, W / 4 + 30, 180, 2, canvas, mTextPaint);
        } else {
            canvas.drawBitmap(mBitmap2, mSrc, mDst2, null);
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setAlpha(mAlpha);
            Shader shader = new LinearGradient(0, H / 5 * 3 - 200, 0, H / 5 * 3,
                    new int[]{Color.TRANSPARENT, Color.BLACK},
                    null, Shader.TileMode.REPEAT);
            mPaint.setShader(shader);
            canvas.drawRect(W / 4 + 5, H / 5 * 3 - 200, W / 4 * 3, H / 5 * 3, mPaint);
            mPaint.setShader(null);
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(r2.getTitle()), 30);
            drawText(_title, 15, W / 4 + 30, H / 5 * 3 - 60, 3, canvas, mTextPaint);
        }

        //绘制第三块新闻
        if (size < 3) return;
        PadResource r3 = mPadResources.get(2);
        if (mBitmap2 == null) {
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(r3.getTitle()), 28);
            drawText(_title, 7, W / 4 + 25, H / 5 * 3 + 45, 3, canvas, mTextPaint);
        } else {
            canvas.drawBitmap(mBitmap2, mSrc, mDst3, null);
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setAlpha(mAlpha);
            Shader shader = new LinearGradient(0, H - 200, 0, H,
                    new int[]{Color.TRANSPARENT, Color.BLACK},
                    null, Shader.TileMode.REPEAT);
            mPaint.setShader(shader);
            canvas.drawRect(W / 4 + 5, H - 200, W / 2, H, mPaint);
            mPaint.setShader(null);
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(r3.getTitle()), 14);
            drawText(_title, 7, W / 4 + 25, H - 60, 3, canvas, mTextPaint);
        }

        //绘制第四块新闻
        if (size < 4) return;
        PadResource r4 = mPadResources.get(3);
        if (mBitmap == null) {
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(r4.getTitle()), 28);
            drawText(_title, 7, W / 2 + 25, H / 5 * 3 + 45, 3, canvas, mTextPaint);
        } else {
            canvas.drawBitmap(mBitmap, mSrc, mDst4, null);
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setAlpha(mAlpha);
            Shader shader = new LinearGradient(0, H - 200, 0, H,
                    new int[]{Color.TRANSPARENT, Color.BLACK},
                    null, Shader.TileMode.REPEAT);
            mPaint.setShader(shader);
            canvas.drawRect(W / 2 + 5, H - 200, W / 4 * 3, H, mPaint);
            mPaint.setShader(null);
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(r4.getTitle()), 14);
            drawText(_title, 7, W / 2 + 25, H - 60, 3, canvas, mTextPaint);
        }

        //绘制第五块新闻
        if (size < 5) return;
        PadResource r5 = mPadResources.get(4);
        if (mBitmap == null) {
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(r5.getTitle()), 21);
            _abstract = sub(replace(r5.getContents()), 18);
            drawText(_title, 7, W / 4 * 3 + 25, 55, 3, canvas, mTextPaint);
            mTextPaint.setTextSize(mAbstractTextSize);
            drawText(_abstract, 9, W / 4 * 3 + 25, 175, 2, canvas, mTextPaint);
        } else {
            canvas.drawBitmap(mBitmap, mSrc, mDst5, null);
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setAlpha(mAlpha);
            Shader shader = new LinearGradient(0, H / 2 - 200, 0, H / 2,
                    new int[]{Color.TRANSPARENT, Color.BLACK},
                    null, Shader.TileMode.REPEAT);
            mPaint.setShader(shader);
            canvas.drawRect(W / 4 * 3 + 5, H / 2 - 200, W, H / 2, mPaint);
            mPaint.setShader(null);
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(r5.getTitle()), 14);
            drawText(_title, 7, W / 4 * 3 + 25, H / 2 - 60, 3, canvas, mTextPaint);
        }

        //绘制第六块新闻
        if (size < 6) return;
        PadResource r6 = mPadResources.get(5);
        if (mBitmap2 == null) {
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(r6.getTitle()), 21);
            _abstract = sub(replace(r6.getContents()), 18);
            drawText(_title, 7, W / 4 * 3 + 25, H / 2 + 55, 3, canvas, mTextPaint);
            mTextPaint.setTextSize(mAbstractTextSize);
            drawText(_abstract, 9, W / 4 * 3 + 25, H / 2 + 175, 2, canvas, mTextPaint);
        } else {
            canvas.drawBitmap(mBitmap2, mSrc, mDst6, null);
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setAlpha(mAlpha);
            Shader shader = new LinearGradient(0, H - 200, 0, H,
                    new int[]{Color.TRANSPARENT, Color.BLACK},
                    null, Shader.TileMode.REPEAT);
            mPaint.setShader(shader);
            canvas.drawRect(W / 4 * 3 + 5, H - 200, W, H, mPaint);
            mPaint.setShader(null);
            mTextPaint.setTextSize(mTitleTextSize);
            _title = sub(replace(r6.getTitle()), 14);
            drawText(_title, 7, W / 4 * 3 + 25, H - 60, 3, canvas, mTextPaint);
        }
*/