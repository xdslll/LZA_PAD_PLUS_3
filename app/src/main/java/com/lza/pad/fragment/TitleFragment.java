package com.lza.pad.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app.EbookActivity;
import com.lza.pad.app.GuideActivity;
import com.lza.pad.app.NewsActivity;
import com.lza.pad.app.TestActivity;
import com.lza.pad.app.wifi.WifiApActivity;
import com.lza.pad.app.wifi.admin.WifiApAdmin;
import com.lza.pad.fragment.base.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/13/15.
 */
public class TitleFragment extends BaseFragment {

    private Calendar mCalendar;
    private TextView mTxtTime, mTxtDate;
    private ImageButton mImgNavEbook, mImgNavNews, mImgNavMore, mImgNavGuide, mImgNavNewbook;
    private LinearLayout mLayoutFreeWifi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.title, container, false);

        mTxtTime = (TextView) view.findViewById(R.id.title_time_text);
        mTxtDate = (TextView) view.findViewById(R.id.title_date_text);
        mLayoutFreeWifi = (LinearLayout) view.findViewById(R.id.title_free_wifi);

        mImgNavEbook = (ImageButton) view.findViewById(R.id.title_nav_ebook);
        mImgNavEbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, EbookActivity.class));
            }
        });

        mImgNavNews = (ImageButton) view.findViewById(R.id.title_nav_news);
        mImgNavNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, NewsActivity.class));
            }
        });

        mImgNavMore = (ImageButton) view.findViewById(R.id.title_nav_more);
        mImgNavMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, TestActivity.class));
            }
        });

        mImgNavGuide = (ImageButton) view.findViewById(R.id.title_nav_guide);
        mImgNavGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, GuideActivity.class));
            }
        });

        mImgNavNewbook = (ImageButton) view.findViewById(R.id.title_nav_newbook);
        mImgNavNewbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, WifiApActivity.class));
            }
        });

        return view;
    }

    ScheduledExecutorService mService = null;

    @Override
    public void onResume() {
        super.onResume();
        mService = Executors.newSingleThreadScheduledExecutor();
        mService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                //更新时间
                mCalendar = Calendar.getInstance();
                mWeekStr = getWeekStr();

                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd,HH:mm:ss");
                String timeStr = sdf.format(mCalendar.getTime());
                //AppLogger.e("timeStr=" + timeStr + ",mTimeStr=" + mTimeStr);
                if (!timeStr.equals(mTimeStr)) {
                    mTimeStr = timeStr;
                    if (isVisible())
                        mHandler.sendEmptyMessage(REQUEST_UPDATE_TIME);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

        boolean hasFreeWifi = WifiApAdmin.isWifiApEnable(mActivity);
        if (hasFreeWifi)
            mLayoutFreeWifi.setVisibility(View.VISIBLE);
        else
            mLayoutFreeWifi.setVisibility(View.GONE);
    }

    String mTimeStr;
    String mWeekStr;
    final int REQUEST_UPDATE_TIME = 0x001;
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
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        mService.shutdown();
    }

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
