package com.royarijit998.vlearnit;

class Comments {

    private String uid, date, time, comment;

    public Comments(String uid, String date, String time, String comment) {
        this.uid = uid;
        this.date = date;
        this.time = time;
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getComment() {
        return comment;
    }
}
