package com.lza.pad.fragment.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.lza.pad.app.socket.model.MinaClient;
import com.lza.pad.app.socket.admin.server.MinaServerHelper;
import com.lza.pad.app.wifi.admin.WifiApAdmin;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadImageCollection;
import com.lza.pad.db.model.pad.PadLayoutModule;
import com.lza.pad.fragment.base.BaseImageFragment;
import com.lza.pad.helper.CommonRequestListener;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.UrlHelper;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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
public class TitleFragment1 extends BaseImageFragment {

    private Calendar mCalendar;
    private TextView mTxtTime, mTxtDate, mTxtConnectUser;
    private ImageView mImgBg;
    private LinearLayout mLayoutFreeWifi;
    private GridView mGridModules;
    private LayoutInflater mInflater;

    private static final int MAX_GRID_SIZE = 6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.title1, container, false);

        mTxtConnectUser = (TextView) view.findViewById(R.id.title_current_connect_user);
        mTxtTime = (TextView) view.findViewById(R.id.title_time_text);
        mTxtDate = (TextView) view.findViewById(R.id.title_date_text);
        mLayoutFreeWifi = (LinearLayout) view.findViewById(R.id.title_free_wifi);
        mGridModules = (GridView) view.findViewById(R.id.title_grid);
        mImgBg = (ImageView) view.findViewById(R.id.title_bg);

        if (mPadModuleInfos != null) {
            int size = mPadModuleInfos.size();
            if (size < MAX_GRID_SIZE) {
                mGridModules.setNumColumns(size);
            } else {
                mGridModules.setNumColumns(MAX_GRID_SIZE);
            }
            mGridModules.setAdapter(new TitleMenuAdapter());
        }

        mGridModules.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果点击了首页，则直接退出
                if (position + 1 == INDEX_HOME_MODULE) return;
                //拼接出代码所在的路径
                String javaCodeFile = getModuleJavaFileName(mPadModuleInfos.get(position));
                try {
                    Class clazz = Class.forName(javaCodeFile);
                    Intent intent = new Intent(mActivity, clazz);
                    intent.putExtra(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
                    PadLayoutModule module = mPadModuleInfos.get(position);
                    intent.putExtra(KEY_PAD_MODULE_INFO, module);
                    startActivity(intent);
                } catch (Exception ex) {

                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPadDeviceInfo != null && mPadControlInfo != null) {
            String getImageUrl = UrlHelper.getImageUrl(mPadDeviceInfo, mPadControlInfo);
            send(getImageUrl, mBgImageListener);
        }
    }

    CommonRequestListener<PadImageCollection> mBgImageListener = new CommonRequestListener<PadImageCollection>() {
        @Override
        public ResponseData<PadImageCollection> parseJson(String json) {
            return JsonParseHelper.parseImageCollectionResponse(json);
        }

        @Override
        public void handleRespone(List<PadImageCollection> content) {
            PadImageCollection imgCollection = content.get(0);
            String imgUrl = imgCollection.getImgs();
            displayImage(imgUrl, mImgBg);
        }
    };

    private class TitleMenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPadModuleInfos.size();
        }

        @Override
        public PadLayoutModule getItem(int position) {
            return mPadModuleInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) convertView = mInflater.inflate(R.layout.title_menu_item, null);
            final ViewHolder holder = getHolder(convertView);
            PadLayoutModule data = getItem(position);
            String imgUrl;
            if (mCurrentModuleIndex == position + 1) {
                imgUrl = data.getLayout_icon2();
            } else {
                imgUrl = data.getLayout_icon();
            }
            //displayImage(imgUrl, holder.img);
            int imgWidth = holder.img.getWidth();
            int imgHeight = holder.img.getHeight();
            ImageSize size = new ImageSize(imgWidth, imgHeight);
            loadImage(imgUrl, size, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.img.setImageBitmap(loadedImage);
                }
            });
            holder.text.setText(data.getModule_name());
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

    /**
     * 自定义时钟服务
     */
    ScheduledExecutorService mCalendarService = null;

    @Override
    public void onResume() {
        super.onResume();
        mCalendarService = Executors.newSingleThreadScheduledExecutor();
        mCalendarService.scheduleAtFixedRate(new Runnable() {
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
                    try {
                        if (isVisible())
                            mHandler.sendEmptyMessage(REQUEST_UPDATE_TIME);
                    } catch (Exception ex) {

                    }
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
        mCalendarService.shutdown();
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
        MinaServerHelper helper = MinaServerHelper.instance();
        List<MinaClient> clients = helper.getClients();
        mTxtConnectUser.setText("/当前连接用户数：" + clients.size());
        /*if (client == null || client.getSession() == null) {

        } else {
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
        }*/
    }
}

/*private String getModuleJavaFileName(PadLayoutModule module) {
        String moduleType = module.getModule_type();
        String moduleStyle = module.getModule_style();
        String moduleIndex = module.getModule_index();
        String packageName = mActivity.getPackageName();
        StringBuffer buffer = new StringBuffer();
        buffer.append(packageName).append(".").append("app.");
        //将包名的首字母变成小写
        if (!TextUtils.isEmpty(moduleType)) {
            moduleType = moduleType.toLowerCase();
            buffer.append(moduleType).append(".");
        }
        //将文件名首字母变成大写
        if (moduleStyle != null && moduleStyle.length() > 1) {
            buffer.append(moduleStyle.substring(0, 1).toUpperCase())
                    .append(moduleStyle.substring(1, moduleStyle.length()));
        } else if (moduleStyle != null && moduleStyle.length() == 1){
            buffer.append(moduleStyle.toUpperCase());
        }
        buffer.append("Activity");
        if (!TextUtils.isEmpty(moduleIndex)) {
            buffer.append(moduleIndex);
        }
        log("activity:" + buffer.toString());
        return buffer.toString();
    }*/