package com.futurologeek.smartcrossing;


import android.content.Context;

public class GetCategory {
    Context context;
    String string;
    public GetCategory(String string , Context context){
        this.context = context;
        this.string = string;
    }
    public static String returnString(Context context,String string){
        String cat[]=context.getResources().getStringArray(R.array.categoryArray);
        switch (string){
            case "fic":
                return cat[1];
            case "bio":
                return cat[2];
            case "bai":
                return cat[3];
            case "ckg":
                return cat[4];
            case "his":
                return cat[5];
            case "com":
                return cat[6];
            case "cst":
                return cat[7];
            case "kds":
                return cat[8];
            case "pls":
                return cat[9];
            case "law":
                return cat[10];
            case "rom":
                return cat[11];
            case "rel":
                return cat[12];
            case "sfi":
                return cat[13];
            case "hlt":
                return cat[14];
            case "NULL":
                return cat[15];
        }
        return "Inne";
    }
}
