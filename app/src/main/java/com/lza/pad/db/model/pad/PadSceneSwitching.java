package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/12/15.
 */
public class PadSceneSwitching implements Parcelable {

    String id;

    List<PadScene> pre_scene = new ArrayList<PadScene>();

    List<PadScene> next_scene = new ArrayList<PadScene>();

    List<PadSwitching> switching_mode = new ArrayList<PadSwitching>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PadScene> getPre_scene() {
        return pre_scene;
    }

    public void setPre_scene(List<PadScene> pre_scene) {
        this.pre_scene = pre_scene;
    }

    public List<PadScene> getNext_scene() {
        return next_scene;
    }

    public void setNext_scene(List<PadScene> next_scene) {
        this.next_scene = next_scene;
    }

    public List<PadSwitching> getSwitching_mode() {
        return switching_mode;
    }

    public void setSwitching_mode(List<PadSwitching> switching_mode) {
        this.switching_mode = switching_mode;
    }

    public PadSceneSwitching() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeTypedList(pre_scene);
        dest.writeTypedList(next_scene);
        dest.writeTypedList(switching_mode);
    }

    private PadSceneSwitching(Parcel in) {
        this.id = in.readString();
        in.readTypedList(pre_scene, PadScene.CREATOR);
        in.readTypedList(next_scene, PadScene.CREATOR);
        in.readTypedList(switching_mode, PadSwitching.CREATOR);
    }

    public static final Creator<PadSceneSwitching> CREATOR = new Creator<PadSceneSwitching>() {
        public PadSceneSwitching createFromParcel(Parcel source) {
            return new PadSceneSwitching(source);
        }

        public PadSceneSwitching[] newArray(int size) {
            return new PadSceneSwitching[size];
        }
    };
}
