package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/13/15.
 */
public class PadWidgetData implements Parcelable {

    private String id;

    private String name;

    private String type;

    private String url;

    private String data_size;

    private String data_each;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getData_size() {
        return data_size;
    }

    public void setData_size(String data_size) {
        this.data_size = data_size;
    }

    public String getData_each() {
        return data_each;
    }

    public void setData_each(String data_each) {
        this.data_each = data_each;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.url);
        dest.writeString(this.data_size);
        dest.writeString(this.data_each);
    }

    public PadWidgetData() {
    }

    private PadWidgetData(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.type = in.readString();
        this.url = in.readString();
        this.data_size = in.readString();
        this.data_each = in.readString();
    }

    public static final Parcelable.Creator<PadWidgetData> CREATOR = new Parcelable.Creator<PadWidgetData>() {
        public PadWidgetData createFromParcel(Parcel source) {
            return new PadWidgetData(source);
        }

        public PadWidgetData[] newArray(int size) {
            return new PadWidgetData[size];
        }
    };
}
