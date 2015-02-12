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

    public static final String TAG_HAVE_UDPATE = "1";
    public static final String TAG_NEED_UDPATE = "0";

    public static final String TAG_AUTO_UPDATE = "1";
    public static final String TAG_MANUAL_UPDATE = "0";

    public static final String TAG_HOTSPOT_ON = "1";
    public static final String TAG_HOTSPOT_OFF = "0";

    public static final String TAG_STATE_ON = "1";
    public static final String TAG_STATE_OFF = "0";

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

    String auto_update;

    String update_time;

    String hotspot_password;

    String hotspot_switch;

    String state;

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

    public String getAuto_update() {
        return auto_update;
    }

    public void setAuto_update(String auto_update) {
        this.auto_update = auto_update;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getHotspot_password() {
        return hotspot_password;
    }

    public void setHotspot_password(String hotspot_password) {
        this.hotspot_password = hotspot_password;
    }

    public String getHotspot_switch() {
        return hotspot_switch;
    }

    public void setHotspot_switch(String hotspot_switch) {
        this.hotspot_switch = hotspot_switch;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
        dest.writeString(auto_update);
        dest.writeString(update_time);
        dest.writeString(hotspot_password);
        dest.writeString(hotspot_switch);
        dest.writeString(state);
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
        auto_update = src.readString();
        update_time = src.readString();
        hotspot_password = src.readString();
        hotspot_switch = src.readString();
        state = src.readString();
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
                "\n布局id：\'" + module_ids + '\'' +
                "\n更新标识：\'" + (Integer.valueOf(update_tag) == 0 ? "否" : "是") + '\'' +
                "\nMac地址：\'" + mac_add + '\'' +
                "\n设备名称：\'" + name + '\'' +
                "\n所属区域：\'" + area + '\'' +
                "\n到期时间：\'" + end_pubdate + '\'' +
                "\n注册时间：\'" + intime + '\'' +
                "\n自动更新：\'" + (Integer.valueOf(auto_update) == 0 ? "否" : "是") + '\'' +
                "\n更新频率：\'" + update_time + "秒" + "\'" +
                "\n热点密码：\'" + hotspot_password + "秒" + "\'" +
                "\n热点开关：\'" + (Integer.valueOf(hotspot_switch) == 0 ? "关" : "开") + '\'' +
                "\n设备状态：\'" + (Integer.valueOf(state) == 0 ? "关" : "开") + '\'';
    }
}
