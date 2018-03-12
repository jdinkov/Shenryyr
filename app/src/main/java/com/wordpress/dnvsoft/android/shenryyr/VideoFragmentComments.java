package com.wordpress.dnvsoft.android.shenryyr;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wordpress.dnvsoft.android.shenryyr.adapters.CommentThreadAdapter;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncGetCommentThreads;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.menus.InsertCommentThreadMenu;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeCommentThread;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

import java.util.ArrayList;

public class VideoFragmentComments extends Fragment implements OnCommentAddEditListener {

    private String videoID;
    private String nextPageTokenCommentThread;
    private CommentThreadAdapter adapter;
    private ArrayList<YouTubeCommentThread> commentThreads = new ArrayList<>();
    private LinearLayout footer;
    private Button buttonLoadMore;
    private TextView textViewCommentCount;
    private OnCommentCountUpdate callback;
    private int spinnerPosition;

    @Override
    public void onFinishEdit() {
        commentThreads.clear();
        getCommentThreads(getCommentsOrder());
    }

    interface OnCommentCountUpdate {
        String getCommentCount();
    }

    public VideoFragmentComments() {
    }

    public static VideoFragmentComments newInstance(String id) {
        VideoFragmentComments videoFragmentComments = new VideoFragmentComments();
        Bundle bundle = new Bundle();
        bundle.putString("VIDEO_ID", id);
        videoFragmentComments.setArguments(bundle);
        return videoFragmentComments;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callback = (OnCommentCountUpdate) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoID = getArguments().getString("VIDEO_ID");

        if (commentThreads.size() == 0) {
            getCommentThreads(getCommentsOrder());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        updateCommentCount();
        if (nextPageTokenCommentThread != null) {
            footer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_video_comments, container, false);

        ListView listViewComments = (ListView) fragment.findViewById(R.id.listViewComments);

        RelativeLayout header = (RelativeLayout) inflater.inflate(R.layout.header_fragment_comments, listViewComments, false);
        Button buttonAddComment = (Button) header.findViewById(R.id.buttonAddComment);
        textViewCommentCount = (TextView) header.findViewById(R.id.textCommentCount);
        Spinner spinner = (Spinner) header.findViewById(R.id.spinnerSortComments);
        listViewComments.addHeaderView(header);

        footer = (LinearLayout) inflater.inflate(R.layout.footer_main, listViewComments, false);
        buttonLoadMore = (Button) footer.findViewById(R.id.buttonFooterMain);
        if (Network.IsDeviceOnline(getActivity())) {
            footer.setVisibility(View.INVISIBLE);
        } else {
            footer.setVisibility(View.VISIBLE);
            buttonLoadMore.setText(R.string.refresh);
        }
        listViewComments.addFooterView(footer, null, false);

        buttonLoadMore.setOnClickListener(buttonLoadMoreOnClickListener);

        adapter = new CommentThreadAdapter(getActivity(), R.layout.list_view_comments, commentThreads, videoID,
                VideoFragmentComments.this);
        listViewComments.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        updateCommentCount();
        buttonAddComment.setOnClickListener(addCommentOnClickListener);

        String[] spinnerEntities = {
                "Top comments",
                "Newest first"
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(), R.layout.support_simple_spinner_dropdown_item, spinnerEntities);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(onItemClickListener);

        return fragment;
    }

    View.OnClickListener addCommentOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InsertCommentThreadMenu insertCommentThreadMenu = new InsertCommentThreadMenu(
                    getActivity(), videoID, VideoFragmentComments.this);

            insertCommentThreadMenu.ShowDialog();
        }
    };

    View.OnClickListener buttonLoadMoreOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            footer.setVisibility(View.INVISIBLE);
            getCommentThreads(getCommentsOrder());
        }
    };

    AdapterView.OnItemSelectedListener onItemClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (spinnerPosition != position) {
                spinnerPosition = position;
                commentThreads.clear();
                footer.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                nextPageTokenCommentThread = null;
                getCommentThreads(getCommentsOrder());
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void getCommentThreads(String order) {
        if (Network.IsDeviceOnline(getActivity())) {
            AsyncGetCommentThreads asyncGetCommentThreads = new AsyncGetCommentThreads(getActivity(),
                    order, videoID, nextPageTokenCommentThread,
                    new TaskCompleted() {
                        @Override
                        public void onTaskComplete(YouTubeResult result) {
                            if (!result.isCanceled() && result.getCommentThread() != null) {
                                if (result.getCommentThread().size() % 20 == 0) {
                                    footer.setVisibility(View.VISIBLE);
                                }

                                nextPageTokenCommentThread = result.getNextPageToken();
                                commentThreads.addAll(result.getCommentThread());
                                buttonLoadMore.setText(R.string.load_more);
                                adapter.notifyDataSetChanged();
                                updateCommentCount();
                            }
                        }
                    });

            asyncGetCommentThreads.execute();
        } else {
            Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
        }
    }

    private void updateCommentCount() {
        if (getView() != null) {
            String commentCount = callback.getCommentCount();
            if (commentCount != null) {
                String commentCountText = commentCount + " comments";
                textViewCommentCount.setText(commentCountText);
            }
        }
    }

    private String getCommentsOrder() {
        switch (spinnerPosition) {
            case 0: {
                return "relevance";
            }
            case 1: {
                return "time";
            }
        }
        return "relevance";
    }
}
