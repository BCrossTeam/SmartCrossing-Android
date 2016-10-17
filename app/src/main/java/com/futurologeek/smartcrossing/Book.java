package com.futurologeek.smartcrossing;

public class Book {
    String title;
    String author;
    int id;
    String year;

    enum Kategoria { Beletrystyka, Biografie, Biznes_i_inwestycje, Gotowanie, Historia, Komputery, Kryminały, Dla_dzieci, Polityka, Prawo, Religia, Romanse, SCI_FI, Zdrowie}


    public Book(int id, String title, String author){
        this.title = title;
        this.author = author;
        this.id = id;
    }

    public Book(String title, String author, String year){
        this.title = title;
        this.author = author;
        this.year = year;
    }

   public String getTitle(){
       return this.title;
   }

    public String getAuthor(){
        return this.author;
    }


}
