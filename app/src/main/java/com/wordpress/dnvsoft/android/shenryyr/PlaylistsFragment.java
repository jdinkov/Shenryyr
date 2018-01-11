package com.wordpress.dnvsoft.android.shenryyr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncPlaylists;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;
import com.wordpress.dnvsoft.android.shenryyr.views.GridViewWithHeaderAndFooter;

public class PlaylistsFragment extends MainFragment {

    public PlaylistsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        listViewTitle.setText(R.string.playlists_title);
        gridViewMain.setOnItemClickListener(onItemClickListener);

        return view;
    }

    GridViewWithHeaderAndFooter.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Bundle data = new Bundle();
            data.putString("PLAYLIST_ID", items.get(position).getId());
            data.putString("PLAYLIST_TITLE", items.get(position).getTitle());
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            Fragment fragment = new PlaylistItemsFragment();
            fragment.setArguments(data);
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack("playlists_fragment");
            fragmentTransaction.commit();
        }
    };

    @Override
    public void Connect() {
        if (Network.IsDeviceOnline(getActivity())) {
            onPreExecute();
            AsyncPlaylists getItems = new AsyncPlaylists(
                    getActivity(), nextPageToken, new TaskCompleted() {
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
