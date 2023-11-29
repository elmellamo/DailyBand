package com.example.dailyband.Collection;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.Love.LoveActivity;
import com.example.dailyband.Models.TestSong;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Setting.NewSettingActivity;
import com.example.dailyband.Utils.DataFetchCallback;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.adapter.MySongAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;
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
    private CircularFillableLoaders circularFillableLoaders;
    private ConstraintLayout circularlayout;
    private ImageView circle_iv;
    private boolean doubleBackToExitPressedOnce = false;


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
        circle_iv = findViewById(R.id.circle_iv);
        setbtn = findViewById(R.id.setbtn);
        circularlayout = findViewById(R.id.circularlayout);
        circularFillableLoaders = (CircularFillableLoaders)findViewById(R.id.circularFillableLoaders);
        circularlayout.bringToFront();

        waveHeader.setVelocity(1);
        waveHeader.setProgress(1);
        waveHeader.isRunning();
        waveHeader.setGradientAngle(45);
        waveHeader.setWaveHeight(40);
        setSize();
        recyclerView = findViewById(R.id.mycollectionlist);
        emptytxt = findViewById(R.id.emptytxt);
        recyclerView.setLayoutManager(new LinearLayoutManager(CollectionActivity.this));
        songs = new ArrayList<>();
        fetchData();

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

    private void getMySong(DataFetchCallback callback){
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
                    emptytxt.setVisibility(songs.size() == 0 ? View.VISIBLE : View.GONE);

                }else{
                    emptytxt.setVisibility(songs.size() == 0 ? View.VISIBLE : View.GONE);
                }
                callback.onDataFetchedSuccessfully();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는데 실패한 경우 처리하는 코드
                callback.onDataFetchFailed();
            }
        });
    }

    private void setSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int background = (int)(metrics.heightPixels * 0.18);
        ConstraintLayout.LayoutParams backgroundimg = (ConstraintLayout.LayoutParams) circle_iv.getLayoutParams();
        backgroundimg.height = background;
        backgroundimg.width = metrics.widthPixels;
        circle_iv.setLayoutParams(backgroundimg);
    }
    private void checkEmpty(){
        emptytxt.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
    private void fetchData() {
        showProgressBar();
        getMySong(new DataFetchCallback() {
            @Override
            public void onDataFetchedSuccessfully() {
                // getInfo 성공 후의 처리
                hideProgressBar();
            }
            @Override
            public void onDataFetchFailed() {
                // getInfo 실패 후의 처리
                hideProgressBar();
                songs.clear(); // 데이터를 가져오는데 실패했으므로 리스트 비우기
                adapter.notifyDataSetChanged(); // 어댑터에 변경된 내용 알림
                emptytxt.setVisibility(songs.size() == 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void showProgressBar() {
        // 프로그레스바를 보여주는 코드
        circularlayout.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        // 프로그레스바를 숨기는 코드
        circularlayout.animate()
                .alpha(0f) // 투명도를 0으로 설정하여 페이드 아웃 애니메이션 적용
                .setDuration(500) // 애니메이션 지속 시간 (밀리초)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // 애니메이션 종료 후에 실행할 작업 (예: 뷰를 숨기거나 제거하는 등)
                        circularlayout.setVisibility(View.GONE);
                    }
                })
                .start();
    }
    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        myStartActivity(HomeMain.class);
    }
}
