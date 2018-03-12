package com.wordpress.dnvsoft.android.shenryyr.models;

import org.json.JSONException;
import org.json.JSONObject;

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

    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", getID());
            jsonObject.put("authorDisplayName", getAuthorDisplayName());
            jsonObject.put("authorImageUrl", getAuthorImageUrl());
            jsonObject.put("authorChannelId", getAuthorChannelId());
            jsonObject.put("commentText", getCommentText());
            jsonObject.put("viewerRating", getViewerRating());
            jsonObject.put("likeCount", getLikeCount());
            jsonObject.put("canReply", getCanReply());
            jsonObject.put("totalReplyCount", getTotalReplyCount());
        } catch (JSONException e) {
            return null;
        }

        return jsonObject.toString();
    }
}
