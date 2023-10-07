package com.example.dailyband;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Models.TestSong;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.Utils.TestAdd;
import com.example.dailyband.adapter.RankingSongAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeMain extends AppCompatActivity{
    private ImageButton addbtn;
    private RecyclerView recyclerView;
    private RankingSongAdapter adapter;
    private List<TestSong> songs;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth mAuth;
    private ImageButton setbtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homemain);

        mFirebaseMethods = new FirebaseMethods(HomeMain.this);
//
        recyclerView = findViewById(R.id.popularlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songs = new ArrayList<>();

        getSongs();

        addbtn = findViewById(R.id.addbtn);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(TestAdd.class);
                //myStartActivity(AddMusic.class);
            }
        });
        setbtn = findViewById(R.id.setbtn);
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { myStartActivity(SettingActivity.class);    }
        });
    }

    private void getSongs(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                songs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TestSong song = snapshot.getValue(TestSong.class);
                    if (song != null) {
                        songs.add(song);
                    }
                }

                // songs 리스트를 love를 기준으로 내림차순으로 정렬하고, love가 같을 경우에는 시간을 기준으로 내림차순으로 정렬
                Collections.sort(songs, new Comparator<TestSong>() {
                    @Override
                    public int compare(TestSong song1, TestSong song2) {
                        // love가 큰 순서대로 정렬
                        int loveComparison = Integer.compare(song2.getLove(), song1.getLove());
                        if (loveComparison != 0) {
                            return loveComparison;
                        }
                        // love가 같은 경우 시간을 큰 순서대로 정렬
                        return song2.getDate_created().compareTo(song1.getDate_created());
                    }
                });

                // 정렬된 데이터를 리사이클러뷰에 표시하기 위해 어댑터에 설정
                RankingSongAdapter adapter = new RankingSongAdapter(HomeMain.this, songs);
                recyclerView.setAdapter(adapter);

                // songs 리스트에 파이어베이스에서 읽어온 데이터가 들어있음
                // 이제 songs 리스트를 정렬하고 리사이클러뷰에 표시할 수 있음
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는데 실패한 경우 처리하는 코드
                Toast.makeText(HomeMain.this, "노래가 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
