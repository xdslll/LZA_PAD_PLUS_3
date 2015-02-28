package com.lza.pad.app.socket.admin.file;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/25/15.
 */
public class MinaFileClientAdmin {

    public static final int PORT = 9999;

    private MinaFileReceiveHandler mHandler;
    private IoSession mIoSession;
    private static final int DEFAULT_RETRY = 2000;

    public void createClientStream(String ipAddress, String filePath) {
        NioSocketConnector connector = new NioSocketConnector();
        DefaultIoFilterChainBuilder chainBuilder = connector.getFilterChain();
        ObjectSerializationCodecFactory factory = new ObjectSerializationCodecFactory();
        factory.setDecoderMaxObjectSize(Integer.MAX_VALUE);
        factory.setEncoderMaxObjectSize(Integer.MAX_VALUE);
        chainBuilder.addLast("logging", new LoggingFilter());
        mHandler = new MinaFileReceiveHandler(filePath);
        connector.setHandler(mHandler);
        for (;;) {
            try {
                ConnectFuture future = connector.connect(new InetSocketAddress(ipAddress, PORT));
                future.awaitUninterruptibly();
                mIoSession = future.getSession();
                break;
            } catch (Exception ex) {
                try {
                    Thread.sleep(DEFAULT_RETRY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        mIoSession.getCloseFuture().awaitUninterruptibly();
        connector.dispose();
    }

}
