package com.wordpress.dnvsoft.android.shenryyr.menus;

import android.content.Context;
import android.widget.Toast;

import com.wordpress.dnvsoft.android.shenryyr.OnCommentAddEditListener;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncInsertCommentThread;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;

public class InsertCommentThreadMenu extends InsertCommentMenu {

    public InsertCommentThreadMenu(Context context, String id, OnCommentAddEditListener listener) {
        super(context, id, listener);
    }

    @Override
    protected String setTitle() {
        return "Add a comment.";
    }

    @Override
    protected void onPositiveButtonClicked() {
        AsyncInsertCommentThread insertCommentThread = new AsyncInsertCommentThread(
                context, Id, editText.getText().toString(),
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled()) {
                            Toast.makeText(context, "Comment added.", Toast.LENGTH_SHORT).show();
                            listener.onFinishEdit();
                        }
                    }
                }
        );

        insertCommentThread.execute();
    }
}
