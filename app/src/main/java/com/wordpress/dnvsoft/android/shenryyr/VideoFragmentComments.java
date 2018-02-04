package com.wordpress.dnvsoft.android.shenryyr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.wordpress.dnvsoft.android.shenryyr.adapters.CommentThreadAdapter;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncGetCommentThreads;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeCommentThread;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

import java.util.ArrayList;

public class VideoFragmentComments extends Fragment {

    private String videoID;
    private String nextPageTokenCommentThread;
    private CommentThreadAdapter adapter;
    private ArrayList<YouTubeCommentThread> commentThreads = new ArrayList<>();
    private LinearLayout footer;
    private Button buttonLoadMore;

    public VideoFragmentComments(String id) {
        videoID = id;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getCommentThreads();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_video_comments, container, false);

        ListView listViewComments = (ListView) fragment.findViewById(R.id.listViewComments);

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

        adapter = new CommentThreadAdapter(getActivity(), R.layout.list_view_comments, commentThreads);
        listViewComments.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        return fragment;
    }

    View.OnClickListener buttonLoadMoreOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            footer.setVisibility(View.INVISIBLE);
            getCommentThreads();
        }
    };

    private void getCommentThreads() {
        if (Network.IsDeviceOnline(getActivity())) {
            AsyncGetCommentThreads asyncGetCommentThreads = new AsyncGetCommentThreads(getActivity(),
                    "relevance", videoID, nextPageTokenCommentThread,
                    new TaskCompleted() {
                        @Override
                        public void onTaskComplete(YouTubeResult result) {
                            if (!result.isCanceled() && result.getCommentThread() != null) {
                                if (result.getCommentThread().size() == 20) {
                                    footer.setVisibility(View.VISIBLE);
                                }

                                nextPageTokenCommentThread = result.getNextPageToken();
                                commentThreads.addAll(result.getCommentThread());
                                buttonLoadMore.setText(R.string.load_more);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

            asyncGetCommentThreads.execute();
        } else {
            Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
        }
    }
}
