package com.futurologeek.smartcrossing;

import android.content.Context;


public class GetStringCode {

    static String getErrorResource(int error, Context ctx){
        switch(error){
            case -1:
                return ctx.getResources().getString(R.string.SUB_ERROR_CODE_001);

            case -2:
                return ctx.getResources().getString(R.string.SUB_ERROR_CODE_001);

            case -3:
                return ctx.getResources().getString(R.string.SUB_ERROR_CODE_003);

            case -4:
                return ctx.getResources().getString(R.string.SUB_ERROR_CODE_004);

            case -5:
                return ctx.getResources().getString(R.string.SUB_ERROR_CODE_001);

            case -6:
                return ctx.getResources().getString(R.string.SUB_ERROR_CODE_001);

            case -7:
                return ctx.getResources().getString(R.string.SUB_ERROR_CODE_001);

            case -8:
                return ctx.getResources().getString(R.string.SUB_ERROR_CODE_001);

            default:
                return ctx.getResources().getString(R.string.ERROR_UNKNOWN);

            case 1:
                return ctx.getResources().getString(R.string.ERROR_CODE_001);

            case 2:
                return ctx.getResources().getString(R.string.ERROR_CODE_002);

            case 3:
                return ctx.getResources().getString(R.string.ERROR_CODE_003);

            case 4:
                return ctx.getResources().getString(R.string.ERROR_CODE_004);

            case 5:
                return ctx.getResources().getString(R.string.ERROR_CODE_005);

            case 6:
                return ctx.getResources().getString(R.string.ERROR_CODE_006);

            case 7:
                return ctx.getResources().getString(R.string.ERROR_CODE_007);

            case 10:
                return ctx.getResources().getString(R.string.ERROR_CODE_010);

            case 11:
                return ctx.getResources().getString(R.string.ERROR_CODE_011);

            case 12:
                return ctx.getResources().getString(R.string.ERROR_CODE_012);

            case 13:
                return ctx.getResources().getString(R.string.ERROR_CODE_013);

            case 14:
                return ctx.getResources().getString(R.string.ERROR_CODE_014);

            case 20:
                return ctx.getResources().getString(R.string.ERROR_CODE_020);

            case 21:
                return ctx.getResources().getString(R.string.ERROR_CODE_021);

            case 22:
                return ctx.getResources().getString(R.string.ERROR_CODE_022);

            case 23:
                return ctx.getResources().getString(R.string.ERROR_CODE_023);

            case 24:
                return ctx.getResources().getString(R.string.ERROR_CODE_024);

            case 25:
                return ctx.getResources().getString(R.string.ERROR_CODE_025);

            case 30:
                return ctx.getResources().getString(R.string.ERROR_CODE_030);

            case 31:
                return ctx.getResources().getString(R.string.ERROR_CODE_031);

            case 32:
                return ctx.getResources().getString(R.string.ERROR_CODE_032);


        }
    }

    public static String getSuccessCode(int id, Context ctx){
        switch (id){
            case 10:
                return ctx.getResources().getString(R.string.SUCCES_CODE_010);

            case 11:
                return ctx.getResources().getString(R.string.SUCCES_CODE_011);

            case 12:
                return ctx.getResources().getString(R.string.SUCCES_CODE_012);

            case 13:
                return ctx.getResources().getString(R.string.SUCCES_CODE_013);

            case 14:
                return ctx.getResources().getString(R.string.SUCCES_CODE_014);

            case 20:
                return ctx.getResources().getString(R.string.SUCCES_CODE_020);

            case 21:
                return ctx.getResources().getString(R.string.SUCCES_CODE_021);

            case 22:
                return ctx.getResources().getString(R.string.SUCCES_CODE_022);

            case 23:
                return ctx.getResources().getString(R.string.SUCCES_CODE_023);

            case 24:
                return ctx.getResources().getString(R.string.SUCCES_CODE_024);

            case 25:
                return ctx.getResources().getString(R.string.SUCCES_CODE_025);

            case 30:
                return ctx.getResources().getString(R.string.SUCCES_CODE_030);

            case 31:
                return ctx.getResources().getString(R.string.SUCCES_CODE_031);

            case 32:
                return ctx.getResources().getString(R.string.SUCCES_CODE_032);

            case 33:
                return ctx.getResources().getString(R.string.SUCCES_CODE_033);

            case 34:
                return ctx.getResources().getString(R.string.SUCCES_CODE_034);

            default:
                return ctx.getResources().getString(R.string.UNKNOWN_SUCCESS);
        }
    }
}

