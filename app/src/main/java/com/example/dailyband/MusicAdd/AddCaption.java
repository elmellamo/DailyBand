package com.example.dailyband.MusicAdd;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.R;
import com.example.dailyband.Utils.FirebaseMethods;

import java.util.ArrayList;
import java.util.List;

public class AddCaption extends AppCompatActivity {
    private String title, postId;
    private Uri uri;
    private TextView writtentitle, savemenu;
    private List<ComplexName> parents;
    private EditText writer_content, play_content, play_singer, play_explain;
    private FirebaseMethods mFirebaseMethods;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_caption);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        uri = Uri.parse(intent.getStringExtra("uri"));

        mFirebaseMethods = new FirebaseMethods(AddCaption.this);
        parents = new ArrayList<>(); //나중에 이거 없애고 intent로 받아와서 원곡 누구 있는지 알아야 함.

        play_explain = findViewById(R.id.play_explain);
        writer_content = findViewById(R.id.writer_content);
        play_content = findViewById(R.id.play_content);
        play_singer = findViewById(R.id.play_singer);
        writtentitle = findViewById(R.id.writtentitle);
        savemenu = findViewById(R.id.savemenu);

        writtentitle.setText(title);

        savemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goUpload();
            }
        });
    }

    private  void goUpload(){
        String writer, play, singer, explain;
        explain = play_explain.getText().toString();
        singer = play_singer.getText().toString();
        play = play_content.getText().toString();
        writer = writer_content.getText().toString();
        if (explain.isEmpty()) {
            explain = "음악 소개가 없습니다";
        }
        if (singer.isEmpty()) {
            singer = "노래를 부른 사람에 대한 설명이 없습니다";
        }
        if (play.isEmpty()) {
            play = "사용한 연주에 대한 설명이 없습니다";
        }
        if (writer.isEmpty()) {
            writer = "작곡한 사람에 대한 설명이 없습니다";
        }

        postId = mFirebaseMethods.addSongToDatabase(title, explain, singer, play, writer, parents);
        mFirebaseMethods.uploadNewStorage(title, uri, postId);
        myStartActivity(HomeMain.class);
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
