package com.lza.pad.app2.ui.widget;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.lza.pad.R;
import com.lza.pad.app2.ui.widget.base.BaseFragment;

import java.io.File;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/6/15.
 */
public class VideoFragment extends BaseFragment {

    private MediaController mMediaController;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video, container, false);
        TextView text = (TextView) view.findViewById(R.id.video_title);
        text.setVisibility(View.VISIBLE);
        text.setText(mPadModuleWidget.getLabel());

        final VideoView videoView = (VideoView) view.findViewById(R.id.video_layout);
        File dir = Environment.getExternalStorageDirectory();
        File videoFile = new File(dir, "Download/1234abcd_1234abcd.mp4");
        if (!videoFile.exists()) {
            videoFile = new File(dir, "1234abcd_1234abcd.mp4");
        }
        final String path = videoFile.getAbsolutePath();
        if (videoFile.exists()) {
            mMediaController = new MediaController(getActivity());
            videoView.setVideoPath(path);
            videoView.setMediaController(mMediaController);
            mMediaController.setMediaPlayer(videoView);
            videoView.start();
            //循环播放
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    videoView.setVideoPath(path);
                    videoView.start();
                }
            });
        }
        return view;
    }
}
