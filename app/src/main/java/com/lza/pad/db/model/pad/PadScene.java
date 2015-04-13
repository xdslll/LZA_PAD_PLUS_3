package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/12/15.
 */
public class PadScene implements Parcelable {

    public static final String IS_ACTIVATE = "1";

    public static final String IS_NOT_ACTIVATE = "0";

    public static final String IS_PRIVACY = "1";

    public static final String IS_NOT_PRIVACY = "0";


    String id;

    String name;

    String description;

    String school_bh;

    String privacy;

    String create_time;

    String update_time;

    String activate;

    String authority;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSchool_bh() {
        return school_bh;
    }

    public void setSchool_bh(String school_bh) {
        this.school_bh = school_bh;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getActivate() {
        return activate;
    }

    public void setActivate(String activate) {
        this.activate = activate;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public PadScene() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.school_bh);
        dest.writeString(this.privacy);
        dest.writeString(this.create_time);
        dest.writeString(this.update_time);
        dest.writeString(this.activate);
        dest.writeString(this.authority);
    }

    private PadScene(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.school_bh = in.readString();
        this.privacy = in.readString();
        this.create_time = in.readString();
        this.update_time = in.readString();
        this.activate = in.readString();
        this.authority = in.readString();
    }

    public static final Creator<PadScene> CREATOR = new Creator<PadScene>() {
        public PadScene createFromParcel(Parcel source) {
            return new PadScene(source);
        }

        public PadScene[] newArray(int size) {
            return new PadScene[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        return o != null &&
                o instanceof PadScene &&
                ((PadScene) o).id.equals(id);
    }
}
