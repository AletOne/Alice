package com.example.wang.alice.mode;

/**
 * Created by Wang on 3/5/18.
 */

public class Message {
    public final static int LEFT_MESSAGE = 1;
    public final static int RIGHT_MESSAGE = 2;

    private String message;
    private int type; // 1 is left, 2 is right;

    public Message(){

    }

    public Message(String message, int type){
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
