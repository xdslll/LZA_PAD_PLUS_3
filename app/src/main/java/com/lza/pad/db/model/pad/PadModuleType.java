package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/13/15.
 */
public class PadModuleType implements Parcelable {

    public static final int MODULE_TYPE_GUIDE = 1;

    public static final int MODULE_TYPE_HOME = 2;

    public static final int MODULE_TYPE_SUBPAGE = 3;

    public static final int MODULE_TYPE_CONTENT = 4;

    public static final int MODULE_TYPE_HELP = 5;


    private String id;

    private String name;

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
        dest.writeString(this.type);
    }

    public PadModuleType() {
    }

    private PadModuleType(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<PadModuleType> CREATOR = new Parcelable.Creator<PadModuleType>() {
        public PadModuleType createFromParcel(Parcel source) {
            return new PadModuleType(source);
        }

        public PadModuleType[] newArray(int size) {
            return new PadModuleType[size];
        }
    };
}
