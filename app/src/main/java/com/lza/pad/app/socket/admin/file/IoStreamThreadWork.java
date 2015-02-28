package com.lza.pad.app.socket.admin.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/25/15.
 */
public class IoStreamThreadWork extends Thread {

    public static final int BUFFER_SIZE = 2 * 1024;

    private BufferedInputStream bis;

    private BufferedOutputStream bos;

    public BufferedInputStream getBis() {
        return bis;
    }

    public void setBis(BufferedInputStream bis) {
        this.bis = bis;
    }

    public BufferedOutputStream getBos() {
        return bos;
    }

    public void setBos(BufferedOutputStream bos) {
        this.bos = bos;
    }

    public IoStreamThreadWork(InputStream is, OutputStream os) {
        bis = new BufferedInputStream(is);
        bos = new BufferedOutputStream(os);
    }

    @Override
    public synchronized void run() {
        byte[] bufferByte = new byte[BUFFER_SIZE];
        int hasRead;
        try {
            while ((hasRead = bis.read(bufferByte)) != -1) {
                bos.write(bufferByte, 0, hasRead);
            }
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
