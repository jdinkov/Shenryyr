package com.wordpress.dnvsoft.android.shenryyr;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.wordpress.dnvsoft.android.shenryyr.adapters.VideoAdapter;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncSearch;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.menus.MissingServiceMenu;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItem;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItemWrapper;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.IConnected;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

import java.util.ArrayList;
import java.util.Stack;

public class VideoActivity extends YouTubeBaseActivity
        implements YouTubePlayer.OnInitializedListener, IConnected {

    private String videoID;
    private String nextPageToken;
    private ListView listView;
    private TextView textView;
    private Button buttonLoadMore;
    private VideoAdapter adapter;
    private YouTubePlayerView youTubePlayerView;
    private ArrayList<VideoItem> items = new ArrayList<>();
    private LinearLayout footer;
    private YouTubePlayer youTubePlayer;
    private int videoPosition;
    private String playlistID;
    private Stack<VideoItem> previous = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        listView = (ListView) findViewById(R.id.listViewVideo);

        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.videoPlayer);
        youTubePlayerView.initialize(YoutubeInfo.DEVELOPER_KEY, VideoActivity.this);

        LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_video_title, listView, false);
        textView = (TextView) header.findViewById(R.id.textViewVideo);
        listView.addHeaderView(header, null, false);

        footer = (LinearLayout) getLayoutInflater().inflate(R.layout.footer_main, listView, false);
        buttonLoadMore = (Button) footer.findViewById(R.id.buttonFooterMain);
        footer.setVisibility(View.GONE);
        listView.addFooterView(footer, null, false);

        buttonLoadMore.setOnClickListener(buttonLoadMoreOnClickListener);
        listView.setOnItemClickListener(onItemClickListener);

        adapter = new VideoAdapter(VideoActivity.this, R.layout.list_view_items_play_list_items,
                R.id.listViewTitlePlayListItems, R.id.listViewThumbnailPlayListItems, items);
        listView.setAdapter(adapter);

        videoPosition = getIntent().getIntExtra("VIDEO_POSITION", Integer.MIN_VALUE);
        if (videoPosition != Integer.MIN_VALUE) {
            VideoItemWrapper wrapper = (VideoItemWrapper) getIntent().getSerializableExtra("ITEMS");
            items.addAll(wrapper.getItems());
            adapter.notifyDataSetChanged();
            playlistID = getIntent().getStringExtra("PLAYLIST_ID");
            textView.setText(items.get(getItemPosition()).getTitle());
        } else {
            videoID = getIntent().getStringExtra("VIDEO_ID");
            textView.setText(getIntent().getStringExtra("VIDEO_TITLE"));
            Connect();
        }
    }

    View.OnClickListener buttonLoadMoreOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Connect();
        }
    };

    ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (youTubePlayer != null) {
                if (videoPosition != Integer.MIN_VALUE) {
                    videoPosition = items.size() - position;
                    textView.setText(items.get(getItemPosition()).getTitle());
                    youTubePlayer.loadPlaylist(playlistID, videoPosition, 0);
                } else {
                    VideoItem current = new VideoItem();
                    current.setId(videoID);
                    current.setTitle(textView.getText().toString());
                    videoID = items.get(position - 1).getId();
                    textView.setText(items.get(position - 1).getTitle());
                    youTubePlayer.loadVideo(videoID);
                    previous.push(current);
                }
                listView.setSelection(0);
            } else {
                Toast.makeText(VideoActivity.this, R.string.error_occurred, Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean b) {
        if (!b) {
            youTubePlayer = player;
            youTubePlayer.setShowFullscreenButton(false);
            youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);

            youTubePlayer.setPlaylistEventListener(new YouTubePlayer.PlaylistEventListener() {
                @Override
                public void onPrevious() {
                    videoPosition--;
                    textView.setText(items.get(getItemPosition()).getTitle());
                }

                @Override
                public void onNext() {
                    videoPosition++;
                    textView.setText(items.get(getItemPosition()).getTitle());
                }

                @Override
                public void onPlaylistEnded() {

                }
            });

            if (videoPosition != Integer.MIN_VALUE) {
                youTubePlayer.loadPlaylist(playlistID, videoPosition, 0);
            } else {
                youTubePlayer.loadVideo(videoID);
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        if (result == YouTubeInitializationResult.SERVICE_MISSING) {
            MissingServiceMenu serviceMenu = new MissingServiceMenu(VideoActivity.this,
                    getString(R.string.service_missing), YoutubeInfo.YOUTUBE_PACKAGE);
            serviceMenu.ShowDialog();
        } else if (result == YouTubeInitializationResult.SERVICE_DISABLED) {
            Toast.makeText(VideoActivity.this, R.string.service_disabled, Toast.LENGTH_LONG).show();
        } else if (result == YouTubeInitializationResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            Toast.makeText(VideoActivity.this, R.string.service_version_update_required, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(VideoActivity.this, result.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LinearLayout.LayoutParams playerParams = (LinearLayout.LayoutParams) youTubePlayerView.getLayoutParams();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            listView.setVisibility(View.GONE);
            playerParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            if (youTubePlayer != null) {
                youTubePlayer.setFullscreen(true);
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            listView.setVisibility(View.VISIBLE);
            playerParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            if (youTubePlayer != null) {
                youTubePlayer.setFullscreen(false);
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (!previous.isEmpty()) {
            VideoItem temp = previous.pop();
            videoID = temp.getId();
            textView.setText(temp.getTitle());
            if (youTubePlayer != null) {
                youTubePlayer.loadVideo(videoID);
            }
            listView.setSelection(0);
        } else {
            super.onBackPressed();
        }
    }

    private void Connect() {
        if (Network.IsDeviceOnline(VideoActivity.this)) {
            String searchOrder = "date";
            String type = "video";
            String fields = "items(id/videoId,snippet(title,thumbnails/medium/url)),nextPageToken";
            onPreExecute();
            AsyncSearch getItems = new AsyncSearch(getApplicationContext(),
                    null, searchOrder, type, fields, nextPageToken,
                    new TaskCompleted() {
                        @Override
                        public void onTaskComplete(YouTubeResult result) {
                            if (!result.isCanceled()) {
                                nextPageToken = result.getNextPageToken();
                                items.addAll(result.getItems());
                                if (items.size() != 0) {
                                    buttonLoadMore.setText(R.string.load_more);
                                } else {
                                    buttonLoadMore.setText(R.string.refresh);
                                }
                                onPostExecute();
                            }
                        }
                    });

            getItems.execute();

        } else {
            onDisconnected();
            Toast.makeText(getApplicationContext(), R.string.no_network, Toast.LENGTH_LONG).show();
        }
    }

    private int getItemPosition() {
        return items.size() - videoPosition - 1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout.LayoutParams playerParams = (LinearLayout.LayoutParams) youTubePlayerView.getLayoutParams();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            listView.setVisibility(View.GONE);
            playerParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            if (youTubePlayer != null) {
                youTubePlayer.setFullscreen(true);
            }
        }
    }

    @Override
    public void onPreExecute() {
        footer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPostExecute() {
        footer.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDisconnected() {
        footer.setVisibility(View.GONE);
    }

    @Override
    public void onCanceled() {
    }
}
