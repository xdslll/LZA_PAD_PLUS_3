package com.lza.pad.db.model;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/22.
 */
public class ResponseData<T> {

    private String state;

    private List<T> content;

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
}
