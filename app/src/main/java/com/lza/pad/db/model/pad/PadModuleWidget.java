package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/13/15.
 */
public class PadModuleWidget implements Parcelable {

    private String id;

    private List<PadModule> module_id = new ArrayList<PadModule>();

    private List<PadWidget> widget_id = new ArrayList<PadWidget>();

    private List<PadWidgetLayout> widget_layout_id = new ArrayList<PadWidgetLayout>();

    private List<PadWidgetData> widget_data_id = new ArrayList<PadWidgetData>();

    private List<PadEvent> event_id = new ArrayList<PadEvent>();

    private List<PadSwitching> switching_id = new ArrayList<PadSwitching>();

    private String index;

    private String can_touch;

    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCan_touch() {
        return can_touch;
    }

    public void setCan_touch(String can_touch) {
        this.can_touch = can_touch;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PadModule> getModule_id() {
        return module_id;
    }

    public void setModule_id(List<PadModule> module_id) {
        this.module_id = module_id;
    }

    public List<PadWidget> getWidget_id() {
        return widget_id;
    }

    public void setWidget_id(List<PadWidget> widget_id) {
        this.widget_id = widget_id;
    }

    public List<PadWidgetLayout> getWidget_layout_id() {
        return widget_layout_id;
    }

    public void setWidget_layout_id(List<PadWidgetLayout> widget_layout_id) {
        this.widget_layout_id = widget_layout_id;
    }

    public List<PadWidgetData> getWidget_data_id() {
        return widget_data_id;
    }

    public void setWidget_data_id(List<PadWidgetData> widget_data_id) {
        this.widget_data_id = widget_data_id;
    }

    public List<PadEvent> getEvent_id() {
        return event_id;
    }

    public void setEvent_id(List<PadEvent> event_id) {
        this.event_id = event_id;
    }

    public List<PadSwitching> getSwitching_id() {
        return switching_id;
    }

    public void setSwitching_id(List<PadSwitching> switching_id) {
        this.switching_id = switching_id;
    }

    public PadModuleWidget() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeTypedList(module_id);
        dest.writeTypedList(widget_id);
        dest.writeTypedList(widget_layout_id);
        dest.writeTypedList(widget_data_id);
        dest.writeTypedList(event_id);
        dest.writeTypedList(switching_id);
        dest.writeString(this.index);
        dest.writeString(this.can_touch);
        dest.writeString(this.label);
    }

    private PadModuleWidget(Parcel in) {
        this.id = in.readString();
        in.readTypedList(module_id, PadModule.CREATOR);
        in.readTypedList(widget_id, PadWidget.CREATOR);
        in.readTypedList(widget_layout_id, PadWidgetLayout.CREATOR);
        in.readTypedList(widget_data_id, PadWidgetData.CREATOR);
        in.readTypedList(event_id, PadEvent.CREATOR);
        in.readTypedList(switching_id, PadSwitching.CREATOR);
        this.index = in.readString();
        this.can_touch = in.readString();
        this.label = in.readString();
    }

    public static final Creator<PadModuleWidget> CREATOR = new Creator<PadModuleWidget>() {
        public PadModuleWidget createFromParcel(Parcel source) {
            return new PadModuleWidget(source);
        }

        public PadModuleWidget[] newArray(int size) {
            return new PadModuleWidget[size];
        }
    };
}
