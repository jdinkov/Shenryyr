package com.wordpress.dnvsoft.android.shenryyr.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.wordpress.dnvsoft.android.shenryyr.YoutubeInfo;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;

import java.io.IOException;

public class AsyncVideosRate extends AsyncYoutube {

    private String videoID;
    private String videoRating;

    public AsyncVideosRate(Context c, String id, String rating, TaskCompleted callback) {
        super(c, callback);
        this.videoID = id;
        this.videoRating = rating;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {

        YouTube.Videos.Rate videoRate = youtube.videos().rate(videoID, videoRating);
        if (accountEmail == null) {
            videoRate.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        videoRate.execute();

        return result;
    }
}
