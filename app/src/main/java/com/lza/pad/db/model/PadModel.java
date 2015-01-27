package com.lza.pad.db.model;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/22.
 */
public class PadModel {

    private String id;

    private String bh;

    private String title;

    private String school_bh;

    private String model_ids;

    private String update_tag;

    private String mac_add;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBh() {
        return bh;
    }

    public void setBh(String bh) {
        this.bh = bh;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSchool_bh() {
        return school_bh;
    }

    public void setSchool_bh(String school_bh) {
        this.school_bh = school_bh;
    }

    public String getModel_ids() {
        return model_ids;
    }

    public void setModel_ids(String model_ids) {
        this.model_ids = model_ids;
    }

    public String getUpdate_tag() {
        return update_tag;
    }

    public void setUpdate_tag(String update_tag) {
        this.update_tag = update_tag;
    }

    public String getMac_add() {
        return mac_add;
    }

    public void setMac_add(String mac_add) {
        this.mac_add = mac_add;
    }
}
