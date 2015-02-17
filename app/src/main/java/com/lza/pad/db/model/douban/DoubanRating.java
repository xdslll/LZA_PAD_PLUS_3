package com.lza.pad.db.model.douban;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 10/31/14.
 */
public class DoubanRating implements Parcelable {

    private int max;

    private int numRaters;

    private String average;

    private int min;

    private String value;

    public DoubanRating() {}

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getNumRaters() {
        return numRaters;
    }

    public void setNumRaters(int numRaters) {
        this.numRaters = numRaters;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(max);
        dest.writeInt(numRaters);
        dest.writeString(average);
        dest.writeInt(min);
        dest.writeString(value);
    }

    public DoubanRating(Parcel source) {
        max = source.readInt();
        numRaters = source.readInt();
        average = source.readString();
        min = source.readInt();
        value = source.readString();
    }

    public static final Creator<DoubanRating> CREATOR = new Creator<DoubanRating>() {
        @Override
        public DoubanRating createFromParcel(Parcel source) {
            return new DoubanRating(source);
        }

        @Override
        public DoubanRating[] newArray(int size) {
            return new DoubanRating[size];
        }
    };
}
