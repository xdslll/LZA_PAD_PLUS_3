package com.lza.pad.app.socket.service;

import com.lza.pad.app.socket.admin.server.MinaServerAdmin;
import com.lza.pad.app.socket.admin.server.ServerMessageHandler;
import com.lza.pad.app.socket.model.MinaClient;
import com.lza.pad.app.socket.model.MinaServer;
import com.lza.pad.helper.GsonHelper;
import com.lza.pad.support.debug.AppLogger;

import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/5.
 */
public class MinaServiceHelper {

    private static MinaServiceHelper sInstance;

    public static MinaServiceHelper instance() {
        if (sInstance == null) sInstance = new MinaServiceHelper();
        return sInstance;
    }

    private MinaServiceHelper() {
        this("南京图书馆大厅大屏");
    }

    private MinaServiceHelper(String name) {
        mMinaServerAdmin = new MinaServerAdmin();
        mMinaServer = new MinaServer();
        mMinaServer.setName(name);
    }

    private MinaServer mMinaServer;
    private MinaServerAdmin mMinaServerAdmin;
    private MinaServerStatus mMinaServerStatus = MinaServerStatus.INIT;
    private List<MinaClient> mMinaClients = new ArrayList<MinaClient>();

    public boolean startMinaServer() {
        AppLogger.e("[" + Thread.currentThread().getName() + "]:MinaServiceHelper startMinaServer");
        boolean flag;
        mMinaServerStatus = MinaServerStatus.STARTING;
        flag = mMinaServerAdmin.start();
        if (flag) {
            mMinaServerStatus = MinaServerStatus.STARTED;
        } else {
            setStatus();
        }
        return flag;
    }

    public boolean stopMinaServer() {
        AppLogger.e("[" + Thread.currentThread().getName() + "]:MinaServiceHelper stopMinaServer");
        boolean flag;
        mMinaServerStatus = MinaServerStatus.CLOSING;
        flag = mMinaServerAdmin.stop();
        if (flag) {
            mMinaServerStatus = MinaServerStatus.CLOSED;
        } else {
            setStatus();
        }
        return flag;
    }

    private void setStatus() {
        if (mMinaServerAdmin.isActive()) {
            mMinaServerStatus = MinaServerStatus.STARTED;
        } else if (mMinaServerAdmin.isDisposed()) {
            mMinaServerStatus = MinaServerStatus.CLOSED;
        } else if (mMinaServerAdmin.isDisposing()) {
            mMinaServerStatus = MinaServerStatus.CLOSING;
        } else {
            mMinaServerStatus = MinaServerStatus.UNKNOWN;
        }
    }

    public boolean isStarted() {
        return mMinaServerStatus == MinaServerStatus.STARTED;
    }

    public boolean isClosed() {
        return mMinaServerStatus == MinaServerStatus.CLOSED;
    }

    public enum MinaServerStatus {
        INIT,
        STARTING,
        STARTED,
        CLOSED,
        CLOSING,
        UNKNOWN
    }

    public void setOnServerIoAdapter(ServerMessageHandler.OnServerIoListener listener) {
        mMinaServerAdmin.setOnServerIoListener(listener);
    }

    public void send(IoSession session, String status, String message) {
        mMinaServer.setSession(session);
        mMinaServer.setStatus(status);
        mMinaServer.setMessage(message);
        String json = GsonHelper.buildExpose().toJson(mMinaServer, MinaServer.class);
        mMinaServer.getSession().write(json);
    }

    public boolean addClient(MinaClient client) {
        if (!mMinaClients.contains(client)) return mMinaClients.add(client);
        return false;
    }

    public List<MinaClient> getClients() {
        return mMinaClients;
    }
}
