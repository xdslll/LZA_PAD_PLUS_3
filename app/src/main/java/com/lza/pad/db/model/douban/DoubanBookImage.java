package com.lza.pad.db.model.douban;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 10/31/14.
 */
public class DoubanBookImage implements Parcelable {

    private String small;

    private String large;

    private String medium;

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.small);
        dest.writeString(this.large);
        dest.writeString(this.medium);
    }

    public DoubanBookImage() {
    }

    private DoubanBookImage(Parcel in) {
        this.small = in.readString();
        this.large = in.readString();
        this.medium = in.readString();
    }

    public static final Parcelable.Creator<DoubanBookImage> CREATOR = new Parcelable.Creator<DoubanBookImage>() {
        public DoubanBookImage createFromParcel(Parcel source) {
            return new DoubanBookImage(source);
        }

        public DoubanBookImage[] newArray(int size) {
            return new DoubanBookImage[size];
        }
    };
}
