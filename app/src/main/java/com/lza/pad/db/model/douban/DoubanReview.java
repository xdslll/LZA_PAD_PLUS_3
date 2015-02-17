package com.lza.pad.db.model.douban;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 10/31/14.
 */
public class DoubanReview implements Parcelable {

    private DoubanRating rating;

    private String votes;//支持人数

    private DoubanAuthor author;//作者

    private String title;//评论标题

    private String updated;//更新时间

    private int comments;//评论数量

    private String summary;//摘要

    private String useless;//

    private String published;//发布时间

    private String alt;//支持人数

    private String id;//评论ID

    private String content;//评论的全部内容

    public DoubanReview() {}

    public DoubanRating getRating() {
        return rating;
    }

    public void setRating(DoubanRating rating) {
        this.rating = rating;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public DoubanAuthor getAuthor() {
        return author;
    }

    public void setAuthor(DoubanAuthor author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUseless() {
        return useless;
    }

    public void setUseless(String useless) {
        this.useless = useless;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(rating, flags);
        dest.writeString(votes);
        dest.writeParcelable(author, flags);
        dest.writeString(title);
        dest.writeString(updated);
        dest.writeInt(comments);
        dest.writeString(summary);
        dest.writeString(useless);
        dest.writeString(published);
        dest.writeString(alt);
        dest.writeString(id);
        dest.writeString(content);
    }

    public DoubanReview(Parcel source) {
        rating = source.readParcelable(DoubanRating.class.getClassLoader());
        votes = source.readString();
        author = source.readParcelable(DoubanAuthor.class.getClassLoader());
        title = source.readString();
        updated = source.readString();
        comments = source.readInt();
        summary = source.readString();
        useless = source.readString();
        published = source.readString();
        alt = source.readString();
        id = source.readString();
        content = source.readString();
    }

    public static final Creator<DoubanReview> CREATOR = new Creator<DoubanReview>() {
        @Override
        public DoubanReview createFromParcel(Parcel source) {
            return new DoubanReview(source);
        }

        @Override
        public DoubanReview[] newArray(int size) {
            return new DoubanReview[size];
        }
    };
}
