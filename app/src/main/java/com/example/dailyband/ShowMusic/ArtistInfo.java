package com.example.dailyband.ShowMusic;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.dailyband.R;


public class ArtistInfo extends AppCompatActivity {
    private TextView artist_detail_textview;
    private String artist, title, postId;
    private boolean isLiked;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);

        artist_detail_textview = findViewById(R.id.artist_detail_textview);
        Intent intent = getIntent();
        artist = intent.getStringExtra("artist_intent");
        title = intent.getStringExtra("title_intent");
        postId = intent.getStringExtra("postId_intent");
        //isLiked = intent.getBooleanExtra("isLiked_intent");

        artist_detail_textview.setText(artist);
    }
}
