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
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncGetCommentThreads;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncGetRating;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncGetVideoDescription;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncSearch;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.menus.MissingServiceMenu;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItem;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItemWrapper;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeCommentThread;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity
        implements YouTubePlayer.OnInitializedListener {

    private String videoID;
    private String nextPageToken;
    private String nextPageTokenCommentThread;
    private VideoItem videoItem;
    private String videoRating;
    private boolean isMinimized;
    private ArrayList<VideoItem> items;
    private YouTubePlayer youTubePlayer;
    private int videoPosition;
    private String playlistID;
    private TabsAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private YouTubePlayerFragment youTubePlayerFragment;
    private String videoTitle;
    private int currentVideoTime;
    private ArrayList<YouTubeCommentThread> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoItem = new VideoItem();
        items = new ArrayList<>();
        comments = new ArrayList<>();

        youTubePlayerFragment =
                (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);

        mSectionsPagerAdapter = new TabsAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container_video);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        videoPosition = getIntent().getIntExtra("VIDEO_POSITION", Integer.MIN_VALUE);
        if (videoPosition != Integer.MIN_VALUE) {
            VideoItemWrapper wrapper = (VideoItemWrapper) getIntent().getSerializableExtra("ITEMS");
            items.addAll(wrapper.getItems());
            playlistID = getIntent().getStringExtra("PLAYLIST_ID");
            videoID = items.get(getItemPosition()).getId();
            videoTitle = items.get(getItemPosition()).getTitle();
        } else {
            videoID = getIntent().getStringExtra("VIDEO_ID");
            videoTitle = getIntent().getStringExtra("VIDEO_TITLE");
        }

        if (Network.IsDeviceOnline(VideoActivity.this)) {
            getVideoDescription().execute();
            getCommentThreads().execute();
            if (GoogleSignIn.getLastSignedInAccount(VideoActivity.this) != null) {
                getVideoRating().execute();
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

    private AsyncGetVideoDescription getVideoDescription() {
        return new AsyncGetVideoDescription(VideoActivity.this, videoID, new TaskCompleted() {
            @Override
            public void onTaskComplete(YouTubeResult result) {
                if (!result.isCanceled() && result.getVideos() != null) {
                    videoItem.setPublishedAt("Published on " + result.getVideos().get(0).getPublishedAt());
                    videoItem.setDescription(result.getVideos().get(0).getDescription());
                    videoItem.setLikeCount(result.getVideos().get(0).getLikeCount());
                    videoItem.setDislikeCount(result.getVideos().get(0).getDislikeCount());
                    videoItem.setViewCount(result.getVideos().get(0).getViewCount() + " views");
                    videoItem.setCommentCount(result.getVideos().get(0).getCommentCount());
                    postDescriptionInFragment();
                }
            }
        });
    }

    private AsyncGetRating getVideoRating() {
        return new AsyncGetRating(VideoActivity.this, videoID, new TaskCompleted() {
            @Override
            public void onTaskComplete(YouTubeResult result) {
                if (!result.isCanceled()) {
                    videoRating = result.getVideos().get(0).getRating();
                    postRatingInFragment();
                }
            }
        });
    }

    private AsyncSearch getRelatedVideos() {
        String searchOrder = "relevance";
        String type = "video";
        String fields = "items(id/videoId,snippet(title,thumbnails/medium/url)),nextPageToken";

        return new AsyncSearch(VideoActivity.this,
                getTagsByTitle(), searchOrder, type, fields, nextPageToken, new TaskCompleted() {
            @Override
            public void onTaskComplete(YouTubeResult result) {
                if (!result.isCanceled() && result.getVideos() != null) {
                    nextPageToken = result.getNextPageToken();
                    for (VideoItem item : result.getVideos()) {
                        if (!item.getId().equals(videoID)) {
                            items.add(item);
                        }
                    }

                    postRelatedVideosInFragment();
                }
            }
        });
    }

    private AsyncGetCommentThreads getCommentThreads() {
        return new AsyncGetCommentThreads(VideoActivity.this,
                "relevance", videoID, nextPageTokenCommentThread, new TaskCompleted() {
            @Override
            public void onTaskComplete(YouTubeResult result) {
                if (!result.isCanceled()) {
                    nextPageTokenCommentThread = result.getNextPageToken();
                    comments.addAll(result.getCommentThread());
                    postCommentsInFragment();
                }
            }
        });
    }

    private int getItemPosition() {
        return items.size() - videoPosition - 1;
    }

    private void postDescriptionInFragment() {
        VideoFragmentDescription fragmentDescription =
                (VideoFragmentDescription) mSectionsPagerAdapter.instantiateItem(mViewPager, 0);
        fragmentDescription.updateFragment(videoItem);
    }

    private void postRatingInFragment() {
        VideoFragmentDescription fragmentDescription =
                (VideoFragmentDescription) mSectionsPagerAdapter.instantiateItem(mViewPager, 0);
        fragmentDescription.updateRadioGroup(videoRating);
    }

    private void postRelatedVideosInFragment() {
        VideoFragmentVideos fragmentVideos =
                (VideoFragmentVideos) mSectionsPagerAdapter.instantiateItem(mViewPager, 1);
        fragmentVideos.updateVideoList(items, nextPageToken);
    }

    private void postCommentsInFragment() {
        VideoFragmentComments fragmentComments =
                (VideoFragmentComments) mSectionsPagerAdapter.instantiateItem(mViewPager, 2);
        fragmentComments.updateComments(comments);
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
                    fragment = VideoFragmentComments.newInstance(comments);
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
}
