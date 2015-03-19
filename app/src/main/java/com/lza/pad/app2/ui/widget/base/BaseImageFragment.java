package com.lza.pad.app2.ui.widget.base;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.lza.pad.helper.ImageHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/20/15.
 */
public class BaseImageFragment extends BaseFragment {

    protected LayoutInflater mInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected void displayImage(String url, ImageView img) {
        ImageHelper.getInstance(mActivity).displayImage(url, img);
    }

    protected void displayImage(String url, ImageView img, DisplayImageOptions options) {
        ImageHelper.getInstance(mActivity).displayImage(url, img, options);
    }

    protected void loadImage(String url, ImageSize size, ImageLoadingListener listener) {
        ImageHelper.getInstance(mActivity).loadImage(url, size, listener);
    }

    protected void loadImage(String url, ImageSize size, DisplayImageOptions options, ImageLoadingListener listener) {
        ImageHelper.getInstance(mActivity).loadImage(url, size, options, listener);
    }

    protected void loadImage(String url, ImageLoadingListener listener) {
        ImageHelper.getInstance(mActivity).loadImage(url, listener);
    }

    protected ImageLoader getImageLoader() {
        return ImageHelper.getInstance(mActivity).getImageLoader();
    }

    protected BitmapDrawable getBitmapDrawable(int resId) {
        try {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), resId);
            return new BitmapDrawable(getResources(), bm);
        } catch (Exception ex) {
            return null;
        }
    }

    protected BitmapDrawable getBitmapDrawable(Bitmap bm) {
        return new BitmapDrawable(getResources(), bm);
    }
}
