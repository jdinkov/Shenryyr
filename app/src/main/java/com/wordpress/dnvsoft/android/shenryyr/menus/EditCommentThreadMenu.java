package com.wordpress.dnvsoft.android.shenryyr.menus;

import android.content.Context;
import android.widget.Toast;

import com.wordpress.dnvsoft.android.shenryyr.OnCommentAddEditListener;
import com.wordpress.dnvsoft.android.shenryyr.R;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncEditCommentThread;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

public class EditCommentThreadMenu extends EditCommentMenu {

    EditCommentThreadMenu(Context context, String Id, String commentText, OnCommentAddEditListener listener) {
        super(context, Id, commentText, listener);
    }

    @Override
    protected void updateOnClick() {
        if (Network.IsDeviceOnline(context)) {
            AsyncEditCommentThread editCommentThread = new AsyncEditCommentThread(
                    context, Id, editText.getText().toString(),
                    new TaskCompleted() {
                        @Override
                        public void onTaskComplete(YouTubeResult result) {
                            Toast.makeText(context, "Comment updated.", Toast.LENGTH_SHORT).show();
                            onCommentEditListener.onFinishEdit();
                        }
                    });

            editCommentThread.execute();
        } else {
            Toast.makeText(context, R.string.no_network, Toast.LENGTH_SHORT).show();
        }
    }
}
