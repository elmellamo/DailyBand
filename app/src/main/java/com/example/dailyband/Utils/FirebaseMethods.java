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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        testSong.setLove(0);

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
}
