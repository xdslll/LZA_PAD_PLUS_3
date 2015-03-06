package com.lza.pad.app.socket.admin.server;

import com.lza.pad.app.socket.admin.codec.CharsetCodeFactory;
import com.lza.pad.support.debug.AppLogger;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/4.
 */
public class MinaServerAdmin {

    private SocketAcceptor acceptor;
    private ServerMessageHandler handler = new ServerMessageHandler();

    private static int PORT = 8888;
    private static final int DEFAULT_IDLE_TIME = 30;

    public void setOnServerIoListener(ServerMessageHandler.OnServerIoListener listener) {
        handler.setOnServerIoListener(listener);
    }

    public boolean start() {
        return start(DEFAULT_IDLE_TIME);
    }

    public boolean start(int idleTime) {
        if (acceptor != null) stop();
        acceptor = new NioSocketAcceptor();
        DefaultIoFilterChainBuilder filterChain = acceptor.getFilterChain();
        filterChain.addLast("codec", new ProtocolCodecFilter(new CharsetCodeFactory()));

        acceptor.setHandler(handler);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, idleTime);

        try {
            AppLogger.e("正在绑定[" + PORT + "]端口...");
            acceptor.setReuseAddress(true);
            acceptor.bind(new InetSocketAddress(PORT));
        } catch (IOException e) {
            AppLogger.e("绑定[" + PORT + "]失败，失败原因：" + e.getMessage() + "," + e.getCause());
            e.printStackTrace();
            PORT++;
            return start();
        }
        return true;
    }

    public boolean stop() {
        try {
            if (acceptor != null) {
                acceptor.unbind(new InetSocketAddress(PORT));
                acceptor.getFilterChain().clear();
                acceptor.dispose();
                acceptor = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            acceptor = null;
            return false;
        }
        return true;
    }

    public boolean isActive() {
        return acceptor != null && acceptor.isActive();
    }

    public boolean isDisposing() {
        return acceptor != null && acceptor.isDisposing();
    }

    public boolean isDisposed() {
        return acceptor == null || acceptor.isDisposed();
    }
}
