package com.wordpress.dnvsoft.android.shenryyr.views;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.Calendar;

public class LinearLayoutWithTouchListener extends LinearLayout {

    private long lastClickTime;
    private OnYouTubePlayerGoBackAndForward callback;

    public interface OnYouTubePlayerGoBackAndForward {
        void youtubePlayerGoBack();

        void youtubePlayerGoForward();
    }

    public LinearLayoutWithTouchListener(Context context) {
        super(context);
        callback = (OnYouTubePlayerGoBackAndForward) context;
    }

    public LinearLayoutWithTouchListener(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        callback = (OnYouTubePlayerGoBackAndForward) context;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            playerTouchedFromTheSides(event.getX());
        }

        return false;
    }

    private void playerTouchedFromTheSides(float eventX) {
        float screenX = getScreenX();
        if (eventX <= screenX * 0.3f) {
            playerGoBack();
        } else if (eventX >= screenX * 0.7f) {
            playerGoForward();
        }
    }

    private float getScreenX() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point.x;
    }

    private boolean isDoubleClicked() {
        long currentTime = Calendar.getInstance().getTime().getTime();
        if (currentTime - lastClickTime >= 300) {
            lastClickTime = currentTime;
            return false;
        }

        return true;
    }

    private void playerGoBack() {
        if (isDoubleClicked()) {
            callback.youtubePlayerGoBack();
        }
    }

    private void playerGoForward() {
        if (isDoubleClicked()) {
            callback.youtubePlayerGoForward();
        }
    }
}
