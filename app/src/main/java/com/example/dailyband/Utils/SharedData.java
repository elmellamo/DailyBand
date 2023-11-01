package com.example.dailyband.Utils;

import android.app.Application;

public class SharedData extends Application {
    public String sharedString;

    public String getStr(){
        return sharedString;
    }

    public void setStr(String tmpstr){
        this.sharedString = tmpstr;
    }
}
