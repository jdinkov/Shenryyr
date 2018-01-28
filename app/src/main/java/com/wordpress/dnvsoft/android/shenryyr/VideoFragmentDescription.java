package com.wordpress.dnvsoft.android.shenryyr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncVideosRate;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItem;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

public class VideoFragmentDescription extends Fragment {

    private String videoID;
    private String videoViewCount;
    private String publishedAt;
    private String description;
    private String videoRating = "none";
    private String likeCount;
    private String dislikeCount;
    private TextView textViewVideoViewCount;
    private TextView textViewPublishedAt;
    private TextView textViewDescription;
    private TextView textViewLikeCount;
    private TextView textViewDislikeCount;
    private RadioButton radioButtonLike;
    private RadioButton radioButtonDislike;

    public VideoFragmentDescription() {
    }

    public static VideoFragmentDescription newInstance(String id, String title) {
        VideoFragmentDescription fragment = new VideoFragmentDescription();
        Bundle bundle = new Bundle();
        bundle.putString("VIDEO_ID", id);
        bundle.putString("VIDEO_TITLE", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void updateFragment(VideoItem item) {
        videoViewCount = item.getViewCount();
        publishedAt = item.getPublishedAt();
        description = item.getDescription();
        likeCount = item.getLikeCount();
        dislikeCount = item.getDislikeCount();
        populateViews();
    }

    public void updateRadioGroup(String rating) {
        videoRating = rating;
        checkRadioGroup(videoRating);
    }

    private void checkRadioGroup(String rating) {
        if (rating != null) {
            switch (rating) {
                case "none": {
                    radioButtonLike.setChecked(false);
                    radioButtonDislike.setChecked(false);
                }
                break;
                case "like": {
                    radioButtonLike.setChecked(true);
                }
                break;
                case "dislike": {
                    radioButtonDislike.setChecked(true);
                }
                break;
            }
        }
    }

    private void populateViews() {
        textViewVideoViewCount.setText(videoViewCount);
        textViewPublishedAt.setText(publishedAt);
        textViewDescription.setText(description);
        textViewLikeCount.setText(likeCount);
        textViewDislikeCount.setText(dislikeCount);
        checkRadioGroup(videoRating);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        populateViews();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_video_description, container, false);

        videoID = getArguments().getString("VIDEO_ID");

        TextView textViewVideoTitle = (TextView) fragment.findViewById(R.id.textViewDescTitle);
        textViewVideoTitle.setText(getArguments().getString("VIDEO_TITLE"));

        textViewVideoViewCount = (TextView) fragment.findViewById(R.id.textViewDescViewCount);
        textViewPublishedAt = (TextView) fragment.findViewById(R.id.textViewDescPublishedAt);
        textViewDescription = (TextView) fragment.findViewById(R.id.textViewDescription);
        textViewLikeCount = (TextView) fragment.findViewById(R.id.likeCount);
        textViewDislikeCount = (TextView) fragment.findViewById(R.id.dislikeCount);
        radioButtonLike = (RadioButton) fragment.findViewById(R.id.radioButtonLike);
        radioButtonDislike = (RadioButton) fragment.findViewById(R.id.radioButtonDislike);
        radioButtonLike.setOnClickListener(onClickListener);
        radioButtonDislike.setOnClickListener(onClickListener);

        return fragment;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Network.IsDeviceOnline(getActivity())) {
                if (GoogleSignIn.getLastSignedInAccount(getActivity()) != null) {
                    final String tempRating = videoRating;
                    switch (v.getId()) {
                        case R.id.radioButtonLike: {
                            if (videoRating.equals("like")) {
                                videoRating = "none";
                            } else {
                                videoRating = "like";
                            }
                        }
                        break;
                        case R.id.radioButtonDislike: {
                            if (videoRating.equals("dislike")) {
                                videoRating = "none";
                            } else {
                                videoRating = "dislike";
                            }
                        }
                        break;
                    }

                    AsyncVideosRate videosRate = new AsyncVideosRate(getActivity(),
                            videoID, videoRating, new TaskCompleted() {
                        @Override
                        public void onTaskComplete(YouTubeResult result) {
                            if (result.isCanceled()) {
                                videoRating = tempRating;
                            }

                            ratingChangedToast(videoRating);
                            checkRadioGroup(videoRating);
                        }
                    });

                    videosRate.execute();
                } else {
                    checkRadioGroup(videoRating);
                    Toast.makeText(getActivity(), R.string.unauthorized, Toast.LENGTH_LONG).show();
                }
            } else {
                checkRadioGroup(videoRating);
                Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
            }
        }
    };

    private void ratingChangedToast(String rating) {
        String toastText = null;
        switch (rating) {
            case "none": {
                toastText = getString(R.string.removed_rating);
            }
            break;
            case "like": {
                toastText = getString(R.string.liked_video);
            }
            break;
            case "dislike": {
                toastText = getString(R.string.disliked_video);
            }
            break;
        }

        Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
    }
}
