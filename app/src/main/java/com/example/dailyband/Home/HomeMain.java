package com.example.dailyband.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Library.LibraryMain;
import com.example.dailyband.Library.MyCollect;
import com.example.dailyband.Library.MyLove;
import com.example.dailyband.Models.TestSong;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Setting.SettingActivity;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.adapter.RankingSongAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeMain extends AppCompatActivity{
    private ImageButton addbtn, setbtn, librarybtn, myInfobtn;
    private TextView username;
    private RecyclerView recyclerView;
    private RankingSongAdapter adapter;
    private List<TestSong> songs;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth mAuth;
    private String nickname;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homemain);

        mFirebaseMethods = new FirebaseMethods(HomeMain.this);
//
        nickname = "사용자";
        addbtn = findViewById(R.id.addbtn);
        librarybtn = findViewById(R.id.librarybtn);
        recyclerView = findViewById(R.id.popularlist);
        myInfobtn = findViewById(R.id.myInfobtn);
        username = findViewById(R.id.username);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songs = new ArrayList<>();
        getInfo();
        getSongs();
        username.setText(nickname);

        librarybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(LibraryMain.class);
            }
        });
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //myStartActivity(TestAdd.class);
                myStartActivity(AddMusic.class);
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

    private void getInfo(){
        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userAccountRef = FirebaseDatabase.getInstance().getReference().child("UserAccount").child(userUID);
        userAccountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userAccountDataSnapshot) {
                if (userAccountDataSnapshot.exists()) {
                    // UserAccount 카테고리에서 name 값을 가져와서 사용하거나 처리할 수 있습니다.
                    nickname = userAccountDataSnapshot.child("name").getValue(String.class);
                    username.setText(nickname);
                } else {
                    // "UserAccount" 카테고리에서 해당 데이터가 없는 경우에 대한 처리
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기가 실패한 경우에 대한 처리
            }
        });
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
