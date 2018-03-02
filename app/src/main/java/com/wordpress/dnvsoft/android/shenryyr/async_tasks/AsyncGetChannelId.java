package com.wordpress.dnvsoft.android.shenryyr.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;

import java.io.IOException;

public class AsyncGetChannelId extends AsyncYoutube {

    public AsyncGetChannelId(Context c, TaskCompleted callback) {
        super(c, callback);
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {

        YouTube.Channels.List channelList = youtube.channels().list("id");
        channelList.setMine(true);
        channelList.setFields("items/id");

        ChannelListResponse response = channelList.execute();
        result.setChannelId(response.getItems().get(0).getId());

        return result;
    }
}
