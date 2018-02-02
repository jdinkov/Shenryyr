package com.wordpress.dnvsoft.android.shenryyr.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wordpress.dnvsoft.android.shenryyr.R;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeCommentThread;

import java.util.ArrayList;

public class CommentThreadAdapter extends CommentAdapter {

    public CommentThreadAdapter(@NonNull Context context, int layout, ArrayList objects) {
        super(context, layout, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, parent, false);
        }

        TextView textViewRepliesCount = (TextView) convertView.findViewById(R.id.textViewRepliesCount);

        final YouTubeCommentThread youTubeComment = (YouTubeCommentThread) objects.get(position);
        textViewRepliesCount.setText(replyText(youTubeComment.getTotalReplyCount()));

        return convertView;
    }

    private String replyText(String number) {
        String text;
        if (number.equals("1")) {
            text = number + " reply";
        } else {
            text = number + " replies";
        }

        return text;
    }
}
