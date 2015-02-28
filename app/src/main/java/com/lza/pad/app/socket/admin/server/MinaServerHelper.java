package com.lza.pad.app.socket.admin.server;

import com.lza.pad.app.socket.model.MinaClient;
import com.lza.pad.app.socket.model.MinaServer;
import com.lza.pad.db.model.pad.PadResource;
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
public class MinaServerHelper {

    private static MinaServerHelper sInstance;

    public static MinaServerHelper instance() {
        if (sInstance == null) sInstance = new MinaServerHelper();
        return sInstance;
    }

    private MinaServerHelper() {
        mMinaServerAdmin = new MinaServerAdmin();
    }

    /*private MinaServerHelper(PadDeviceInfo deviceInfo) {
        this(deviceInfo.getName());
    }

    private MinaServerHelper(String name) {
        mMinaServerAdmin = new MinaServerAdmin();
        //server = new MinaServer();
        //server.setName(name);
    }*/

    //private MinaServer server;
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

    public void sendFile(IoSession session, PadResource data) {
        MinaServer server = new MinaServer();
        server.setSession(session);
        server.setStatus(MinaServer.STATUS_OK);
        server.setAction(MinaServer.ACTION_SEND_FILE);
        server.setData(data);
        String json = GsonHelper.buildExpose().toJson(server, MinaServer.class);
        server.getSession().write(json);
    }

    public void sendFile(IoSession session, String filePath, long fileLength) {
        MinaServer server = new MinaServer();
        server.setSession(session);
        server.setStatus(MinaServer.STATUS_OK);
        server.setAction(MinaServer.ACTION_SEND_FILE_READY);
        server.setFileName(filePath);
        server.setFileLength(fileLength);
        server.setData(null);
        String json = GsonHelper.buildExpose().toJson(server, MinaServer.class);
        server.getSession().write(json);
    }

    public void sendOK(IoSession session, String action) {
        MinaServer server = new MinaServer();
        server.setSession(session);
        server.setStatus(MinaServer.STATUS_OK);
        server.setAction(action);
        String json = GsonHelper.buildExpose().toJson(server, MinaServer.class);
        server.getSession().write(json);
    }

    public void sendFailed(IoSession session, String action, String message) {
        MinaServer server = new MinaServer();
        server.setSession(session);
        server.setStatus(MinaServer.STATUS_FAILED);
        server.setAction(action);
        server.setMessage(message);
        String json = GsonHelper.buildExpose().toJson(server, MinaServer.class);
        server.getSession().write(json);
    }

    public void send(IoSession session, String status, String message) {
        MinaServer server = new MinaServer();
        server.setSession(session);
        server.setStatus(status);
        server.setMessage(message);
        String json = GsonHelper.buildExpose().toJson(server, MinaServer.class);
        server.getSession().write(json);
    }

    public boolean addClient(MinaClient client) {
        if (!mMinaClients.contains(client)) return mMinaClients.add(client);
        return false;
    }

    public boolean removeClient(MinaClient client) {
        if (mMinaClients.contains(client)) {
            return mMinaClients.remove(client);
        }
        return false;
    }

    public void removeClient(IoSession session) {
        for (MinaClient client : mMinaClients) {
            if (client.getSession().equals(session)) {
                mMinaClients.remove(client);
            }
        }
    }

    public List<MinaClient> getClients() {
        return mMinaClients;
    }
}
