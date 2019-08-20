package com.dropout.dropmeme.Models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

public class HomeMeme {

    private String caption;
    private Timestamp time_ago;
    private String meme_image;
    private String meme_id;
    private String user_uid;

    public HomeMeme(String caption, Timestamp time_ago, String meme_image, String meme_id, String user_uid) {
        this.caption = caption;
        this.time_ago = time_ago;
        this.meme_image = meme_image;
        this.meme_id = meme_id;
        this.user_uid = user_uid;
    }

    public HomeMeme(){

    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getMeme_id() {
        return meme_id;
    }

    public void setMeme_id(String meme_id) {
        this.meme_id = meme_id;
    }


    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Timestamp getTime_ago() {
        return time_ago;
    }

    public void setTime_ago(Timestamp time_ago) {
        this.time_ago = time_ago;
    }

    public String getMeme_image() {
        return meme_image;
    }

    public void setMeme_image(String meme_image) {
        this.meme_image = meme_image;
    }
}
