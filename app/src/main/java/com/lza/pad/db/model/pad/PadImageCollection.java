package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/20/15.
 */
public class PadImageCollection implements Parcelable {

    String title;

    String imgs;

    String control_id;

    String module_id;

    String layout_id;

    String school_bh;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public String getControl_id() {
        return control_id;
    }

    public void setControl_id(String control_id) {
        this.control_id = control_id;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

    public String getLayout_id() {
        return layout_id;
    }

    public void setLayout_id(String layout_id) {
        this.layout_id = layout_id;
    }

    public String getSchool_bh() {
        return school_bh;
    }

    public void setSchool_bh(String school_bh) {
        this.school_bh = school_bh;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.imgs);
        dest.writeString(this.control_id);
        dest.writeString(this.module_id);
        dest.writeString(this.layout_id);
        dest.writeString(this.school_bh);
    }

    public PadImageCollection() {
    }

    private PadImageCollection(Parcel in) {
        this.title = in.readString();
        this.imgs = in.readString();
        this.control_id = in.readString();
        this.module_id = in.readString();
        this.layout_id = in.readString();
        this.school_bh = in.readString();
    }

    public static final Parcelable.Creator<PadImageCollection> CREATOR = new Parcelable.Creator<PadImageCollection>() {
        public PadImageCollection createFromParcel(Parcel source) {
            return new PadImageCollection(source);
        }

        public PadImageCollection[] newArray(int size) {
            return new PadImageCollection[size];
        }
    };
}
