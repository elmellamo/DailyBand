package com.example.dailyband.ShowMusic;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyband.Collection.CollectionActivity;
import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.Love.LoveActivity;
import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.Models.TestSong;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Setting.NewSettingActivity;
import com.example.dailyband.Utils.DataFetchCallback;
import com.example.dailyband.adapter.ArtistAdapter;
import com.example.dailyband.adapter.CollabAdapter;
import com.example.dailyband.adapter.MySongAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ArtistInfo extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView artist_detail_textview,info_text;
    private String artist, title, postId, userUid;
    private CircularFillableLoaders circularFillableLoaders;
    private ArtistAdapter adapter;
    private ImageButton addbtn, setbtn, librarybtn, myInfobtn, homeBtn;
    private ImageView bird_img;
    private List<TestSong> songs;
    private ConstraintLayout circularlayout;
    private RecyclerView artistsong;
    private ConstraintLayout background_artist;
    private LinearLayout emptytxt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);

        info_text = findViewById(R.id.info_text);
        artist_detail_textview = findViewById(R.id.artist_detail_textview);
        bird_img = findViewById(R.id.bird_img);
        background_artist = findViewById(R.id.background_artist);
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
        artistsong = findViewById(R.id.artistsong);
        emptytxt = findViewById(R.id.emptytxt);
        circularlayout = findViewById(R.id.circularlayout);
        circularFillableLoaders = (CircularFillableLoaders)findViewById(R.id.circularFillableLoaders);
        circularlayout.bringToFront();


        info_text.setSingleLine(true);    // 한줄로 표시하기
        info_text.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        info_text.setSelected(true);
        setSize();
        artistsong.setLayoutManager(new LinearLayoutManager(ArtistInfo.this));
        songs = new ArrayList<>();
        info_text.setText(artist);
        fetchData();

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

    private void setSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int profileimg = (int) (metrics.widthPixels * 0.39);
        ConstraintLayout.LayoutParams paramsprofile = (ConstraintLayout.LayoutParams) bird_img.getLayoutParams();
        paramsprofile.width = profileimg;
        paramsprofile.height = profileimg;
        bird_img.setLayoutParams(paramsprofile);

        int background = (int)(metrics.heightPixels * 0.36);
        ConstraintLayout.LayoutParams backgroundimg = (ConstraintLayout.LayoutParams) background_artist.getLayoutParams();
        backgroundimg.height = background;
        backgroundimg.width = metrics.widthPixels;
        background_artist.setLayoutParams(backgroundimg);
    }

    private void getMySong(DataFetchCallback callback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mysongRef = databaseReference.child("user_songs").child(userUid);
        mysongRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    songs.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TestSong song = snapshot.getValue(TestSong.class);
                        songs.add(song);
                    }

                    adapter = new ArtistAdapter(ArtistInfo.this, songs);
                    artistsong.setAdapter(adapter);
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
                // 데이터를 가져오는데 실패한 경우 처리하는 코드
                callback.onDataFetchFailed();
            }
        });
    }
    private void fetchData() {
        showProgressBar();

        getMySong(new DataFetchCallback() {
            @Override
            public void onDataFetchedSuccessfully() {
                // getInfo 성공 후의 처리

                getProfile(new DataFetchCallback() {
                    @Override
                    public void onDataFetchedSuccessfully() {
                        // getSongs 성공 후의 처리
                        hideProgressBar();
                        // 여기에서 프로그레스바를 숨김
                    }

                    @Override
                    public void onDataFetchFailed() {
                        // getSongs 실패 후의 처리
                        hideProgressBar();
                        adapter.notifyDataSetChanged(); // 어댑터에 변경된 내용 알림
                        Log.d("테스트", "여기서 실패");
                    }
                });
            }

            @Override
            public void onDataFetchFailed() {
                // getInfo 실패 후의 처리
                Log.d("테스트", "처음부터 실패");
                hideProgressBar();

                songs.clear(); // 데이터를 가져오는데 실패했으므로 리스트 비우기
                emptytxt.setVisibility(songs.size() == 0 ? View.VISIBLE : View.GONE);
                // 여기에서 프로그레스바를 숨김
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

    private void getProfile(DataFetchCallback callback){
        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + userUid + ".jpg");

        profileImageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                // 파일 메타데이터를 성공적으로 가져온 경우 파일이 존재함
                // 여기에서 이미지 다운로드 및 화면에 표시하는 작업 수행
                profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(ArtistInfo.this).load(uri).into(bird_img);
                        callback.onDataFetchedSuccessfully();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // 이미지 다운로드 실패 시 처리 (예: 기본 이미지 설정 또는 오류 메시지 출력)

                        callback.onDataFetchedSuccessfully();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // 파일 메타데이터 가져오기 실패 시 파일이 존재하지 않음
                // 해당 경우에 대한 처리를 수행 (예: 기본 이미지 설정 또는 다른 처리)

                callback.onDataFetchFailed();
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
         // 현재 액티비티 종료

        Log.d("테스트", "title "+title+"  postid "+postId);
        if(title.equals(postId)){
            DatabaseReference songsRef = FirebaseDatabase.getInstance().getReference("songs");
            songsRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        TestSong selectedSong = dataSnapshot.getValue(TestSong.class);
                        Intent intent = new Intent(ArtistInfo.this, NewPickMusic.class);
                        intent.putExtra("selectedSong", selectedSong);

                        Log.d("테스트", "plz...");
                        //startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 에러 처리 (예: 데이터베이스 접근 실패 등)

                    Log.d("테스트", "에러 뜸");
                }
            });

        }else{
            setResult(Activity.RESULT_OK); // 현재 액티비티의 결과를 설정
            finish();
        }
    }

}
