package com.lza.pad.event.model;

import com.lza.pad.event.state.BindViewEventTag;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 10/29/14.
 */
public class BindViewEventInfo {

    private Object obj;

    private List<Object> objList;

    private BindViewEventTag tag;

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public BindViewEventTag getTag() {
        return tag;
    }

    public void setTag(BindViewEventTag tag) {
        this.tag = tag;
    }

    public List<Object> getObjList() {
        return objList;
    }

    public void setObjList(List<Object> objList) {
        this.objList = objList;
    }
}
