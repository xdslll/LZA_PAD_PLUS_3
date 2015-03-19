package com.lza.pad.fragment.guide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lza.pad.R;
import com.lza.pad.fragment.base._BaseFragment;
import com.lza.pad.widget.LibraryMap;
import com.lza.pad.widget.panoramic.shadow.ball.PanoramicActivity;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/24.
 */
public class LibraryMapFragment1 extends _BaseFragment {

    LibraryMap mLibraryMap;
    ImageView mLibraryImg;
    View mLibraryImgBg;

    int mCurrentIndex;
    String mCurrentMapTitle, mCurrentMapFunc;

    Handler mHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.library_map, container, false);
        mLibraryMap = (LibraryMap) view.findViewById(R.id.library_map_map);
        mLibraryImg = (ImageView) view.findViewById(R.id.library_map_img);
        mLibraryImgBg = view.findViewById(R.id.library_map_img_bg);

        mLibraryMap.setOnCorClickListener(new LibraryMap.OnCorClickListener() {
            @Override
            public void onCorClick(int index, float x, float y) {

                if (index == 0) {
                    mLibraryImg.setImageResource(R.drawable.test_library_thumb1);
                    mCurrentMapTitle = "凯旋门";
                    mCurrentMapFunc = "订票";
                } else if (index == 1) {
                    mLibraryImg.setImageResource(R.drawable.test_library_thumb3);
                    mCurrentMapTitle = "露天停车场";
                    mCurrentMapFunc = "预约车位";
                } else if (index == 2) {
                    mLibraryImg.setImageResource(R.drawable.test_library_thumb2);
                    mCurrentMapTitle = "内部场景";
                    mCurrentMapFunc = "预约座位";
                } else if (index == 3) {
                    mLibraryImg.setImageResource(R.drawable.test_library_thumb5);
                    mCurrentMapTitle = "长廊";
                    mCurrentMapFunc = "";
                }

                mLibraryImg.setVisibility(View.VISIBLE);
                mLibraryImgBg.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.FlipInX)
                        .duration(500)
                        .playOn(mLibraryImg);
                YoYo.with(Techniques.FlipInX)
                        .duration(500)
                        .playOn(mLibraryImgBg);

                mCurrentIndex = index;
            }

            @Override
            public void onNoneClick() {
                if (mLibraryImg.getVisibility() == View.VISIBLE || mLibraryImgBg.getVisibility() == View.VISIBLE) {
                    YoYo.with(Techniques.FlipOutX)
                            .duration(500)
                            .playOn(mLibraryImg);
                    YoYo.with(Techniques.FlipOutX)
                            .duration(500)
                            .playOn(mLibraryImgBg);

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLibraryImg.setVisibility(View.INVISIBLE);
                            mLibraryImgBg.setVisibility(View.INVISIBLE);
                        }
                    }, 500);

                } else {
                    mLibraryMap.corAnimAll();
                }
            }
        });
        mLibraryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, PanoramicActivity.class);
                intent.putExtra(KEY_MAP_INDEX, mCurrentIndex);
                intent.putExtra(KEY_MAP_TITLE, mCurrentMapTitle);
                intent.putExtra(KEY_MAP_FUNC_TEXT, mCurrentMapFunc);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLibraryImg != null && mLibraryImg.getVisibility() == View.VISIBLE)
            mLibraryImg.setVisibility(View.INVISIBLE);
        if (mLibraryImgBg != null && mLibraryImgBg.getVisibility() == View.VISIBLE)
            mLibraryImgBg.setVisibility(View.INVISIBLE);
    }
}
