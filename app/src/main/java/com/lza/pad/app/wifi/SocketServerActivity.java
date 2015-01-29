package com.lza.pad.app.wifi;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.gson.Gson;
import com.lza.pad.R;
import com.lza.pad.helper.GsonHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.RuntimeUtility;
import com.lza.pad.widget.DrawableView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/29.
 */
public class SocketServerActivity extends Activity {

    Button mBtnServer;
    DrawableView mDrawableView;

    ServerSocket mServer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.socket_server);
        mBtnServer = (Button) findViewById(R.id.socket_server_start);
        mDrawableView = (DrawableView) findViewById(R.id.socket_server_drawableview);

        mBtnServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                v.setVisibility(View.GONE);
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
                                Socket client = mServer.accept();
                                new Thread(new ServerThread(client)).start();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                task.execute();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    String mStrX, mStrY;
    private void performTouch() {
        StringBuilder cmd = new StringBuilder();
        cmd.append("input tap ").append(mStrX).append(" ").append(mStrY);
        Log.e("TAG", "command --> " + cmd);
        if (RuntimeUtility.root()) {
            RuntimeUtility.execShellCmd(cmd.toString());
        }
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
                    if (mClient.isClosed()) break;
                    if ((line = mIn.readLine()) != null) {
                        AppLogger.e("收到客户端消息：" + line);
                        PrintStream out = new PrintStream(mClient.getOutputStream());
                        out.println("服务端已收到您的消息");

                        Gson gson = GsonHelper.instance();
                        Cor cor = gson.fromJson(line, Cor.class);
                        if (cor.x >= 0 && cor.y >= 0) {
                            mStrX = String.valueOf(cor.x);
                            mStrY = String.valueOf(cor.y);
                            int type = cor.type;
                            AppLogger.e("x=" + mStrX + ",y=" + mStrY + ",type=" + type);

                            mMainHandler.sendEmptyMessage(REQUEST_PERFORM_TOUCH);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static final int REQUEST_PERFORM_TOUCH = 0x001;
    private Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_PERFORM_TOUCH) {
                performTouch();
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDrawableView.onTouchEvent(event);
    }

    boolean mHasRoot = false;

    private class Cor {
        int x, y, type;
    }
}
