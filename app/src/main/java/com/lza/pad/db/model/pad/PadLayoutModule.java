package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/6.
 */
public class PadLayoutModule implements Parcelable {

    public static Map<String, String> SUBJECT_TYPE = new HashMap<String, String>();

    static {
        SUBJECT_TYPE.put("1", "学科");
    }

    String id;

    String px;

    String module_id;

    String layout_id;

    String layout_icon;

    String layout_icon2;

    String module_name;

    String keyword;

    String subject;

    String module_type;

    String module_style;

    String module_index;

    String default_keyword;

    String default_subject;

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

    public String getLayout_icon() {
        return layout_icon;
    }

    public void setLayout_icon(String layout_icon) {
        this.layout_icon = layout_icon;
    }

    public String getLayout_icon2() {
        return layout_icon2;
    }

    public void setLayout_icon2(String layout_icon2) {
        this.layout_icon2 = layout_icon2;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getModule_type() {
        return module_type;
    }

    public void setModule_type(String module_type) {
        this.module_type = module_type;
    }

    public String getModule_style() {
        return module_style;
    }

    public void setModule_style(String module_style) {
        this.module_style = module_style;
    }

    public String getModule_index() {
        return module_index;
    }

    public void setModule_index(String module_index) {
        this.module_index = module_index;
    }

    public String getDefault_keyword() {
        return default_keyword;
    }

    public void setDefault_keyword(String default_keyword) {
        this.default_keyword = default_keyword;
    }

    public String getDefault_subject() {
        return default_subject;
    }

    public void setDefault_subject(String default_subject) {
        this.default_subject = default_subject;
    }

    public PadLayoutModule() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.px);
        dest.writeString(this.module_id);
        dest.writeString(this.layout_id);
        dest.writeString(this.layout_icon);
        dest.writeString(this.layout_icon2);
        dest.writeString(this.module_name);
        dest.writeString(this.keyword);
        dest.writeString(this.subject);
        dest.writeString(this.module_type);
        dest.writeString(this.module_style);
        dest.writeString(this.module_index);
        dest.writeString(this.default_keyword);
        dest.writeString(this.default_subject);
    }

    private PadLayoutModule(Parcel in) {
        this.id = in.readString();
        this.px = in.readString();
        this.module_id = in.readString();
        this.layout_id = in.readString();
        this.layout_icon = in.readString();
        this.layout_icon2 = in.readString();
        this.module_name = in.readString();
        this.keyword = in.readString();
        this.subject = in.readString();
        this.module_type = in.readString();
        this.module_style = in.readString();
        this.module_index = in.readString();
        this.default_keyword = in.readString();
        this.default_subject = in.readString();
    }

    public static final Creator<PadLayoutModule> CREATOR = new Creator<PadLayoutModule>() {
        public PadLayoutModule createFromParcel(Parcel source) {
            return new PadLayoutModule(source);
        }

        public PadLayoutModule[] newArray(int size) {
            return new PadLayoutModule[size];
        }
    };

    public String getSubjectType(String key) {
        return SUBJECT_TYPE.get(key);
    }
}
