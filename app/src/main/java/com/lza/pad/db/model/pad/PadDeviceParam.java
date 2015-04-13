package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/30/15.
 */
public class PadDeviceParam implements Parcelable {

    List<PadDeviceInfo> device_id;

    List<PadParam> param_id;

    public List<PadDeviceInfo> getDevice_id() {
        return device_id;
    }

    public void setDevice_id(List<PadDeviceInfo> device_id) {
        this.device_id = device_id;
    }

    public List<PadParam> getParam_id() {
        return param_id;
    }

    public void setParam_id(List<PadParam> param_id) {
        this.param_id = param_id;
    }

    public PadDeviceParam() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(device_id);
        dest.writeTypedList(param_id);
    }

    private PadDeviceParam(Parcel in) {
        in.readTypedList(device_id, PadDeviceInfo.CREATOR);
        in.readTypedList(param_id, PadParam.CREATOR);
    }

    public static final Creator<PadDeviceParam> CREATOR = new Creator<PadDeviceParam>() {
        public PadDeviceParam createFromParcel(Parcel source) {
            return new PadDeviceParam(source);
        }

        public PadDeviceParam[] newArray(int size) {
            return new PadDeviceParam[size];
        }
    };
}
