package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/3/15.
 */
public class PadVersionInfo implements Parcelable {

    public static final int FILE_TYPE = 101;

    String id;

    String version_code;

    String version_name;

    String url;

    String upgrade_info;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion_code() {
        return version_code;
    }

    public void setVersion_code(String version_code) {
        this.version_code = version_code;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUpgrade_info() {
        return upgrade_info;
    }

    public void setUpgrade_info(String upgrade_info) {
        this.upgrade_info = upgrade_info;
    }


    public PadVersionInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.version_code);
        dest.writeString(this.version_name);
        dest.writeString(this.url);
        dest.writeString(this.upgrade_info);
    }

    private PadVersionInfo(Parcel in) {
        this.id = in.readString();
        this.version_code = in.readString();
        this.version_name = in.readString();
        this.url = in.readString();
        this.upgrade_info = in.readString();
    }

    public static final Creator<PadVersionInfo> CREATOR = new Creator<PadVersionInfo>() {
        public PadVersionInfo createFromParcel(Parcel source) {
            return new PadVersionInfo(source);
        }

        public PadVersionInfo[] newArray(int size) {
            return new PadVersionInfo[size];
        }
    };
}
