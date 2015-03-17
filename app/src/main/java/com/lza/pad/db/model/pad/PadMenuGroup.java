package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/13/15.
 */
public class PadMenuGroup implements Parcelable {

    private String id;

    private String group;

    private String menu;

    private String event;

    private String index;

    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.group);
        dest.writeString(this.menu);
        dest.writeString(this.event);
        dest.writeString(this.index);
        dest.writeString(this.description);
    }

    public PadMenuGroup() {
    }

    private PadMenuGroup(Parcel in) {
        this.id = in.readString();
        this.group = in.readString();
        this.menu = in.readString();
        this.event = in.readString();
        this.index = in.readString();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<PadMenuGroup> CREATOR = new Parcelable.Creator<PadMenuGroup>() {
        public PadMenuGroup createFromParcel(Parcel source) {
            return new PadMenuGroup(source);
        }

        public PadMenuGroup[] newArray(int size) {
            return new PadMenuGroup[size];
        }
    };
}
