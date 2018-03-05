package com.wordpress.dnvsoft.android.shenryyr.menus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.wordpress.dnvsoft.android.shenryyr.OnCommentAddEditListener;
import com.wordpress.dnvsoft.android.shenryyr.R;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncInsertCommentReply;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

public class InsertCommentReplyMenu {

    private String commentId;
    private Context context;
    private OnCommentAddEditListener listener;

    public InsertCommentReplyMenu(Context context, String id, OnCommentAddEditListener listener) {
        this.context = context;
        this.commentId = id;
        this.listener = listener;
    }

    public void ShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.menu_insert_comment, null);
        final EditText editText = (EditText) layout.findViewById(R.id.editTextInsertComment);

        builder.setTitle("Add a reply.");

        builder.setView(layout);

        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Network.IsDeviceOnline(context)) {
                    AsyncInsertCommentReply asyncInsertCommentReply = new AsyncInsertCommentReply(
                            context, commentId, editText.getText().toString(),
                            new TaskCompleted() {
                                @Override
                                public void onTaskComplete(YouTubeResult result) {
                                    listener.onFinishEdit();
                                }
                            });

                    asyncInsertCommentReply.execute();
                } else {
                    Toast.makeText(context, R.string.no_network, Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton(R.string.negative_button, null);

        builder.create().show();
    }
}
