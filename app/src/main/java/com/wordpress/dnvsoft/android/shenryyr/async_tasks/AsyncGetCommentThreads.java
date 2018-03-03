package com.wordpress.dnvsoft.android.shenryyr.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.wordpress.dnvsoft.android.shenryyr.YoutubeInfo;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeCommentThread;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncGetCommentThreads extends AsyncYoutube {

    private String order;
    private String videoID;
    private String pageToken;

    public AsyncGetCommentThreads(Context c, String order, String videoID, String pageToken, TaskCompleted callback) {
        super(c, callback);
        this.order = order;
        this.videoID = videoID;
        this.pageToken = pageToken;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {

        ArrayList<YouTubeCommentThread> commentThreadArrayList = new ArrayList<>();

        YouTube.CommentThreads.List commentThreadList = youtube.commentThreads().list("snippet");
        commentThreadList.setVideoId(videoID);
        commentThreadList.setPageToken(pageToken);
        commentThreadList.setOrder(order);
        commentThreadList.setFields("nextPageToken,items(snippet(canReply,totalReplyCount,isPublic,topLevelComment(id,snippet(authorDisplayName,authorProfileImageUrl,authorChannelId,textOriginal,viewerRating,likeCount))))");
        commentThreadList.setMaxResults((long) 20);
        if (accountEmail == null) {
            commentThreadList.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        CommentThreadListResponse response = commentThreadList.execute();
        result.setNextPageToken(response.getNextPageToken());
        int commentThreadSize = response.getItems().size();
        for (int i = 0; i < commentThreadSize; i++) {
            if (response.getItems().get(i).getSnippet().getIsPublic()) {
                YouTubeCommentThread commentThread = new YouTubeCommentThread();

                commentThread.setCanReply(response.getItems().get(i).getSnippet().getCanReply());
                commentThread.setTotalReplyCount(response.getItems().get(i).getSnippet().getTotalReplyCount().toString());

                commentThread.setID(response.getItems().get(i).getSnippet().getTopLevelComment().getId());
                commentThread.setAuthorDisplayName(response.getItems().get(i).getSnippet().getTopLevelComment().getSnippet().getAuthorDisplayName());
                commentThread.setAuthorImageUrl(response.getItems().get(i).getSnippet().getTopLevelComment().getSnippet().getAuthorProfileImageUrl());
                commentThread.setCommentText(response.getItems().get(i).getSnippet().getTopLevelComment().getSnippet().getTextOriginal());
                commentThread.setViewerRating(response.getItems().get(i).getSnippet().getTopLevelComment().getSnippet().getViewerRating());
                commentThread.setLikeCount(response.getItems().get(i).getSnippet().getTopLevelComment().getSnippet().getLikeCount().toString());
                String authorChannelId = response.getItems().get(i).getSnippet().getTopLevelComment().getSnippet().getAuthorChannelId().toString();
                commentThread.setAuthorChannelId(authorChannelId.substring(7, authorChannelId.length() - 1));

                commentThreadArrayList.add(commentThread);
            }
        }

        result.setCommentThread(commentThreadArrayList);
        return result;
    }
}
