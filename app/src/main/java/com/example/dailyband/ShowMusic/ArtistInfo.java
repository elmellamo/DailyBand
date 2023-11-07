package com.example.dailyband.ShowMusic;


import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyband.Collection.CollectionActivity;
import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.Love.LoveActivity;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Setting.NewSettingActivity;
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
    private ImageButton addbtn, setbtn, librarybtn, myInfobtn, homeBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);

        info_text = findViewById(R.id.info_text);
        artist_detail_textview = findViewById(R.id.artist_detail_textview);
        artist_detail_textview.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        artist = intent.getStringExtra("artist_intent");
        title = intent.getStringExtra("title_intent");
        postId = intent.getStringExtra("postId_intent");
        userUid = intent.getStringExtra("userUid_intent");
        //isLiked = intent.getBooleanExtra("isLiked_intent");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        homeBtn = findViewById(R.id.homeBtn);
        myInfobtn = findViewById(R.id.myInfobtn);
        librarybtn = findViewById(R.id.librarybtn);
        setbtn = findViewById(R.id.setbtn);
        addbtn = findViewById(R.id.addbtn);

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
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(HomeMain.class);
            }
        });
        myInfobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(LoveActivity.class);
            }
        });
        librarybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(CollectionActivity.class);
            }
        });
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArtistInfo.this, AddMusic.class);
                intent.putExtra("parent_Id", "ori");
                startActivity(intent);
            }
        });
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { myStartActivity(NewSettingActivity.class);    }
        });
    }
    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
