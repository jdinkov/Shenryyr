package com.wordpress.dnvsoft.android.shenryyr.async_tasks;

import android.content.Context;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.wordpress.dnvsoft.android.shenryyr.YoutubeInfo;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItem;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncPlaylistThumbnail extends AsyncYoutube {

    private String PlaylistID;

    public AsyncPlaylistThumbnail(Context context, String playlistID, TaskCompleted callback) {
        super(context, callback);
        this.PlaylistID = playlistID;
    }

    @Override
    @SuppressWarnings("DuplicateThrows")
    YouTubeResult DoItInBackground() throws GoogleJsonResponseException, IOException {
        PlaylistListResponse playlistListResponse;

        YouTube.Playlists.List playList = youtube.playlists().list("snippet");
        playList.setId(PlaylistID);
        playList.setFields("items/snippet/thumbnails/maxres/url");
        if (accountEmail == null) {
            playList.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        playlistListResponse = playList.execute();
        String maxResUrl = playlistListResponse.getItems().get(0).getSnippet().getThumbnails().getMaxres().getUrl();

        VideoItem item = new VideoItem();
        ArrayList<VideoItem> items = new ArrayList<>();
        item.setThumbnailMaxResUrl(maxResUrl);
        items.add(item);

        result.setItems(items);
        return result;
    }
}
