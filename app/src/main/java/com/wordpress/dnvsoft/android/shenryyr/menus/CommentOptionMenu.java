package com.wordpress.dnvsoft.android.shenryyr.menus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.wordpress.dnvsoft.android.shenryyr.R;

import java.util.ArrayList;

public class CommentOptionMenu {

    private Context context;
    private int checkedItem = -1;
    private String commentId;
    private String[] optionArray;
    private final String NO_OPTION = " No Option Available ";
    private final String ADD_REPLY = " Add a Reply ";
    private final String EDIT = " Edit ";
    private final String DELETE = " Delete ";

    public enum OptionsToDisplay {
        NONE, INSERT_REPLY, EDIT, INSERT_AND_EDIT
    }

    public CommentOptionMenu(Context c, String id, Enum<OptionsToDisplay> options) {
        this.context = c;
        this.commentId = id;
        optionArray = getOptions(options);
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
                ArrayList<String[]> temp = new ArrayList<>();
                temp.add(optionInsert);
                temp.add(optionEdit);
                return (String[]) temp.toArray();
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
        }
    }
}
