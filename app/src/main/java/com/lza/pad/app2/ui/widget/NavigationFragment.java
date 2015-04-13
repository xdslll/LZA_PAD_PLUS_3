package com.lza.pad.app2.ui.widget;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app2.event.base.OnItemClickListener;
import com.lza.pad.app2.ui.widget.base.BaseImageFragment;
import com.lza.pad.db.model.pad.PadModule;
import com.lza.pad.db.model.pad.PadSceneModule;
import com.lza.pad.wifi.admin.WifiApAdmin;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/13/15.
 */
public class NavigationFragment extends BaseImageFragment {

    private Calendar mCalendar;
    private TextView mTxtTime, mTxtDate, mTxtConnectUser, mTxtVersion;
    private ImageView mImgBg;
    private LinearLayout mLayoutFreeWifi;
    private GridView mGridModules;

    private static final int MAX_GRID_SIZE = 6;

    private WifiApAdmin mWifiApAdmin;

    /**
     * 自定义时钟服务
     */
    private ScheduledExecutorService mCalendarService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWifiApAdmin = new WifiApAdmin(mActivity);
        //mWifiApAdmin = WifiApAdmin.getInstance(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_nav1, container, false);
        mTxtConnectUser = (TextView) view.findViewById(R.id.title_current_connect_user);
        mTxtTime = (TextView) view.findViewById(R.id.title_time_text);
        mTxtDate = (TextView) view.findViewById(R.id.title_date_text);
        mLayoutFreeWifi = (LinearLayout) view.findViewById(R.id.title_free_wifi);
        mGridModules = (GridView) view.findViewById(R.id.title_grid);
        mImgBg = (ImageView) view.findViewById(R.id.title_bg);

        if (mPadSubpageModule != null) {
            int size = mPadSubpageModule.size();
            if (size < MAX_GRID_SIZE) {
                mGridModules.setNumColumns(size);
            } else {
                mGridModules.setNumColumns(MAX_GRID_SIZE);
            }
            mGridModules.setAdapter(new TitleMenuAdapter());
        }

        mGridModules.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                super.onItemClick(parent, view, position, id);
                //ToastUtils.showLong(mActivity, "跳转到相应模块");
                launchSubpageModule(position);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String imgUrl = mPadWidgetData.getUrl();
        if (!isEmpty(imgUrl)) {
            int w = mPadWidgetLayout.getWidget_width();
            int h = mPadWidgetLayout.getWidget_height();
            ImageSize size = new ImageSize(w, h);
            loadImage(imgUrl, size, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mImgBg.setImageBitmap(loadedImage);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startCalendarService();
        boolean hasFreeWifi = mWifiApAdmin.isWifiApEnable();
        if (hasFreeWifi)
            mLayoutFreeWifi.setVisibility(View.VISIBLE);
        else
            mLayoutFreeWifi.setVisibility(View.GONE);
        mHandler.sendEmptyMessage(REQUEST_UPDATE_CONNECT_USER_LOOP);
    }

    @Override
    public void onPause() {
        super.onPause();
        shutdownCalendarService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void startCalendarService() {
        mCalendarService = Executors.newSingleThreadScheduledExecutor();
        mCalendarService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                //更新时间
                mCalendar = Calendar.getInstance();
                mWeekStr = getWeekStr();

                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd,HH:mm:ss");
                String timeStr = sdf.format(mCalendar.getTime());
                if (!timeStr.equals(mTimeStr)) {
                    mTimeStr = timeStr;
                    try {
                        if (isVisible())
                            mHandler.sendEmptyMessage(REQUEST_UPDATE_TIME);
                    } catch (Exception ex) {

                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void shutdownCalendarService() {
        mCalendarService.shutdown();
    }

    private class TitleMenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPadSubpageModule.size();
        }

        @Override
        public PadSceneModule getItem(int position) {
            return mPadSubpageModule.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) convertView = mInflater.inflate(R.layout.title_menu_item, null);
            final ViewHolder holder = getHolder(convertView);
            PadSceneModule data = getItem(position);
            PadModule module = pickFirst(data.getModule_id());
            String imgUrl = module.getIco();
            int imgWidth = holder.img.getWidth();
            int imgHeight = holder.img.getHeight();
            ImageSize size = new ImageSize(imgWidth, imgHeight);
            loadImage(imgUrl, size, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.img.setImageBitmap(loadedImage);
                }
            });
            holder.text.setText(data.getLabel());
            return convertView;
        }

        private ViewHolder getHolder(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder(view);
                view.setTag(holder);
            }
            return holder;
        }
    }

    private class ViewHolder {
        TextView text;
        ImageView img;

        ViewHolder(View view) {
            text = (TextView) view.findViewById(R.id.title_menu_item_text);
            img = (ImageView) view.findViewById(R.id.title_menu_item_ico);
        }
    }

    String mTimeStr;
    String mWeekStr;
    final int REQUEST_UPDATE_TIME = 0x01;
    final int REQUEST_UPDATE_CONNECT_USER = 0x02;
    final int REQUEST_UPDATE_CONNECT_USER_LOOP = 0x03;
    public static final int CALENDAR_DELAY = 1000;

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_UPDATE_TIME) {
                if (!TextUtils.isEmpty(mTimeStr)) {
                    String[] timeStrSplit = mTimeStr.split(",");
                    mTxtDate.setText(timeStrSplit[0]);
                    mTxtTime.setText(timeStrSplit[1]);
                    mTxtDate.append("\t" + mWeekStr);
                }
            } else if (msg.what == REQUEST_UPDATE_CONNECT_USER) {
                //sendEmptyMessageDelayed(REQUEST_UPDATE_CONNECT_USER_LOOP, CALENDAR_DELAY);
            } else if (msg.what == REQUEST_UPDATE_CONNECT_USER_LOOP) {
                //sendEmptyMessage(REQUEST_UPDATE_CONNECT_USER);
            }
        }
    };

    private String getWeekStr() {
        int weekIndex = mCalendar.get(Calendar.DAY_OF_WEEK);
        if (weekIndex == Calendar.MONDAY) return "星期一";
        else if (weekIndex == Calendar.TUESDAY) return "星期二";
        else if (weekIndex == Calendar.WEDNESDAY) return "星期三";
        else if (weekIndex == Calendar.THURSDAY) return "星期四";
        else if (weekIndex == Calendar.FRIDAY) return "星期五";
        else if (weekIndex == Calendar.SATURDAY) return "星期六";
        else if (weekIndex == Calendar.SUNDAY) return "星期天";
        else return "";
    }
}
