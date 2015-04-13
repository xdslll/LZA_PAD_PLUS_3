package com.lza.pad.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/16/15.
 */
public final class ImageHelper {

    private static ImageHelper sInstance;
    private DisplayImageOptions mDefaultOptions;

    int DEFALUT_THREAD_POOL_SIZE = 3;
    int DEFAULT_LRU_MEMORY_CACHE_SIZE = 10;

    private ImageHelper(Context c) {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(c)
                .threadPoolSize(DEFALUT_THREAD_POOL_SIZE)
                .memoryCache(new LruMemoryCache(DEFAULT_LRU_MEMORY_CACHE_SIZE))
                //.writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(configuration);

        mDefaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();
    }

    public synchronized static ImageHelper getInstance(Context c) {
        if (sInstance == null) {
            sInstance = new ImageHelper(c);
        }
        return sInstance;
    }

    public void displayImage(String url, ImageView img) {
        ImageLoader.getInstance().displayImage(url, img, mDefaultOptions);
    }

    public void displayImage(String url, ImageView img, DisplayImageOptions options) {
        ImageLoader.getInstance().displayImage(url, img, options);
    }

    public void loadImage(String url, ImageLoadingListener listener) {
        ImageLoader.getInstance().loadImage(url, mDefaultOptions, listener);
    }

    public void loadImage(String url, ImageSize size, ImageLoadingListener listener) {
        ImageLoader.getInstance().loadImage(url, size, mDefaultOptions, listener);
    }

    public void loadImage(String url, ImageSize size, DisplayImageOptions options, ImageLoadingListener listener) {
        ImageLoader.getInstance().loadImage(url, size, options, listener);
    }

    public ImageLoader getImageLoader() {
        return ImageLoader.getInstance();
    }

    public void clearDiskCache() {
        ImageLoader.getInstance().clearDiskCache();
    }

}
