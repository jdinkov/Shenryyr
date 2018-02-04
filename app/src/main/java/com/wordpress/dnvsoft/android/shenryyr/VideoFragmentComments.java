package com.wordpress.dnvsoft.android.shenryyr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public VideoFragmentComments(String id) {
        videoID = id;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Network.IsDeviceOnline(getActivity())) {
            getCommentThreads().execute();
        } else {
            Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_video_comments, container, false);

        ListView listViewComments = (ListView) fragment.findViewById(R.id.listViewComments);

        adapter = new CommentThreadAdapter(getActivity(), R.layout.list_view_comments, commentThreads);
        listViewComments.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        return fragment;
    }

    private AsyncGetCommentThreads getCommentThreads() {
        return new AsyncGetCommentThreads(getActivity(), "relevance", videoID, nextPageTokenCommentThread,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled()) {
                            nextPageTokenCommentThread = result.getNextPageToken();
                            commentThreads.addAll(result.getCommentThread());
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
