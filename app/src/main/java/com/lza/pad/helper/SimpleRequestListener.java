package com.lza.pad.helper;

import com.lza.pad.db.model.ResponseData;
import com.lza.pad.helper.event.model.ResponseEventInfo;
import com.lza.pad.helper.event.state.ResponseEventTag;

import java.util.List;

/**
 * 通过的请求处理事件
 *
 * @author xiads
 * @Date 15/2/10.
 */
public class SimpleRequestListener<T> implements RequestHelper.OnRequestListener {

    @Override
    public void onResponse(ResponseEventInfo response) {
        //处理响应数据为空的情况
        if (response == null) {
            onResponseDataEmpty();
            return;
        }
        //处理响应成功的情况
        if (response.getTag() == ResponseEventTag.ON_RESONSE) {
            String json = response.getResponseData();
            //解析Json，用户可以拦截解析的结果
            if (handlerJson(json)) return;
            //解析Json为指定数据类型
            ResponseData<T> data = parseJson(json);
            //如果解析数据为空，则
            if (data == null) {
                onResponseParseFailed(json);
                return;
            }
            //拦截对响应数据的处理
            if (handlerResponse(data)) return;
            //获取处理状态
            String state = data.getState();
            //如果响应状态不为1，则进行异常处理
            if (state == null || !state.equals(ResponseData.RESPONSE_STATE_OK)) {
                onResponseStateError(data);
                return;
            }
            //拦截状态响应成功时的情况
            if (handleResponseStatusOK(json)) return;
            //处理content字段为空的情况
            if (data.getContent() == null || data.getContent().size() <= 0) {
                onResponseContentEmpty();
                return;
            }
            //处理响应数据
            handleRespone(data.getContent());
        } else if (response.getTag() == ResponseEventTag.ON_ERROR) {
            handleRespone(response.getError());
        } else {
            handleUnknownRespone(response);
        }
    }

    public boolean handlerResponse(ResponseData<T> data) {
        return false;
    }

    /**
     * 解析Json，需要子类继承，否则将返回NULL
     * 主要重写的方法，如果不重写该方法，将很有可能引发异常
     *
     * @param json
     * @return
     */
    public ResponseData<T> parseJson(String json) {
        try {
            return JsonParseHelper.parseSimpleResponse(json);
        } catch (Exception ex) {
            return null;
        }
    }

    public boolean handlerJson(String json) {
        return false;
    }

    /**
     * 响应数据为空
     */
    public void onResponseDataEmpty() {
        handleResponseFailed();
    }

    /**
     * Json解析失败
     *
     * @param json
     */
    public void onResponseParseFailed(String json) {
        handleResponseFailed();
    }

    /**
     * 返回状态错误
     */
    public void onResponseStateError(ResponseData<T> response) {
        handleResponseFailed();
    }

    /**
     * 响应内容为空
     */
    public void onResponseContentEmpty() {
        handleResponseFailed();
    }

    /**
     * 成功获取数据后的操作
     * 主要重写的方法
     *
     * @param content
     */
    public void handleRespone(List<T> content) {}

    /**
     * 处理请求失败的场景
     *
     * @param error
     */
    public void handleRespone(Throwable error) {
        handleResponseFailed();
    }

    /**
     * 处理未知错误的请求
     *
     * @param response
     */
    public void handleUnknownRespone(ResponseEventInfo response) {}

    /**
     * 处理返回值为1，即响应成功时的事件
     * @param json
     */
    public boolean handleResponseStatusOK(String json) {
        return false;
    }

    /**
     * 统一处理异常
     */
    public void handleResponseFailed() {

    }
}
