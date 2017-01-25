package com.kd3developers.norem;

/**
 * Created by wa kimani on 11/29/2016.
 */

public class MsgItems {

    String headline;
    String message;
    String time;

    public MsgItems(String headline, String message, String time) {
        this.headline=headline;
        this.message = message;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

}
