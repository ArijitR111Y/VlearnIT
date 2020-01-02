package com.royarijit998.vlearnit;

public class Posts {
    public String key, uid, date, time, description, postImg, ufullname, uprofileImg;

    public Posts(){}

    public Posts(String key, String uid, String date, String time, String description, String postImg, String ufullname, String uprofileImg) {
        this.key = key;
        this.uid = uid;
        this.date = date;
        this.time = time;
        this.description = description;
        this.postImg = postImg;
        this.ufullname = ufullname;
        this.uprofileImg = uprofileImg;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostImg() {
        return postImg;
    }

    public void setPostImg(String postImg) {
        this.postImg = postImg;
    }

    public String getUfullname() {
        return ufullname;
    }

    public void setUfullname(String ufullname) {
        this.ufullname = ufullname;
    }

    public String getUprofileImg() {
        return uprofileImg;
    }

    public void setUprofileImg(String uprofileImg) {
        this.uprofileImg = uprofileImg;
    }
}
