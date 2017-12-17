package com.wordpress.dnvsoft.android.shenryyr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wordpress.dnvsoft.android.shenryyr.async_tasks.AsyncGetRating;
import com.wordpress.dnvsoft.android.shenryyr.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;
import com.wordpress.dnvsoft.android.shenryyr.network.Network;

public class AboutFragment extends Fragment {

    public AboutFragment() {
    }

    TextView textViewVideoId;
    TextView textViewRating;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragment = inflater.inflate(R.layout.fragment_about, container, false);

        Button getRatingButton = (Button) fragment.findViewById(R.id.button2);
        textViewVideoId = (TextView) fragment.findViewById(R.id.textView2);
        textViewRating = (TextView) fragment.findViewById(R.id.textView3);
        getRatingButton.setOnClickListener(onClickListener);

        return fragment;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Connect();
        }
    };

    private void Connect() {
        if (Network.IsDeviceOnline(getActivity())) {
            AsyncGetRating getRating = new AsyncGetRating(getActivity(), new TaskCompleted() {
                @Override
                public void onTaskComplete(YouTubeResult result) {
                    if (!result.isCanceled()) {
                        textViewVideoId.setText(result.getItems().get(0).getId());
                        textViewRating.setText(result.getItems().get(0).getRating());
                    }
                }
            });

            getRating.execute();
        }
    }
}
