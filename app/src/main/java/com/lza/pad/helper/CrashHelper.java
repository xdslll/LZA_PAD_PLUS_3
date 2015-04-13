package com.lza.pad.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.lza.pad.app.socket.admin.server.MinaServerHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.ToastUtils;

/**
 * 捕获全局异常事件
 *
 * @author xiads
 * @Date 10/27/14.
 */
public class CrashHelper implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashHelper mCrashHelper;
    private Context mContext;

    private CrashHelper(Context context) {
        this.mContext = context;
    }

    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public static CrashHelper getInstance(Context context) {
        if (mCrashHelper == null) {
            mCrashHelper = new CrashHelper(context);
        }
        return mCrashHelper;
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        if(!handleException(ex) && mDefaultHandler != null){
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }else{
            //关闭MinaServer
            AppLogger.e("程序崩溃,正在关闭MinaServer...");
            MinaServerHelper.instance().stopMinaServer();
            //等待3秒后重新启动程序
            try {
                Thread.sleep(3000);
                ex.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.setClassName("com.lza.pad", "app2.ui.device.DeviceAuthorityActivity");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            PendingIntent restartIntent = PendingIntent.getActivity(
                    mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //退出程序
            AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                    restartIntent); // 1秒钟后重启应用

            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        /*new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                ToastUtils.showLong(mContext,
                        "很抱歉，程序出现异常，即将被您重启程序。异常原因：" + ex.getMessage());
                ex.printStackTrace();
                Looper.loop();
            }
        }.start();*/
        new Handler(Looper.myLooper()).post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showLong(mContext,
                        "很抱歉，程序出现异常，即将被您重启程序。异常原因：" + ex.getMessage());
                ex.printStackTrace();
            }
        });
        return true;
    }
}
