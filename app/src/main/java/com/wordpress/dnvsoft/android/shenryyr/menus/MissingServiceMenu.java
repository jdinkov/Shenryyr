package com.wordpress.dnvsoft.android.shenryyr.menus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.wordpress.dnvsoft.android.shenryyr.R;
import com.wordpress.dnvsoft.android.shenryyr.YoutubeInfo;

public class MissingServiceMenu {

    private Context context;
    private String message;
    private String servicePackage;

    public MissingServiceMenu(Context con, String mess, String pack) {
        this.context = con;
        this.message = mess;
        this.servicePackage = pack;
    }

    public void ShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                            (YoutubeInfo.GOOGLE_PLAY + servicePackage)));
                } catch (android.content.ActivityNotFoundException a) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                            (YoutubeInfo.BROWSER_URL + servicePackage)));
                }
            }
        });
        builder.setNegativeButton(R.string.negative_button, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
