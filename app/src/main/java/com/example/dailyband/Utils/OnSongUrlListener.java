package com.example.dailyband.Utils;
public interface OnSongUrlListener {
    void onSuccess(String songUrl);
    void onFailure(String errorMessage);
}