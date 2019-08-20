package com.dropout.dropmeme.Models;

public class Users {

    private String user_name;
    private String user_id;
    private String user_phone;
    private String user_email;
    private String uId;
    private String user_thumbImage;
    private String user_image;
    private String user_about;

    public Users(){

    }

    public Users(String user_name, String user_id, String user_phone, String user_email, String uId, String user_thumbImage, String user_image, String user_about) {
        this.user_name = user_name;
        this.user_id = user_id;
        this.user_phone = user_phone;
        this.user_email = user_email;
        this.uId = uId;
        this.user_thumbImage = user_thumbImage;
        this.user_image = user_image;
        this.user_about = user_about;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUser_thumbImage() {
        return user_thumbImage;
    }

    public void setUser_thumbImage(String user_thumbImage) {
        this.user_thumbImage = user_thumbImage;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_about() {
        return user_about;
    }

    public void setUser_about(String user_about) {
        this.user_about = user_about;
    }
}
