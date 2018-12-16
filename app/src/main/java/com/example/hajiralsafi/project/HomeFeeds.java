package com.example.hajiralsafi.project;


import java.sql.Timestamp;

public class HomeFeeds  {


    public String user_id , post , image , name , date ;

    public HomeFeeds() {
    }

    public HomeFeeds(String user_id, String post, String image , String name , String date) {
        this.user_id = user_id;
        this.post = post;
        this.image = image;
        this.name = name ;
        this.date = date ;


    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
