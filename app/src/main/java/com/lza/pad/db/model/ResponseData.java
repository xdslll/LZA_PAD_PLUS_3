package com.lza.pad.db.model;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/22.
 */
public class ResponseData<T> {

    /**
     * 请求成功
     */
    public static final String RESPONSE_STATE_OK = "1";

    /**
     * 没有布局
     */
    public static final String RESPONSE_STATE_NO_LAYOUT = "2";

    /**
     * 请求参数中无Mac地址
     */
    public static final String RESPONSE_STATE_NO_MAC_ADDRESS = "3";

    /**
     * 非法请求，请求地址或参数有误
     */
    public static final String RESPONSE_STATE_REQUEST_ERROR = "where_tag error";

    /**
     * 非法设备
     */
    public static final String RESPONSE_ILLEGAL_DEVICE = "-1";

    /**
     * 无需更新
     */
    public static final String RESPONSE_NOT_NEED_UPDATE = "6";


    private String state;

    private List<T> content;

    private String message;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
