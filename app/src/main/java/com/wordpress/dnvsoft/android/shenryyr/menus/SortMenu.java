package com.wordpress.dnvsoft.android.shenryyr.menus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.wordpress.dnvsoft.android.shenryyr.R;

public class SortMenu {

    private int checkedRadioItem;
    private Context context;
    private SortItems callback;
    private String searchOrder = "date";

    public SortMenu(Context cont, int radioItem, SortItems call) {
        this.context = cont;
        this.checkedRadioItem = radioItem;
        this.callback = call;
    }

    public void Sort() {
        final String[] choice = {context.getString(R.string.menu_sort_date),
                context.getString(R.string.menu_sort_rating),
                context.getString(R.string.menu_sort_relevance),
                context.getString(R.string.menu_sort_title),
                context.getString(R.string.menu_sort_count)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.menu_sort_by);
        builder.setSingleChoiceItems(choice, checkedRadioItem, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                checkedRadioItem = item;
                searchOrder = choice[item];
            }
        });

        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onConnect(searchOrder, checkedRadioItem);
            }
        });
        builder.setNegativeButton(R.string.negative_button, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
