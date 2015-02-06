package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/6.
 */
public class PadDeviceInfo implements Parcelable {

    String id;

    String bh;

    String school_bh;

    String module_ids;

    String update_tag;

    String mac_add;

    String name;

    String area;

    String end_pubdate;

    String intime;

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

    public String getSchool_bh() {
        return school_bh;
    }

    public void setSchool_bh(String school_bh) {
        this.school_bh = school_bh;
    }

    public String getModule_ids() {
        return module_ids;
    }

    public void setModule_ids(String module_ids) {
        this.module_ids = module_ids;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getEnd_pubdate() {
        return end_pubdate;
    }

    public void setEnd_pubdate(String end_pubdate) {
        this.end_pubdate = end_pubdate;
    }

    public String getIntime() {
        return intime;
    }

    public void setIntime(String intime) {
        this.intime = intime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(bh);
        dest.writeString(school_bh);
        dest.writeString(module_ids);
        dest.writeString(update_tag);
        dest.writeString(mac_add);
        dest.writeString(name);
        dest.writeString(area);
        dest.writeString(end_pubdate);
        dest.writeString(intime);
    }

    public PadDeviceInfo() {

    }

    public PadDeviceInfo(Parcel src) {
        id = src.readString();
        bh = src.readString();
        school_bh = src.readString();
        module_ids = src.readString();
        update_tag = src.readString();
        mac_add = src.readString();
        name = src.readString();
        area = src.readString();
        end_pubdate = src.readString();
        intime = src.readString();
    }

    public static final Creator<PadDeviceInfo> CREATOR = new Creator<PadDeviceInfo>() {
        @Override
        public PadDeviceInfo createFromParcel(Parcel source) {
            return new PadDeviceInfo(source);
        }

        @Override
        public PadDeviceInfo[] newArray(int size) {
            return new PadDeviceInfo[size];
        }
    };

    @Override
    public String toString() {
        return "设备id：\'" + id + '\'' +
                "\n设备编号：\'" + bh + '\'' +
                "\n学校编号：\'" + school_bh + '\'' +
                "\n模块id：\'" + module_ids + '\'' +
                "\n更新标识：\'" + (Integer.valueOf(update_tag) == 0 ? "否" : "是") + '\'' +
                "\nMac地址：\'" + mac_add + '\'' +
                "\n设备名称：\'" + name + '\'' +
                "\n所属区域：\'" + area + '\'' +
                "\n到期时间：\'" + end_pubdate + '\'' +
                "\n注册时间：\'" + intime + '\'';
    }
}
