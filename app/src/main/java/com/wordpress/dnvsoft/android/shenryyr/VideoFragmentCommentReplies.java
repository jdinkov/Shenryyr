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
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.squareup.picasso.Picasso;
import com.wordpress.dnvsoft.android.shenryyr.adapters.CommentAdapter;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncGetComments;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.menus.EditCommentMenu;
import com.wordpress.dnvsoft.android.shenryyr.menus.InsertCommentReplyMenu;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeComment;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

import java.util.ArrayList;

public class VideoFragmentCommentReplies extends Fragment implements EditCommentMenu.OnCommentEditListener {

    private String nextPageToken;
    private LinearLayout footer;
    private Button buttonLoadMore;
    private CommentAdapter adapter;
    private YouTubeComment youTubeComment = new YouTubeComment();
    private ArrayList<YouTubeComment> youTubeComments = new ArrayList<>();
    private static final String COMMENT_ID = "COMMENT_ID";
    private static final String COMMENT_IMAGE_URL = "COMMENT_IMAGE_URL";
    private static final String COMMENT_DISPLAY_NAME = "COMMENT_DISPLAY_NAME";
    private static final String COMMENT_TEXT = "COMMENT_TEXT";
    private static final String COMMENT_LIKE_COUNT = "COMMENT_LIKE_COUNT";
    private static final String COMMENT_VIEWER_RATING = "COMMENT_VIEWER_RATING";

    public VideoFragmentCommentReplies() {
    }

    public static VideoFragmentCommentReplies newInstance(YouTubeComment youTubeComment) {
        VideoFragmentCommentReplies videoFragmentCommentReplies = new VideoFragmentCommentReplies();
        Bundle bundle = new Bundle();
        bundle.putString(COMMENT_ID, youTubeComment.getID());
        bundle.putString(COMMENT_IMAGE_URL, youTubeComment.getAuthorImageUrl());
        bundle.putString(COMMENT_DISPLAY_NAME, youTubeComment.getAuthorDisplayName());
        bundle.putString(COMMENT_TEXT, youTubeComment.getCommentText());
        bundle.putString(COMMENT_LIKE_COUNT, youTubeComment.getLikeCount());
        bundle.putString(COMMENT_VIEWER_RATING, youTubeComment.getViewerRating());
        videoFragmentCommentReplies.setArguments(bundle);
        return videoFragmentCommentReplies;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        youTubeComment.setID(getArguments().getString(COMMENT_ID));
        youTubeComment.setAuthorImageUrl(getArguments().getString(COMMENT_IMAGE_URL));
        youTubeComment.setAuthorDisplayName(getArguments().getString(COMMENT_DISPLAY_NAME));
        youTubeComment.setCommentText(getArguments().getString(COMMENT_TEXT));
        youTubeComment.setLikeCount(getArguments().getString(COMMENT_LIKE_COUNT));
        youTubeComment.setViewerRating(getArguments().getString(COMMENT_VIEWER_RATING));

        if (Network.IsDeviceOnline(getActivity())) {
            getYouTubeCommentReplies().execute();
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (youTubeComments.size() != 0 && youTubeComments.size() % 20 == 0) {
            footer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_replies, container, false);

        ListView listView = (ListView) view.findViewById(R.id.listViewCommentReplies);

        RelativeLayout header = (RelativeLayout) inflater.inflate(R.layout.list_view_comments, listView, false);
        ImageView imageViewProfilePic = (ImageView) header.findViewById(R.id.imageViewProfilePic);
        TextView textViewProfileName = (TextView) header.findViewById(R.id.textViewProfileName);
        TextView textViewCommentText = (TextView) header.findViewById(R.id.textViewCommentText);
        ImageView imageViewLike = (ImageView) header.findViewById(R.id.imageViewLike);
        TextView textViewLikeCount = (TextView) header.findViewById(R.id.textViewLikeCount);
        ImageView imageViewDislike = (ImageView) header.findViewById(R.id.imageViewDislike);
        Button buttonEditComment = (Button) header.findViewById(R.id.buttonEditComment);
        buttonEditComment.setVisibility(View.GONE);

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

        listView.addHeaderView(header, null, false);

        Button buttonExit = (Button) view.findViewById(R.id.buttonExit);
        Button buttonAddReply = (Button) view.findViewById(R.id.buttonAddReply);
        buttonExit.setOnClickListener(onClickListener);
        buttonAddReply.setOnClickListener(onClickListener);

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

        adapter = new CommentAdapter(getActivity(), R.layout.list_view_comments, youTubeComments, true,
                VideoFragmentCommentReplies.this);
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        return view;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonFooterMain: {
                    if (Network.IsDeviceOnline(getActivity())) {
                        getYouTubeCommentReplies().execute();
                    }
                }
                break;
                case R.id.buttonExit: {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                break;
                case R.id.buttonAddReply: {
                    if (GoogleSignIn.getLastSignedInAccount(getActivity()) != null) {
                        InsertCommentReplyMenu commentReplyMenu = new InsertCommentReplyMenu(getActivity(), youTubeComment.getID());
                        commentReplyMenu.ShowDialog();
                    } else {
                        Toast.makeText(getActivity(), R.string.unauthorized, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
        }
    };

    private AsyncGetComments getYouTubeCommentReplies() {
        return new AsyncGetComments(getActivity(), null, nextPageToken, youTubeComment.getID(),
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

    @Override
    public void onFinishEdit() {
        youTubeComments.clear();
        getYouTubeCommentReplies().execute();
    }
}
