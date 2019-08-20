package com.dropout.dropmeme.Models;


import com.google.firebase.Timestamp;

public class Comments {
    private String comment;
    private String comment_id;
    private String commenter_uid;
    private String meme_id;
    private Timestamp time_ago;

    public Comments(String comment, String comment_id, String commenter_uid, String meme_id, Timestamp time_ago) {
        this.comment = comment;
        this.comment_id = comment_id;
        this.commenter_uid = commenter_uid;
        this.meme_id = meme_id;
        this.time_ago = time_ago;
    }

    public Comments(){

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getCommenter_uid() {
        return commenter_uid;
    }

    public void setCommenter_uid(String commenter_uid) {
        this.commenter_uid = commenter_uid;
    }

    public String getMeme_id() {
        return meme_id;
    }

    public void setMeme_id(String meme_id) {
        this.meme_id = meme_id;
    }

    public Timestamp getTime_ago() {
        return time_ago;
    }

    public void setTime_ago(Timestamp time_ago) {
        this.time_ago = time_ago;
    }
}
