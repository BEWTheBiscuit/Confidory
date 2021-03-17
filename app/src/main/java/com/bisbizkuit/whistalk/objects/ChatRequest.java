package com.bisbizkuit.whistalk.objects;

public class ChatRequest {

    boolean anonity;
    String requestedID;
    String requestingID;
    String chatID;

    public ChatRequest(boolean anonity, String requestedID, String requestingID, String chatID) {
        this.anonity = anonity;
        this.requestedID = requestedID;
        this.requestingID = requestingID;
        this.chatID = chatID;
    }

    public ChatRequest() {
    }

    public boolean getAnonity() {
        return anonity;
    }

    public void setAnonity(boolean anonity) {
        this.anonity = anonity;
    }

    public String getRequestedID() {
        return requestedID;
    }

    public void setRequestedID(String requestedID) {
        this.requestedID = requestedID;
    }

    public String getRequestingID() {
        return requestingID;
    }

    public void setRequestingID(String requestingID) {
        this.requestingID = requestingID;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }
}
