package com.example.surfhey.modelItem;

import java.io.Serializable;

public class modelSurf implements Serializable {
    private String poster,date,title,detailItem;

    public modelSurf(String title, String date, String poster, String detailItem) {
        this.title = title;
        this.date = date;
        this.poster = poster;
        this.detailItem = detailItem;
    }
    public String getPoster() {
        return poster;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detailItem;
    }

    public String getDate() {
        return date;
    }
}
