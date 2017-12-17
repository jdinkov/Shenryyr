package com.wordpress.dnvsoft.android.shenryyr.models;

import java.io.Serializable;
import java.util.ArrayList;

public class VideoItemWrapper implements Serializable {

    private ArrayList<VideoItem> itemDetails;

    public VideoItemWrapper(ArrayList<VideoItem> items) {
        this.itemDetails = items;
    }

    public ArrayList<VideoItem> getItems() {
        return itemDetails;
    }
}
