package com.wordpress.dnvsoft.android.shenryyr.models;

import java.util.ArrayList;

public class YouTubeResult {

    private String channelId;
    private boolean isCanceled;
    private String nextPageToken;
    private ArrayList<VideoItem> videos;
    private ArrayList<YouTubeCommentThread> commentThread;
    private ArrayList<YouTubeComment> commentReplies;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public ArrayList<VideoItem> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<VideoItem> videos) {
        this.videos = videos;
    }

    public ArrayList<YouTubeCommentThread> getCommentThread() {
        return commentThread;
    }

    public void setCommentThread(ArrayList<YouTubeCommentThread> comments) {
        this.commentThread = comments;
    }

    public ArrayList<YouTubeComment> getCommentReplies() {
        return commentReplies;
    }

    public void setCommentReplies(ArrayList<YouTubeComment> commentReplies) {
        this.commentReplies = commentReplies;
    }
}
