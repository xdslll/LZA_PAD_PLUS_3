package com.lza.pad.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 10/22/14.
 */
@DatabaseTable(tableName = "cache_image")
public class CacheImage {

    public static final String COL_TYPE = "type";
    public static final String COL_KEY = "key";
    public static final String COL_VALUE = "value";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(index = true)
    private String key;

    @DatabaseField
    private String value;

    @DatabaseField(index = true)
    private String type;

    public CacheImage() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        boolean flag = false;
        if (o != null && o instanceof CacheImage) {
            CacheImage data = (CacheImage) o;
            if (data.getKey().equals(key) &&
                    data.getValue().equals(value) &&
                    data.getType().equals(type)) {
                flag = true;
            }
        }
        return flag;
    }
}
