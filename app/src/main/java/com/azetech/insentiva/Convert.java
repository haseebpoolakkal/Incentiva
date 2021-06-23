package com.azetech.insentiva;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Convert {
    public String ObjectToString(Object o) {
        String v = null;
        try {
            if (!o.equals("")) {
                v = String.valueOf(o);
            }
        } catch (Exception e) {
            return v;
        }
        return v;
    }

    public int StringToInt(String s) {
        int v = 0;
        try {
            if (!s.equals("")) {
                v = Integer.parseInt(s);
            }
        } catch (Exception e) {
            return v;
        }
        return v;
    }

    public String IntToString(int i) {
        String v = null;
        try {
            v = String.valueOf(i);
        } catch (Exception e) {
            return v;
        }
        return v;
    }

    public String subtract(String a, String b) {
        String value = "0";
        try {
            value = IntToString(StringToInt(a) - StringToInt(b));
            return value;
        } catch (Exception e) {
            return value;
        }
    }

    public String addition(String a, String b) {
        String value = "0";
        try {
            value = IntToString(StringToInt(a) + StringToInt(b));
            return value;
        } catch (Exception e) {
            return value;
        }
    }

    public String multiply(String a, String b){
        String value = "0";
        try{
            value = IntToString(StringToInt(a)*StringToInt(b));
            return value;
        } catch (Exception e){
            return value;
        }
    }

    public String divide(String a, String b){
        String value = "0";
        try{
            if (StringToInt(b) > 0) {
                value = IntToString(StringToInt(a) / StringToInt(b));
                return value;
            }
            else {
                return value;
            }
        } catch (Exception e){
            return value;
        }
    }

    public String percentage(String percent, String amount){
        String value = IntToString((StringToInt(percent) * StringToInt(amount)) / 100);
        return value;
    }

    public String getDate(){
        Calendar calfordate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        String date = currentDate.format(calfordate.getTime());
        return date;
    }

    public String getMonth(){
        Calendar calfordate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM");
        String date = currentDate.format(calfordate.getTime());
        return date;
    }

    public String getDay(){
        Calendar calfordate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat currentDate = new SimpleDateFormat("dd");
        String date = currentDate.format(calfordate.getTime());
        return date;
    }

    public String getYear(){
        Calendar calfordate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy");
        String date = currentDate.format(calfordate.getTime());
        return date;
    }
    public String getTime(){
        Calendar calforTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String time = currentTime.format(calforTime.getTime());
        return time;
    }
}
