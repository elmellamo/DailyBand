package com.example.dailyband;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dailyband.Utils.FirebaseMethods;

public class PickMusic extends AppCompatActivity {
    private ImageView heartbtn;
    private boolean isLiked = false;
    private TextView picksongname;
    private FirebaseMethods mFirebaseMethods;
    private String postId;
    private String title;
    private String writer_uid;

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
                mFirebaseMethods.addOrRemoveLike(postId, isLiked, writer_uid, new FirebaseMethods.OnLikeActionListener() {
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

    private void updateHeartButton(boolean isLiked) {
        if (isLiked) {
            heartbtn.setImageResource(R.drawable.full_heart);
        } else {
            heartbtn.setImageResource(R.drawable.empty_heart);
        }
    }
}
