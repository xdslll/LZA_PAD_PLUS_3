package com.lza.pad.db.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 9/30/14.
 */
@DatabaseTable(tableName = "global_settings")
public class GlobalSettings implements Parcelable {

    public static final String COL_TYPE = "type";
    public static final String COL_NAME = "name";
    public static final String COL_CNNAME = "cnName";
    public static final String COL_VALUE = "value";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String type;

    @DatabaseField
    private String name;

    @DatabaseField
    private String cnName;

    @DatabaseField
    private String value;

    public GlobalSettings() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
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
        dest.writeInt(id);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeString(cnName);
        dest.writeString(value);
    }

    public static final Creator<GlobalSettings> CREATOR = new Creator<GlobalSettings>() {
        @Override
        public GlobalSettings createFromParcel(Parcel source) {
            GlobalSettings gs = new GlobalSettings();
            gs.id = source.readInt();
            gs.type = source.readString();
            gs.name = source.readString();
            gs.cnName = source.readString();
            gs.value = source.readString();
            return gs;
        }

        @Override
        public GlobalSettings[] newArray(int size) {
            return new GlobalSettings[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        boolean flag = false;
        if (o != null && o instanceof GlobalSettings) {
            GlobalSettings settings = (GlobalSettings) o;
            if (settings.getName().equals(name) &&
                    settings.getType().equals(type) &&
                    settings.getValue().equals(value)) {
                flag = true;
            }
        }
        return flag;
    }
}
