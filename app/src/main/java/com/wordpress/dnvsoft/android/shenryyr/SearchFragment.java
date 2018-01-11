package com.wordpress.dnvsoft.android.shenryyr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AlignmentSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wordpress.dnvsoft.android.shenryyr.adapters.VideoAdapter;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncSearch;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.menus.SortItems;
import com.wordpress.dnvsoft.android.shenryyr.menus.SortMenu;
import com.wordpress.dnvsoft.android.shenryyr.models.VideoItem;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.IConnected;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements IConnected {

    private boolean hasChanged = false;
    private int checkedRadioItem = 0;
    private String nextPageToken;
    private String searchOrder = "date";
    private ListView listView;
    private EditText searchText;
    private String searchParameter = "";
    private LinearLayout footer;
    private ProgressBar progressBar;
    private VideoAdapter adapter;
    private ArrayList<VideoItem> items = new ArrayList<>();

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_search, container, false);

        listView = (ListView) fragment.findViewById(R.id.listViewSearch);
        searchText = (EditText) fragment.findViewById(R.id.searchText);
        ImageView imageView = (ImageView) fragment.findViewById(R.id.searchButton);
        progressBar = (ProgressBar) fragment.findViewById(R.id.progressBarSearch);

        footer = (LinearLayout) inflater.inflate(R.layout.footer_main, listView, false);
        Button button = (Button) footer.findViewById(R.id.buttonFooterMain);
        footer.setVisibility(View.GONE);
        listView.addFooterView(footer, null, false);

        adapter = new VideoAdapter(getActivity(), R.layout.list_view_items_play_list_items,
                R.id.listViewTitlePlayListItems, R.id.listViewThumbnailPlayListItems, items);

        listView.setAdapter(adapter);
        searchText.setImeActionLabel(getString(R.string.keyboard_enter), KeyEvent.KEYCODE_ENTER);
        searchText.setOnKeyListener(onKeyListener);
        button.setOnClickListener(onClickListener);
        imageView.setOnClickListener(onClickListener);
        listView.setOnItemClickListener(onItemClickListener);
        searchText.setOnFocusChangeListener(onFocusChangeListener);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                hasChanged = true;
                searchParameter = searchText.getText().toString();
            }
        });

        setHasOptionsMenu(true);

        return fragment;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Connect();
        }
    };

    View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                Connect();
                hideKeyboard(v);
            }
            return false;
        }
    };

    ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), VideoActivity.class);
            intent.putExtra("VIDEO_ID", items.get(position).getId());
            intent.putExtra("VIDEO_TITLE", items.get(position).getTitle());
            startActivity(intent);
        }
    };

    EditText.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        }
    };

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void Connect() {
        if (searchParameter.equals("")) {
            return;
        }
        if (Network.IsDeviceOnline(getActivity())) {
            if (hasChanged) {
                items.clear();
                nextPageToken = null;
            }
            hasChanged = false;

            String type = "video";
            String fields = "items(id/videoId,snippet(title,thumbnails/medium/url)),nextPageToken";
            onPreExecute();
            AsyncSearch getItems = new AsyncSearch(getActivity(), searchParameter,
                    searchOrder, type, fields, nextPageToken, new TaskCompleted() {
                @Override
                public void onTaskComplete(YouTubeResult result) {
                    if (!result.isCanceled() && result.getItems() != null) {
                        nextPageToken = result.getNextPageToken();
                        items.addAll(result.getItems());
                        if (items.size() == 0) {
                            String text = getActivity().getString(R.string.toast_no_mached_content) +
                                    searchParameter + "'";
                            Spannable centeredText = new SpannableString(text);
                            centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                    0, text.length() - 1,
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            Toast.makeText(getActivity(), centeredText, Toast.LENGTH_LONG).show();
                        }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_by) {
            SortMenu sortMenu = new SortMenu(getActivity(), checkedRadioItem, new SortItems() {
                @Override
                public void onConnect(String order, int radioItem) {
                    searchOrder = order;
                    checkedRadioItem = radioItem;
                    listView.setSelection(0);
                    hasChanged = true;
                    Connect();
                }
            });
            sortMenu.Sort();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        footer.setVisibility(View.GONE);
    }

    @Override
    public void onPostExecute() {
        progressBar.setVisibility(View.GONE);
        if (items.size() != 0 && items.size() % 20 == 0) {
            footer.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDisconnected() {
        footer.setVisibility(View.GONE);
    }

    @Override
    public void onCanceled() {
        progressBar.setVisibility(View.GONE);
    }
}
