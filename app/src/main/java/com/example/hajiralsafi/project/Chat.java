package com.example.hajiralsafi.project;

public class Chat  {

    private String message, from , uid , time , name , date , type ;





    public Chat(String message, String time ,String from , String uid , String name , String date , String type) {
        this.message = message;
        this.from = from ;
        this.time = time;
        this.uid = uid ;
        this.name = name ;
        this.date = date ;
        this.type = type ;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Chat () {


    }
}