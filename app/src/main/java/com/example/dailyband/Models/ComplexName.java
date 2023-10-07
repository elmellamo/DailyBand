package com.example.dailyband.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class ComplexName implements Serializable {
    private String songid;
    private String title;
    private String writeruid;
    public ComplexName(){

    }

    public ComplexName(String songid, String title, String writeruid) {
        this.title = title;
        this.songid = songid;
        this.writeruid = writeruid;
    }

    public String getSongid(){return songid;}
    public void setSongid(String songid){this.songid = songid;}
    public String getTitle(){return title;}
    public void setTitle(String title){this.title= title;}
    public String getWriteruid(){return writeruid;}
    public void setWriteruid(String writeruid){this.writeruid = writeruid;}


    @Override
    public String toString() {
        return "ComplexName{" +
                "songid = '" + songid +'\''+
                ", title='" + title +'\''+
                ", writeruid='" + writeruid +
                '}';
    }
}
