package com.lza.pad.db.model.douban;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 10/31/14.
 */
public class DoubanAuthor implements Parcelable {

    private String name;

    private String is_banned;

    private String is_suicide;

    private String url;//用户个人空间

    private String avatar;//用户头像

    private String uid;//用户ID

    private String alt;//

    private String type;//评论类型（用户提交/其他）

    private String id;//评论ID

    private String large_avatar;//用户大头像

    public DoubanAuthor() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIs_banned() {
        return is_banned;
    }

    public void setIs_banned(String is_banned) {
        this.is_banned = is_banned;
    }

    public String getIs_suicide() {
        return is_suicide;
    }

    public void setIs_suicide(String is_suicide) {
        this.is_suicide = is_suicide;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLarge_avatar() {
        return large_avatar;
    }

    public void setLarge_avatar(String large_avatar) {
        this.large_avatar = large_avatar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(is_banned);
        dest.writeString(is_suicide);
        dest.writeString(url);
        dest.writeString(avatar);
        dest.writeString(uid);
        dest.writeString(alt);
        dest.writeString(type);
        dest.writeString(id);
        dest.writeString(large_avatar);
    }

    public DoubanAuthor(Parcel source) {
        name = source.readString();
        is_banned = source.readString();
        is_suicide = source.readString();
        url = source.readString();
        avatar = source.readString();
        uid = source.readString();
        alt = source.readString();
        type = source.readString();
        id = source.readString();
        large_avatar = source.readString();
    }

    public static final Creator<DoubanAuthor> CREATOR = new Creator<DoubanAuthor>() {
        @Override
        public DoubanAuthor createFromParcel(Parcel source) {
            return new DoubanAuthor(source);
        }

        @Override
        public DoubanAuthor[] newArray(int size) {
            return new DoubanAuthor[size];
        }
    };
}
