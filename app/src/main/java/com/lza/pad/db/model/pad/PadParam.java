package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/30/15.
 */
public class PadParam implements Parcelable {

    public static final String KEY_HAS_DOUBAN = "has_douban";
    public static final String VALUE_NOT_HAS_DOUBAN = "0";
    public static final String VALUE_HAS_DOUBAN = "1";


    String key;

    String value;

    String description;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        dest.writeString(this.key);
        dest.writeString(this.value);
        dest.writeString(this.description);
    }

    public PadParam() {
    }

    private PadParam(Parcel in) {
        this.key = in.readString();
        this.value = in.readString();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<PadParam> CREATOR = new Parcelable.Creator<PadParam>() {
        public PadParam createFromParcel(Parcel source) {
            return new PadParam(source);
        }

        public PadParam[] newArray(int size) {
            return new PadParam[size];
        }
    };
}
