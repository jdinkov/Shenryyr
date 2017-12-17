package com.wordpress.dnvsoft.android.shenryyr.models;

import java.util.ArrayList;

public class YouTubeResult {

    private boolean isCanceled;
    private String nextPageToken;
    private ArrayList<VideoItem> items;

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

    public ArrayList<VideoItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<VideoItem> items) {
        this.items = items;
    }
}
