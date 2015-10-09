package com.protheansoftware.gab.model;

/**
 * Created by oskar on 2015-10-08.
 */
public class Message {
    private int id;
    private int senderId;
    private int recieverId;
    private String message;

    public Message(int id, int senderId, int recieverId, String message){
        this.id = id;

        this.senderId = senderId;
        this.recieverId = recieverId;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getRecieverId() {
        return recieverId;
    }

    public String getMessage() {
        return message;
    }
}
