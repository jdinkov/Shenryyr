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
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncEditCommentReply;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

public class EditCommentMenu {

    protected Context context;
    private String commentText;
    String Id;
    EditText editText;
    OnCommentAddEditListener onCommentEditListener;

    EditCommentMenu(Context context, String Id, String commentText, OnCommentAddEditListener listener) {
        this.context = context;
        this.Id = Id;
        this.commentText = commentText;
        this.onCommentEditListener = listener;
    }

    void ShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.menu_insert_comment, null);
        editText = (EditText) layout.findViewById(R.id.editTextInsertComment);
        editText.setText(commentText);

        builder.setTitle("Edit comment.");

        builder.setView(layout);

        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateOnClick();
            }
        });

        builder.setNegativeButton(R.string.negative_button, null);
        builder.create().show();
    }

    protected void updateOnClick() {
        if (Network.IsDeviceOnline(context)) {
            AsyncEditCommentReply editCommentReply = new AsyncEditCommentReply(
                    context, Id, editText.getText().toString(),
                    new TaskCompleted() {
                        @Override
                        public void onTaskComplete(YouTubeResult result) {
                            if (!result.isCanceled()) {
                                Toast.makeText(context, "Comment updated.", Toast.LENGTH_SHORT).show();
                                onCommentEditListener.onFinishEdit();
                            }
                        }
                    });

            editCommentReply.execute();
        } else {
            Toast.makeText(context, R.string.no_network, Toast.LENGTH_SHORT).show();
        }
    }
}
