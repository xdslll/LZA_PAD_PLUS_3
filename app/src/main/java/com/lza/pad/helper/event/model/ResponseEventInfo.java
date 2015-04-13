package com.lza.pad.helper.event.model;

import com.lza.pad.helper.event.state.ResponseEventTag;

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

    private int statusCode;

    private String cookie;

    private Throwable error;

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

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
