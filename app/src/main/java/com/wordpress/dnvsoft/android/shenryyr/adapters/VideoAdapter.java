package com.wordpress.dnvsoft.android.shenryyr.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeThumbnailView;
import com.squareup.picasso.Picasso;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItem;

import java.util.ArrayList;

public class VideoAdapter extends ArrayAdapter<VideoItem> {

    private Context context;
    private int layout;
    private int title;
    private int thumbnail;
    private ArrayList<VideoItem> objects;

    public VideoAdapter(Context context, int layout, int title, int thumbnail, ArrayList<VideoItem> objects) {
        super(context, layout, objects);
        this.context = context;
        this.layout = layout;
        this.title = title;
        this.thumbnail = thumbnail;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(title);
        YouTubeThumbnailView youTubeThumbnailView = (YouTubeThumbnailView) convertView.findViewById(thumbnail);

        final VideoItem videoItemView = objects.get(position);
        textView.setText(videoItemView.getTitle());
        Picasso.with(context).load(videoItemView.getThumbnailURL()).into(youTubeThumbnailView);

        return convertView;
    }
}
