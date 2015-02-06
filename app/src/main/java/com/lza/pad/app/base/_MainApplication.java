package com.lza.pad.app.base;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.lza.pad.helper.GsonHelper;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.RuntimeUtility;
import com.lza.pad.support.utils.ToastUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/4/15.
 */
@Deprecated
public class _MainApplication extends Application implements Consts {

    private static _MainApplication mCtx;

    public static _MainApplication getInstance() {
        return mCtx;
    }

    public static String DEFAULT_URL = "http://114.212.7.87/book_center/interface.cx?";

    public String getUrl() {
        return RuntimeUtility.getFromSP(this, KEY_URL, DEFAULT_URL);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mCtx = this;
        startServer();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RequestHelper.releaseService();
        stopServer();
    }

    /**
     * 测试远程控制
     */
    private static ServerSocket mServer = null;

    private void stopServer() {
        try {
            if (mServer != null) {
                mServer.close();
                AppLogger.e("服务端关闭成功！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mServer = null;
        }
    }

    Socket mClient = null;

    private void startServer() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (mServer == null) {
                        mServer = new ServerSocket(8888);
                        AppLogger.e("服务端启动成功！端口号：8888");
                    }
                    while (true) {
                        if (mServer == null || mServer.isClosed()) break;
                        //Socket client = mServer.accept();
                        //new Thread(new ServerThread(client)).start();
                        mClient = mServer.accept();
                        AppLogger.e("客户端连接成功！");
                        //mMainHandler.sendEmptyMessage(REQUEST_SEND_SCREEN);
                        mIfSendScreen = true;
                        //startSendScreenTask();
                        new Thread(new ServerThread(mClient)).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
    }

    String mKey;
    private static final int REQUEST_PERFORM_TOUCH = 0x01;
    private static final int REQUEST_SEND_SCREEN = 0x02;
    private static final int REQUEST_PERFORM_KEY = 0x03;
    private static final int REQUEST_PERFORM_SHAKE = 0x04;
    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_PERFORM_TOUCH) {
                performTouch();
            } else if (msg.what == REQUEST_SEND_SCREEN) {
                sendScreen();
            } else if (msg.what == REQUEST_PERFORM_KEY) {
                performKey();
            } else if (msg.what == REQUEST_PERFORM_SHAKE) {
                performShake();
            }
        }
    };

    public void sendScreen() {
        byte[] img = mScreenListener.captureScreen();
        sendFileToClient(mClient, img, TYPE_CAPTURE_SCREEN);
    }

    private void performShake() {
        if (mSensorShakeListener != null) {
            File file = mSensorShakeListener.onShake();
            sendFileToClient(mClient, file, TYPE_SHAKE);
        }
    }

    private void performKey() {
        String cmd = "input keyevent " + mKey;
        Log.e("TAG", "command --> " + cmd);
        if (isRoot()) {
            execShellCmd(cmd);
        } else {
            ToastUtils.showShort(this, "Root失败！");
        }
    }

    String mStrX, mStrY;
    private void performTouch() {
        StringBuilder cmd = new StringBuilder();
        cmd.append("input tap ").append(mStrX).append(" ").append(mStrY);
        Log.e("TAG", "command --> " + cmd);
        if (isRoot()) {
            execShellCmd(cmd.toString());
        } else {
            ToastUtils.showShort(this, "Root失败！");
        }
    }

    boolean mHasRoot = false;

    private boolean isRoot() {
        try {
            if (!mHasRoot) {
                //获取Root权限
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                os.writeBytes("exit\n");
                os.flush();
                int exitValue = process.waitFor();
                if (exitValue == 0) {
                    mHasRoot = true;
                }
                process.destroy();
            }
        } catch (IOException e) {
            e.printStackTrace();
            mHasRoot = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            mHasRoot = false;
        }
        return mHasRoot;
    }

    private void execShellCmd(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ScheduledExecutorService mService;
    private void startSendScreenTask() {
        mService = Executors.newSingleThreadScheduledExecutor();
        mService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (mIfSendScreen) {
                    mMainHandler.sendEmptyMessage(REQUEST_SEND_SCREEN);
                } else {
                    mService.shutdownNow();
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    private class ServerThread implements Runnable {

        Socket mClient;
        BufferedReader mIn;

        private ServerThread(Socket client) throws IOException {
            this.mClient = client;
            mIn = new BufferedReader(new InputStreamReader(mClient.getInputStream()));
        }

        @Override
        public void run() {
            try {
                String line;
                while (true) {
                    if ((line = mIn.readLine()) != null) {
                        AppLogger.e("收到客户端消息：" + line);
                        //PrintStream out = new PrintStream(mClient.getOutputStream());
                        //out.println("服务端已收到您的消息");

                        Gson gson = GsonHelper.instance();
                        Cor cor = gson.fromJson(line, Cor.class);
                        int type = cor.type;
                        AppLogger.e("x=" + mStrX + ",y=" + mStrY + ",type=" + type);
                        if (type == TYPE_TOUCH) {
                            if (cor.x >= 0 && cor.y >= 0) {
                                mStrX = String.valueOf(cor.x);
                                mStrY = String.valueOf(cor.y);
                                mMainHandler.sendEmptyMessage(REQUEST_PERFORM_TOUCH);
                            }
                        } else if (type == TYPE_KEY) {
                            mKey = String.valueOf(cor.key);
                            mMainHandler.sendEmptyMessage(REQUEST_PERFORM_KEY);
                        } else if (type == TYPE_SHAKE) {
                            mKey = String.valueOf(cor.key);
                            mMainHandler.sendEmptyMessage(REQUEST_PERFORM_SHAKE);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                mIfSendScreen = false;
            }
        }
    }

    /*public void sendScreen() {
        if (hasClient())
            sendScreen(mClient);
    }*/

    boolean mIfSendScreen = true;

    public void sendScreen(Socket client) {
        if (client == null || client.isClosed() || !client.isConnected()) {
            mIfSendScreen = false;
            return;
        }
        try {
            byte[] img = mScreenListener.captureScreen();
            if (img != null) {
                AppLogger.e("截图成功！图片容量:" + img.length);
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                dos.writeInt(img.length);
                dos.write(img);
                dos.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            mIfSendScreen = false;
        }
    }

    public void sendFileToClient(Socket client, byte[] data, int type) {
        if (client == null || client.isClosed()) return;
        if (data == null || data.length == 0) return;
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(client.getOutputStream());
            dos.writeInt(type);
            dos.writeUTF("文件");
            dos.writeLong(data.length);
            dos.write(data);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendFileToClient(Socket client, File file, int type) {
        if (client == null || client.isClosed()) return;
        if (file == null || !file.exists()) return;
        DataOutputStream dos = null;
        FileInputStream fis = null;
        int size = 1024 * 100;
        byte[] temp = new byte[size];
        int length;
        try {
            AppLogger.e("文件路径：" + file.getAbsolutePath());

            dos = new DataOutputStream(client.getOutputStream());
            fis = new FileInputStream(file);

            dos.writeInt(type);
            dos.writeUTF(file.getName());
            dos.writeLong(file.length());

            while ((length = fis.read(temp, 0, temp.length)) > 0) {
                dos.write(temp, 0, length);
                dos.flush();
            }

            AppLogger.e("文件[" + file.getName() + "]发送成功！");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface OnSensorShakeListener {
        File onShake();
    }

    public interface OnCaptureScreenListener {
        byte[] captureScreen();
    }

    private OnSensorShakeListener mSensorShakeListener = new OnSensorShakeListener() {
        @Override
        public File onShake() {
            return null;
        }
    };

    public void setOnSensorShakeListener(OnSensorShakeListener listener) {
        this.mSensorShakeListener = listener;
    }

    public void unRegisterSensorShake() {
        this.mSensorShakeListener = null;
    }

    private OnCaptureScreenListener mScreenListener = new OnCaptureScreenListener() {
        @Override
        public byte[] captureScreen() {
            return null;
        }
    };

    public void setOnCaptureScreenListener(OnCaptureScreenListener listener) {
        this.mScreenListener = listener;
    }

    public boolean hasClient() {
        return mClient != null;
    }

    private static final int TYPE_TOUCH = 1;
    private static final int TYPE_KEY = 2;
    private static final int TYPE_SHAKE = 3;
    private static final int TYPE_CAPTURE_SCREEN = 4;

    private class Cor {
        int x, y, type, key;
    }
}
