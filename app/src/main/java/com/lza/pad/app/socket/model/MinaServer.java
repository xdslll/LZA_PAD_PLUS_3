package com.lza.pad.app.socket.model;

import com.google.gson.annotations.Expose;
import com.lza.pad.db.model.pad.PadResource;

import org.apache.mina.core.session.IoSession;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/5.
 */
public class MinaServer {

    public static final String STATUS_OK = "status_ok";

    public static final String STATUS_FAILED = "status_failed";

    public static final String STATUS_ERROR = "status_error";

    public static final String ACTION_SEND_FILE = "action_send_file";

    public static final String ACTION_SEND_FILE_READY = "action_send_file_ready";

    public static final String ACTION_SEND_FILE_FAILED = "action_send_file_failed";

    public static final String ACTION_SHAKE = "action_shake";

    IoSession session;

    @Expose
    String name;

    @Expose
    String message;

    @Expose
    String status;

    @Expose
    String fileName;

    @Expose
    long fileLength;

    @Expose
    String action;

    @Expose
    PadResource data;

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public PadResource getData() {
        return data;
    }

    public void setData(PadResource data) {
        this.data = data;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }
}
