package com.lza.pad.db.model.douban;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 10/31/14.
 */
public class DoubanBook implements Parcelable {

    private DoubanRating rating;

    private String subtitle;//副标题

    private String[] author;//作者

    private String pubdate;//出版年

    private List<DoubanTag> tags;//标签，用于推荐其他图书

    private String origin_title;//原作名

    private String image;//封面图片

    private String binding;//装帧

    private String[] translator;//译者

    private String catalog;//目录

    private String pages;//页数

    private DoubanBookImage images;//封面图片

    private String alt;//豆瓣读书网址

    private String id;//图书ID

    private String publisher;//出版商

    private String isbn10;//10位ISBN号

    private String isbn13;//13位ISBN号

    private String title;//标题

    private String alt_title;//原标题

    private String author_intro;//作者介绍

    private String summary;//概述

    private String price;//定价

    private String msg;

    private String code;

    private String request;

    private List<DoubanReview> reviews;

    private String count;

    private int total;

    private String start;

    public DoubanBook() {}

    public DoubanRating getRating() {
        return rating;
    }

    public void setRating(DoubanRating rating) {
        this.rating = rating;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String[] getAuthor() {
        return author;
    }

    public void setAuthor(String[] author) {
        this.author = author;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public List<DoubanTag> getTags() {
        return tags;
    }

    public void setTags(List<DoubanTag> tags) {
        this.tags = tags;
    }

    public String getOrigin_title() {
        return origin_title;
    }

    public void setOrigin_title(String origin_title) {
        this.origin_title = origin_title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String[] getTranslator() {
        return translator;
    }

    public void setTranslator(String[] translator) {
        this.translator = translator;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public DoubanBookImage getImages() {
        return images;
    }

    public void setImages(DoubanBookImage images) {
        this.images = images;
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlt_title() {
        return alt_title;
    }

    public void setAlt_title(String alt_title) {
        this.alt_title = alt_title;
    }

    public String getAuthor_intro() {
        return author_intro;
    }

    public void setAuthor_intro(String author_intro) {
        this.author_intro = author_intro;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public List<DoubanReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<DoubanReview> reviews) {
        this.reviews = reviews;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getIsbn() {
        return isbn13;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Creator<DoubanBook> CREATOR = new Creator<DoubanBook>() {
        @Override
        public DoubanBook createFromParcel(Parcel source) {
            return new DoubanBook();
        }

        @Override
        public DoubanBook[] newArray(int size) {
            return new DoubanBook[size];
        }
    };

    public String getImageUrl() {
        DoubanBookImage doubanImg = getImages();
        String imgUrl = doubanImg.getLarge();
        if (TextUtils.isEmpty(imgUrl)) imgUrl = doubanImg.getMedium();
        return imgUrl;
    }

    public String getBookAuthor() {
        if (author != null && author.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < author.length; i++) {
                String au = author[0];
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(au);
            }
            return sb.toString();
        }
        return "";
    }
}
