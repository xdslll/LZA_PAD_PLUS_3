package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/6.
 */
public class PadLayoutModule implements Parcelable {

    String id;

    String px;

    String module_id;

    String layout_id;

    String layout_icon;

    String layout_icon2;

    String module_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPx() {
        return px;
    }

    public void setPx(String px) {
        this.px = px;
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

    public String getLayout_icon() {
        return layout_icon;
    }

    public void setLayout_icon(String layout_icon) {
        this.layout_icon = layout_icon;
    }

    public String getLayout_icon2() {
        return layout_icon2;
    }

    public void setLayout_icon2(String layout_icon2) {
        this.layout_icon2 = layout_icon2;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(px);
        dest.writeString(module_id);
        dest.writeString(layout_id);
        dest.writeString(layout_icon);
        dest.writeString(layout_icon2);
        dest.writeString(module_name);
    }

    public PadLayoutModule() {

    }

    public PadLayoutModule(Parcel src) {
        id = src.readString();
        px = src.readString();
        module_id = src.readString();
        layout_id = src.readString();
        layout_icon = src.readString();
        layout_icon2 = src.readString();
        module_name = src.readString();
    }

    public static final Creator<PadLayoutModule> CREATOR = new Creator<PadLayoutModule>() {
        @Override
        public PadLayoutModule createFromParcel(Parcel source) {
            return new PadLayoutModule(source);
        }

        @Override
        public PadLayoutModule[] newArray(int size) {
            return new PadLayoutModule[size];
        }
    };

    @Override
    public String toString() {
        return "PadLayoutModule{" +
                "id='" + id + '\'' +
                ", px='" + px + '\'' +
                ", module_id='" + module_id + '\'' +
                ", layout_id='" + layout_id + '\'' +
                ", layout_icon='" + layout_icon + '\'' +
                ", layout_icon2='" + layout_icon2 + '\'' +
                '}';
    }
}
