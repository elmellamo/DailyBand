package com.example.dailyband.ShowMusic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.R;
import com.example.dailyband.MusicAdd.TestParent;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.adapter.CollabAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickMusic extends AppCompatActivity {
    private ImageView heartbtn;
    private boolean isLiked = false;
    private ImageView optionmenu;
    private TextView picksongname;
    private FirebaseMethods mFirebaseMethods;
    private String postId;
    private String title;
    private String writer_uid;
    private RecyclerView recyclerView;
    private LinearLayout emptytxt;
    private void updateHeartButton(boolean like) {
        heartbtn = findViewById(R.id.heartbtn);
        if (like) {
            heartbtn.setImageResource(R.drawable.heart_full);
            isLiked = true;
        } else {
            heartbtn.setImageResource(R.drawable.heart_outline_new);
            isLiked = false;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickmusic);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        title = intent.getStringExtra("title");
        writer_uid = intent.getStringExtra("user_id");

        mFirebaseMethods = new FirebaseMethods(PickMusic.this);

        picksongname = findViewById(R.id.pick_songname);
        picksongname.setText(title);
        heartbtn = findViewById(R.id.heartbtn);
        optionmenu = findViewById(R.id.optionmenu);
        emptytxt = findViewById(R.id.emptytxt);

        recyclerView = findViewById(R.id.collaborationlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getCollab(postId);

        optionmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(PickMusic.this, TestParent.class);
                intent2.putExtra("parent_Id", postId);
                intent2.putExtra("parent_title", title);
                intent2.putExtra("writer_uid", writer_uid); //해당 노래를 작곡한 사람
                startActivity(intent2);
            }
        });

        mFirebaseMethods.chkIsLiked(postId, new FirebaseMethods.OnLikeCheckListener() {
            @Override
            public void onLikeChecked(boolean isLiked) {
                // 좋아요 상태를 받아왔을 때의 처리
                updateHeartButton(isLiked);
            }

            @Override
            public void onLikeCheckFailed(String errorMessage) {
                // 좋아요 상태 확인에 실패했을 때의 처리
                Log.e("로그", "좋아요 확인 실패: " + errorMessage);
            }
        });

        heartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // isLiked 값 반전해야 함
                isLiked = !isLiked;
                // 여기서 해당 postId를 사용자의 좋아요 리스트에 추가 또는 삭제하기
                mFirebaseMethods.addOrRemoveLike(title, postId, isLiked, writer_uid, new FirebaseMethods.OnLikeActionListener() {
                    @Override
                    public void onLikeAdded() {
                        // 좋아요가 추가되었을 때의 처리
                        updateHeartButton(true);
                    }

                    @Override
                    public void onLikeRemoved() {
                        // 좋아요가 제거되었을 때의 처리
                        updateHeartButton(false);
                    }

                    @Override
                    public void onFailed(String errorMessage) {
                        // 좋아요 추가 또는 제거에 실패한 경우의 처리
                        // errorMessage를 이용하여 실패 이유를 확인할 수 있음
                        Log.e("로그", "좋아요 처리 실패: " + errorMessage);
                    }
                });
            }
        });
    }

    private void getCollab(String postId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        List<ComplexName> collabsongs = new ArrayList<>();
        DatabaseReference collabRef = databaseReference.child("my_children").child(postId);
        collabRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                collabsongs.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    ComplexName song = new ComplexName();
                    song.setWriteruid(objectMap.get("writeruid").toString());
                    song.setTitle(objectMap.get("title").toString());
                    song.setSongid(objectMap.get("songid").toString());
                    collabsongs.add(song);
                }

                CollabAdapter adapter = new CollabAdapter(PickMusic.this, collabsongs);

                adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        checkEmpty();
                    }

                    void checkEmpty(){
                        emptytxt.setVisibility(adapter.getItemCount()==0?View.VISIBLE:View.GONE);
                    }
                });

                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
