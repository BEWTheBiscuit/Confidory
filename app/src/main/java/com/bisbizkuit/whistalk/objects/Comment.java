package com.bisbizkuit.whistalk.objects;

public class Comment {

    String date;
    String commentPublisher;
    String commentContent;
    String commentId;

    public Comment() {
    }

    public Comment(String date, String commentPublisher, String commentContent, String commentId) {
        this.date = date;
        this.commentPublisher = commentPublisher;
        this.commentContent = commentContent;
        this.commentId = commentId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCommentPublisher() {
        return commentPublisher;
    }

    public void setCommentPublisher(String commentPublisher) {
        this.commentPublisher = commentPublisher;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
