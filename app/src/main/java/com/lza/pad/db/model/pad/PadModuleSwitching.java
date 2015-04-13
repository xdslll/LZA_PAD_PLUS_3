package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/12/15.
 */
public class PadModuleSwitching implements Parcelable {

    String id;

    List<PadModuleType> pre_module = new ArrayList<PadModuleType>();

    List<PadModuleType> next_module = new ArrayList<PadModuleType>();

    List<PadSwitching> switching_mode = new ArrayList<PadSwitching>();

    List<PadSchool> school_bh = new ArrayList<PadSchool>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PadModuleType> getPre_module() {
        return pre_module;
    }

    public void setPre_module(List<PadModuleType> pre_module) {
        this.pre_module = pre_module;
    }

    public List<PadModuleType> getNext_module() {
        return next_module;
    }

    public void setNext_module(List<PadModuleType> next_module) {
        this.next_module = next_module;
    }

    public List<PadSwitching> getSwitching_mode() {
        return switching_mode;
    }

    public void setSwitching_mode(List<PadSwitching> switching_mode) {
        this.switching_mode = switching_mode;
    }

    public List<PadSchool> getSchool_bh() {
        return school_bh;
    }

    public void setSchool_bh(List<PadSchool> school_bh) {
        this.school_bh = school_bh;
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
        dest.writeTypedList(school_bh);
    }

    private PadModuleSwitching(Parcel in) {
        this.id = in.readString();
        in.readTypedList(pre_module, PadModuleType.CREATOR);
        in.readTypedList(next_module, PadModuleType.CREATOR);
        in.readTypedList(switching_mode, PadSwitching.CREATOR);
        in.readTypedList(school_bh, PadSchool.CREATOR);
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
