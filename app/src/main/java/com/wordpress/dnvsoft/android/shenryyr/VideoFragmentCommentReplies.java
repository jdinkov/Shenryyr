package com.wordpress.dnvsoft.android.shenryyr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wordpress.dnvsoft.android.shenryyr.adapters.CommentAdapter;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncGetComments;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeComment;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

import java.util.ArrayList;

public class VideoFragmentCommentReplies extends Fragment {

    private String commentId;
    private String nextPageToken;
    private LinearLayout footer;
    private Button buttonLoadMore;
    private CommentAdapter adapter;
    private ImageView imageViewProfilePic;
    private TextView textViewProfileName;
    private TextView textViewCommentText;
    private ImageView imageViewLike;
    private TextView textViewLikeCount;
    private ImageView imageViewDislike;
    private ArrayList<YouTubeComment> youTubeComments = new ArrayList<>();

    public VideoFragmentCommentReplies() {
    }

    public static VideoFragmentCommentReplies newInstance(String id) {
        VideoFragmentCommentReplies videoFragmentCommentReplies = new VideoFragmentCommentReplies();
        Bundle bundle = new Bundle();
        bundle.putString("COMMENT_ID", id);
        videoFragmentCommentReplies.setArguments(bundle);
        return videoFragmentCommentReplies;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        commentId = getArguments().getString("COMMENT_ID");

        if (Network.IsDeviceOnline(getActivity())) {
            getYouTubeCommentHeader().execute();
            getYouTubeCommentReplies().execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_replies, container, false);

        ListView listView = (ListView) view.findViewById(R.id.listViewCommentReplies);

        RelativeLayout header = (RelativeLayout) inflater.inflate(R.layout.list_view_comments, listView, false);
        imageViewProfilePic = (ImageView) header.findViewById(R.id.imageViewProfilePic);
        textViewProfileName = (TextView) header.findViewById(R.id.textViewProfileName);
        textViewCommentText = (TextView) header.findViewById(R.id.textViewCommentText);
        imageViewLike = (ImageView) header.findViewById(R.id.imageViewLike);
        textViewLikeCount = (TextView) header.findViewById(R.id.textViewLikeCount);
        imageViewDislike = (ImageView) header.findViewById(R.id.imageViewDislike);
        listView.addHeaderView(header, null, false);

        footer = (LinearLayout) inflater.inflate(R.layout.footer_main, listView, false);
        buttonLoadMore = (Button) footer.findViewById(R.id.buttonFooterMain);
        if (Network.IsDeviceOnline(getActivity())) {
            footer.setVisibility(View.INVISIBLE);
        } else {
            footer.setVisibility(View.VISIBLE);
            buttonLoadMore.setText(R.string.refresh);
        }
        listView.addFooterView(footer, null, false);

        buttonLoadMore.setOnClickListener(onClickListener);

        adapter = new CommentAdapter(getActivity(), R.layout.list_view_comments, youTubeComments);
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        return view;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Network.IsDeviceOnline(getActivity())) {
                getYouTubeCommentReplies().execute();
            }
        }
    };

    private AsyncGetComments getYouTubeCommentHeader() {
        return new AsyncGetComments(getActivity(), commentId, null, null,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled()) {
                            YouTubeComment youTubeComment = result.getCommentReplies().get(0);
                            Picasso.with(getActivity()).load(youTubeComment.getAuthorImageUrl()).into(imageViewProfilePic);
                            textViewProfileName.setText(youTubeComment.getAuthorDisplayName());
                            textViewCommentText.setText(youTubeComment.getCommentText());
                            textViewLikeCount.setText(youTubeComment.getLikeCount());
                            switch (youTubeComment.getViewerRating()) {
                                case "like": {
                                    imageViewLike.setImageDrawable(getResources().getDrawable(R.drawable.liked_video));
                                }
                                break;
                                case "dislike": {
                                    imageViewDislike.setImageDrawable(getResources().getDrawable(R.drawable.disliked_video));
                                }
                                break;
                            }
                        }
                    }
                });
    }

    private AsyncGetComments getYouTubeCommentReplies() {
        return new AsyncGetComments(getActivity(), null, nextPageToken, commentId,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getCommentReplies() != null) {
                            if (result.getCommentReplies().size() == 20) {
                                footer.setVisibility(View.VISIBLE);
                            }

                            nextPageToken = result.getNextPageToken();
                            youTubeComments.addAll(result.getCommentReplies());
                            buttonLoadMore.setText(R.string.load_more);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
