package com.lza.pad.helper;

import com.android.volley.VolleyError;
import com.lza.pad.event.model.ResponseEventInfo;
import com.lza.pad.event.state.ResponseEventTag;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/10.
 */
public abstract class DoubanRequestListener<T> implements RequestHelper.OnRequestListener {

    @Override
    public void onResponse(ResponseEventInfo response) {
        if (response == null) {
            onResponseDataEmpty();
            return;
        }
        if (response.getTag() == ResponseEventTag.ON_RESONSE) {
            String json = response.getResponseData();
            T data = parseJson(json);
            if (data == null) {
                onResponseDataEmpty(json);
                return;
            }
            handleRespone(data);
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
    public abstract T parseJson(String json);

    /**
     * 响应数据为空
     */
    public void onResponseDataEmpty() {}

    /**
     * Json解析失败
     *
     * @param json
     */
    public void onResponseDataEmpty(String json) {}

    /**
     * 成功获取数据后的操作
     * 主要重写的方法
     *
     * @param data
     */
    public abstract void handleRespone(T data);

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

}
