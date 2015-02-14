package com.lza.pad.fragment.home;

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
import com.lza.pad.app.ebook.EbookActivity;
import com.lza.pad.app.guide.GuideActivity;
import com.lza.pad.app.news.NewsActivity;
import com.lza.pad.app.socket.MinaServerActivity;
import com.lza.pad.app.socket.model.MinaClient;
import com.lza.pad.app.socket.service.MinaServiceHelper;
import com.lza.pad.app.wifi.WifiApActivity;
import com.lza.pad.app.wifi.admin.WifiApAdmin;
import com.lza.pad.fragment.base.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/13/15.
 */
public class TitleFragment2 extends BaseFragment {

    private Calendar mCalendar;
    private TextView mTxtTime, mTxtDate, mTxtConnectUser;
    private ImageButton mImgNavEbook, mImgNavNews, mImgNavMore, mImgNavGuide, mImgNavNewbook;
    private LinearLayout mLayoutFreeWifi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.title, container, false);

        mTxtConnectUser = (TextView) view.findViewById(R.id.title_current_connect_user);
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
                startActivity(new Intent(mActivity, MinaServerActivity.class));
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

        boolean hasFreeWifi = WifiApAdmin.instance(mActivity).isWifiApEnable();
        if (hasFreeWifi)
            mLayoutFreeWifi.setVisibility(View.VISIBLE);
        else
            mLayoutFreeWifi.setVisibility(View.GONE);

        mHandler.sendEmptyMessage(REQUEST_UPDATE_CONNECT_USER_LOOP);
    }

    String mTimeStr;
    String mWeekStr;
    final int REQUEST_UPDATE_TIME = 0x01;
    final int REQUEST_UPDATE_CONNECT_USER = 0x02;
    final int REQUEST_UPDATE_CONNECT_USER_LOOP = 0x03;

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
                updateConnectUser(null);
                sendEmptyMessageDelayed(REQUEST_UPDATE_CONNECT_USER_LOOP, 1000);
            } else if (msg.what == REQUEST_UPDATE_CONNECT_USER_LOOP) {
                sendEmptyMessage(REQUEST_UPDATE_CONNECT_USER);
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

    @Override
    public void onEventMainThread(MinaClient client) {
        updateConnectUser(client);
    }

    MinaClient mClient;

    private void updateConnectUser(MinaClient client) {
        MinaServiceHelper helper = MinaServiceHelper.instance();
        List<MinaClient> clients = helper.getClients();
        //AppLogger.e("已连接到Mina服务端的客户端数量：" + clients.size());
        if (client != null) {
            mClient = client;
        }
        if (mClient == null && clients.size() > 0) {
            mClient = clients.get(0);
        }
        if (mClient != null) {
            mTxtConnectUser.setText("/当前连接用户数：" + clients.size() + "\n/实时连接用户：[" + mClient.getAcademy() + "]" + mClient.getName());
        } else {
            mTxtConnectUser.setText("/当前连接用户数：" + clients.size());
        }
    }
}
