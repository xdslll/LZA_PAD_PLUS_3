package com.lza.pad.db.model.douban;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 10/31/14.
 */
public class DoubanTag implements Parcelable {

    private int count;

    private String name;

    private String title;

    public DoubanTag() {}

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.count);
        dest.writeString(this.name);
        dest.writeString(this.title);
    }

    private DoubanTag(Parcel in) {
        this.count = in.readInt();
        this.name = in.readString();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<DoubanTag> CREATOR = new Parcelable.Creator<DoubanTag>() {
        public DoubanTag createFromParcel(Parcel source) {
            return new DoubanTag(source);
        }

        public DoubanTag[] newArray(int size) {
            return new DoubanTag[size];
        }
    };
}
