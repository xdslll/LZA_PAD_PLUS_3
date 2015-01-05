package com.lza.pad.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 10/21/14.
 */
@DatabaseTable(tableName = "api_param")
public class ApiParam {

    public static final String COL_TYPE = "type";
    public static final String COL_KEY = "key";
    public static final String COL_VALUE = "value";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String type;

    @DatabaseField
    private String key;

    @DatabaseField
    private String value;

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

    @Override
    public boolean equals(Object o) {
        boolean flag = false;
        if (o != null && o instanceof ApiParam) {
            ApiParam data = (ApiParam) o;
            if (data.getType().equals(type) &&
                    data.getKey().equals(key) &&
                    data.getValue().equals(value)) {
                flag = true;
            }
        }
        return flag;
    }
}
