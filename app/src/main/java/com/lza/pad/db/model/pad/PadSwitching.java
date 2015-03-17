package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/12/15.
 */
public class PadSwitching implements Parcelable {

    /**
     * 触发模式
     * FIX_TIME     固定时间触发
     * FIX_DELAY    固定延迟触发
     * FIX_DAY      固定日期触发
     */
    public static final String TRIGGER_MODE_FIX_TIME = "FIX_TIME";
    public static final String TRIGGER_MODE_FIX_DELAY = "FIX_DELAY";
    public static final String TRIGGER_MODE_FIX_DAY = "FIX_DAY";

    String id;

    String name;

    String tigger_mode;

    String trigger_date;

    String trigger_interval;

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

    public String getTigger_mode() {
        return tigger_mode;
    }

    public void setTigger_mode(String tigger_mode) {
        this.tigger_mode = tigger_mode;
    }

    public String getTrigger_date() {
        return trigger_date;
    }

    public void setTrigger_date(String trigger_date) {
        this.trigger_date = trigger_date;
    }

    public String getTrigger_interval() {
        return trigger_interval;
    }

    public void setTrigger_interval(String trigger_interval) {
        this.trigger_interval = trigger_interval;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.tigger_mode);
        dest.writeString(this.trigger_date);
        dest.writeString(this.trigger_interval);
    }

    public PadSwitching() {
    }

    private PadSwitching(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.tigger_mode = in.readString();
        this.trigger_date = in.readString();
        this.trigger_interval = in.readString();
    }

    public static final Parcelable.Creator<PadSwitching> CREATOR = new Parcelable.Creator<PadSwitching>() {
        public PadSwitching createFromParcel(Parcel source) {
            return new PadSwitching(source);
        }

        public PadSwitching[] newArray(int size) {
            return new PadSwitching[size];
        }
    };
}
