package com.wordpress.dnvsoft.android.shenryyr;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncLatestVideos;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;
import com.wordpress.dnvsoft.android.shenryyr.views.GridViewWithHeaderAndFooter;

public class LatestVideosFragment extends MainFragment {

    public LatestVideosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        listViewTitle.setText(R.string.latest_videos_title);
        gridViewMain.setOnItemClickListener(onItemClickListener);

        return view;
    }

    GridViewWithHeaderAndFooter.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), VideoActivity.class);
            intent.putExtra("VIDEO_ID", items.get(position).getId());
            intent.putExtra("VIDEO_TITLE", items.get(position).getTitle());
            startActivity(intent);
        }
    };

    @Override
    public void Connect() {
        if (Network.IsDeviceOnline(getActivity())) {
            onPreExecute();
            AsyncLatestVideos getItems = new AsyncLatestVideos(getActivity(),
                    YoutubeInfo.UPLOADS_ID, nextPageToken, (long) 20, new TaskCompleted() {
                @Override
                public void onTaskComplete(YouTubeResult result) {
                    if (!result.isCanceled() && result.getItems() != null) {
                        nextPageToken = result.getNextPageToken();
                        items.addAll(result.getItems());
                        onPostExecute();
                    } else {
                        onCanceled();
                    }
                }
            });

            getItems.execute();

        } else {
            onDisconnected();
            Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
        }
    }
}
