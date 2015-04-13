package com.lza.pad.app.socket.admin.file;

import com.lza.pad.support.debug.AppLogger;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/25/15.
 */
public class MinaFileServerAdmin {

    public static int PORT = 9999;

    private MinaFileSendHandler mHandler;

    private NioSocketAcceptor mAcceptor;

    private static MinaFileServerAdmin mInstance;

    public static MinaFileServerAdmin getInstance() {
        if (mInstance == null) {
            mInstance = new MinaFileServerAdmin();
        }
        return mInstance;
    }

    private MinaFileServerAdmin() {}

    public boolean isDisposed() {
        return mAcceptor != null && mAcceptor.isDisposed();
    }
    public boolean isActive() {
        return mAcceptor != null && mAcceptor.isActive();
    }

    public boolean start() {
        mAcceptor = new NioSocketAcceptor();
        DefaultIoFilterChainBuilder chainBuilder = mAcceptor.getFilterChain();

        ObjectSerializationCodecFactory factory = new ObjectSerializationCodecFactory();
        factory.setDecoderMaxObjectSize(Integer.MAX_VALUE);
        factory.setEncoderMaxObjectSize(Integer.MAX_VALUE);
        chainBuilder.addLast("logging", new LoggingFilter());

        mHandler = new MinaFileSendHandler();
        mAcceptor.setHandler(mHandler);
        InetSocketAddress address = null;
        try {
            address = new InetSocketAddress(PORT);
            mAcceptor.bind(address);
        } catch (IOException e) {
            e.printStackTrace();
            PORT++;
            return start();
        }
        AppLogger.e("文件服务器已经启动，端口号：" + PORT);
        return true;
    }

    public boolean stop() {
        try {
            if (mAcceptor != null) {
                mAcceptor.unbind(new InetSocketAddress(PORT));
                mAcceptor.getFilterChain().clear();
                mAcceptor.dispose();
                mAcceptor = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mAcceptor = null;
            return false;
        }
        return true;
    }

    public void setFilePath(String filePath) {
        mHandler.setFilePath(filePath);
    }

}
