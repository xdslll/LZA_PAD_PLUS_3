package com.lza.pad.fragment.poster;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lza.pad.R;
import com.lza.pad.fragment.base.BaseFragment;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.file.FileTools;

import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import java.io.File;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/11.
 */
public class PosterFragment1 extends BaseFragment {

    private final String PATH = "image";
    private final String FILE_NAME = "test_poster";

    private ViewFlow mViewFlow;
    private CircleFlowIndicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mViews.add(createView(1));
        //mViews.add(createView(2));
        //mViews.add(createView(3));
        //mViews.add(createView(4));
    }

    /**
     * 获取海报文件
     *
     * @param index
     * @return 文件是否存在
     */
    private File createrFile(int index) {
        //获取缓存路径
        File dir = FileTools.createCacheFile(PATH);
        //获取海报文件
        String fileName = FILE_NAME + index;
        File file = new File(dir, fileName + ".jpg");
        if (file.exists()) return file;
        file = new File(dir, fileName + ".jpeg");
        if (file.exists()) return file;
        file = new File(dir, fileName + ".png");
        if (file.exists()) return file;
        return null;
    }


    private Bitmap createBitmap(File file) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inJustDecodeBounds = false;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize = 4;
        Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), opt);

        return bm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.poster, container, false);

        mViewFlow = (ViewFlow) view.findViewById(R.id.poster_viewflow);
        mIndicator = (CircleFlowIndicator) view.findViewById(R.id.poster_viewflow_indicator);

        mViewFlow.setAdapter(new ViewFlowAdapter(mActivity));
        mViewFlow.setFlowIndicator(mIndicator);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHandler.sendEmptyMessageDelayed(REQUEST_NEXT, TURN_TO_PAGE_DELAY);

        mViewFlow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mCurrentSelection = mViewFlow.getSelectedItemPosition();
                    return true;
                }
                return false;
            }
        });
    }

    int mCurrentSelection = 0;
    int MAX_SIZE = 5;
    int TURN_TO_PAGE_DELAY = 5 * 1000;

    public static final int REQUEST_NEXT = 0x01;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_NEXT) {
                AppLogger.e("开始翻页..." + mCurrentSelection);
                if (mCurrentSelection == MAX_SIZE) {
                    mCurrentSelection = 0;
                }
                mViewFlow.setSelection(mCurrentSelection);
                mCurrentSelection++;
                mHandler.sendEmptyMessageDelayed(REQUEST_NEXT, TURN_TO_PAGE_DELAY);
            }
        }
    };



    private class ViewFlowAdapter extends BaseAdapter {

        private LayoutInflater mInflater = null;

        public ViewFlowAdapter(Context c) {
            mInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.poster_img, null);
                holder.image = (ImageView) convertView.findViewById(R.id.poster_img_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            File file = createrFile(position);
            if (file != null)
                holder.image.setImageBitmap(createBitmap(file));

            return convertView;
        }
    }

    private class ViewHolder {
        ImageView image;
    }
}