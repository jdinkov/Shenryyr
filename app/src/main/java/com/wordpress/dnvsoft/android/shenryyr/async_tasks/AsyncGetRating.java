package com.wordpress.dnvsoft.android.shenryyr.async_tasks;

import android.content.Context;
import android.util.Log;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoGetRatingResponse;
import com.wordpress.dnvsoft.android.shenryyr.YoutubeInfo;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItem;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncGetRating extends AsyncYoutube {

    public AsyncGetRating(Context c, TaskCompleted callback) {
        super(c, callback);
    }

    @Override
    @SuppressWarnings("DuplicateThrows")
    YouTubeResult DoItInBackground() throws GoogleJsonResponseException, IOException {

        VideoItem item = new VideoItem();
        ArrayList<VideoItem> items = new ArrayList<>();

        YouTube.Videos.GetRating videosRating = youtube.videos().getRating("rfGAi47zewc");
        videosRating.setFields("items");
        if (accountEmail == null) {
            videosRating.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        VideoGetRatingResponse response = videosRating.execute();
        item.setId(response.getItems().get(0).getVideoId());
        item.setRating(response.getItems().get(0).getRating());
        Log.d("VideoGetRatingResponseV", response.getItems().get(0).getVideoId());
        Log.d("VideoGetRatingResponseR", response.getItems().get(0).getRating());
        items.add(item);
        result.setItems(items);

        return result;
    }
}