package com.wordpress.dnvsoft.android.shenryyr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wordpress.dnvsoft.android.shenryyr.models.VideoItem;

public class VideoFragmentDescription extends Fragment {

    private String videoViewCount;
    private String publishedAt;
    private String description;
    private TextView textViewVideoViewCount;
    private TextView textViewPublishedAt;
    private TextView textViewDescription;

    public VideoFragmentDescription() {
    }

    public static VideoFragmentDescription newInstance(String title) {
        VideoFragmentDescription fragment = new VideoFragmentDescription();
        Bundle bundle = new Bundle();
        bundle.putString("VIDEO_TITLE", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void updateFragment(VideoItem item){
        videoViewCount = item.getViewCount();
        textViewVideoViewCount.setText(videoViewCount);
        publishedAt = item.getPublishedAt();
        textViewPublishedAt.setText(publishedAt);
        description = item.getDescription();
        textViewDescription.setText(description);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        textViewVideoViewCount.setText(videoViewCount);
        textViewPublishedAt.setText(publishedAt);
        textViewDescription.setText(description);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_video_description, container, false);

        TextView textViewVideoTitle = (TextView) fragment.findViewById(R.id.textViewDescTitle);
        textViewVideoTitle.setText(getArguments().getString("VIDEO_TITLE"));

        textViewVideoViewCount = (TextView) fragment.findViewById(R.id.textViewDescViewCount);
        textViewPublishedAt = (TextView) fragment.findViewById(R.id.textViewDescPublishedAt);
        textViewDescription = (TextView) fragment.findViewById(R.id.textViewDescription);

        return fragment;
    }
}
