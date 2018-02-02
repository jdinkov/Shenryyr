package com.wordpress.dnvsoft.android.shenryyr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wordpress.dnvsoft.android.shenryyr.adapters.CommentThreadAdapter;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeCommentThread;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeCommentThreadWrapper;

import java.util.ArrayList;

public class VideoFragmentComments extends Fragment {

    private CommentThreadAdapter adapter;
    private static String COMMENT_THREAD = "comment_thread";
    private ArrayList<YouTubeCommentThread> commentThreads = new ArrayList<>();

    public VideoFragmentComments() {
    }

    public static VideoFragmentComments newInstance(ArrayList<YouTubeCommentThread> comments) {
        VideoFragmentComments fragment = new VideoFragmentComments();
        Bundle bundle = new Bundle();
        bundle.putSerializable(COMMENT_THREAD, new YouTubeCommentThreadWrapper(comments));
        fragment.setArguments(bundle);
        return fragment;
    }

    public void updateComments(ArrayList<YouTubeCommentThread> comments) {
        commentThreads.addAll(comments);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_video_comments, container, false);

        ListView listViewComments = (ListView) fragment.findViewById(R.id.listViewComments);

        adapter = new CommentThreadAdapter(getActivity(), R.layout.list_view_comments, commentThreads);
        listViewComments.setAdapter(adapter);

        if (commentThreads.size() == 0) {
            YouTubeCommentThreadWrapper wrapper =
                    (YouTubeCommentThreadWrapper) getArguments().getSerializable(COMMENT_THREAD);
            commentThreads.addAll(wrapper.getCommentThreads());
        }

        adapter.notifyDataSetChanged();

        return fragment;
    }
}
