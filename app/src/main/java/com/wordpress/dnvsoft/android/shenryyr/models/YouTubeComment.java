package com.wordpress.dnvsoft.android.shenryyr.models;

public class YouTubeComment {

    private String ID;
    private String authorDisplayName;
    private String authorImageUrl;
    private String commentText;
    private String viewerRating;
    private String likeCount;
    private String authorChannelId;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
    }

    public String getAuthorImageUrl() {
        return authorImageUrl;
    }

    public void setAuthorImageUrl(String authorImageUrl) {
        this.authorImageUrl = authorImageUrl;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getViewerRating() {
        return viewerRating;
    }

    public void setViewerRating(String viewerRating) {
        this.viewerRating = viewerRating;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getAuthorChannelId() {
        return authorChannelId;
    }

    public void setAuthorChannelId(String authorChannelId) {
        this.authorChannelId = authorChannelId;
    }
}
