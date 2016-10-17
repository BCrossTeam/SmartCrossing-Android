package com.futurologeek.smartcrossing;

public class Place {
    private String name;
    private int bookcount;
    double latitude;
    double longitude;
    private int id;


    public Place(int id, String name, double latitude, double longitude,  int bookcount){
        this.id = id;
        this.name = name;
        this.bookcount = bookcount;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public Place(int id, String name, double latitude, double longitude){
        this.name = name;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    double getLatitude(){
        return this.latitude;
    }

    int getId(){
        return this.id;
    }
    double getLongitude(){
        return this.longitude;
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
