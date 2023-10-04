package com.example.dailyband.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.dailyband.HomeMain;
import com.example.dailyband.Models.TestSong;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class FirebaseMethods {
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userID;
    private StorageReference mStorageReference;
    public int uploadkey;
    private List<String> pleaseUpload;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;

        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }


    //like 카테고리에 해당 Post에 유저가 좋아요 했는지 안했는지 체크하는 함수
    public interface OnLikeCheckListener {
        void onLikeChecked(boolean isLiked);
        void onLikeCheckFailed(String errorMessage);
    }

    public interface OnLikeActionListener {
        void onLikeAdded();
        void onLikeRemoved();
        void onFailed(String errorMessage);
    }

    public void chkIsLiked(String postId, final OnLikeCheckListener listener) {
        DatabaseReference likeRef = myRef.child("like").child(postId).child(userID);
        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 사용자가 해당 음악을 이미 좋아요한 상태
                    listener.onLikeChecked(true);
                } else {
                    // 사용자가 해당 음악을 좋아요하지 않은 상태
                    listener.onLikeChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onLikeCheckFailed(databaseError.getMessage());
            }
        });
    }

    // 음악을 좋아요 했을 때의 동작을 처리하는 메서드
    public void addLike(String songId, String writer_uid, final OnLikeActionListener listener) {
        // 1. like 카테고리에서 해당 음악 postId의 child에 현재 사용자 uid를 추가하고 값을 true로 설정
        myRef.child("like").child(songId).child(userID).setValue(true)
                .addOnSuccessListener(aVoid -> {
                    // 2. songs 카테고리에서 해당 음악 postId의 love의 수를 하나 늘림
                    myRef.child("songs").child(songId).child("love")
                            .runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                    int currentLove = 0;
                                    if (mutableData.getValue() != null) {
                                        currentLove = mutableData.getValue(int.class);
                                        mutableData.setValue(currentLove + 1);
                                    }else{
                                        mutableData.setValue(0);
                                    }

                                    Log.e("태그", "add 1번 "+currentLove);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                                    if (committed) {
                                        // 3. user_songs에서 사용자가 해당 음악 postId를 찾은 후에 love의 수를 하나 늘림
                                        myRef.child("user_songs").child(writer_uid).child(songId).child("love")
                                                .runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                                        int currentLove = 0;
                                                        if (mutableData.getValue() != null) {
                                                            currentLove = mutableData.getValue(int.class);
                                                            mutableData.setValue(currentLove + 1);
                                                        }else{
                                                            mutableData.setValue(0);
                                                        }

                                                        Log.e("태그", "add 2번 "+currentLove);

                                                        return Transaction.success(mutableData);
                                                    }

                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                                                        if (committed) {
                                                            listener.onLikeAdded();
                                                        } else {
                                                            listener.onFailed("Failed to update user_songs love count.");
                                                        }
                                                    }
                                                });
                                    } else {
                                        listener.onFailed("Failed to update songs love count.");
                                    }
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    listener.onFailed("Failed to add like: " + e.getMessage());
                });
    }

    // 음악 좋아요를 취소했을 때의 동작을 처리하는 메서드
    public void removeLike(String songId, String writer_uid, final OnLikeActionListener listener) {
        // 1. like 카테고리에서 해당 음악 postId의 child에 현재 사용자 uid를 제거
        myRef.child("like").child(songId).child(userID).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // 2. songs 카테고리에서 해당 음악 postId의 love의 수를 하나 줄임
                    myRef.child("songs").child(songId).child("love")
                            .runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                    int currentLove = 0;
                                    if (mutableData.getValue() != null) {
                                        currentLove = mutableData.getValue(int.class);
                                        mutableData.setValue(currentLove - 1);
                                    }else{
                                        mutableData.setValue(0);
                                    }

                                    Log.e("태그", "remove 1번 "+currentLove);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                                    if (committed) {
                                        // 3. user_songs에서 사용자가 해당 음악 postId를 찾은 후에 love의 수를 하나 줄임
                                        myRef.child("user_songs").child(writer_uid).child(songId).child("love")
                                                .runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                                        int currentLove = 0;
                                                        if (mutableData.getValue() != null) {
                                                            currentLove = mutableData.getValue(int.class);
                                                            mutableData.setValue(currentLove - 1);
                                                        }else{
                                                            mutableData.setValue(0);
                                                        }

                                                        Log.e("태그", "remove 2번 "+currentLove);
                                                        return Transaction.success(mutableData);
                                                    }

                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                                                        if (committed) {
                                                            listener.onLikeRemoved();
                                                        } else {
                                                            listener.onFailed("Failed to update user_songs love count.");
                                                        }
                                                    }
                                                });
                                    } else {
                                        listener.onFailed("Failed to update songs love count.");
                                    }
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    listener.onFailed("Failed to remove like: " + e.getMessage());
                });
    }

    public void addOrRemoveLike(String songId, boolean isLiked, String writer_uid, OnLikeActionListener listener) {
        if (isLiked) {
            // 사용자가 좋아요를 누르지 않은 상태에서 눌렀을 때는 좋아요를 추가합니다.
            addLike(songId, writer_uid, listener);
        } else {
            // 사용자가 이미 좋아요를 누른 상태에서 또 눌렀을 때는 좋아요를 취소합니다.
            removeLike(songId, writer_uid, listener);
        }
    }


    //부모, 자식 없이 우선 타이틀, 녹음만 스토리지에 올리기
    //흑흑 녹음 왜 오류나는 거임 >> 타이틀만 우선해서 하트순> 최신순 실험해보자
    public void uploadNewStorage(final String title, String outputFile, String postId) {
        Log.e("로그", "음악 업로드 중...");

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //uploadkey=1;
        Uri fileUri = Uri.fromFile(new File(outputFile));
        StorageReference storageReference = mStorageReference
                .child("songs/" + postId + "/" + fileUri.getLastPathSegment());
        storageReference.putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
           //업로드 시 성공 처리
            Toast.makeText(mContext, "노래가 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e->{
            //업로드 실패 시 처리
            Toast.makeText(mContext, "노래 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        });


        Intent intent = new Intent(mContext, HomeMain.class);
        mContext.startActivity(intent);
    }

    public String addSongToDatabase(String title){
        Log.e("로그", "addSongToDatabase: 데이터 베이스에 노래 추가");

        String newPostKey = myRef.child("songs").push().getKey();
        TestSong testSong = new TestSong();
        testSong.setTitle(title);
        testSong.setDate_created(getTimeStamp());
        testSong.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        testSong.setPost_id(newPostKey);
        testSong.setLove(1);

        // 데이터베이스에 넣기
        assert newPostKey != null;
        myRef.child("user_songs").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(newPostKey).setValue(testSong);
        myRef.child("songs").child(newPostKey).setValue(testSong);

        return newPostKey;
    }

    private String getTimeStamp(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.KOREA);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return simpleDateFormat.format(new Date());
    }

    private String getUserID() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }
}
