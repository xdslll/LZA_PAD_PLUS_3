package com.lza.pad.app.socket.admin.client;

import com.lza.pad.app.socket.admin.codec.CharsetCodeFactory;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/4.
 */
public class MinaClientAdmin {

    String ip;
    int port;
    private SocketConnector connector;
    private ConnectFuture future;
    private IoSession session;
    private ClientMessageHandlerAdapter handler = new ClientMessageHandlerAdapter();


    public MinaClientAdmin() {}

    public MinaClientAdmin(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void setOnClientIoListener(ClientMessageHandlerAdapter.OnClientIoListener listener) {
        handler.setOnClientIoListener(listener);
    }

    public boolean connect() {
        return connect(ip, port);
    }

    public boolean connect(String ip, int port) {
        connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(3000);
        DefaultIoFilterChainBuilder filterChain = connector.getFilterChain();
        filterChain.addLast("codec", new ProtocolCodecFilter(new CharsetCodeFactory()));

        connector.setHandler(handler);
        future = connector.connect(new InetSocketAddress(ip, port));
        future.awaitUninterruptibly();
        session = future.getSession();
        return true;
    }

    public void setAttribute(Object key, Object value) {
        session.setAttribute(key, value);
    }

    public void send(String message) {
        session.write(message);
    }

    public boolean close() {
        CloseFuture f = session.getCloseFuture();
        f.awaitUninterruptibly(1000);
        connector.getFilterChain().clear();
        connector.dispose();
        return true;
    }

    public SocketConnector getConnector() {
        return connector;
    }

    public IoSession getSession() {
        return session;
    }
}
