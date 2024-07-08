package com.example.surfhey.modelItem;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class modelSurf implements Serializable {
    private String authorname, imageurl,date,title,detailItem, likes;
    private Timestamp timestampcreated;

    public modelSurf(String authorname, String title, String date, String imageurl, String detail, String likes, Timestamp timestampcreated) {
        this.authorname = authorname;
        this.title = title;
        this.date = date;
        this.imageurl = imageurl;
        this.detailItem = detail;
        this.likes = likes;
        this.timestampcreated = timestampcreated;

    }
    public String getAuthorname() {
        return authorname;
    }

    public String getImageURL() {
        return imageurl;
    }

    public String getTitle() {return title;
    }

    public String getDetail() {
        return detailItem;
    }

    public String getDate() {
        return date;
    }
    public String getLikes() {
        return likes;
    }
    public Timestamp getTimestampcreated() {
        return timestampcreated;
    }
}
