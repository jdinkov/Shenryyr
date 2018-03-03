package com.wordpress.dnvsoft.android.shenryyr.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadSnippet;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;

import java.io.IOException;

public class AsyncEditCommentThread extends AsyncYoutube {

    private String commentId;
    private String commentText;

    public AsyncEditCommentThread(Context c, String commentId, String commentText, TaskCompleted callback) {
        super(c, callback);
        this.commentId = commentId;
        this.commentText = commentText;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        CommentThread commentThread = new CommentThread();
        CommentThreadSnippet commentThreadSnippet = new CommentThreadSnippet();
        Comment comment = new Comment();
        CommentSnippet commentSnippet = new CommentSnippet();

        commentSnippet.setTextOriginal(commentText);
        comment.setSnippet(commentSnippet);
        commentThreadSnippet.setTopLevelComment(comment);
        commentThread.setId(commentId);
        commentThread.setSnippet(commentThreadSnippet);

        YouTube.CommentThreads.Update update = youtube.commentThreads().update("snippet", commentThread);
        update.execute();

        return result;
    }
}
