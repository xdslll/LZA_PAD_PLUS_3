package com.lza.pad.app2.ui.widget;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app2.event.base.OnClickListener;
import com.lza.pad.app2.ui.widget.base.BaseImageFragment;
import com.lza.pad.db.model.pad.PadModuleType;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/13/15.
 */
public class GuideFragment extends BaseImageFragment {

    ImageView mImageView;
    ViewStub mViewStub;
    LinearLayout mLayoutLoading;
    TextView mTxtLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_imageview, container, false);
        mImageView = (ImageView) view.findViewById(R.id.common_img);
        mViewStub = (ViewStub) view.findViewById(R.id.common_img_viewstub);
        mViewStub.inflate();
        if (mLayoutLoading == null) {
            mLayoutLoading = (LinearLayout) view.findViewById(R.id.common_loading_layout);
        }
        if (mTxtLoading == null) {
            mTxtLoading = (TextView) view.findViewById(R.id.common_loading_layout_text);
        }
        if (parseInt(mPadModuleType.getType()) == PadModuleType.MODULE_TYPE_GUIDE) {
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    launchHomeModule();
                }
            });
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingImageView();
    }

    private void loadingImageView() {
        //确定图片尺寸
        int w = mPadWidgetLayout.getWidget_width();
        int h = mPadWidgetLayout.getWidget_height();
        ImageSize size = new ImageSize(w, h);
        //确定图片链接
        String url = mPadWidgetData.getUrl();
        if (isEmpty(url)) return;
        loadImage(url, size, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mLayoutLoading.setVisibility(View.VISIBLE);
                mTxtLoading.setText("图片加载失败，正在重试...");
                loadingImageView();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mImageView.setImageBitmap(loadedImage);
                mLayoutLoading.setVisibility(View.GONE);
            }
        });
    }

}
