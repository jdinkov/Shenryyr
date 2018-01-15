package com.wordpress.dnvsoft.android.shenryyr.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.wordpress.dnvsoft.android.shenryyr.YoutubeInfo;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItem;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncPlayLists extends AsyncYoutube {

    private String nextPageToken;

    public AsyncPlayLists(Context context, String nextPageToken, TaskCompleted callback) {
        super(context, callback);
        this.nextPageToken = nextPageToken;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        ArrayList<VideoItem> videoItems = new ArrayList<>();
        YouTube.Playlists.List playLists = youtube.playlists().list("id, snippet");

        playLists.setChannelId(YoutubeInfo.CHANNEL_ID);
        playLists.setFields("items(id,snippet(title,thumbnails/medium/url)),nextPageToken");
        playLists.setPageToken(nextPageToken);
        playLists.setMaxResults((long) 20);
        if (accountEmail == null) {
            playLists.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        PlaylistListResponse playlistListResponse = playLists.execute();
        nextPageToken = playlistListResponse.getNextPageToken();
        int size = playlistListResponse.getItems().size();
        for (int i = 0; i < size; i++) {
            VideoItem item = new VideoItem();
            item.setTitle(playlistListResponse.getItems().get(i).getSnippet().getTitle());
            item.setThumbnailURL(playlistListResponse.getItems().get(i).getSnippet().getThumbnails().getMedium().getUrl());
            item.setId(playlistListResponse.getItems().get(i).getId());
            videoItems.add(item);
        }

        result.setNextPageToken(nextPageToken);
        result.setItems(videoItems);
        return result;
    }
}
