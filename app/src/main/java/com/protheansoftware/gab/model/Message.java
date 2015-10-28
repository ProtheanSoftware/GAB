package com.protheansoftware.gab.model;

/**
 * Data model for handling message data in database
 * @author oskar
 * Created by oskar on 2015-10-08.
 */
public class Message {
    private int id;
    private int senderId;
    private int recieverId;
    private String message;
    private String sinchID;
    public Message(int id, int senderId, int recieverId, String message, String sinchID){
        this.id = id;

        this.senderId = senderId;
        this.recieverId = recieverId;
        this.message = message;
        this.sinchID = sinchID;
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

    public String getSinchID() {
        return sinchID;
    }
}
