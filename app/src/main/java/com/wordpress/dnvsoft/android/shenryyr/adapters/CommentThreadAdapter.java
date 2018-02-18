package com.wordpress.dnvsoft.android.shenryyr.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wordpress.dnvsoft.android.shenryyr.R;
import com.wordpress.dnvsoft.android.shenryyr.VideoActivity;
import com.wordpress.dnvsoft.android.shenryyr.VideoFragmentCommentReplies;
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
        textViewRepliesCount.setTag(position);
        textViewRepliesCount.setOnClickListener(onClickListener);
        if (!youTubeComment.getTotalReplyCount().equals("0")) {
            textViewRepliesCount.setTextColor(convertView.getResources().getColor(R.color.colorPrimaryDark));
        } else {
            textViewRepliesCount.setTextColor(convertView.getResources().getColor(R.color.textColor));
        }

        return convertView;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            YouTubeCommentThread youTubeCommentThread = (YouTubeCommentThread) objects.get(position);
            if (!youTubeCommentThread.getTotalReplyCount().equals("0")) {
                FragmentTransaction fragmentTransaction =
                        ((VideoActivity) context).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.root_fragment,
                        VideoFragmentCommentReplies.newInstance(youTubeCommentThread.getID()));
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    };

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
