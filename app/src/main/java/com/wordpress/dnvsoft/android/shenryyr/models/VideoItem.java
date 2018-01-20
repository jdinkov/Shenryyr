package com.wordpress.dnvsoft.android.shenryyr.models;

import java.io.Serializable;

public class VideoItem implements Serializable {

    private String title;
    private String thumbnailURL;
    private String thumbnailMaxResUrl;
    private String ID;
    private String rating;
    private String description;
    private String viewCount;
    private String likeCount;
    private String dislikeCount;
    private String commentCount;
    private String publishedAt;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(String dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}
