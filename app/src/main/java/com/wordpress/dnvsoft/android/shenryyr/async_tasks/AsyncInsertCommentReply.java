package com.wordpress.dnvsoft.android.shenryyr.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;

import java.io.IOException;

public class AsyncInsertCommentReply extends AsyncYoutube {

    private String commentId;
    private String commentText;

    public AsyncInsertCommentReply(Context c, String id, String text, TaskCompleted callback) {
        super(c, callback);
        this.commentId = id;
        this.commentText = text;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        Comment comment = new Comment();
        CommentSnippet commentSnippet = new CommentSnippet();
        commentSnippet.setParentId(commentId);
        commentSnippet.setTextOriginal(commentText);
        comment.setSnippet(commentSnippet);

        YouTube.Comments.Insert commentInsert = youtube.comments().insert("snippet", comment);
        commentInsert.execute();

        return result;
    }
}
