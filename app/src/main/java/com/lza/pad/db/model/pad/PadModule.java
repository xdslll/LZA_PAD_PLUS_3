package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/13/15.
 */
public class PadModule implements Parcelable {

    private String id;

    private String name;

    private String ico;

    private String ico2;

    private String privacy;

    private String school_bh;

    private String create_time;

    private String update_time;

    private String activate;

    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico;
    }

    public String getIco2() {
        return ico2;
    }

    public void setIco2(String ico2) {
        this.ico2 = ico2;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getSchool_bh() {
        return school_bh;
    }

    public void setSchool_bh(String school_bh) {
        this.school_bh = school_bh;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getActivate() {
        return activate;
    }

    public void setActivate(String activate) {
        this.activate = activate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.ico);
        dest.writeString(this.ico2);
        dest.writeString(this.privacy);
        dest.writeString(this.school_bh);
        dest.writeString(this.create_time);
        dest.writeString(this.update_time);
        dest.writeString(this.activate);
        dest.writeString(this.description);
    }

    public PadModule() {
    }

    private PadModule(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.ico = in.readString();
        this.ico2 = in.readString();
        this.privacy = in.readString();
        this.school_bh = in.readString();
        this.create_time = in.readString();
        this.update_time = in.readString();
        this.activate = in.readString();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<PadModule> CREATOR = new Parcelable.Creator<PadModule>() {
        public PadModule createFromParcel(Parcel source) {
            return new PadModule(source);
        }

        public PadModule[] newArray(int size) {
            return new PadModule[size];
        }
    };
}
