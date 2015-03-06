package com.lza.pad.app.socket;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lza.pad.R;
import com.lza.pad.app.base._BaseActivity;
import com.lza.pad.app.socket.admin.server.MinaServerAdmin;
import com.lza.pad.app.socket.admin.server.OnServerIoAdapter;
import com.lza.pad.app.socket.admin.server.ServerMessageHandler;
import com.lza.pad.app.socket.model.MinaClient;
import com.lza.pad.app.socket.model.MinaServer;
import com.lza.pad.helper.GsonHelper;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/4.
 */
public class MinaServerActivity extends _BaseActivity {
    Button mBtnStartServer, mBtnStopServer;
    TextView mTxtLogMessage;

    MinaServerAdmin mMinaServerAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mina_server);
        mBtnStartServer = (Button) findViewById(R.id.server_start);
        mBtnStopServer = (Button) findViewById(R.id.server_close);
        mTxtLogMessage = (TextView) findViewById(R.id.server_message);

        mBtnStopServer.setEnabled(false);

        mBtnStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMinaServer();
            }
        });

        mBtnStopServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMinaServer();
            }
        });

        mMinaServerAdmin = new MinaServerAdmin();
        mMinaServerAdmin.setOnServerIoListener(mListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMinaServer();
    }

    private static final int REQUEST_START_SERVER_SUCCESSFULLY = 0x01;
    private static final int REQUEST_START_SERVER_FAILED = 0x02;
    private static final int REQUEST_STOP_SERVER_SUCCESSFULLY = 0x03;
    private static final int REQUEST_STOP_SERVER_FAILED = 0x04;

    Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_START_SERVER_SUCCESSFULLY) {
                mBtnStartServer.setEnabled(false);
                mBtnStopServer.setEnabled(true);
                mTxtLogMessage.append(wrap("服务启动成功！"));
                mMinaServer = new MinaServer();
                mMinaServer.setName("南京大学（鼓楼校区）图书馆大厅大屏");
            } else if (msg.what == REQUEST_START_SERVER_FAILED) {
                mTxtLogMessage.append(wrap("服务启动失败！"));
            } else if (msg.what == REQUEST_STOP_SERVER_SUCCESSFULLY) {
                mBtnStopServer.setEnabled(false);
                mBtnStartServer.setEnabled(true);
                mTxtLogMessage.append(wrap("服务关闭成功！"));
            } else if (msg.what == REQUEST_STOP_SERVER_FAILED) {
                mTxtLogMessage.append(wrap("服务关闭失败！"));
            }
        }
    };

    public void appendLog(final String msg) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mTxtLogMessage.append(wrap(msg));
            }
        });
    }

    public String wrap(String msg) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss ");
        String prefix = sdf.format(new Date());
        StringBuffer sb = new StringBuffer();
        sb.append(prefix);
        sb.append(msg);
        sb.append("\n");
        return sb.toString();
    }

    public void startMinaServer() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                return mMinaServerAdmin.start();
            }

            @Override
            protected void onPostExecute(Boolean ret) {
                if (ret) {
                    mMainHandler.sendEmptyMessage(REQUEST_START_SERVER_SUCCESSFULLY);
                } else {
                    mMainHandler.sendEmptyMessage(REQUEST_START_SERVER_FAILED);
                }
            }
        }.execute();
    }

    public void stopMinaServer() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                return mMinaServerAdmin.stop();
            }

            @Override
            protected void onPostExecute(Boolean ret) {
                if (ret) {
                    mMainHandler.sendEmptyMessage(REQUEST_STOP_SERVER_SUCCESSFULLY);
                } else {
                    mMainHandler.sendEmptyMessage(REQUEST_STOP_SERVER_FAILED);
                }
            }
        }.execute();
    }


    public ServerMessageHandler.OnServerIoListener mListener = new OnServerIoAdapter() {

        @Override
        public void onSessionCreated(IoSession session) {
            appendLog("创建一个新连接：" + session.getRemoteAddress());
        }

        @Override
        public void onSessionOpened(IoSession session) {
            appendLog("打开一个连接：" + session.getRemoteAddress() + ",BothIdleCount:" + session.getBothIdleCount());
        }

        @Override
        public void onSessionClosed(IoSession session) {
            appendLog("关闭当前session:" + session.getId() + ",ip:" + session.getRemoteAddress());
        }

        @Override
        public void onSessionIdle(IoSession session, IdleStatus status) {
            appendLog("当前连接[" + session.getRemoteAddress() + "]处于空闲状态，" + status.toString());
        }

        @Override
        public void onExceptionCaught(IoSession session, Throwable cause) {
            appendLog("服务器发生异常：" + cause.getCause() + "," + cause.getMessage());
        }

        @Override
        public void onMessageReceived(IoSession session, Object message) {
            appendLog("服务器接收到[" + session.getRemoteAddress() + "]消息：" + message);
            /* 处理业务逻辑 */
            Gson gson = GsonHelper.instance();
            try {
                MinaClient client = gson.fromJson(message.toString(), MinaClient.class);
                client.setSession(session);
                if (!mMinaClients.contains(client))
                    mMinaClients.add(client);
                if (client.getAction().equals(MinaClient.ACTION_CONNECT)) {
                    send(session, MinaServer.STATUS_OK, "服务端已接受您的连接请求");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onMessageSent(IoSession session, Object message) {
            appendLog("服务器向[" + session.getRemoteAddress() + "]发送消息：" + message);
        }
    };

    List<MinaClient> mMinaClients = new ArrayList<MinaClient>();
    MinaServer mMinaServer;

    private void send(IoSession session, String status, String message) {
        mMinaServer.setSession(session);
        mMinaServer.setStatus(status);
        mMinaServer.setMessage(message);
        String json = GsonHelper.buildExpose().toJson(mMinaServer, MinaServer.class);
        mMinaServer.getSession().write(json);
    }
}
