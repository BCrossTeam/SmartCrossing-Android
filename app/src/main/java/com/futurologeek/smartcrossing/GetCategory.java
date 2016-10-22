package com.futurologeek.smartcrossing;


import android.content.Context;

public class GetCategory {
    Context context;
    String string;


    public static String returnCategory(Context context, String string) {
        String cat[] = context.getResources().getStringArray(R.array.categoryArray);
        switch (string) {
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
            default:
                return "Inne";
        }
    }

    public static String returnCatCode(Context context, String string) {
        String cat[] = context.getResources().getStringArray(R.array.categoryArray);

        if (string.equals(cat[1])) {
            return "fic";
        } else if (string.equals(cat[2])) {
            return "bio";
        } else if (string.equals(cat[3])) {
            return "bai";
        } else if (string.equals(cat[4])) {
            return "ckg";
        } else if (string.equals(cat[5])) {
            return "his";
        } else if (string.equals(cat[6])) {
            return "com";
        } else if (string.equals(cat[7])) {
            return "cst";
        } else if (string.equals(cat[8])) {
            return "kds";
        } else if (string.equals(cat[9])) {
            return "pls";
        } else if (string.equals(cat[10])) {
            return "law";
        } else if (string.equals(cat[11])) {
            return "rom";
        } else if (string.equals(cat[12])) {
            return "rel";
        } else if (string.equals(cat[13])) {
            return "sfi";
        } else if (string.equals(cat[14])) {
            return "hlt";
        } else if (string.equals(cat[15])) {
            return "NULL";
        } else {
            return "OTHER";
        }

    }
}
