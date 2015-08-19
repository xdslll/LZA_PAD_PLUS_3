package com.lza.pad.db.model.old;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/8/13.
 */
public class OldResponse {

    int pagesize;

    String status;

    int returnnum;

    List<OldBook> contents;

    List<OldBookDetail> contents1;

    String pagenum;

    int ye;

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getReturnnum() {
        return returnnum;
    }

    public void setReturnnum(int returnnum) {
        this.returnnum = returnnum;
    }

    public List<OldBook> getContents() {
        return contents;
    }

    public void setContents(List<OldBook> contents) {
        this.contents = contents;
    }

    public String getPagenum() {
        return pagenum;
    }

    public void setPagenum(String pagenum) {
        this.pagenum = pagenum;
    }

    public int getYe() {
        return ye;
    }

    public void setYe(int ye) {
        this.ye = ye;
    }

    public List<OldBookDetail> getContents1() {
        return contents1;
    }

    public void setContents1(List<OldBookDetail> contents1) {
        this.contents1 = contents1;
    }
}
