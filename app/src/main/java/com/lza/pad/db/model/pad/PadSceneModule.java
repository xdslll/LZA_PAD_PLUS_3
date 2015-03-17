package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/13/15.
 */
public class PadSceneModule implements Parcelable {

    private String id;

    private List<PadScene> scene_id;

    private List<PadModule> module_id;

    private List<PadModuleType> module_type_id;

    private String menu_group_id;

    private String index;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PadScene> getScene_id() {
        return scene_id;
    }

    public void setScene_id(List<PadScene> scene_id) {
        this.scene_id = scene_id;
    }

    public List<PadModule> getModule_id() {
        return module_id;
    }

    public void setModule_id(List<PadModule> module_id) {
        this.module_id = module_id;
    }

    public List<PadModuleType> getModule_type_id() {
        return module_type_id;
    }

    public void setModule_type_id(List<PadModuleType> module_type_id) {
        this.module_type_id = module_type_id;
    }

    public String getMenu_group_id() {
        return menu_group_id;
    }

    public void setMenu_group_id(String menu_group_id) {
        this.menu_group_id = menu_group_id;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public PadSceneModule() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeTypedList(scene_id);
        dest.writeTypedList(module_id);
        dest.writeTypedList(module_type_id);
        dest.writeString(this.menu_group_id);
        dest.writeString(this.index);
    }

    private PadSceneModule(Parcel in) {
        this.id = in.readString();
        in.readTypedList(scene_id, PadScene.CREATOR);
        in.readTypedList(module_id, PadModule.CREATOR);
        in.readTypedList(module_type_id, PadModuleType.CREATOR);
        this.menu_group_id = in.readString();
        this.index = in.readString();
    }

    public static final Creator<PadSceneModule> CREATOR = new Creator<PadSceneModule>() {
        public PadSceneModule createFromParcel(Parcel source) {
            return new PadSceneModule(source);
        }

        public PadSceneModule[] newArray(int size) {
            return new PadSceneModule[size];
        }
    };
}
