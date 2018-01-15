package com.wordpress.dnvsoft.android.shenryyr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wordpress.dnvsoft.android.shenryyr.adapters.VideoAdapter;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItem;
import com.wordpress.dnvsoft.android.shenryyr.network.IConnected;
import com.wordpress.dnvsoft.android.shenryyr.views.GridViewWithHeaderAndFooter;

import java.util.ArrayList;

public abstract class MainFragment extends Fragment implements IConnected {

    protected View view;
    protected View spacer;
    protected GridViewWithHeaderAndFooter gridViewMain;
    protected Button button;
    protected ImageView imageViewReload;
    protected View header;
    protected LinearLayout footer;
    protected ImageView listViewThumbnail;
    protected TextView listViewTitle;
    protected ProgressBar progressBar;
    protected String nextPageToken;
    protected VideoAdapter adapter;
    protected ArrayList<VideoItem> items = new ArrayList<>();

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        listViewTitle = (TextView) view.findViewById(R.id.headerTitleMain);
        listViewThumbnail = (ImageView) view.findViewById(R.id.headerImageMain);
        gridViewMain = (GridViewWithHeaderAndFooter) view.findViewById(R.id.listViewMain);
        imageViewReload = (ImageView) view.findViewById(R.id.imageReloadMain);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarMain);

        header = inflater.inflate(R.layout.header_main, gridViewMain, false);
        spacer = header.findViewById(R.id.spacer);
        gridViewMain.addHeaderView(header, null, false);

        footer = (LinearLayout) inflater.inflate(R.layout.footer_main, gridViewMain, false);
        button = (Button) footer.findViewById(R.id.buttonFooterMain);
        gridViewMain.addFooterView(footer, null, false);

        button.setOnClickListener(onClick);
        imageViewReload.setOnClickListener(onClick);
        gridViewMain.setOnScrollListener(onScrollListener);

        adapter = new VideoAdapter(getContext(), R.layout.list_view_items_main,
                R.id.listViewTitleMain, R.id.listViewThumbnailMain, items);
        gridViewMain.setAdapter(adapter);
        setHasOptionsMenu(true);

        Connect();

        return view;
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Connect();
        }
    };

    AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (gridViewMain.getFirstVisiblePosition() == 0) {
                View firstChild = gridViewMain.getChildAt(0);
                int topY = 0;
                if (firstChild != null) {
                    topY = firstChild.getTop();
                }
                int heroTopY = spacer.getTop();
                listViewTitle.setY(Math.max(0, heroTopY + topY));
                listViewThumbnail.setY(topY * 0.5f);
            } else {
                listViewTitle.setY(0);
            }
        }
    };

    public abstract void Connect();

    @Override
    public void onPreExecute() {
        imageViewReload.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        footer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPostExecute() {
        imageViewReload.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        footer.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDisconnected() {
        imageViewReload.setVisibility(View.VISIBLE);
        footer.setVisibility(View.GONE);
    }

    @Override
    public void onCanceled() {
        imageViewReload.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}
