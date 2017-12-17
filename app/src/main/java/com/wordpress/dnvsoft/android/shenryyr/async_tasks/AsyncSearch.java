package com.wordpress.dnvsoft.android.shenryyr.async_tasks;

import android.content.Context;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.wordpress.dnvsoft.android.shenryyr.YoutubeInfo;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItem;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncSearch extends AsyncYoutube {

    private String parameter;
    private String orderBy;
    private String type;
    private String fields;
    private String pageToken;

    public AsyncSearch(Context context, String parameter, String orderBy,
                       String type, String fields, String pageToken, TaskCompleted callback) {
        super(context, callback);
        this.parameter = parameter;
        this.orderBy = orderBy;
        this.type = type;
        this.fields = fields;
        this.pageToken = pageToken;
    }

    @Override
    @SuppressWarnings("DuplicateThrows")
    YouTubeResult DoItInBackground() throws GoogleJsonResponseException, IOException {
        VideoItem item;
        ArrayList<VideoItem> videoItems = new ArrayList<>();

        YouTube.Search.List searchList = youtube.search().list("snippet");
        searchList.setChannelId(YoutubeInfo.CHANNEL_ID);
        searchList.setFields(fields);
        searchList.setPageToken(pageToken);
        searchList.setOrder(orderBy);
        searchList.setType(type);
        searchList.setQ(parameter);
        searchList.setMaxResults((long) 20);
        if (accountEmail == null) {
            searchList.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        SearchListResponse searchListResponse = searchList.execute();
        pageToken = searchListResponse.getNextPageToken();
        int size = searchListResponse.getItems().size();
        for (int i = 0; i < size; i++) {
            item = new VideoItem();
            if (type.equals("playlist")) {
                item.setId(searchListResponse.getItems().get(i).getId().getPlaylistId());
            } else {
                item.setId(searchListResponse.getItems().get(i).getId().getVideoId());
            }
            item.setTitle(searchListResponse.getItems().get(i).getSnippet().getTitle());
            item.setThumbnailURL(searchListResponse.getItems().get(i).getSnippet().getThumbnails().getMedium().getUrl());
            videoItems.add(item);
        }

        result.setNextPageToken(pageToken);
        result.setItems(videoItems);
        return result;
    }
}
