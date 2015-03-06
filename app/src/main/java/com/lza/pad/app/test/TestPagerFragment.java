package com.lza.pad.app.test;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.UniversalUtility;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/20/15.
 */
@Deprecated
public class TestPagerFragment extends Fragment {

    int index = 0;
    GridView mGrid;

    PadDeviceInfo mPadDeviceInfo;
    ImageSize mImageSize = new ImageSize(480, 640);
    LayoutInflater mInflater;
    DisplayImageOptions mOptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test_img_oom_item, container, false);
        mGrid = (GridView) view.findViewById(R.id.test_img_oom_grid);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        index = getArguments().getInt("index");
        AppLogger.e("index=" + index);

        //ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(getActivity());
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getActivity())
                .threadPoolSize(5)                      //限定线程池数量
                .memoryCache(new WeakMemoryCache())     //设定缓存类型
                .build();
        ImageLoader.getInstance().init(configuration);

        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .showImageOnLoading(R.drawable.default_ebook_cover)
                .build();

        /*ImageLoader.getInstance().loadImage(imageUrl, mImageSize, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mImageView.setImageBitmap(loadedImage);
            }
        });*/

        //ImageLoader.getInstance().displayImage(imageUrl, mImageView, options);

        //mGrid.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, false));

        mInflater = LayoutInflater.from(getActivity());

        mPadDeviceInfo = new PadDeviceInfo();
        mPadDeviceInfo.setMac_add(UniversalUtility.getMacAddress(getActivity()));
        String url = UrlHelper.getResourcesUrl(mPadDeviceInfo, PadResource.RESOURCE_EBOOK, 4, index + 1);
        RequestHelper.getInstance(getActivity(), url, mResourceListener).send();
    }

    List<PadResource> mResourceDatas;
    SimpleRequestListener<PadResource> mResourceListener = new SimpleRequestListener<PadResource>() {

        @Override
        public ResponseData<PadResource> parseJson(String json) {
            return JsonParseHelper.parseResourceResponse(json);
        }

        @Override
        public void handleRespone(List<PadResource> content) {
            mResourceDatas = content;
            mGrid.setAdapter(new TestAdapter());
        }
    };

    private class TestAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mResourceDatas.size();
        }

        @Override
        public PadResource getItem(int position) {
            return mResourceDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.title_menu_item, null);
            }
            final ViewHolder holder = getHolder(convertView);
            holder.txt.setText(getItem(position).getTitle());
            holder.img.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            String imgUrl = getItem(position).getIco();
            //ImageLoader.getInstance().displayImage(imgUrl, holder.img, mOptions);
            ImageLoader.getInstance().loadImage(imgUrl, mOptions, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.img.setImageBitmap(loadedImage);
                }
            });
            return convertView;
        }
    }

    private ViewHolder getHolder(View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        return holder;
    }

    class ViewHolder {
        TextView txt;
        ImageView img;

        ViewHolder(View view) {
            txt = (TextView) view.findViewById(R.id.title_menu_item_text);
            img = (ImageView) view.findViewById(R.id.title_menu_item_ico);
        }
    }
}
