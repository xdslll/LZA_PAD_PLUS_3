package com.lza.pad.app.test;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lza.pad.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/19/15.
 */
public class TestActivity2 extends FragmentActivity {

    //String imageUrl = "http://114.212.7.87/book_center/upload/base_img//%E5%8D%97%E5%A4%A72015%E6%96%B0%E5%B9%B4%E6%B5%B7%E6%8A%A5.jpg";

    ViewPager mViewPager;
    ImageView mImageView;

    TextView mTxtPage;

    int size = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_img_oom);
        mViewPager = (ViewPager) findViewById(R.id.test_img_oom_viewpager);
        mImageView = (ImageView) findViewById(R.id.test_img_oom_image);
        mTxtPage = (TextView) findViewById(R.id.test_img_oom_page);

        mViewPager.setAdapter(new TestFragmentAdapter(getSupportFragmentManager()));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTxtPage.setText("第" + (position % size + 1) + "页");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        timer();
    }

    class TestFragmentAdapter extends FragmentPagerAdapter {

        public TestFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            int pos = position % size;
            TestPagerFragment fragment = new TestPagerFragment();
            Bundle arg = new Bundle();
            arg.putInt("index", pos);
            fragment.setArguments(arg);
            return fragment;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }
    }

    private void timer() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mMainHandler.sendEmptyMessage(REQUEST_VIEWPAGER_NEXT);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public static final int REQUEST_VIEWPAGER_NEXT = 0x01;
    Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_VIEWPAGER_NEXT) {
                int page = mViewPager.getCurrentItem();
                int next = (page + 1) % size;
                mViewPager.setCurrentItem(next, true);
            }
        }
    };
}
