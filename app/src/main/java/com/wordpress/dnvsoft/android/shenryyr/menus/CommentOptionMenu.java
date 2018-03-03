package com.wordpress.dnvsoft.android.shenryyr.menus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.wordpress.dnvsoft.android.shenryyr.R;

public class CommentOptionMenu {

    private Context context;
    private int checkedItem = -1;
    private String commentId;
    private String videoId;
    private EditCommentMenu.OnCommentEditListener listener;
    private String[] optionArray;
    private final String NO_OPTION = " No Option Available ";
    private final String ADD_REPLY = " Add a Reply ";
    private final String EDIT = " Edit ";
    private final String DELETE = " Delete ";

    public enum OptionsToDisplay {
        NONE, INSERT_REPLY, EDIT, INSERT_AND_EDIT
    }

    public CommentOptionMenu(Context c, String commentId, Enum<OptionsToDisplay> options, String videoId,
                             EditCommentMenu.OnCommentEditListener listener) {
        this.context = c;
        this.commentId = commentId;
        optionArray = getOptions(options);
        this.videoId = videoId;
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
                InsertCommentReplyMenu commentReplyMenu = new InsertCommentReplyMenu(context, commentId);
                commentReplyMenu.ShowDialog();
            }
            break;
            case EDIT: {
                if (videoId == null) {
                    EditCommentMenu menu = new EditCommentMenu(context, commentId, listener);
                    menu.ShowDialog();
                } else {
                    EditCommentThreadMenu menu = new EditCommentThreadMenu(context, commentId, listener);
                    menu.ShowDialog();
                }
            }
            break;
        }
    }
}
