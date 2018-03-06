package com.wordpress.dnvsoft.android.shenryyr.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadSnippet;
import com.wordpress.dnvsoft.android.shenryyr.YoutubeInfo;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;

import java.io.IOException;

public class AsyncInsertCommentThread extends AsyncYoutube {

    private String videoId;
    private String commentText;

    public AsyncInsertCommentThread(Context c, String videoId, String commentText, TaskCompleted callback) {
        super(c, callback);
        this.videoId = videoId;
        this.commentText = commentText;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        Comment comment = new Comment();
        CommentSnippet commentSnippet = new CommentSnippet();
        CommentThread commentThread = new CommentThread();
        CommentThreadSnippet commentThreadSnippet = new CommentThreadSnippet();

        commentSnippet.setTextOriginal(commentText);
        comment.setSnippet(commentSnippet);
        commentThreadSnippet.setTopLevelComment(comment);
        commentThreadSnippet.setVideoId(videoId);
        commentThreadSnippet.setChannelId(YoutubeInfo.CHANNEL_ID);
        commentThread.setSnippet(commentThreadSnippet);

        youtube.commentThreads().insert("snippet", commentThread).execute();

        return result;
    }
}
