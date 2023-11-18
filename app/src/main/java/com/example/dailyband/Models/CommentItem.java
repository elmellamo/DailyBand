package com.example.dailyband.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class CommentItem implements Parcelable {
    private String date_created;
    private String post_id;
    private String user_id;
    private String comment_id;
    private String contents;
    private int love;

    public CommentItem() {
    }

    public CommentItem(String comment_id, String contents, String post_id,
                    String user_id, int love, String date_created) {
        this.date_created = date_created;
        this.post_id = post_id;
        this.user_id = user_id;
        this.love = love;
        this.comment_id = comment_id;
        this.contents = contents;
    }

    protected CommentItem(Parcel in) {
        date_created = in.readString();
        post_id = in.readString();
        user_id = in.readString();
        love = in.readInt();
        comment_id = in.readString();
        contents = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date_created);
        dest.writeString(post_id);
        dest.writeString(user_id);
        dest.writeInt(love);
        dest.writeString(comment_id);
        dest.writeString(contents);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CommentItem> CREATOR = new Creator<CommentItem>() {
        @Override
        public CommentItem createFromParcel(Parcel in) {
            return new CommentItem(in);
        }

        @Override
        public CommentItem[] newArray(int size) {
            return new CommentItem[size];
        }
    };

    public static Creator<CommentItem> getCREATOR() {
        return CREATOR;
    }

    public String getComment_id(){return  comment_id;}
    public  void setComment_id(String comment_id){this.comment_id = comment_id;}
    public  String getContents(){return  contents;}
    public  void setContents(String contents){this.contents = contents;}
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

        return "CommentItem{" +
                "contents='" + contents + '\''+
                ", date_created='" + date_created + '\'' +
                ", post_id='" + post_id + '\'' +
                ", love='" + love + '\'' +
                ", comment_id='" + comment_id + '\''+
                ", user_id='" + user_id +
                '}';
    }
}