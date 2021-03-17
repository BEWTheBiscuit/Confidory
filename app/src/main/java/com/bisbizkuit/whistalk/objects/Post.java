package com.bisbizkuit.whistalk.objects;

public class Post {

    String postId;
    String publisher;
    String date;
    String text;
    Boolean anonity;
    Boolean viewPublic;

    public Post() {
    }

    public Post(String postId, String publisher, String date, String text, Boolean anonity, Boolean viewPublic) {
        this.postId = postId;
        this.publisher = publisher;
        this.date = date;
        this.text = text;
        this.anonity = anonity;
        this.viewPublic = viewPublic;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getAnonity() {
        return anonity;
    }

    public void setAnonity(Boolean anonity) {
        this.anonity = anonity;
    }

    public Boolean getViewPublic() {
        return viewPublic;
    }

    public void setViewPublic(Boolean viewPublic) {
        this.viewPublic = viewPublic;
    }
}
