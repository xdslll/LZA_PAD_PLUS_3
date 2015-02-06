package com.lza.pad.app.socket.model;

import com.google.gson.annotations.Expose;

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

    IoSession session;

    @Expose
    String name;

    @Expose
    String message;

    @Expose
    String status;

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
}
