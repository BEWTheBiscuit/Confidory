package com.bisbizkuit.whistalk.objects;

public class notification {

    private String userId;
    private String text;
    private String postId;
    private boolean isPost;
    private String date;
    private String id;

    public notification(String userId, String text, String postId, boolean ispost, String back, String id) {
        this.userId = userId;
        this.text = text;
        this.postId = postId;
        this.isPost = ispost;
        this.date = date;
        this.id = id;
    }

    public notification() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public boolean getIsPost() {
        return isPost;
    }

    public void setIsPost(boolean post) {
        isPost = post;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
