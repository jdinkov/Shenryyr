package com.wordpress.dnvsoft.android.shenryyr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeThumbnailView;
import com.squareup.picasso.Picasso;
import com.wordpress.dnvsoft.android.shenryyr.adapters.VideoAdapter;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncPlaylistItems;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncPlaylistThumbnail;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItem;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItemWrapper;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.IConnected;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

import java.util.ArrayList;
import java.util.Collections;

public class PlaylistItemsFragment extends Fragment implements IConnected {

    private int checkedRadioItem = 0;
    private int tempRadioItem = 0;
    private String PlaylistID;
    private ImageView imageViewReload;
    private YouTubeThumbnailView listViewThumbnail;
    private TextView listViewTitle;
    private ProgressBar progressBar;
    private VideoAdapter adapter;
    private String nextPageToken;
    private ArrayList<VideoItem> items = new ArrayList<>();

    public PlaylistItemsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_items, container, false);

        ListView listView = (ListView) view.findViewById(R.id.listViewPlaylistItems);
        imageViewReload = (ImageView) view.findViewById(R.id.imageReloadItems);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarPlaylistItems);

        LinearLayout header = (LinearLayout) inflater.inflate(R.layout.playlist_items_header, listView, false);
        listViewThumbnail = (YouTubeThumbnailView) header.findViewById(R.id.listViewThumbnailHeader);
        listViewTitle = (TextView) header.findViewById(R.id.listViewTitleHeader);
        listView.addHeaderView(header, null, false);

        PlaylistID = getArguments().getString("PLAYLIST_ID");

        imageViewReload.setOnClickListener(onClick);
        listView.setOnItemClickListener(onItemClickListener);
        adapter = new VideoAdapter(getActivity(), R.layout.list_view_items_play_list_items,
                R.id.listViewTitlePlayListItems, R.id.listViewThumbnailPlayListItems, items);

        listView.setAdapter(adapter);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Connect();
        super.onViewCreated(view, savedInstanceState);
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Connect();
        }
    };

    ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ArrayList<VideoItem> tempArrayList = new ArrayList<>();
            tempArrayList.addAll(items);
            int videoPosition;
            Intent intent = new Intent(getActivity(), VideoActivity.class);
            intent.putExtra("PLAYLIST_ID", getArguments().getString("PLAYLIST_ID"));
            if (checkedRadioItem != 0) {
                Collections.reverse(tempArrayList);
                videoPosition = position - 1;
            } else {
                videoPosition = items.size() - position;
            }
            intent.putExtra("VIDEO_POSITION", videoPosition);
            intent.putExtra("ITEMS", new VideoItemWrapper(tempArrayList));
            startActivity(intent);
        }
    };

    protected void Connect() {
        if (Network.IsDeviceOnline(getActivity())) {
            onPreExecute();
            AsyncPlaylistItems getItems = new AsyncPlaylistItems(
                    getActivity(), PlaylistID, nextPageToken, (long) 50,
                    new TaskCompleted() {
                        @Override
                        public void onTaskComplete(YouTubeResult result) {
                            if (!result.isCanceled() && result.getItems() != null) {
                                nextPageToken = result.getNextPageToken();
                                items.addAll(result.getItems());
                                listViewTitle.setText(getArguments().getString("PLAYLIST_TITLE"));
                                onPostExecute();
                            } else {
                                onCanceled();
                            }
                        }
                    }
            );

            AsyncPlaylistThumbnail asyncPlaylistThumbnail = new AsyncPlaylistThumbnail(
                    getActivity(), PlaylistID, new TaskCompleted() {
                @Override
                public void onTaskComplete(YouTubeResult result) {
                    Picasso.with(getActivity()).load(result.getItems().get(0).getThumbnailMaxResUrl()).into(listViewThumbnail);
                }
            });

            asyncPlaylistThumbnail.execute();
            getItems.execute();

        } else {
            onDisconnected();
            Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_by) {
            SortBy();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void SortBy() {
        final CharSequence[] choice = {getString(R.string.menu_sort_newest),
                getString(R.string.menu_sort_oldest)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.menu_sort_by);
        builder.setSingleChoiceItems(choice, checkedRadioItem, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        tempRadioItem = 0;
                        break;
                    case 1:
                        tempRadioItem = 1;
                        break;
                }
            }
        });
        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkedRadioItem != tempRadioItem) {
                    checkedRadioItem = tempRadioItem;
                    Collections.reverse(items);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton(R.string.negative_button, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPreExecute() {
        imageViewReload.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPostExecute() {
        imageViewReload.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDisconnected() {
        imageViewReload.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCanceled() {
        imageViewReload.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}
