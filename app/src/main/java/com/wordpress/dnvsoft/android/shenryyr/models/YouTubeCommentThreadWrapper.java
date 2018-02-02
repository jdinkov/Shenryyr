package com.wordpress.dnvsoft.android.shenryyr.models;

import java.io.Serializable;
import java.util.ArrayList;

public class YouTubeCommentThreadWrapper implements Serializable {

    private ArrayList<YouTubeCommentThread> commentThreads;

    public YouTubeCommentThreadWrapper(ArrayList<YouTubeCommentThread> comments) {
        this.commentThreads = comments;
    }

    public ArrayList<YouTubeCommentThread> getCommentThreads() {
        return commentThreads;
    }
}
