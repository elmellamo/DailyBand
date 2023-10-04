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
    private int love;

    public TestSong() {
    }

    public TestSong(String title, String date_created, String post_id,
                String user_id, int love) {
        this.date_created = date_created;
        this.post_id = post_id;
        this.user_id = user_id;
        this.title = title;
        this.love = love;
    }

    protected TestSong(Parcel in) {
        date_created = in.readString();
        post_id = in.readString();
        user_id = in.readString();
        title = in.readString();
        love = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date_created);
        dest.writeString(post_id);
        dest.writeString(user_id);
        dest.writeString(title);
        dest.writeInt(love);
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
                ", user_id='" + user_id +
                '}';
    }
}