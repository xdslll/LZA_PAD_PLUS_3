package com.lza.pad.event.model;

import com.lza.pad.event.state.ResponseEventTag;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 11/4/14.
 */
public class ResponseEventInfo {

    private ResponseEventTag tag;

    private String responseData;

    private String errorMessage;

    private String url;

    public ResponseEventTag getTag() {
        return tag;
    }

    public void setTag(ResponseEventTag tag) {
        this.tag = tag;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
