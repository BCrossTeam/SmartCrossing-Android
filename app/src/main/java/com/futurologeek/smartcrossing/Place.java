package com.futurologeek.smartcrossing;

public class Place {
    private String name;
    private int bookcount;


    public Place(String name, int bookcount){
        this.name = name;
        this.bookcount = bookcount;

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