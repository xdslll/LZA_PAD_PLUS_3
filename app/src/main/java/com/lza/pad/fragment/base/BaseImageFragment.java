package com.lza.pad.fragment.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/20/15.
 */
public class BaseImageFragment extends BaseFragment {

    protected DisplayImageOptions mOptions;
    protected LayoutInflater mInflater;
    protected ImageLoader mImageLoader;

    protected int DEFALUT_THREAD_POOL_SIZE = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(mActivity);

        DEFALUT_THREAD_POOL_SIZE = getImageThreadPoolSize();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getActivity())
                .threadPoolSize(DEFALUT_THREAD_POOL_SIZE)                      //限定线程池数量
                .memoryCache(new WeakMemoryCache())     //设定内存缓存类型为弱引用
                .build();
        ImageLoader.getInstance().init(configuration);

        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();

        mImageLoader = ImageLoader.getInstance();
    }

    protected int getImageThreadPoolSize() {
        return DEFALUT_THREAD_POOL_SIZE;
    }

    protected void displayImage(String url, ImageView img) {
        mImageLoader.displayImage(url, img, mOptions);
    }
}
