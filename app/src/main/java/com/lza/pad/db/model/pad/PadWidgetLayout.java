package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/13/15.
 */
public class PadWidgetLayout implements Parcelable {

    public static final String MATCH_PARENT = "-1";

    private String id;

    private String name;

    private String height;

    private int widget_width;

    private int widget_height;

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

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public int getWidget_width() {
        return widget_width;
    }

    public void setWidget_width(int widget_width) {
        this.widget_width = widget_width;
    }

    public int getWidget_height() {
        return widget_height;
    }

    public void setWidget_height(int widget_height) {
        this.widget_height = widget_height;
    }

    public PadWidgetLayout() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.height);
        dest.writeInt(this.widget_width);
        dest.writeInt(this.widget_height);
    }

    private PadWidgetLayout(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.height = in.readString();
        this.widget_width = in.readInt();
        this.widget_height = in.readInt();
    }

    public static final Creator<PadWidgetLayout> CREATOR = new Creator<PadWidgetLayout>() {
        public PadWidgetLayout createFromParcel(Parcel source) {
            return new PadWidgetLayout(source);
        }

        public PadWidgetLayout[] newArray(int size) {
            return new PadWidgetLayout[size];
        }
    };
}
