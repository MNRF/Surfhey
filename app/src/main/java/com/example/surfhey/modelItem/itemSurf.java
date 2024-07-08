package com.example.surfhey.modelItem;

import com.google.firebase.Timestamp;

public class itemSurf {
    public static String[] itemAuthorname = new String[0];
    public static String[] itemImageURL = new String[0];
    public static String[] itemDate = new String[0];
    public static String[] itemTitle = new String[0];
    public static String[] itemDetail = new String[0];
    public static String[] itemLikes = new String[0];
    public static String[] itemPostID = new String[0];

    public static void updateData(String[] authorname , String[] imageurls, String[] dates, String[] titles, String[] details, String[] likes, String[] postID) {
        itemAuthorname = authorname;
        itemImageURL = imageurls;
        itemDate = dates;
        itemTitle = titles;
        itemDetail = details;
        itemLikes = likes;
        itemPostID = postID;

    }
}