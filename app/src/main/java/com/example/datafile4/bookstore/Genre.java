package com.example.datafile4.bookstore;

/**
 * Created by datafile4 on 8/29/16.
 */
public class Genre {
    private int mID;
    private String mGenreName;

    public Genre(int ID, String genreName){
        mID = ID;
        mGenreName = genreName;
    }

    public String getGenreName(){
        return mGenreName;
    }

    public int getGenreID() {
        return mID;
    }
}
