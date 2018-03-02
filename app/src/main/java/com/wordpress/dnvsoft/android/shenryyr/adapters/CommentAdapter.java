package com.wordpress.dnvsoft.android.shenryyr.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wordpress.dnvsoft.android.shenryyr.R;
import com.wordpress.dnvsoft.android.shenryyr.menus.CommentOptionMenu;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeComment;

import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter {

    protected Context context;
    protected int layout;
    private boolean isPaddingSet;
    String channelId;
    ArrayList objects;

    public CommentAdapter(@NonNull Context context, int layout, ArrayList objects, boolean isPaddingSet) {
        super(context, layout, objects);
        this.context = context;
        this.layout = layout;
        this.objects = objects;
        this.isPaddingSet = isPaddingSet;
        SharedPreferences preferences =
                context.getSharedPreferences("CHANNEL_ID_PREFERENCES", Context.MODE_PRIVATE);
        channelId = preferences.getString("CHANNEL_ID", null);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, parent, false);
        }

        if (isPaddingSet) {
            float scale = context.getResources().getDisplayMetrics().density;
            convertView.setPadding((int) scale * 50, 0, 0, 0);
        }

        ImageView imageViewProfilePic = (ImageView) convertView.findViewById(R.id.imageViewProfilePic);
        TextView textViewProfileName = (TextView) convertView.findViewById(R.id.textViewProfileName);
        TextView textViewCommentText = (TextView) convertView.findViewById(R.id.textViewCommentText);
        ImageView imageViewLike = (ImageView) convertView.findViewById(R.id.imageViewLike);
        TextView textViewLikeCount = (TextView) convertView.findViewById(R.id.textViewLikeCount);
        ImageView imageViewDislike = (ImageView) convertView.findViewById(R.id.imageViewDislike);
        Button buttonEditComment = (Button) convertView.findViewById(R.id.buttonEditComment);

        final YouTubeComment youTubeComment = (YouTubeComment) objects.get(position);
        Picasso.with(context).load(youTubeComment.getAuthorImageUrl()).into(imageViewProfilePic);
        textViewProfileName.setText(youTubeComment.getAuthorDisplayName());
        textViewCommentText.setText(youTubeComment.getCommentText());
        textViewLikeCount.setText(youTubeComment.getLikeCount());
        buttonEditComment.setTag(position);
        if (isPaddingSet && !youTubeComment.getAuthorChannelId().equals(channelId)) {
            buttonEditComment.setVisibility(View.GONE);
        } else {
            buttonEditComment.setVisibility(View.VISIBLE);
        }
        buttonEditComment.setOnClickListener(editOnClickListener);
        switch (youTubeComment.getViewerRating()) {
            case "like": {
                imageViewLike.setImageDrawable(convertView.getResources().getDrawable(R.drawable.liked_video));
            }
            break;
            case "dislike": {
                imageViewDislike.setImageDrawable(convertView.getResources().getDrawable(R.drawable.disliked_video));
            }
            break;
        }

        return convertView;
    }

    private View.OnClickListener editOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            SelectOption(position);
        }
    };

    protected void SelectOption(int position) {
        YouTubeComment comment = (YouTubeComment) objects.get(position);
        Enum<CommentOptionMenu.OptionsToDisplay> option;
        if (comment.getAuthorChannelId().equals(channelId)) {
            option = CommentOptionMenu.OptionsToDisplay.EDIT;
        } else {
            option = CommentOptionMenu.OptionsToDisplay.NONE;
        }

        CommentOptionMenu menu = new CommentOptionMenu(context, comment.getID(), option);
        menu.ShowDialog();
    }
}
