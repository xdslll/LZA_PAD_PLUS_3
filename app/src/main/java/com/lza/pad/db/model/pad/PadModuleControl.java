package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/7.
 */
public class PadModuleControl implements Parcelable {

    private String id;

    private String title;

    private String model_id;

    private String widgets_id;

    private String px;

    private String source_type;

    private String control_type;

    private String control_name;

    private String control_index;

    private String control_height;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModel_id() {
        return model_id;
    }

    public void setModel_id(String model_id) {
        this.model_id = model_id;
    }

    public String getWidgets_id() {
        return widgets_id;
    }

    public void setWidgets_id(String widgets_id) {
        this.widgets_id = widgets_id;
    }

    public String getPx() {
        return px;
    }

    public void setPx(String px) {
        this.px = px;
    }

    public String getSource_type() {
        return source_type;
    }

    public void setSource_type(String source_type) {
        this.source_type = source_type;
    }

    public String getControl_type() {
        return control_type;
    }

    public void setControl_type(String control_type) {
        this.control_type = control_type;
    }

    public String getControl_index() {
        return control_index;
    }

    public void setControl_index(String control_index) {
        this.control_index = control_index;
    }

    public String getControl_height() {
        return control_height;
    }

    public void setControl_height(String control_height) {
        this.control_height = control_height;
    }

    public String getControl_name() {
        return control_name;
    }

    public void setControl_name(String control_name) {
        this.control_name = control_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(model_id);
        dest.writeString(widgets_id);
        dest.writeString(px);
        dest.writeString(source_type);
        dest.writeString(control_type);
        dest.writeString(control_name);
        dest.writeString(control_index);
        dest.writeString(control_height);
    }

    public PadModuleControl() {}

    public PadModuleControl(Parcel src) {
        id = src.readString();
        title = src.readString();
        model_id = src.readString();
        widgets_id = src.readString();
        px = src.readString();
        source_type = src.readString();
        control_type = src.readString();
        control_name = src.readString();
        control_index = src.readString();
        control_height = src.readString();
    }

    private static final Creator<PadModuleControl> CREATOR = new Creator<PadModuleControl>() {
        @Override
        public PadModuleControl createFromParcel(Parcel source) {
            return new PadModuleControl(source);
        }

        @Override
        public PadModuleControl[] newArray(int size) {
            return new PadModuleControl[size];
        }
    };

    @Override
    public String toString() {
        return "PadModuleControl{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", model_id='" + model_id + '\'' +
                ", widgets_id='" + widgets_id + '\'' +
                ", px='" + px + '\'' +
                ", source_type='" + source_type + '\'' +
                '}';
    }
}
