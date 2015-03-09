package com.lza.pad.wifi.admin;

import android.os.Looper;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/26.
 */
@Deprecated
public abstract class _WifiTimerCheck {

    private int mCount = 0;
    private int mTimeoutCount = 1;
    private int mSleepTime = 1000;
    private boolean mExitFlag = false;
    private Thread mThread = null;

    public abstract void onTimerCheck();

    public abstract void onTimeout();

    public _WifiTimerCheck() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                while (!mExitFlag) {
                    mCount++;
                    if (mCount < mTimeoutCount) {
                        onTimerCheck();
                        try {
                            Thread.sleep(mSleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            exit();
                        }
                    } else {
                        onTimeout();
                    }
                }
                Looper.loop();
            }
        });
    }

    public void start(int timeoutCount, int sleepTime) {
        mTimeoutCount = timeoutCount;
        mSleepTime = sleepTime;

        mThread.start();
    }

    public void exit() {
        mExitFlag = true;
    }

}
