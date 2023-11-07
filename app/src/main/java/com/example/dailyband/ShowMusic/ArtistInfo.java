package com.example.dailyband.ShowMusic;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dailyband.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ArtistInfo extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView artist_detail_textview,info_text;
    private String artist, title, postId, userUid;
    private boolean isLiked;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);

        info_text = findViewById(R.id.info_text);
        artist_detail_textview = findViewById(R.id.artist_detail_textview);
        Intent intent = getIntent();
        artist = intent.getStringExtra("artist_intent");
        title = intent.getStringExtra("title_intent");
        postId = intent.getStringExtra("postId_intent");
        userUid = intent.getStringExtra("userUid_intent");
        //isLiked = intent.getBooleanExtra("isLiked_intent");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        info_text.setText(artist);
        mDatabase.child("user_introduce").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userUid)) {
                    String artist_introduce = snapshot.child(userUid).getValue().toString();
                    artist_detail_textview.setText(artist_introduce);
                }
                else{
                    artist_detail_textview.setText("아티스트 소개글이 없습니다");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
