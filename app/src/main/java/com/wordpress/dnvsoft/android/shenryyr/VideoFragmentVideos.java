package com.wordpress.dnvsoft.android.shenryyr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.wordpress.dnvsoft.android.shenryyr.adapters.VideoAdapter;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncSearch;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItem;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItemWrapper;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

import java.util.ArrayList;

public class VideoFragmentVideos extends Fragment {

    private LinearLayout footer;
    private Button buttonLoadMore;
    private VideoAdapter adapter;
    private String nextPageToken;
    private String playlistId;
    private String videoTags;
    private String videoID;
    private ArrayList<VideoItem> videoItems = new ArrayList<>();

    public VideoFragmentVideos(ArrayList<VideoItem> items, String playlistId, String videoID, String videoTags) {
        this.videoItems = items;
        this.playlistId = playlistId;
        this.videoID = videoID;
        this.videoTags = videoTags;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (playlistId == null) {
            getVideosFromYouTube();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_videos, container, false);

        ListView listView = (ListView) view.findViewById(R.id.listViewVideo);

        footer = (LinearLayout) inflater.inflate(R.layout.footer_main, listView, false);
        buttonLoadMore = (Button) footer.findViewById(R.id.buttonFooterMain);
        if (Network.IsDeviceOnline(getActivity())) {
            footer.setVisibility(View.GONE);
        } else {
            footer.setVisibility(View.VISIBLE);
            buttonLoadMore.setText(R.string.refresh);
        }
        listView.addFooterView(footer, null, false);

        buttonLoadMore.setOnClickListener(buttonLoadMoreOnClickListener);
        listView.setOnItemClickListener(onItemClickListener);

        adapter = new VideoAdapter(getContext(), R.layout.list_view_items_play_list_items,
                R.id.listViewTitlePlayListItems, R.id.listViewThumbnailPlayListItems, videoItems);
        listView.setAdapter(adapter);

        if (playlistId != null) {
            footer.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();

        return view;
    }

    View.OnClickListener buttonLoadMoreOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            footer.setVisibility(View.GONE);
            getVideosFromYouTube();
        }
    };

    private void getVideosFromYouTube() {
        if (Network.IsDeviceOnline(getActivity())) {

            String searchOrder = "relevance";
            String type = "video";
            String fields = "items(id/videoId,snippet(title,thumbnails/medium/url)),nextPageToken";

            AsyncSearch getItems = new AsyncSearch(getContext(),
                    videoTags, searchOrder, type, fields, nextPageToken,
                    new TaskCompleted() {
                        @Override
                        public void onTaskComplete(YouTubeResult result) {
                            if (!result.isCanceled() && result.getVideos() != null) {
                                if (result.getVideos().size() == 20) {
                                    footer.setVisibility(View.VISIBLE);
                                }

                                nextPageToken = result.getNextPageToken();
                                for (VideoItem item : result.getVideos()) {
                                    if (!item.getId().equals(videoID)) {
                                        videoItems.add(item);
                                    }
                                }

                                buttonLoadMore.setText(R.string.load_more);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

            getItems.execute();
        } else {
            Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
        }
    }

    ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), VideoActivity.class);
            if (playlistId != null) {
                int videoPosition = videoItems.size() - position - 1;

                intent.putExtra("PLAYLIST_ID", playlistId);
                intent.putExtra("VIDEO_POSITION", videoPosition);
                intent.putExtra("ITEMS", new VideoItemWrapper(videoItems));
            } else {
                intent.putExtra("VIDEO_ID", videoItems.get(position).getId());
                intent.putExtra("VIDEO_TITLE", videoItems.get(position).getTitle());
            }

            startActivity(intent);
        }
    };
}
