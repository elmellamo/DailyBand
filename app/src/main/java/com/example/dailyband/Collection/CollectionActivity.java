package com.example.dailyband.Collection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.Love.LoveActivity;
import com.example.dailyband.Models.TestSong;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Setting.NewSettingActivity;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.adapter.MySongAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scwang.wave.MultiWaveHeader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionActivity extends AppCompatActivity {
    MultiWaveHeader waveHeader;
    private RecyclerView recyclerView;
    private MySongAdapter adapter;
    private List<TestSong> songs;
    private FirebaseMethods mFirebaseMethods;
    private LinearLayout emptytxt;

    private ImageButton addbtn, setbtn, librarybtn, myInfobtn, homeBtn;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycollection_activity);

        mFirebaseMethods = new FirebaseMethods(CollectionActivity.this);
        waveHeader = findViewById(R.id.wave_header);
        addbtn = findViewById(R.id.addbtn);
        librarybtn = findViewById(R.id.librarybtn);
        myInfobtn = findViewById(R.id.myInfobtn);
        homeBtn = findViewById(R.id.homeBtn);
        setbtn = findViewById(R.id.setbtn);

        waveHeader.setVelocity(1);
        waveHeader.setProgress(1);
        waveHeader.isRunning();
        waveHeader.setGradientAngle(45);
        waveHeader.setWaveHeight(40);

        recyclerView = findViewById(R.id.mycollectionlist);
        emptytxt = findViewById(R.id.emptytxt);
        recyclerView.setLayoutManager(new LinearLayoutManager(CollectionActivity.this));
        songs = new ArrayList<>();
        getMySong();

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
                Intent intent = new Intent(CollectionActivity.this, AddMusic.class);
                intent.putExtra("parent_Id", "ori");
                startActivity(intent);
            }
        });
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { myStartActivity(NewSettingActivity.class);    }
        });
    }

    private void getMySong(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String myUID = mFirebaseMethods.getMyUid();
        DatabaseReference mysongRef = databaseReference.child("user_songs").child(myUID);
        mysongRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    songs.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> objectMap = (HashMap<String, Object>) snapshot.getValue();
                        TestSong song = new TestSong();
                        song.setLove(Integer.parseInt(objectMap.get("love").toString()));
                        song.setDate_created(objectMap.get("date_created").toString());
                        song.setTitle(objectMap.get("title").toString());
                        song.setPost_id(objectMap.get("post_id").toString());
                        song.setUser_id(objectMap.get("user_id").toString());
                        songs.add(song);
                    }

                    adapter = new MySongAdapter(CollectionActivity.this, songs);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    checkEmpty();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는데 실패한 경우 처리하는 코드
            }
        });
    }
    private void checkEmpty(){
        emptytxt.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
