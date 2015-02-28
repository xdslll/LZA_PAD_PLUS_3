package com.lza.pad.app.socket.admin.file;

import android.text.TextUtils;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.stream.StreamIoHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/25/15.
 */
public class MinaFileReceiveHandler extends StreamIoHandler {

    public MinaFileReceiveHandler(String filePath) {
        this.filePath = filePath;
    }

    public static final int CORE_POOL_SIZE = 3;
    public static final int MAXIMUM_POOL_SIZE = 6;
    public static final int KEEP_ALIVE_TIME = 3;

    @Override
    protected void processStreamIo(IoSession session, InputStream is, OutputStream os) {
        if (TextUtils.isEmpty(filePath)) return;

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(CORE_POOL_SIZE),
                new ThreadPoolExecutor.DiscardPolicy()
        );
        FileOutputStream fos = null;
        File receiveFile = new File(filePath);

        try {
            fos = new FileOutputStream(receiveFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        threadPool.execute(new IoStreamThreadWork(is, fos));
    }

    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
