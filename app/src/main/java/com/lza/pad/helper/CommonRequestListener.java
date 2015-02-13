package com.lza.pad.helper;

import com.android.volley.VolleyError;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.event.model.ResponseEventInfo;
import com.lza.pad.event.state.ResponseEventTag;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/10.
 */
public class CommonRequestListener<T> implements RequestHelper.OnRequestListener {

    @Override
    public void onResponse(ResponseEventInfo response) {
        if (response == null) {
            onResponseDataEmpty();
            return;
        }
        if (response.getTag() == ResponseEventTag.ON_RESONSE) {
            String json = response.getResponseData();
            ResponseData<T> data = parseJson(json);
            if (data == null) {
                onResponseParseFailed(json);
                return;
            }
            String state = data.getState();
            if (!state.equals(ResponseData.RESPONSE_STATE_OK)) {
                onResponseStateError(data);
                return;
            }
            handleResponseSuccess();
            if (data.getContent() == null || data.getContent().size() <= 0) {
                onResponseContentEmpty();
                return;
            }
            handleRespone(data.getContent());
        } else if (response.getTag() == ResponseEventTag.ON_ERROR) {
            handleRespone(response.getError());
        } else {
            handleUnknownRespone(response);
        }
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

    /**
     * 响应数据为空
     */
    public void onResponseDataEmpty() {}

    /**
     * Json解析失败
     *
     * @param json
     */
    public void onResponseParseFailed(String json) {}

    /**
     * 返回状态错误
     */
    public void onResponseStateError(ResponseData<T> response) {}

    /**
     * 响应内容为空
     */
    public void onResponseContentEmpty() {}

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
    public void handleRespone(VolleyError error) {}

    /**
     * 处理未知错误的请求
     *
     * @param response
     */
    public void handleUnknownRespone(ResponseEventInfo response) {}

    /**
     * 处理返回值为1，即响应成功时的事件
     */
    public void handleResponseSuccess() {}
}