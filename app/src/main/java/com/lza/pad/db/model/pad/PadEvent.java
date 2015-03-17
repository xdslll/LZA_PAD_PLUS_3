package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/13/15.
 */
public class PadEvent implements Parcelable {

    private String id;

    private String name;

    private String event_code_path;

    private String type;

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

    public String getEvent_code_path() {
        return event_code_path;
    }

    public void setEvent_code_path(String event_code_path) {
        this.event_code_path = event_code_path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.event_code_path);
        dest.writeString(this.type);
    }

    public PadEvent() {
    }

    private PadEvent(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.event_code_path = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<PadEvent> CREATOR = new Parcelable.Creator<PadEvent>() {
        public PadEvent createFromParcel(Parcel source) {
            return new PadEvent(source);
        }

        public PadEvent[] newArray(int size) {
            return new PadEvent[size];
        }
    };
}
