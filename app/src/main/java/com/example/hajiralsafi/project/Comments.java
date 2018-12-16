package com.example.hajiralsafi.project;

public class Comments {

    public String uid , comment , date , time , name ;

    public Comments() {
    }


    public Comments(String uid, String comment, String date, String time, String name) {
        this.uid = uid;
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.name = name;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
