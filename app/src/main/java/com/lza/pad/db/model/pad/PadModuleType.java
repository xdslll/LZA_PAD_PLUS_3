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

    private String is_dialog;

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

    public String getIs_dialog() {
        return is_dialog;
    }

    public void setIs_dialog(String is_dialog) {
        this.is_dialog = is_dialog;
    }

    public PadModuleType() {
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
        dest.writeString(this.is_dialog);
    }

    private PadModuleType(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.type = in.readString();
        this.is_dialog = in.readString();
    }

    public static final Creator<PadModuleType> CREATOR = new Creator<PadModuleType>() {
        public PadModuleType createFromParcel(Parcel source) {
            return new PadModuleType(source);
        }

        public PadModuleType[] newArray(int size) {
            return new PadModuleType[size];
        }
    };
}
