package com.wordpress.dnvsoft.android.shenryyr.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wordpress.dnvsoft.android.shenryyr.R;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeComment;

import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter {

    protected Context context;
    protected int layout;
    ArrayList objects;

    public CommentAdapter(@NonNull Context context, int layout, ArrayList objects) {
        super(context, layout, objects);
        this.context = context;
        this.layout = layout;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, parent, false);
        }

        ImageView imageViewProfilePic = (ImageView) convertView.findViewById(R.id.imageViewProfilePic);
        TextView textViewProfileName = (TextView) convertView.findViewById(R.id.textViewProfileName);
        TextView textViewCommentText = (TextView) convertView.findViewById(R.id.textViewCommentText);
        ImageView imageViewLike = (ImageView) convertView.findViewById(R.id.imageViewLike);
        TextView textViewLikeCount = (TextView) convertView.findViewById(R.id.textViewLikeCount);
        ImageView imageViewDislike = (ImageView) convertView.findViewById(R.id.imageViewDislike);

        final YouTubeComment youTubeComment = (YouTubeComment) objects.get(position);
        Picasso.with(context).load(youTubeComment.getAuthorImageUrl()).into(imageViewProfilePic);
        textViewProfileName.setText(youTubeComment.getAuthorDisplayName());
        textViewCommentText.setText(youTubeComment.getCommentText());
        textViewLikeCount.setText(youTubeComment.getLikeCount());
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
}
