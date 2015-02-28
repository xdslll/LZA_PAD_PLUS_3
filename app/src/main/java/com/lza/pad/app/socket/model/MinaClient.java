package com.lza.pad.app.socket.model;

import com.google.gson.annotations.Expose;

import org.apache.mina.core.session.IoSession;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/5.
 */
public class MinaClient {

    /**
     * 客户端请求连接
     */
    public static final String ACTION_CONNECT = "action_connect";

    /**
     * 客户端请求控制
     */
    public static final String ACTION_APPLY_FOR_TAKE_CONTROL = "action_apply_for_take_control";

    /**
     * 客户端控制
     */
    public static final String ACTION_TAKE_CONTROL = "action_take_control";

    /**
     * 客户端请求屏幕截图
     */
    public static final String ACTION_APPLY_FOR_CAPTURE_SCREEN = "action_apply_for_capture_screen";

    /**
     * 客户端获取屏幕截图
     */
    public static final String ACTION_CAPTURE_SCREEN = "action_capture_screen";

    /**
     * 客户端请求获取资源
     */
    public static final String ACTION_APPLY_FOR_RESOURCE = "action_apply_for_resource";

    /**
     * 客户端获取资源
     */
    public static final String ACTION_GET_RESOURCE = "action_get_resource";

    /**
     * 客户端请求报修
     */
    public static final String ACTION_APPLY_FOR_REPAIR = "action_apply_for_repair";

    /**
     * 客户端提交报修申请
     */
    public static final String ACTION_SUBMIT_REPAIR = "action_submit_repair";

    /**
     * 客户端摇一摇
     */
    public static final String ACTION_SHAKE = "action_shake";

    /**
     * 客户端同意下载
     */
    public static final String ACTION_APPLY_FOR_DOWNLOAD_FILE = "action_apply_for_download_file";

    IoSession session;

    @Expose
    String name;

    @Expose
    String academy;

    @Expose
    String action;

    @Expose
    int id;

    @Expose
    String filePath;

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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAcademy() {
        return academy;
    }

    public void setAcademy(String academy) {
        this.academy = academy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
