package com.example.dailyband.Utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.Models.CommentItem;
import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.Models.TestSong;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
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

    public String getMyUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void getSongUrl(String postId, final OnSongUrlListener listener) {
        StorageReference songRef = mStorageReference.child("songs/"+postId+"/song");
        songRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String downloadUrl = uri.toString();
                listener.onSuccess(downloadUrl);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure("fail");
            }
        });
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
    public void chkIsLiked(String songId, final OnLikeCheckListener listener) {
        DatabaseReference likeRef = myRef.child("user_like").child(userID).child(songId);
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
    public void addLike(String title, String songId, String writer_uid, final OnLikeActionListener listener) {
        ComplexName complexName = new ComplexName();
        complexName.setSongid(songId);
        complexName.setTitle(title);
        complexName.setWriteruid(writer_uid);
        // 1. like 카테고리에서 해당 음악 postId의 child에 현재 사용자 uid를 추가하고 값을 true로 설정
        myRef.child("user_like").child(userID).child(songId).setValue(complexName)
                .addOnSuccessListener(aVoid -> {
                    // 2. songs 카테고리에서 해당 음악 postId의 love의 수를 하나 늘림
                    myRef.child("songs").child(songId).child("love").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int currentLove = 0;
                            if (dataSnapshot.exists()) {
                                currentLove = dataSnapshot.getValue(Integer.class);
                            }
                            myRef.child("songs").child(songId).child("love").setValue(currentLove + 1);

                            // 3. user_songs에서 사용자가 해당 음악 postId를 찾은 후에 love의 수를 하나 늘림
                            myRef.child("user_songs").child(writer_uid).child(songId).child("love").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int currentLove = 0;
                                    if (dataSnapshot.exists()) {
                                        currentLove = dataSnapshot.getValue(Integer.class);
                                    }
                                    myRef.child("user_songs").child(writer_uid).child(songId).child("love").setValue(currentLove + 1);
                                    listener.onLikeAdded();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    listener.onFailed("Failed to update user_songs love count.");
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            listener.onFailed("Failed to update songs love count.");
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    listener.onFailed("Failed to add like: " + e.getMessage());
                });
    }
    public void removeLike(String songId, String writer_uid, final OnLikeActionListener listener) {
        // 1. like 카테고리에서 해당 음악 postId의 child에 현재 사용자 uid를 제거
        myRef.child("user_like").child(userID).child(songId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // 2. songs 카테고리에서 해당 음악 postId의 love의 수를 하나 줄임
                    myRef.child("songs").child(songId).child("love").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int currentLove = 0;
                            if (dataSnapshot.exists()) {
                                currentLove = dataSnapshot.getValue(Integer.class);
                            }
                            myRef.child("songs").child(songId).child("love").setValue(currentLove - 1);
                            Log.e("태그", "remove~~~ "+  (currentLove)+"    "+(currentLove-1));

                            // 3. user_songs에서 사용자가 해당 음악 postId를 찾은 후에 love의 수를 하나 줄임
                            myRef.child("user_songs").child(writer_uid).child(songId).child("love").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int currentLove = 0;
                                    if (dataSnapshot.exists()) {
                                        currentLove = dataSnapshot.getValue(Integer.class);
                                    }
                                    myRef.child("user_songs").child(writer_uid).child(songId).child("love").setValue(currentLove - 1);
                                    Log.e("태그", "remove "+  (currentLove) +"    "+(currentLove-1));

                                    listener.onLikeRemoved();
                                    // 좋아요가 제거되었을 때 updateHeartButton 함수 호출
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    listener.onFailed("Failed to update user_songs love count.");
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            listener.onFailed("Failed to update songs love count.");
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    listener.onFailed("Failed to remove like: " + e.getMessage());
                });
    }
    public void addOrRemoveLike(String title, String songId, boolean isLiked, String writer_uid, OnLikeActionListener listener) {
        if (isLiked) {
            // 사용자가 좋아요를 누르지 않은 상태에서 눌렀을 때는 좋아요를 추가합니다.
            addLike(title, songId, writer_uid, listener);
        } else {
            // 사용자가 이미 좋아요를 누른 상태에서 또 눌렀을 때는 좋아요를 취소합니다.
            removeLike(songId, writer_uid, listener);
        }
    }


    public void CommentaddOrRemoveLike(String commentId, boolean isLiked, String writer_uid, OnLikeActionListener listener) {
        if (isLiked) {
            // 사용자가 좋아요를 누르지 않은 상태에서 눌렀을 때는 좋아요를 추가합니다.
            //addLike(title, songId, writer_uid, listener);
        } else {
            // 사용자가 이미 좋아요를 누른 상태에서 또 눌렀을 때는 좋아요를 취소합니다.
            //removeLike(songId, writer_uid, listener);
        }
    }
    public void uploadNewStorage(final String title, Uri fileUri, String postId) {
        if (fileUri == null) {
            Log.e("테스트", "File URI is null");
            // 적절한 예외 처리 또는 오류 메시지를 사용하여 사용자에게 알림
            return;
        }
        Log.e("테스트", "음악 업로드 중...");

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //uploadkey=1;
        //Uri fileUri = Uri.fromFile(new File(outputFile));

        // 업로드 경로 설정: "songs/{postId}/{fileName}"
        String uploadPath = "songs/" + postId + "/song";

        StorageReference storageReference = mStorageReference
                .child(uploadPath);
        storageReference.putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
            //업로드 시 성공 처리
            Toast.makeText(mContext, "노래가 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e->{
            //업로드 실패 시 처리
            Toast.makeText(mContext, "노래 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            Log.e("테스트", "노래 실패 원인??? >> "+e);

            int errorCode = ((StorageException) e).getErrorCode();
            String errorMessage = e.getMessage();
            Log.e("테스트", "노래 실패 원인 11 "+errorCode);
            Log.e("테스트", "노래 실패 원인 22 "+errorMessage);

        });

        Intent intent = new Intent(mContext, HomeMain.class);
        mContext.startActivity(intent);
    }

    public String addSongToDatabase(String title, String explain, String singer, String play, String writer, List<ComplexName> original){
        Log.e("로그", "addSongToDatabase: 데이터 베이스에 노래 추가");

        String newPostKey = myRef.child("songs").push().getKey();
        TestSong testSong = new TestSong();
        testSong.setTitle(title);
        testSong.setDate_created(getTimeStamp());
        testSong.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        testSong.setPost_id(newPostKey);
        testSong.setLove(1);
        testSong.setPlay(play);
        testSong.setExplain(explain);
        testSong.setSinger(singer);
        testSong.setWriter(writer);

        // 데이터베이스에 넣기
        assert newPostKey != null;
        myRef.child("user_songs").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(newPostKey).setValue(testSong);
        myRef.child("songs").child(newPostKey).setValue(testSong);
        
        if(!original.isEmpty()){
            //나의 postid 밑에 참고한 original들을 밑으로 리스트 쭉 넣고
            ComplexName mycomplex = new ComplexName();
            mycomplex.setSongid(newPostKey);
            mycomplex.setTitle(title);
            mycomplex.setWriteruid(userID);

            for(ComplexName ori : original){
                myRef.child("my_parents").child(newPostKey).child(ori.getSongid()).setValue(ori);
                myRef.child("my_children").child(ori.getSongid()).child(mycomplex.getSongid()).setValue(mycomplex);
                //mychildren 밑에는 origianl곡의 postid넣고, 그 밑에 콜라보한 곡의 id넣고, complexname 다 넣고
                //myparents밑에는 현재 곡의 postid넣고, 그 밑에 원곡되는 부모 곡의 id넣고, complexname 다 넣고
            }
        }

        return newPostKey;
    }

    public void addToOriginalComment(String comment, String comment_postID){
        String newCommentKey = myRef.child("comment").child(comment_postID).push().getKey();
        CommentItem oriComment = new CommentItem();

        oriComment.setComment_id(newCommentKey);
        oriComment.setContents(comment);
        oriComment.setLove(1);
        oriComment.setDate_created(getTimeStamp());
        oriComment.setPost_id(comment_postID);
        oriComment.setUser_id(userID);

        // 데이터베이스에 넣기
        assert newCommentKey != null;
        myRef.child("comment").
                child(comment_postID).child(newCommentKey).setValue(oriComment);
        Toast.makeText(mContext, "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
    }

    public void addToChildComment(String comment, String ori_commentid){
        String newReCommentKey = myRef.child("comment_child").child(ori_commentid).push().getKey();
        CommentItem oriComment = new CommentItem();

        oriComment.setComment_id(newReCommentKey);
        oriComment.setContents(comment);
        oriComment.setLove(1);
        oriComment.setDate_created(getTimeStamp());
        oriComment.setPost_id(ori_commentid);
        oriComment.setUser_id(userID);

        // 데이터베이스에 넣기
        assert newReCommentKey != null;
        myRef.child("comment_child").
                child(ori_commentid).child(newReCommentKey).setValue(oriComment);
        Toast.makeText(mContext, "답글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
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
