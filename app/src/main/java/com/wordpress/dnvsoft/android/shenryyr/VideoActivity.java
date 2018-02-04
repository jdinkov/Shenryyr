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

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.wordpress.dnvsoft.android.shenryyr.menus.MissingServiceMenu;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItem;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItemWrapper;

import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity
        implements YouTubePlayer.OnInitializedListener {

    private String videoID;
    private boolean isMinimized;
    private ArrayList<VideoItem> items;
    private YouTubePlayer youTubePlayer;
    private int videoPosition;
    private String playlistID;
    private YouTubePlayerFragment youTubePlayerFragment;
    private String videoTitle;
    private int currentVideoTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        items = new ArrayList<>();

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

        youTubePlayerFragment =
                (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);

        TabsAdapter mSectionsPagerAdapter = new TabsAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container_video);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
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

    private int getItemPosition() {
        return items.size() - videoPosition - 1;
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

        VideoFragmentDescription fragmentDescription;
        VideoFragmentVideos fragmentVideos;
        VideoFragmentComments fragmentComments;

        TabsAdapter(FragmentManager fm) {
            super(fm);
            fragmentDescription = new VideoFragmentDescription(videoID, videoTitle);
            fragmentVideos = new VideoFragmentVideos(items, playlistID, videoID, getTagsByTitle());
            fragmentComments = new VideoFragmentComments(videoID);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0: {
                    fragment = fragmentDescription;
                }
                break;
                case 1: {
                    fragment = fragmentVideos;
                }
                break;
                case 2: {
                    fragment = fragmentComments;
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
