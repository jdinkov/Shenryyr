package com.wordpress.dnvsoft.android.shenryyr.menus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.wordpress.dnvsoft.android.shenryyr.OnCommentAddEditListener;
import com.wordpress.dnvsoft.android.shenryyr.R;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncDeleteComment;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

public class CommentOptionMenu {

    private Context context;
    private int checkedItem = -1;
    private String commentId;
    private String videoId;
    private String commentText;
    private OnCommentAddEditListener listener;
    private String[] optionArray;
    private final String NO_OPTION = " No option available ";
    private final String ADD_REPLY = " Add a reply ";
    private final String EDIT = " Edit ";
    private final String DELETE = " Delete ";

    public enum OptionsToDisplay {
        NONE, INSERT_REPLY, EDIT, INSERT_AND_EDIT
    }

    public CommentOptionMenu(Context c, String commentId, String commentText, Enum<OptionsToDisplay> options, String videoId,
                             OnCommentAddEditListener listener) {
        this.context = c;
        this.commentId = commentId;
        optionArray = getOptions(options);
        this.videoId = videoId;
        this.commentText = commentText;
        this.listener = listener;
    }

    private final String[] optionNone = {
            NO_OPTION
    };

    private final String[] optionInsert = {
            ADD_REPLY
    };

    private final String[] optionEdit = {
            EDIT,
            DELETE
    };

    private final String[] optionAll = {
            ADD_REPLY,
            EDIT,
            DELETE
    };

    public void ShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Options:");
        builder.setSingleChoiceItems(optionArray, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedItem = which;
            }
        });

        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkedItem != -1) {
                    checkSelectedOption(optionArray[checkedItem]);
                }
            }
        });

        builder.setNegativeButton(R.string.negative_button, null);

        builder.create().show();
    }

    private String[] getOptions(Enum options) {
        switch ((OptionsToDisplay) options) {
            case NONE: {
                return optionNone;
            }
            case INSERT_REPLY: {
                return optionInsert;
            }
            case EDIT: {
                return optionEdit;
            }
            case INSERT_AND_EDIT: {
                return optionAll;
            }
        }
        return optionNone;
    }

    private void checkSelectedOption(String returnValue) {
        switch (returnValue) {
            case ADD_REPLY: {
                InsertCommentMenu commentReplyMenu = new InsertCommentMenu(context, commentId, listener);
                commentReplyMenu.ShowDialog();
            }
            break;
            case EDIT: {
                if (videoId == null) {
                    EditCommentMenu menu = new EditCommentMenu(context, commentId, commentText, listener);
                    menu.ShowDialog();
                } else {
                    EditCommentThreadMenu menu = new EditCommentThreadMenu(context, commentId, commentText, listener);
                    menu.ShowDialog();
                }
            }
            break;
            case DELETE: {
                ShowDialogOnDelete();
            }
            break;
        }
    }

    private void ShowDialogOnDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Comment?");
        builder.setMessage("Are you sure you want to delete this comment?");

        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Network.IsDeviceOnline(context)) {
                    AsyncDeleteComment asyncDeleteComment = new AsyncDeleteComment(
                            context, commentId, new TaskCompleted() {
                        @Override
                        public void onTaskComplete(YouTubeResult result) {
                            if (!result.isCanceled()) {
                                Toast.makeText(context, "Comment deleted.", Toast.LENGTH_SHORT).show();
                                listener.onFinishEdit();
                            }
                        }
                    });

                    asyncDeleteComment.execute();
                } else {
                    Toast.makeText(context, R.string.no_network, Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton(R.string.negative_button, null);

        builder.create().show();
    }
}
