package com.wordpress.dnvsoft.android.shenryyr.async_tasks;

import android.content.Context;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.wordpress.dnvsoft.android.shenryyr.YoutubeInfo;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItem;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncLatestVideos extends AsyncYoutube {

    private String PlaylistID;
    private long maxResults;
    private String nextPageToken;

    public AsyncLatestVideos(Context context, String playlistID,
                             String nextPageToken, long maxResults, TaskCompleted callback) {
        super(context, callback);
        this.PlaylistID = playlistID;
        this.nextPageToken = nextPageToken;
        this.maxResults = maxResults;
    }

    @Override
    @SuppressWarnings("DuplicateThrows")
    YouTubeResult DoItInBackground() throws GoogleJsonResponseException, IOException {
        ArrayList<VideoItem> videoItems = new ArrayList<>();
        YouTube.PlaylistItems.List playListItems = youtube.playlistItems().list("id,snippet,status");

        playListItems.setPlaylistId(PlaylistID);
        playListItems.setPageToken(nextPageToken);
        playListItems.setFields("items(status/privacyStatus,snippet(title,thumbnails/medium/url,resourceId/videoId)),nextPageToken");
        playListItems.setMaxResults(maxResults);
        if (accountEmail == null) {
            playListItems.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        PlaylistItemListResponse playlistItemListResponse = playListItems.execute();
        nextPageToken = playlistItemListResponse.getNextPageToken();
        playListItems.setPageToken(nextPageToken);
        int size = playlistItemListResponse.getItems().size();
        for (int i = 0; i < size; i++) {
            if (!playlistItemListResponse.getItems().get(i).getStatus().getPrivacyStatus().equals("private")) {
                VideoItem item = new VideoItem();
                item.setTitle(playlistItemListResponse.getItems().get(i).getSnippet().getTitle());
                item.setThumbnailURL(playlistItemListResponse.getItems().get(i).getSnippet().getThumbnails().getMedium().getUrl());
                item.setId(playlistItemListResponse.getItems().get(i).getSnippet().getResourceId().getVideoId());
                videoItems.add(item);
            }
        }

        result.setNextPageToken(nextPageToken);
        result.setItems(videoItems);
        return result;
    }
}
