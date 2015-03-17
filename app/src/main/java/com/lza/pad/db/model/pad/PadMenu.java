package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/13/15.
 */
public class PadMenu implements Parcelable {

    private String id;

    private String name;

    private String ico;

    private String ico2;

    private String show_as_action;

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

    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico;
    }

    public String getIco2() {
        return ico2;
    }

    public void setIco2(String ico2) {
        this.ico2 = ico2;
    }

    public String getShow_as_action() {
        return show_as_action;
    }

    public void setShow_as_action(String show_as_action) {
        this.show_as_action = show_as_action;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.ico);
        dest.writeString(this.ico2);
        dest.writeString(this.show_as_action);
    }

    public PadMenu() {
    }

    private PadMenu(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.ico = in.readString();
        this.ico2 = in.readString();
        this.show_as_action = in.readString();
    }

    public static final Parcelable.Creator<PadMenu> CREATOR = new Parcelable.Creator<PadMenu>() {
        public PadMenu createFromParcel(Parcel source) {
            return new PadMenu(source);
        }

        public PadMenu[] newArray(int size) {
            return new PadMenu[size];
        }
    };
}
