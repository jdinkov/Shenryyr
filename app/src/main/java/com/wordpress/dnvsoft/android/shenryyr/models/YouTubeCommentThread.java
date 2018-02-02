package com.wordpress.dnvsoft.android.shenryyr.models;

public class YouTubeCommentThread extends YouTubeComment {

    private boolean canReply;
    private String totalReplyCount;

    public boolean getCanReply() {
        return canReply;
    }

    public void setCanReply(boolean canReply) {
        this.canReply = canReply;
    }

    public String getTotalReplyCount() {
        return totalReplyCount;
    }

    public void setTotalReplyCount(String totalReplyCount) {
        this.totalReplyCount = totalReplyCount;
    }
}
