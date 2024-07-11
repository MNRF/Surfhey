package com.example.surfhey.modelItem;

import com.google.cloud.Timestamp;

import java.io.Serializable;

public class modelSurf implements Serializable {
    private String authorname, imageurl,date,title,detailItem, likes, postID;

    public modelSurf(String authorname, String title, String date, String imageurl, String detail, String likes, String postID) {
        this.authorname = authorname;
        this.title = title;
        this.date = date;
        this.imageurl = imageurl;
        this.detailItem = detail;
        this.likes = likes;
        this.postID = postID;

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
    public String getPostID() {
        return postID;
    }
}
