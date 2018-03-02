package com.wordpress.dnvsoft.android.shenryyr;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncGetRating;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncGetVideoDescription;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncVideosRate;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

public class VideoFragmentDescription extends Fragment {

    private String videoID;
    private String videoTitle;
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
    private RadioGroup radioGroup;
    private final String RATING_NONE = "none";
    private final String RATING_LIKE = "like";
    private final String RATING_DISLIKE = "dislike";
    private OnVideoDescriptionResponse callback;

    interface OnVideoDescriptionResponse {
        void setCommentCount(String commentCount);
    }

    public VideoFragmentDescription() {
    }

    public static VideoFragmentDescription newInstance(String id, String title) {
        VideoFragmentDescription videoFragmentDescription = new VideoFragmentDescription();
        Bundle bundle = new Bundle();
        bundle.putString("VIDEO_ID", id);
        bundle.putString("VIDEO_TITLE", title);
        videoFragmentDescription.setArguments(bundle);
        return videoFragmentDescription;
    }

    private void checkRadioGroup(String rating) {
        if (getView() != null && rating != null) {
            switch (rating) {
                case RATING_NONE: {
                    radioGroup.clearCheck();
                }
                break;
                case RATING_LIKE: {
                    radioButtonLike.setChecked(true);
                }
                break;
                case RATING_DISLIKE: {
                    radioButtonDislike.setChecked(true);
                }
                break;
            }
        }
    }

    private void populateViews() {
        if (getView() != null) {
            textViewVideoViewCount.setText(videoViewCount);
            textViewPublishedAt.setText(publishedAt);
            textViewDescription.setText(description);
            textViewLikeCount.setText(likeCount);
            textViewDislikeCount.setText(dislikeCount);
            checkRadioGroup(videoRating);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        populateViews();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callback = (OnVideoDescriptionResponse) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoID = getArguments().getString("VIDEO_ID");
        videoTitle = getArguments().getString("VIDEO_TITLE");

        if (Network.IsDeviceOnline(getActivity())) {
            getVideoDescription().execute();
            if (GoogleSignIn.getLastSignedInAccount(getActivity()) != null) {
                getVideoRating().execute();
            }
        } else {
            Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_video_description, container, false);

        TextView textViewVideoTitle = (TextView) fragment.findViewById(R.id.textViewDescTitle);
        textViewVideoTitle.setText(videoTitle);

        textViewVideoViewCount = (TextView) fragment.findViewById(R.id.textViewDescViewCount);
        textViewPublishedAt = (TextView) fragment.findViewById(R.id.textViewDescPublishedAt);
        textViewDescription = (TextView) fragment.findViewById(R.id.textViewDescription);
        textViewLikeCount = (TextView) fragment.findViewById(R.id.likeCount);
        textViewDislikeCount = (TextView) fragment.findViewById(R.id.dislikeCount);
        radioButtonLike = (RadioButton) fragment.findViewById(R.id.radioButtonLike);
        radioButtonDislike = (RadioButton) fragment.findViewById(R.id.radioButtonDislike);
        radioGroup = (RadioGroup) fragment.findViewById(R.id.radioGroupLikedVideos);
        radioButtonLike.setOnClickListener(onClickListener);
        radioButtonDislike.setOnClickListener(onClickListener);

        populateViews();

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
                            if (videoRating.equals(RATING_LIKE)) {
                                videoRating = RATING_NONE;
                            } else {
                                videoRating = RATING_LIKE;
                            }
                        }
                        break;
                        case R.id.radioButtonDislike: {
                            if (videoRating.equals(RATING_DISLIKE)) {
                                videoRating = RATING_NONE;
                            } else {
                                videoRating = RATING_DISLIKE;
                            }
                        }
                        break;
                    }

                    AsyncVideosRate videosRate = new AsyncVideosRate(getActivity(), videoID, videoRating,
                            new TaskCompleted() {
                                @Override
                                public void onTaskComplete(YouTubeResult result) {
                                    if (result.isCanceled()) {
                                        videoRating = tempRating;
                                    } else {
                                        ratingChangedToast(videoRating);
                                    }

                                    checkRadioGroup(videoRating);
                                }
                            });

                    videosRate.execute();
                } else {
                    Toast.makeText(getActivity(), R.string.unauthorized, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
            }
            checkRadioGroup(videoRating);
        }
    };

    private AsyncGetVideoDescription getVideoDescription() {
        return new AsyncGetVideoDescription(getActivity(), videoID,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getVideos() != null) {
                            publishedAt = "Published on " + result.getVideos().get(0).getPublishedAt();
                            description = result.getVideos().get(0).getDescription();
                            likeCount = result.getVideos().get(0).getLikeCount();
                            dislikeCount = result.getVideos().get(0).getDislikeCount();
                            videoViewCount = result.getVideos().get(0).getViewCount() + " views";
                            callback.setCommentCount(result.getVideos().get(0).getCommentCount());
                            populateViews();
                        }
                    }
                });
    }

    private AsyncGetRating getVideoRating() {
        return new AsyncGetRating(getActivity(), videoID,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled()) {
                            videoRating = result.getVideos().get(0).getRating();
                            checkRadioGroup(videoRating);
                        }
                    }
                });
    }

    private void ratingChangedToast(String rating) {
        String toastText = null;
        switch (rating) {
            case RATING_NONE: {
                toastText = getString(R.string.removed_rating);
            }
            break;
            case RATING_LIKE: {
                toastText = getString(R.string.liked_video);
            }
            break;
            case RATING_DISLIKE: {
                toastText = getString(R.string.disliked_video);
            }
            break;
        }

        Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
    }
}
