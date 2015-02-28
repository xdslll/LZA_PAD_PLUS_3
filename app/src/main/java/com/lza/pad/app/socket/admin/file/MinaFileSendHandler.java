package com.lza.pad.app.socket.admin.file;

import android.text.TextUtils;

import com.lza.pad.support.debug.AppLogger;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.stream.StreamIoHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/25/15.
 */
public class MinaFileSendHandler extends StreamIoHandler {

    /*@Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        AppLogger.e("向[" + session.getRemoteAddress() + "]发送消息：" + message.toString());
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        AppLogger.e("与[" + session.getRemoteAddress() + "]创建连接");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        super.exceptionCaught(session, cause);
        AppLogger.e("[" + session.getRemoteAddress() + "]触发异常：" + cause.toString());
    }

    @Override
    public void messageReceived(IoSession session, Object buf) {
        super.messageReceived(session, buf);
        AppLogger.e("接收[" + session.getRemoteAddress() + "]的消息");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        AppLogger.e("与[" + session.getRemoteAddress() + "]连接关闭");
    }

    @Override
    public void sessionOpened(IoSession session) {
        super.sessionOpened(session);
        AppLogger.e("与[" + session.getRemoteAddress() + "]连接打开");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        super.sessionIdle(session, status);
        AppLogger.e("与[" + session.getRemoteAddress() + "]连接空闲，状态：" + status.toString());
    }*/

    @Override
    protected void processStreamIo(IoSession session, InputStream inputStream, OutputStream outputStream) {
        AppLogger.e("与[" + session.getRemoteAddress() + "]开始传输文件...文件路径：" + filePath);
        File sendFile = null;
        if (!TextUtils.isEmpty(filePath)) {
            sendFile = new File(filePath);
        }
        if (sendFile == null) return;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(sendFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        new IoStreamThreadWork(fis, outputStream).start();
    }

    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
