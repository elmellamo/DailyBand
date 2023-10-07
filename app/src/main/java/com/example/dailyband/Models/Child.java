package com.example.dailyband.Models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class Child implements Parcelable {
    private List<ComplexName> parents;

    public Child() {
    }

    public Child(List<ComplexName> parents) {
        this.parents = parents;
    }

    protected Child(Parcel in) {
        in.readList(parents, ComplexName.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(parents);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Child> CREATOR = new Creator<Child>() {
        @Override
        public Child createFromParcel(Parcel in) {
            return new Child(in);
        }

        @Override
        public Child[] newArray(int size) {
            return new Child[size];
        }
    };

    public List<ComplexName> getParents() {
        return parents;
    }

    public void setParents(List<ComplexName> parents) {
        this.parents = parents;
    }

    @Override
    public String toString() {
        return "Child {" +
                "parents = '" + parents +
                '}';
    }
}
