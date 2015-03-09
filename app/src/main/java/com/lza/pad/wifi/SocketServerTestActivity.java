package com.lza.pad.wifi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app.base._BaseActivity;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.ToastUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/28.
 */
public class SocketServerTestActivity extends _BaseActivity {

    TextView mTxtMsg;
    Button mBtnLaunch;

    Handler mMainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.socket_server_layout);
        mTxtMsg = (TextView) findViewById(R.id.socket_server_layout_msg);
        mBtnLaunch = (Button) findViewById(R.id.socket_server_layout_launch);
        mBtnLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initServer();
            }
        });
    }

    ServerSocket mServerSocket;
    boolean mServerFlag = true;
    private void initServer() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mServerSocket = new ServerSocket(8888);
                    AppLogger.e("waiting a connection from the client" + mServerSocket);
                    AppLogger.e("mServerSocket.getInetAddress().toString()=" + mServerSocket.getInetAddress().toString());
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showShort(SocketServerTestActivity.this, "Server端启动成功！");
                        }
                    });

                    while (mServerFlag) {
                        Socket client = mServerSocket.accept();
                        /*String hostAddress = client.getLocalAddress().getHostAddress();
                        String inetAddress = client.getInetAddress().getHostAddress();
                        AppLogger.e("local:" + hostAddress + "| inetAddress" + inetAddress + "|" + client.getRemoteSocketAddress());
                        AppLogger.e("local name:" + client.getLocalAddress().getHostName() + "| inetAddress"
                                + client.getInetAddress().getHostName() + "|" + InetAddress.getLocalHost().getHostAddress());

                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String line;
                        while ((line = in.readLine()) != null) {
                            displayMessage(line);
                        }

                        in.close();*/

                        PrintStream ps = new PrintStream(client.getOutputStream());
                        ps.println("已收到!");
                        ps.close();
                        client.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServerFlag = false;
        try {
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mServerSocket = null;
        }
    }

    private void displayMessage(final String msg) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm]");
                String date = sdf.format(new Date());
                StringBuilder sb = new StringBuilder();
                sb.append("Client");
                sb.append(date);
                sb.append(":");
                sb.append(msg);
                sb.append("\n");
                mTxtMsg.append(sb);
            }
        });
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }
}
