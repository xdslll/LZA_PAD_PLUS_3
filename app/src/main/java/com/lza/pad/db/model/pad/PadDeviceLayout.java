package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/6.
 */
public class PadDeviceLayout implements Parcelable {

    String id;

    String px;

    String module_id;

    String layout_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPx() {
        return px;
    }

    public void setPx(String px) {
        this.px = px;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

    public String getLayout_id() {
        return layout_id;
    }

    public void setLayout_id(String layout_id) {
        this.layout_id = layout_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(px);
        dest.writeString(module_id);
        dest.writeString(layout_id);
    }

    public PadDeviceLayout() {

    }


}
