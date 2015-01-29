package com.lza.pad.app;

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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/4/15.
 */
public class MainApplication extends Application implements Consts {

    private static MainApplication mCtx;

    public static MainApplication getInstance() {
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

    private static final int REQUEST_PERFORM_TOUCH = 0x01;
    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_PERFORM_TOUCH) {
                performTouch();
            }
        }
    };

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

    private class Cor {
        int x, y, type;
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
                        if (cor.x >= 0 && cor.y >= 0) {
                            mStrX = String.valueOf(cor.x);
                            mStrY = String.valueOf(cor.y);
                            int type = cor.type;
                            AppLogger.e("x=" + mStrX + ",y=" + mStrY + ",type=" + type);

                            mMainHandler.sendEmptyMessage(REQUEST_PERFORM_TOUCH);
                            sendScreen(mClient);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendScreen() {
        if (hasClient())
            sendScreen(mClient);
    }

    public void sendScreen(Socket client) {
        if (client == null || client.isClosed()) return;
        try {
            byte[] img = mListener.captureScreen();
            if (img != null) {
                AppLogger.e("截图成功！图片容量:" + img.length);
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                dos.writeInt(img.length);
                dos.write(img);
                dos.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setOnClientListener(OnClientListener listener) {
        this.mListener = listener;
    }

    private OnClientListener mListener = new OnClientListener() {
        @Override
        public byte[] captureScreen() {
            return null;
        }
    };

    public interface OnClientListener {
        byte[] captureScreen();
    }

    public boolean hasClient() {
        return mClient != null;
    }
}
