package com.lza.pad.db.model;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/22.
 */
public class ResponseData<T> {

    public static final String RESPONSE_STATE_OK = "1";
    public static final String RESPONSE_STATE_NO_LAYOUT = "2";
    public static final String RESPONSE_STATE_NO_MAC_ADDRESS = "3";


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
