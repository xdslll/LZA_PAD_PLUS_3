package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/12/15.
 */
public class PadAuthority implements Parcelable {

    String id;

    String name;

    String group;

    String value;

    String scene_parse_code;

    String module_parse_code;

    String widget_parse_code;

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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getScene_parse_code() {
        return scene_parse_code;
    }

    public void setScene_parse_code(String scene_parse_code) {
        this.scene_parse_code = scene_parse_code;
    }

    public String getModule_parse_code() {
        return module_parse_code;
    }

    public void setModule_parse_code(String module_parse_code) {
        this.module_parse_code = module_parse_code;
    }

    public String getWidget_parse_code() {
        return widget_parse_code;
    }

    public void setWidget_parse_code(String widget_parse_code) {
        this.widget_parse_code = widget_parse_code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.group);
        dest.writeString(this.value);
        dest.writeString(this.scene_parse_code);
        dest.writeString(this.module_parse_code);
        dest.writeString(this.widget_parse_code);
    }

    public PadAuthority() {
    }

    private PadAuthority(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.group = in.readString();
        this.value = in.readString();
        this.scene_parse_code = in.readString();
        this.module_parse_code = in.readString();
        this.widget_parse_code = in.readString();
    }

    public static final Parcelable.Creator<PadAuthority> CREATOR = new Parcelable.Creator<PadAuthority>() {
        public PadAuthority createFromParcel(Parcel source) {
            return new PadAuthority(source);
        }

        public PadAuthority[] newArray(int size) {
            return new PadAuthority[size];
        }
    };
}
