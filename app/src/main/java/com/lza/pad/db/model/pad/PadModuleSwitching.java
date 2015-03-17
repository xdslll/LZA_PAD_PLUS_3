package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/12/15.
 */
public class PadModuleSwitching implements Parcelable {

    String id;

    List<PadModule> pre_module;

    List<PadModule> next_module;

    List<PadSwitching> switching_mode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PadModule> getPre_module() {
        return pre_module;
    }

    public void setPre_module(List<PadModule> pre_module) {
        this.pre_module = pre_module;
    }

    public List<PadModule> getNext_module() {
        return next_module;
    }

    public void setNext_module(List<PadModule> next_module) {
        this.next_module = next_module;
    }

    public List<PadSwitching> getSwitching_mode() {
        return switching_mode;
    }

    public void setSwitching_mode(List<PadSwitching> switching_mode) {
        this.switching_mode = switching_mode;
    }

    public PadModuleSwitching() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeTypedList(pre_module);
        dest.writeTypedList(next_module);
        dest.writeTypedList(switching_mode);
    }

    private PadModuleSwitching(Parcel in) {
        this.id = in.readString();
        in.readTypedList(pre_module, PadModule.CREATOR);
        in.readTypedList(next_module, PadModule.CREATOR);
        in.readTypedList(switching_mode, PadSwitching.CREATOR);
    }

    public static final Creator<PadModuleSwitching> CREATOR = new Creator<PadModuleSwitching>() {
        public PadModuleSwitching createFromParcel(Parcel source) {
            return new PadModuleSwitching(source);
        }

        public PadModuleSwitching[] newArray(int size) {
            return new PadModuleSwitching[size];
        }
    };
}
