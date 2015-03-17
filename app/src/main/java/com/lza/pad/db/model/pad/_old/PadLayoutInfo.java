package com.lza.pad.db.model.pad._old;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/12/15.
 */
@Deprecated
public class PadLayoutInfo implements Parcelable {

    String id;

    String title;

    String info;

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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.info);
    }

    public PadLayoutInfo() {
    }

    private PadLayoutInfo(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.info = in.readString();
    }

    public static final Parcelable.Creator<PadLayoutInfo> CREATOR = new Parcelable.Creator<PadLayoutInfo>() {
        public PadLayoutInfo createFromParcel(Parcel source) {
            return new PadLayoutInfo(source);
        }

        public PadLayoutInfo[] newArray(int size) {
            return new PadLayoutInfo[size];
        }
    };
}
