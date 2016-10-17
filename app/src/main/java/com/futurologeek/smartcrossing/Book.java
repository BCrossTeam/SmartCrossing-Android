package com.futurologeek.smartcrossing;

public class Book {
    String title;
    String author;
    String rok;
    String kategoria;
    String creator;
    int id;
    String ISBN;
    enum Kategoria { Beletrystyka, Biografie, Biznes_i_inwestycje, Gotowanie, Historia, Komputery, Kryminały, Dla_dzieci, Polityka, Prawo, Religia, Romanse, SCI_FI, Zdrowie}

    private Kategoria lololo = Kategoria.Beletrystyka;

    public Book(String title, String author, String rok, String isbn){
        this.title = title;
        this.author = author;
        this.rok = rok;
        this.ISBN = isbn;
    }

    public Book(String title, String author, String rok){
        this.title = title;
        this.author = author;
        this.rok = rok;
    }

   public String getTitle(){
       return this.title;
   }

    public String getAuthor(){
        return this.author;
    }


}
