package com.lza.pad.app.socket.admin.server;

import com.lza.pad.support.debug.AppLogger;

import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/4.
 */
public class ServerMessageHandler implements IoHandler {

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        AppLogger.e("创建一个新连接：" + session.getRemoteAddress());
        //session.write("欢迎连接服务端！");
        listener.onSessionCreated(session);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        AppLogger.e("打开一个连接：" + session.getRemoteAddress() + "," + session.getBothIdleCount());
        listener.onSessionOpened(session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        AppLogger.e("关闭当前session:" + session.getId() + ",ip:" + session.getRemoteAddress());

        CloseFuture closeFuture = session.close(true);
        closeFuture.addListener(new IoFutureListener<IoFuture>() {
            @Override
            public void operationComplete(IoFuture future) {
                if (future instanceof CloseFuture) {
                    ((CloseFuture) future).setClosed();
                    AppLogger.e("Session[" + future.getSession().getId() + "] setClosed()");
                }
            }
        });
        listener.onSessionClosed(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        AppLogger.e("当前连接[" + session.getRemoteAddress() + "]处于空闲状态，" + status.toString());
        listener.onSessionIdle(session, status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        AppLogger.e("服务器发生异常：" + cause.getCause() + "," + cause.getMessage());
        listener.onExceptionCaught(session, cause);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        AppLogger.e("服务器接收到数据：" + message);
        listener.onMessageReceived(session, message);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        AppLogger.e("服务器发送消息：" + message);
        listener.onMessageSent(session, message);
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {

    }

    public interface OnServerIoListener {
        void onSessionCreated(IoSession session);
        void onSessionOpened(IoSession session);
        void onSessionClosed(IoSession session);
        void onSessionIdle(IoSession session, IdleStatus status);
        void onExceptionCaught(IoSession session, Throwable cause);
        void onMessageReceived(IoSession session, Object message);
        void onMessageSent(IoSession session, Object message);
    }

    OnServerIoListener listener = new OnServerIoAdapter();

    public void setOnServerIoListener(OnServerIoListener listener) {
        this.listener = listener;
    }
}
