package com.wordpress.dnvsoft.android.shenryyr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncGetRating;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncGetVideoDescription;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncSearch;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.menus.MissingServiceMenu;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItem;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItemWrapper;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.IConnected;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity
        implements YouTubePlayer.OnInitializedListener, IConnected {

    private String videoID;
    private String nextPageToken;
    private VideoItem videoItem;
    private String videoRating;
    private boolean isMinimized;
    //private ListView listView;
    //private TextView textView;
    //private Button buttonLoadMore;
    //private VideoAdapter adapter;
    //private YouTubePlayerView youTubePlayerView;
    private ArrayList<VideoItem> items;
    //private LinearLayout footer;
    private YouTubePlayer youTubePlayer;
    private int videoPosition;
    private String playlistID;
    //private Stack<VideoItem> previous;
    private TabsAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private YouTubePlayerFragment youTubePlayerFragment;
    private String videoTitle;
    private int currentVideoTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoItem = new VideoItem();
        items = new ArrayList<>();
        //previous = new Stack<>();

        //listView = (ListView) findViewById(R.id.listViewVideo);

//        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.videoPlayer);
//        youTubePlayerView.initialize(YoutubeInfo.DEVELOPER_KEY, VideoActivity.this);

        //LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_video_title, listView, false);
        //textView = (TextView) header.findViewById(R.id.textViewVideo);
        //listView.addHeaderView(header, null, false);

        //footer = (LinearLayout) getLayoutInflater().inflate(R.layout.footer_main, listView, false);
        //buttonLoadMore = (Button) footer.findViewById(R.id.buttonFooterMain);
        //footer.setVisibility(View.GONE);
        //listView.addFooterView(footer, null, false);

        //buttonLoadMore.setOnClickListener(buttonLoadMoreOnClickListener);
        //listView.setOnItemClickListener(onItemClickListener);

        //adapter = new VideoAdapter(VideoActivity.this, R.layout.list_view_items_play_list_items,
        //        R.id.listViewTitlePlayListItems, R.id.listViewThumbnailPlayListItems, items);
        //listView.setAdapter(adapter);

        youTubePlayerFragment =
                (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
        //youTubePlayerFragment.initialize(YoutubeInfo.DEVELOPER_KEY, this);

//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.youTubeContainer, new YouTubeFragment());
//        transaction.commit();

        mSectionsPagerAdapter = new TabsAdapter(getSupportFragmentManager());
//
        mViewPager = (ViewPager) findViewById(R.id.container_video);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        videoPosition = getIntent().getIntExtra("VIDEO_POSITION", Integer.MIN_VALUE);
        if (videoPosition != Integer.MIN_VALUE) {
            VideoItemWrapper wrapper = (VideoItemWrapper) getIntent().getSerializableExtra("ITEMS");
            items.addAll(wrapper.getItems());
            //adapter.notifyDataSetChanged();
            playlistID = getIntent().getStringExtra("PLAYLIST_ID");
            //textView.setText(items.get(getItemPosition()).getTitle());
            videoID = items.get(getItemPosition()).getId();
            videoTitle = items.get(getItemPosition()).getTitle();
        } else {
            videoID = getIntent().getStringExtra("VIDEO_ID");
            //textView.setText(getIntent().getStringExtra("VIDEO_TITLE"));
            videoTitle = getIntent().getStringExtra("VIDEO_TITLE");
        }

        AsyncGetVideoDescription description = getVideoDescription();
        AsyncGetRating rating = getVideoRating();

        if (Network.IsDeviceOnline(VideoActivity.this)) {
            description.execute();
            if (GoogleSignIn.getLastSignedInAccount(VideoActivity.this) != null) {
                rating.execute();
            }
        } else {
            Toast.makeText(VideoActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
        }

        if (videoPosition == Integer.MIN_VALUE) {
            if (Network.IsDeviceOnline(VideoActivity.this)) {
                getRelatedVideos().execute();
            } else {
                Toast.makeText(VideoActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
            }
        }
    }

//    View.OnClickListener buttonLoadMoreOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            getRelatedVideos();
//        }
//    };
//
//    ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            if (youTubePlayer != null) {
//                if (videoPosition != Integer.MIN_VALUE) {
//                    videoPosition = items.size() - position;
//                    //textView.setText(items.get(getItemPosition()).getTitle());
//                    youTubePlayer.loadPlaylist(playlistID, videoPosition, 0);
//                } else {
//                    VideoItem current = new VideoItem();
//                    current.setId(videoID);
//                    //current.setTitle(textView.getText().toString());
//                    videoID = items.get(position - 1).getId();
//                    //textView.setText(items.get(position - 1).getTitle());
//                    youTubePlayer.loadVideo(videoID);
//                    //previous.push(current);
//                }
//                //listView.setSelection(0);
//            } else {
//                Toast.makeText(VideoActivity.this, R.string.error_occurred, Toast.LENGTH_LONG).show();
//            }
//        }
//    };

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
                    Intent intent = new Intent(VideoActivity.this, VideoActivity.class);

                    intent.putExtra("PLAYLIST_ID", playlistID);
                    intent.putExtra("VIDEO_POSITION", videoPosition);
                    intent.putExtra("ITEMS", new VideoItemWrapper(items));
                    startActivity(intent);
                }

                @Override
                public void onNext() {
                    videoPosition++;
                    Intent intent = new Intent(VideoActivity.this, VideoActivity.class);

                    intent.putExtra("PLAYLIST_ID", playlistID);
                    intent.putExtra("VIDEO_POSITION", videoPosition);
                    intent.putExtra("ITEMS", new VideoItemWrapper(items));
                    startActivity(intent);
                }

                @Override
                public void onPlaylistEnded() {

                }
            });

            if (isMinimized) {
                if (videoPosition != Integer.MIN_VALUE) {
                    youTubePlayer.cuePlaylist(playlistID, videoPosition, currentVideoTime);
                } else {
                    youTubePlayer.cueVideo(videoID, currentVideoTime);
                }
            } else {
                if (videoPosition != Integer.MIN_VALUE) {
                    youTubePlayer.loadPlaylist(playlistID, videoPosition, currentVideoTime);
                } else {
                    youTubePlayer.loadVideo(videoID, currentVideoTime);
                }
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

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        LinearLayout.LayoutParams playerParams = (LinearLayout.LayoutParams) youTubePlayerView.getLayoutParams();
//
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            playerParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
//            if (youTubePlayer != null) {
//                youTubePlayer.setFullscreen(true);
//            }
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            playerParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//            if (youTubePlayer != null) {
//                youTubePlayer.setFullscreen(false);
//            }
//        }
//    }

//    @Override
//    public void onBackPressed() {
//
//        if (!previous.isEmpty()) {
//            VideoItem temp = previous.pop();
//            videoID = temp.getId();
//            //textView.setText(temp.getTitle());
//            if (youTubePlayer != null) {
//                youTubePlayer.loadVideo(videoID);
//            }
//            //listView.setSelection(0);
//        } else {
//            super.onBackPressed();
//        }
//    }

    private AsyncGetVideoDescription getVideoDescription() {
        //if (Network.IsDeviceOnline(VideoActivity.this)) {
        //String fields = "items(snippet(publishedAt,description),statistics(viewCount,likeCount,dislikeCount))";

        return new AsyncGetVideoDescription(VideoActivity.this, videoID, new TaskCompleted() {
            @Override
            public void onTaskComplete(YouTubeResult result) {
                if (!result.isCanceled() && result.getItems() != null) {
                    videoItem.setPublishedAt("Published on " + result.getItems().get(0).getPublishedAt());
                    videoItem.setDescription(result.getItems().get(0).getDescription());
                    videoItem.setLikeCount(result.getItems().get(0).getLikeCount());
                    videoItem.setDislikeCount(result.getItems().get(0).getDislikeCount());
                    videoItem.setViewCount(result.getItems().get(0).getViewCount() + " views");
                    postDescriptionInFragment();
                }
            }
        });

//        }
//        else {
//            //onDisconnected();
//            Toast.makeText(getApplicationContext(), R.string.no_network, Toast.LENGTH_LONG).show();
//            return null;
//        }
    }

    private AsyncGetRating getVideoRating() {
        //if (Network.IsDeviceOnline(VideoActivity.this)) {

        return new AsyncGetRating(VideoActivity.this, videoID, new TaskCompleted() {
            @Override
            public void onTaskComplete(YouTubeResult result) {
                if (!result.isCanceled()) {
                    videoRating = result.getItems().get(0).getRating();
                    postRatingInFragment();
                }
            }
        });

//        } else {
//            //onDisconnected();
//            Toast.makeText(getApplicationContext(), R.string.no_network, Toast.LENGTH_LONG).show();
//            return null;
//        }
    }

    private AsyncSearch getRelatedVideos() {
        //if (Network.IsDeviceOnline(VideoActivity.this)) {
        String searchOrder = "relevance";
        String type = "video";
        String fields = "items(id/videoId,snippet(title,thumbnails/medium/url)),nextPageToken";
        //onPreExecute();

        return new AsyncSearch(VideoActivity.this,
                getTagsByTitle(), searchOrder, type, fields, nextPageToken, new TaskCompleted() {
            @Override
            public void onTaskComplete(YouTubeResult result) {
                if (!result.isCanceled() && result.getItems() != null) {
                    nextPageToken = result.getNextPageToken();
                    for (VideoItem item : result.getItems()) {
                        if (!item.getId().equals(videoID)) {
                            items.add(item);
                        }
                    }
//                    if (items.size() != 0) {
//                        //buttonLoadMore.setText(R.string.load_more);
//                    } else {
//                        //buttonLoadMore.setText(R.string.refresh);
//                    }
                    postRelatedVideosInFragment();
                }
            }
        });

//        } else {
//            //onDisconnected();
//            Toast.makeText(getApplicationContext(), R.string.no_network, Toast.LENGTH_LONG).show();
//            return null;
//        }
    }

    private int getItemPosition() {
        return items.size() - videoPosition - 1;
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        LinearLayout.LayoutParams playerParams = (LinearLayout.LayoutParams) youTubePlayerView.getLayoutParams();
//
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            playerParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
//            if (youTubePlayer != null) {
//                youTubePlayer.setFullscreen(true);
//            }
//        }
//    }

    private void postRatingInFragment() {
        VideoFragmentDescription fragmentDescription =
                (VideoFragmentDescription) mSectionsPagerAdapter.instantiateItem(mViewPager, 0);
        fragmentDescription.updateRadioGroup(videoRating);
    }

    private void postDescriptionInFragment() {
        VideoFragmentDescription fragmentDescription =
                (VideoFragmentDescription) mSectionsPagerAdapter.instantiateItem(mViewPager, 0);
        fragmentDescription.updateFragment(videoItem);

    }

    private void postRelatedVideosInFragment() {
        VideoFragmentVideos fragmentVideos =
                (VideoFragmentVideos) mSectionsPagerAdapter.instantiateItem(mViewPager, 1);
        fragmentVideos.updateVideoList(items, nextPageToken);
    }

    @Override
    public void onPreExecute() {
        //footer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPostExecute() {
        //footer.setVisibility(View.VISIBLE);
        //adapter.notifyDataSetChanged();
//        VideoFragmentDescription fragmentDescription =
//                (VideoFragmentDescription) mSectionsPagerAdapter.instantiateItem(mViewPager, 0);
//        fragmentDescription.updateFragment(videoItem);
//
//        VideoFragmentVideos fragmentVideos =
//                (VideoFragmentVideos) mSectionsPagerAdapter.instantiateItem(mViewPager, 1);
//        fragmentVideos.updateVideoList(items, nextPageToken);
    }

    @Override
    public void onDisconnected() {
        //footer.setVisibility(View.GONE);
    }

    @Override
    public void onCanceled() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        youTubePlayerFragment.initialize(YoutubeInfo.DEVELOPER_KEY, this);
    }

    @Override
    protected void onStop() {
        if (youTubePlayer != null) {
            currentVideoTime = youTubePlayer.getCurrentTimeMillis();
            youTubePlayer.release();
            isMinimized = true;
        }
        super.onStop();
    }

    private String getTagsByTitle() {
        String[] tempArray = videoTitle.split("\\W+");
        return TextUtils.join("|", tempArray);
    }

//    @Override
//    public String getPlaylistID() {
//        return playlistID;
//    }
//
//    @Override
//    public String getVideoTags() {
//        return getTagsByTitle();
//    }
//
//    @Override
//    public String getNextPageToken() {
//        return nextPageToken;
//    }
//
//    @Override
//    public String getVideoID() {
//        return videoID;
//    }

    public class TabsAdapter extends FragmentPagerAdapter {

        TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0: {
                    fragment = VideoFragmentDescription.newInstance(videoID, videoTitle);
                }
                break;
                case 1: {
                    fragment = VideoFragmentVideos.newInstance(items, playlistID, videoID, getTagsByTitle());
                }
                break;
                case 2: {
                    fragment = VideoFragmentDescription.newInstance(videoID, videoTitle);
                }
                break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

//    public class VideoFragmentVideos extends Fragment {
//
//        private ListView listView;
//        private LinearLayout footer;
//        private Button buttonLoadMore;
//        private VideoAdapter adapter;
//        private String nextPageToken;
//        ArrayList<VideoItem> videoItems = new ArrayList<>();
//        String videoId;
//        //private String videoID;
//
//        public VideoFragmentVideos() {
//        }
//
//        public VideoFragmentVideos newInstance(ArrayList<VideoItem> items) {
//            VideoFragmentVideos fragment = new VideoFragmentVideos();
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("VIDEO_ITEMS", items);
//            fragment.setArguments(bundle);
//            return fragment;
//        }
//
//        public void updateVideoList(ArrayList<VideoItem> videoItem) {
//            videoItems.addAll(videoItem);
//            adapter.notifyDataSetChanged();
//        }
//
//        @Nullable
//        @Override
//        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//            View view = inflater.inflate(R.layout.fragment_video_videos, container, false);
//
//            listView = (ListView) view.findViewById(R.id.listViewVideo);
//
//            footer = (LinearLayout) inflater.inflate(R.layout.footer_main, listView, false);
//            buttonLoadMore = (Button) footer.findViewById(R.id.buttonFooterMain);
//            footer.setVisibility(View.GONE);
//            listView.addFooterView(footer, null, false);
//
//            buttonLoadMore.setOnClickListener(buttonLoadMoreOnClickListener);
//            //listView.setOnItemClickListener(onItemClickListener);
//
//            adapter = new VideoAdapter(getContext(), R.layout.list_view_items_play_list_items,
//                    R.id.listViewTitlePlayListItems, R.id.listViewThumbnailPlayListItems, videoItems);
//            listView.setAdapter(adapter);
//
//            ArrayList<VideoItem> tempList;
//            tempList = (ArrayList<VideoItem>) getArguments().getSerializable("VIDEO_ITEMS");
//            if (tempList != null) {
//                videoItems.addAll(tempList);
//            }
//
//            adapter.notifyDataSetChanged();
//            //connectToDB();
//
//            return view;
//        }
//
//        private void getVideosFromYouTube() {
//            String searchOrder = "date";
//            String type = "video";
//            String fields = "items(id/videoId,snippet(title,thumbnails/medium/url)),nextPageToken";
//
//            AsyncSearch getItems = new AsyncSearch(getContext(),
//                    null, searchOrder, type, fields, nextPageToken,
//                    new TaskCompleted() {
//                        @Override
//                        public void onTaskComplete(YouTubeResult result) {
//                            if (!result.isCanceled()) {
//                                nextPageToken = result.getNextPageToken();
//                                for (VideoItem item : result.getItems()) {
//                                    if (!item.getId().equals(videoId)) {
//                                        videoItems.add(item);
//                                    }
//                                }
////                                    if (items.size() != 0) {
////                                        //buttonLoadMore.setText(R.string.load_more);
////                                    } else {
////                                        //buttonLoadMore.setText(R.string.refresh);
////                                    }
//                                adapter.notifyDataSetChanged();
////                                    //onPostExecute();
//                            }
//                        }
//                    });
//
//            getItems.execute();
//        }
//
//        View.OnClickListener buttonLoadMoreOnClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getVideosFromYouTube();
//            }
//        };
//
//        ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//
//                ArrayList<VideoItem> tempArrayList = new ArrayList<>();
//                tempArrayList.addAll(videoItems);
//                int videoPosition;
//                Intent intent = new Intent(getActivity(), VideoActivity.class);
//                intent.putExtra("PLAYLIST_ID", getArguments().getString("PLAYLIST_ID"));
//
//                videoPosition = videoItems.size() - position;
//
//                intent.putExtra("VIDEO_POSITION", videoPosition);
//                intent.putExtra("ITEMS", new VideoItemWrapper(tempArrayList));
//                startActivity(intent);
//
////                  if (youTubePlayer != null) {
////                    if (videoPosition != Integer.MIN_VALUE) {
////                        videoPosition = items.size() - position;
////                        textView.setText(items.get(getItemPosition()).getTitle());
////                        youTubePlayer.loadPlaylist(playlistID, videoPosition, 0);
////                    } else {
////                        VideoItem current = new VideoItem();
////                        current.setId(videoID);
////                        current.setTitle(textView.getText().toString());
////                        videoID = items.get(position - 1).getId();
////                        textView.setText(items.get(position - 1).getTitle());
////                        youTubePlayer.loadVideo(videoID);
////                        previous.push(current);
////                    }
////                    listView.setSelection(0);
////                } else {
////                    Toast.makeText(VideoActivity.this, R.string.error_occurred, Toast.LENGTH_LONG).show();
////                }
//            }
//        };
//    }
}
