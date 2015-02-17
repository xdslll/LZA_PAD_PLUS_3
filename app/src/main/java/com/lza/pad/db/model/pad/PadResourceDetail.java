package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/17/15.
 */
public class PadResourceDetail implements Parcelable {

    private String id;

    private String bh;

    private String title;

    private String step;

    private String page;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBh() {
        return bh;
    }

    public void setBh(String bh) {
        this.bh = bh;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.bh);
        dest.writeString(this.title);
        dest.writeString(this.step);
        dest.writeString(this.page);
    }

    public PadResourceDetail() {
    }

    private PadResourceDetail(Parcel in) {
        this.id = in.readString();
        this.bh = in.readString();
        this.title = in.readString();
        this.step = in.readString();
        this.page = in.readString();
    }

    public static final Parcelable.Creator<PadResourceDetail> CREATOR = new Parcelable.Creator<PadResourceDetail>() {
        public PadResourceDetail createFromParcel(Parcel source) {
            return new PadResourceDetail(source);
        }

        public PadResourceDetail[] newArray(int size) {
            return new PadResourceDetail[size];
        }
    };
}
