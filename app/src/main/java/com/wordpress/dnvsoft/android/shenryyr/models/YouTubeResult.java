package com.wordpress.dnvsoft.android.shenryyr.models;

import java.util.ArrayList;

public class YouTubeResult {

    private boolean isCanceled;
    private String nextPageToken;
    private ArrayList<VideoItem> videos;
    private ArrayList<YouTubeCommentThread> commentThread;

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
}
