package com.wordpress.dnvsoft.android.shenryyr.models;

import java.io.Serializable;

public class VideoItem implements Serializable {

    private String title;
    private String thumbnailURL;
    private String thumbnailMaxResUrl;
    private String ID;
    private String rating;

    public String getId() {
        return ID;
    }

    public void setId(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnail) {
        this.thumbnailURL = thumbnail;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getThumbnailMaxResUrl() {
        return thumbnailMaxResUrl;
    }

    public void setThumbnailMaxResUrl(String thumbnailMaxResUrl) {
        this.thumbnailMaxResUrl = thumbnailMaxResUrl;
    }
}
