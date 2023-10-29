package com.example.dailyband.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class TestSong implements Parcelable {
    private String date_created;
    private String post_id;
    private String user_id;
    private String title;
    private String writer;
    private String explain;
    private String singer;
    private String play;
    private int love;

    public TestSong() {
    }

    public TestSong(String title, String date_created, String post_id,
                String user_id, int love, String writer, String explain, String singer,
                    String play) {
        this.date_created = date_created;
        this.post_id = post_id;
        this.user_id = user_id;
        this.title = title;
        this.love = love;
        this.writer = writer;
        this.explain = explain;
        this.singer = singer;
        this.play = play;
    }

    protected TestSong(Parcel in) {
        date_created = in.readString();
        post_id = in.readString();
        user_id = in.readString();
        title = in.readString();
        love = in.readInt();
        writer = in.readString();
        explain = in.readString();
        singer = in.readString();
        play = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date_created);
        dest.writeString(post_id);
        dest.writeString(user_id);
        dest.writeString(title);
        dest.writeInt(love);
        dest.writeString(writer);
        dest.writeString(explain);
        dest.writeString(singer);
        dest.writeString(play);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TestSong> CREATOR = new Creator<TestSong>() {
        @Override
        public TestSong createFromParcel(Parcel in) {
            return new TestSong(in);
        }

        @Override
        public TestSong[] newArray(int size) {
            return new TestSong[size];
        }
    };

    public static Creator<TestSong> getCREATOR() {
        return CREATOR;
    }

    public String getWriter(){return  writer;}
    public  void setWriter(String writer){this.writer = writer;}
    public  String getExplain(){return  explain;}
    public  void setExplain(String explain){this.explain = explain;}
    public  String getSinger(){return singer;}
    public  void setSinger(String singer){this.singer = singer;}
    public String getPlay(){return  play;}
    public  void setPlay(String play){this.play = play;}

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }


    public String getUser_id() { return user_id; }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getLove() {
        return love;
    }

    public void setLove(int love) {
        this.love = love;
    }
    @Override
    public String toString() {

        return "Post{" +
                "title='" + title + '\''+
                ", date_created='" + date_created + '\'' +
                ", post_id='" + post_id + '\'' +
                ", love='" + love + '\'' +
                ", writer='" + writer + '\''+
                ", explain='" + explain + '\'' +
                ", singer='" + singer + '\'' +
                ", play='" + play + '\'' +
                ", user_id='" + user_id +
                '}';
    }
}