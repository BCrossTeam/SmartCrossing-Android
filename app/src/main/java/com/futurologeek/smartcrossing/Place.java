package com.futurologeek.smartcrossing;

public class Place {
    private String name;
    private int bookcount;
    double latitude;
    double longitude;


    public Place(String name, int bookcount, double latitude, double longitude){
        this.name = name;
        this.bookcount = bookcount;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    double getLatitude(){
        return latitude;
    }

    double getLongitude(){
        return longitude;
    }
    String getNamePlace(){
        return this.name;
    }

    int getBookcount(){
        return this.bookcount;
    }

    void setBookName(String newname){
        this.name = newname;
    }

    void setBookcount(int newcount){
        this.bookcount = newcount;
    }
}
