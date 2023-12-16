package com.example.dailyband.ShowMusic;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.dailyband.Models.TestSong;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Utils.OnDeleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.scwang.wave.MultiWaveHeader;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DetailInfoFragment extends Fragment implements OnDeleteListener {

    private View view;
    private ConstraintLayout commentlayout, downloadlayout, artistlayout, creditlayout, collablayout, collalayout, orilayout, deletelayout;
    private boolean isLiked;
    private String title, artist, postId, userUid;
    private ImageView heartbtn;
    private TextView songtitle, songwriter, lovenum;
    private String myId;
    private ArrayList<String> originalComment;
    private ArrayList<String> childComment;
    public DetailInfoFragment() {
    }

    public void setDetailInfo(boolean isLiked, String title, String artist, String postId, String userUid){
        this.isLiked = isLiked;
        this.title = title;
        this.artist = artist;
        this.postId = postId;
        this.userUid = userUid;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.detail_info, container, false);

        commentlayout = view.findViewById(R.id.commentlayout);
        downloadlayout = view.findViewById(R.id.downloadlayout);
        artistlayout = view.findViewById(R.id.artistlayout);
        creditlayout = view.findViewById(R.id.creditlayout);
        collablayout = view.findViewById(R.id.collablayout);
        collalayout = view.findViewById(R.id.collalayout);
        orilayout = view.findViewById(R.id.orilayout);
        lovenum = view.findViewById(R.id.lovenum);
        deletelayout = view.findViewById(R.id.deletelayout);
        heartbtn = view.findViewById(R.id.heartbtn);
        songtitle = view.findViewById(R.id.songtitle);
        songwriter = view.findViewById(R.id.songwriter);


        originalComment = new ArrayList<>();
        childComment = new ArrayList<>();



        if(isLiked){
            heartbtn.setImageResource(R.drawable.dark_heart_full);
        }else{
            heartbtn.setImageResource(R.drawable.dark_heart_empty);
        }


        myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(myId.equals(userUid)){
            Log.d("테스트", "setvisible!!!!!!!!!");
            deletelayout.setVisibility(View.VISIBLE);
        }


        songtitle.setText(title);
        songwriter.setText(artist);

        songwriter.setSingleLine(true);    // 한줄로 표시하기
        songwriter.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        songwriter.setSelected(true);

        songtitle.setSingleLine(true);    // 한줄로 표시하기
        songtitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        songtitle.setSelected(true);
        setLovenum();

        deletelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                해당 게시물은 postid라고 한다면
                comment에서 해당 게시물 아예 삭제 >> 여기 commentid가져와서 여기서 또
                user_comment_love에서 각 사용자들 돌면서 >> 해당 commentid 삭제

                songs에서 해당 게시물 삭제
                userlike에서는 각 사용자들 돌면서 해당 postid 삭제
                user_songs에서도 해당 사용자, 해당 게시물 삭제
                 */
                totaldelete();

            }
        });

        commentlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //해당 액티비티 관련 인포 뜨게 만들어야 한다.
                if(getActivity() instanceof NewPickMusic){
                    NewPickMusic newPickMusic = (NewPickMusic) getActivity();
                    newPickMusic.showUpComment();
                }
            }
        });

        downloadlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();

                values.put(MediaStore.Audio.Media.TITLE, title);
                values.put(MediaStore.Audio.Media.DISPLAY_NAME, title+".wav");
                values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (System.currentTimeMillis() / 1000));
                values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/x-wav");
                values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Daily Band/Downloads/");

                Uri uri = getContext().getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

                try {
                    OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri);

                    StorageReference songRef = FirebaseStorage.getInstance().getReference().child("songs/" + postId + "/song");
                    songRef.getStream((state, inputStream) -> {

                        long totalBytes = state.getTotalByteCount();
                        long bytesDownloaded = 0;

                        byte[] buffer = new byte[1024];
                        int size;
                        while ((size = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, size);
                            bytesDownloaded += size;
                        }

                        inputStream.close();

                    }).addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(), "다운로드 완료", Toast.LENGTH_SHORT).show();
                        //Log.e("로그", "지금 되고 있는거야?" + uri.toString());
                    }).addOnFailureListener(e -> {
                        Log.w("asdf", "download:FAILURE", e);
                    });
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        artistlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //완전 새로운 액티비티를 열어야 한다.
                //ArtistInfo 열어야함, intent 보내면서
                Intent intent = new Intent(getActivity(), ArtistInfo.class);
                //isLiked, title, artist, postId;

                //intent.putExtra("isLiked_intent", isLiked);
                intent.putExtra("title_intent", title);
                intent.putExtra("artist_intent", artist);
                intent.putExtra("postId_intent", postId);
                intent.putExtra("userUid_intent",userUid);
                startActivityForResult(intent, 1);
            }
        });
        creditlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //해당 액티비티 관련 인포 뜨게 만들어야 한다.
                if(getActivity() instanceof NewPickMusic){
                    NewPickMusic newPickMusic = (NewPickMusic) getActivity();
                    newPickMusic.showUpInfo();
                }
            }
        });
        collablayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddMusic.class);
                intent.putExtra("parent_Id", postId);
                startActivity(intent);
            }
        });
        collalayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() instanceof NewPickMusic){
                    NewPickMusic newPickMusic = (NewPickMusic) getActivity();
                    newPickMusic.showUpColla();
                }
            }
        });
        orilayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() instanceof NewPickMusic){
                    NewPickMusic newPickMusic = (NewPickMusic) getActivity();
                    newPickMusic.showUpParent();
                }
            }
        });


        return view;
    }

    private void setLovenum(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("songs").child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    TestSong thissong = dataSnapshot.getValue(TestSong.class);
                    if(thissong.getLove()-1 >999){
                        lovenum.setText("999+");
                    }else{
                        lovenum.setText(String.valueOf(thissong.getLove()-1));
                    }
                } else {
                    // 해당 postId에 대한 데이터가 존재하지 않음
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터를 가져오는데 실패한 경우
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e("로그", "프레그먼트에서 좋아요 확인 >>>"+isLiked );
        // isLiked 값에 따라 heartbtn의 이미지 설정
        if (isLiked) {
            heartbtn.setImageResource(R.drawable.dark_heart_full);
        } else {
            heartbtn.setImageResource(R.drawable.dark_heart_empty);
        }
        // 기타 로직 수행
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                // ArtistInfo 액티비티에서 돌아온 후 처리할 작업
                // 이전 액티비티로 돌아왔을 때 해야 할 작업을 여기에 추가하세요
            }
        }
    }

    private void totaldelete(){
        /*      삭제하고 싶은 해당 게시물의 id를 postid라고 한다면

        deleteComment() 함수
                comment에서 해당 게시물 postid 아래에 있는 것들  originalComment에 저장하고, 아예 삭제
                >> originalComment를 순회하면서 comment_child에 있는 것들 여기 childComment리스트에 commentid 저장, 아예 삭제
                >> user_comment_love에서 각 사용자들 돌면서 >> originalcomment, childcomment리스트에 있는 것들 있으면 다 없애기

        deleteSong() 함수
                songs에서 해당 게시물 삭제
                user_songs에서도 해당 사용자, 해당 게시물 삭제
        deleteSongLike() 함수
                userlike에서는 각 사용자들 돌면서 해당 postid 삭제

        deleteFamily() 함수
                my_children에 child로 postId 있느냐를 따지고
                ㅇ-> 자식이 있다면 해당 자식들 다 모아서 childList 리스트에 넣어. 그리고 해당 키는 지워.
                리스트에 있는 자식들을 my_parents에서 순회해야 한다. 그리고 그 안에 딸린 것에 postid 나를 지워야해

                그리고 my_parents에 child로 postId 있느냐? (부모 - 자식)
                o -> 부모가 있다면 해당 부모들 다 모아서 parentList 리스트에 넣어. 그리고 해당 키는 지워.
                리스트에 있는 부모들을 my_children에서 순회해야 해. 그리고 그 안에 딸린 것에서 나를 지워.



                1           2             3          4
                (나 - 자식) (부모-나-자식) (부모 - 나) (나)
                1번, 2번 둘다 나가 결국 부모임
                2, 3번은 둘다 내가 자식인 경우 따져야

                1번은 내가 my_children에 child로 있느냐를 따지고
                ㅇ-> 자식이 있다면 해당 자식들 다 모아서 리스트에 넣어. 그리고 해당 키는 지워.
                리스트에 있는 자식들을 my_parents에서 순회해야 한다. 그리고 그 안에 딸린 것에 postid 나를 지워야해
                x -> 있을 수 없는 경우이다!

                2번은 내가 my_children에 child로 있느냐 ? (나-자식)
                ㅇ-> 자식이 있다면 해당 자식들 다 모아서 리스트에 넣어. 그리고 해당 키는 지워.
                리스트에 있는 자식들을 my_parents에서 순회해야 한다. 그리고 그 안에 딸린 것에 postid 나를 지워야해
                그리고 my_parents에 child로 내가 있느냐? (부모 - 자식)
                o -> 부모가 있다면 해당 부모들 다 모아서 리스트에 넣어. 그리고 해당 키는 지워.
                리스트에 있는 부모들을 my_children에서 순회해야 해. 그리고 그 안에 딸린 것에서 나를 지워.

                3번은 my_parents에 child로 내가 있느냐? (부모 - 자식)
                o -> 부모가 있다면 해당 부모들 다 모아서 리스트에 넣어. 그리고 해당 키는 지워.
                리스트에 있는 부모들을 my_children에서 순회해야 해. 그리고 그 안에 딸린 것에서 나를 지워.

                4번은 my_parents, my_children 두가지 경우 다 child로 내가 없는 경우
                 */

        deleteSong();
    }

    private void deleteSong(){
        //songs에서 해당 게시물 삭제
        DatabaseReference songRef = FirebaseDatabase.getInstance().getReference().child("songs").child(postId);
        songRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 삭제 성공시 동작할 내용
                        Log.d("테스트", "songs에서 데이터 삭제 성공");
                        deleteUserSong();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 삭제 실패시 동작할 내용
                        Toast.makeText(getContext(), "노래 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        Log.e("테스트", "데이터 삭제 실패", e);
                    }
                });
    }
    private void deleteUserSong(){
        //user_songs에서 해당 게시물 삭제
        DatabaseReference usersongRef = FirebaseDatabase.getInstance().getReference().child("user_songs").child(userUid).child(postId);
        usersongRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 삭제 성공시 동작할 내용
                        Log.d("테스트", "user_songs에서 데이터 삭제 성공");
                        deleteSongLike();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 삭제 실패시 동작할 내용
                        Toast.makeText(getContext(), "노래 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        Log.e("테스트", "데이터 삭제 실패", e);
                    }
                });
    }
    private void deleteSongLike(){
        //userlike에서는 각 사용자들 돌면서 해당 postid 삭제
        DatabaseReference postLoveRef = FirebaseDatabase.getInstance().getReference().child("user_like");
        postLoveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {//userSnapshot이 user_like에 있는 각각 사용자 uid
                    for (DataSnapshot postIdSnapshot : userSnapshot.getChildren()) {//각 사용자 uid의 하위에 있는 좋아요 한 음악들의 postid
                        String belowpostId = postIdSnapshot.getKey();
                        if (belowpostId.equals(postId)) {
                            postIdSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // 삭제 성공 시 수행할 작업
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // 삭제 실패 시 수행할 작업
                                        }
                                    });
                            break; // 찾은 후 바로 종료
                        }
                    }
                }

                deleteComment();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 처리 중 오류가 발생했을 때 수행할 작업
            }
        });
    }

    private void deleteComment(){
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("comment").child(postId);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                originalComment = new ArrayList<>();
                childComment = new ArrayList<>();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String oricommentKey = snapshot.getKey();
                        originalComment.add(oricommentKey);
                    }

                    // 삭제된 후에 user_comment_love를 처리하기 위해 comment 노드 삭제를 성공적으로 처리한 후에 작업해야 함
                    commentRef.removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("테스트", "comment > postId 노드 삭제 성공");

                                    // 댓글을 삭제한 후, comment_child에서 삭제하고자 하는 데이터들을 찾아 삭제
                                    deleteCommentChild(originalComment);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("테스트", "comment > postId 노드 삭제 실패", e);
                                }
                            });
                } else {
                    Log.d("테스트", "comment > postId 노드가 존재하지 않음");
                    // 추가 작업 혹은 오류 처리 등을 수행
                    deleteFamily();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("테스트", "데이터 가져오기 실패", databaseError.toException());
            }
        });
    }

    // 댓글을 삭제한 후, comment_child에서 삭제하고자 하는 데이터들을 찾아 삭제하는 메서드
    private void deleteCommentChild(ArrayList<String> oriComment) {
        DatabaseReference commentChildRef = FirebaseDatabase.getInstance().getReference().child("comment_child");
        childComment.clear();

        AtomicInteger counter = new AtomicInteger(oriComment.size());

        for (String commentChildKey : oriComment) {
            commentChildRef.child(commentChildKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String childcommentKey = snapshot.getKey();
                            childComment.add(childcommentKey);
                        }

                        dataSnapshot.getRef().removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("테스트", "comment_child > postId 노드 삭제 과정 중");
                                        // 댓글 데이터 삭제 후 user_comment_love 처리
                                        // 삭제된 comment_child 노드별로 카운터 감소
                                        if (counter.decrementAndGet() == 0) {
                                            // 모든 comment_child 삭제가 완료되면 deleteUserCommentLove 호출
                                            deleteUserCommentLove(oriComment, childComment);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("테스트", "comment_child > postId 노드 삭제 실패", e);
                                        if (counter.decrementAndGet() == 0) {
                                            Log.e("테스트", "데이터 삭제 실패", e);
                                        }

                                        Toast.makeText(getContext(), "노래 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.d("테스트", "원댓은 있는데 대댓이 없음");
                        // 대댓글이 없는 경우에도 카운터 감소
                        if (counter.decrementAndGet() == 0) {
                            deleteUserCommentLove(oriComment, childComment);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("테스트", "데이터 가져오기 실패", databaseError.toException());
                    Toast.makeText(getContext(), "노래 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // deleteCommentChild의 처리가 모두 완료된 후 호출되는 함수
    private void deleteUserCommentLove(ArrayList<String> oriComment, ArrayList<String> childrenComment) {
        DatabaseReference userCommentLoveRef = FirebaseDatabase.getInstance().getReference().child("user_comment_love");
        AtomicInteger deleteCounter = new AtomicInteger(0);
        int totalOperations = oriComment.size() + childrenComment.size();

        userCommentLoveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean hasLiked = false;
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        for (String commentKey : oriComment) {
                            if (userSnapshot.hasChild(commentKey)) {
                                hasLiked = true;
                                userSnapshot.child(commentKey).getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("테스트", "user_comment_love > postId 노드 삭제 성공");
                                                checkDeleteCompletion(deleteCounter, totalOperations);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("테스트", "user_comment_love > postId 노드 삭제 실패", e);
                                                Toast.makeText(getContext(), "노래 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        for (String childCommentKey : childrenComment) {
                            if (userSnapshot.hasChild(childCommentKey)) {
                                userSnapshot.child(childCommentKey).getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("테스트", "user_comment_love > childrenComment 노드 삭제 성공");
                                                checkDeleteCompletion(deleteCounter, totalOperations);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("테스트", "user_comment_love > childrenComment 노드 삭제 실패", e);
                                                Toast.makeText(getContext(), "노래 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }
                    if (!hasLiked) {
                        // 해당 노래에 좋아요를 누른 사용자가 없는 경우
                        deleteFamily();
                    }
                } else {
                    // 좋아요 데이터가 하나도 없는 경우에도 deleteFamily() 호출
                    deleteFamily();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("테스트", "데이터 가져오기 실패", databaseError.toException());
                Toast.makeText(getContext(), "노래 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 모든 작업이 완료되었는지 체크하는 함수
    private void checkDeleteCompletion(AtomicInteger counter, int totalOperations) {
        int count = counter.incrementAndGet();
        if (count >= totalOperations) {
            // All delete operations are completed
            Log.d("테스트", "모든 삭제 작업 완료");
            // Call deleteFamily() after all operations are completed
            deleteFamily();
        }
    }

    private void deleteFamily() {
        Log.d("테스트", "패밀리...");
        DatabaseReference myChildrenRef = FirebaseDatabase.getInstance().getReference().child("my_children").child(postId);

        myChildrenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> childList = new ArrayList<>();

                // Check if children exist
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String childKey = childSnapshot.getKey();
                        childList.add(childKey);
                    }

                    // Remove postId from my_children
                    myChildrenRef.removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Successfully removed postId from my_children
                                    Log.d("테스트", "my_children > postId 노드 삭제 성공");

                                    // Remove postId from each child's my_parents list
                                    DatabaseReference myParentsRef = FirebaseDatabase.getInstance().getReference().child("my_parents");
                                    deleteChildParents(childList, myParentsRef);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to remove postId from my_children
                                    Log.e("테스트", "my_children > postId 노드 삭제 실패", e);
                                }
                            });
                }else{
                    //다음 단계로 가야함 부모는 없는가?
                    deleteParent();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle data fetching error
                Log.e("테스트", "데이터 가져오기 실패", databaseError.toException());
            }
        });
    }
    private void deleteChildParents(ArrayList<String> childList, DatabaseReference myParentsRef) {
        for (String childKey : childList) {
            myParentsRef.child(childKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot parentSnapshot : dataSnapshot.getChildren()) {
                        if (parentSnapshot.getValue().equals(postId)) {
                            parentSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Successfully removed postId from child's parent list
                                            Log.d("테스트", "my_parents > child > postId 노드 삭제 성공");
                                            checkAllOperationsCompleted(childList.size());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Failed to remove postId from child's parent list
                                            Log.e("테스트", "my_parents > child > postId 노드 삭제 실패", e);
                                            checkAllOperationsCompleted(childList.size());
                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle data fetching error
                    Log.e("테스트", "데이터 가져오기 실패", databaseError.toException());
                    checkAllOperationsCompleted(childList.size());
                }
            });
        }
    }

    private void checkAllOperationsCompleted(int totalOperations) {
        // Keep track of successful operations
        AtomicInteger counter = new AtomicInteger(0);

        if (counter.incrementAndGet() >= totalOperations) {
            // All operations are completed successfully
            Log.d("테스트", "모든 작업 완료");

            // Call deleteParent() when all operations are completed
            deleteParent();
        }
    }

    private void deleteParent() {
        DatabaseReference myParentsRef = FirebaseDatabase.getInstance().getReference().child("my_parents").child(postId);

        myParentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> parentList = new ArrayList<>();

                // Check if parents exist
                if (dataSnapshot.exists()) {
                    for (DataSnapshot parentSnapshot : dataSnapshot.getChildren()) {
                        String parentKey = parentSnapshot.getKey();
                        parentList.add(parentKey);
                    }

                    // Remove postId from my_parents
                    myParentsRef.removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Successfully removed postId from my_parents
                                    Log.d("테스트", "my_parents > postId 노드 삭제 성공");

                                    // Remove postId from each parent's my_children list
                                    DatabaseReference myChildrenRef = FirebaseDatabase.getInstance().getReference().child("my_children");
                                    deleteParentsChildren(parentList, myChildrenRef);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to remove postId from my_parents
                                    Log.e("테스트", "my_parents > postId 노드 삭제 실패", e);
                                }
                            });
                }else{
                    //부모가 없다면 바로
                    onDeletedCompleted();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle data fetching error
                Log.e("테스트", "데이터 가져오기 실패", databaseError.toException());
            }
        });
    }

    private void deleteParentsChildren(ArrayList<String> parentList, DatabaseReference myChildrenRef) {
        AtomicInteger parentcounter = new AtomicInteger(0);
        int totalParents = parentList.size();
        Log.d("테스트", "여기가 진자 마지막??");

        for (String parentKey : parentList) {
            myChildrenRef.child(parentKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.getValue().equals(postId)) {
                            childSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Successfully removed postId from parent's children list
                                            Log.d("테스트", "my_children > parent > postId 노드 삭제 성공");
                                            checkAllOperationsCompleted(parentcounter, totalParents);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Failed to remove postId from parent's children list
                                            Log.e("테스트", "my_children > parent > postId 노드 삭제 실패", e);
                                            checkAllOperationsCompleted(parentcounter, totalParents);
                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle data fetching error
                    Log.e("테스트", "데이터 가져오기 실패", databaseError.toException());
                    checkAllOperationsCompleted(parentcounter, totalParents);
                }
            });
        }
    }

    private void checkAllOperationsCompleted(AtomicInteger tmpcounter, int totalParents) {
        if (tmpcounter.incrementAndGet() >= totalParents) {
            // All operations are completed successfully
            Log.d("테스트", "모든 작업 완료");

            // Call onDeletedCompleted() when all operations are completed
            onDeletedCompleted();
        }
    }


    @Override
    public void onDeletedCompleted() {
        if(getActivity() instanceof NewPickMusic){
            NewPickMusic newPickMusic = (NewPickMusic) getActivity();
            newPickMusic.deleteActivity(); //이건 프로필이미지 눌렀을 때를 위하여!
        }
    }

}