package com.example.dailyband.Library;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.Models.TestSong;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Setting.SettingActivity;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.adapter.LoveAdapter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyLove extends AppCompatActivity {
    private ImageButton addbtn, setbtn, homeBtn;
    private RecyclerView recyclerView;
    private LoveAdapter adapter;
    private List<ComplexName> songs;
    private FirebaseMethods mFirebaseMethods;
    private LinearLayout emptytxt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylovemusic);

        mFirebaseMethods = new FirebaseMethods(MyLove.this);
        addbtn = findViewById(R.id.addbtn);
        homeBtn = findViewById(R.id.homeBtn);
        setbtn = findViewById(R.id.setbtn);
        recyclerView = findViewById(R.id.lovelist);
        emptytxt = findViewById(R.id.emptytxt);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songs = new ArrayList<>();
        getLove();

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(AddMusic.class);
            }
        });
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { myStartActivity(SettingActivity.class);    }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(HomeMain.class);
            }
        });
    }

    private void getLove(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String myUID = mFirebaseMethods.getMyUid();
        DatabaseReference loveRef = databaseReference.child("user_like").child(myUID);
        loveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    songs.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> objectMap = (HashMap<String, Object>) snapshot.getValue();
                        ComplexName song = new ComplexName();
                        song.setWriteruid(objectMap.get("writeruid").toString());
                        song.setTitle(objectMap.get("title").toString());
                        song.setSongid(objectMap.get("songid").toString());
                        songs.add(song);
                    }

                    adapter = new LoveAdapter(MyLove.this, songs);
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
