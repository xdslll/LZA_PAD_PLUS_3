package com.lza.pad.app.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.lza.pad.R;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_img_oom);
        mViewPager = (ViewPager) findViewById(R.id.test_img_oom_viewpager);
        mImageView = (ImageView) findViewById(R.id.test_img_oom_image);

        mViewPager.setAdapter(new TestFragmentAdapter(getSupportFragmentManager()));
    }

    class TestFragmentAdapter extends FragmentPagerAdapter {

        public TestFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            TestPagerFragment fragment = new TestPagerFragment();
            Bundle arg = new Bundle();
            arg.putInt("index", position);
            fragment.setArguments(arg);
            return fragment;
        }

        @Override
        public int getCount() {
            return 20;
        }
    }
}
