package com.lza.pad.db.model.pad._old;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/7.
 */
@Deprecated
public class PadModuleControl implements Parcelable {

    public static final String BOOLEAN_SHOW_SLIDE = "1";
    public static final String BOOLEAN_NOT_SHOW_SLIDE = "0";

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

    private String control_data_size;

    private String control_data_each;

    private String contents;

    private String if_show_slide;

    private String slide_show_time;

    private String slide_show_period;

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

    public String getControl_data_size() {
        return control_data_size;
    }

    public void setControl_data_size(String control_data_size) {
        this.control_data_size = control_data_size;
    }

    public String getControl_data_each() {
        return control_data_each;
    }

    public void setControl_data_each(String control_data_each) {
        this.control_data_each = control_data_each;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getIf_show_slide() {
        return if_show_slide;
    }

    public void setIf_show_slide(String if_show_slide) {
        this.if_show_slide = if_show_slide;
    }

    public String getSlide_show_time() {
        return slide_show_time;
    }

    public void setSlide_show_time(String slide_show_time) {
        this.slide_show_time = slide_show_time;
    }

    public String getSlide_show_period() {
        return slide_show_period;
    }

    public void setSlide_show_period(String slide_show_period) {
        this.slide_show_period = slide_show_period;
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
        dest.writeString(control_data_size);
        dest.writeString(control_data_each);
        dest.writeString(contents);
        dest.writeString(if_show_slide);
        dest.writeString(slide_show_time);
        dest.writeString(slide_show_period);
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
        control_data_size = src.readString();
        control_data_each = src.readString();
        contents = src.readString();
        if_show_slide = src.readString();
        slide_show_time = src.readString();
        slide_show_period = src.readString();
    }

    public static final Creator<PadModuleControl> CREATOR = new Creator<PadModuleControl>() {
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
                ", control_type='" + control_type + '\'' +
                ", control_name='" + control_name + '\'' +
                ", control_index='" + control_index + '\'' +
                ", control_height='" + control_height + '\'' +
                ", control_data_size='" + control_data_size + '\'' +
                ", control_data_each='" + control_data_each + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }
}
