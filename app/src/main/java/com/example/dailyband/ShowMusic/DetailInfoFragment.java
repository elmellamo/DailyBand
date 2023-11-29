package com.example.dailyband.ShowMusic;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

public class DetailInfoFragment extends Fragment {

    private View view;
    private ConstraintLayout commentlayout, downloadlayout, artistlayout, creditlayout, collablayout, collalayout, orilayout, deletelayout;
    private boolean isLiked;
    private String title, artist, postId, userUid;
    private ImageView heartbtn;
    private TextView songtitle, songwriter, lovenum;
    private String myId;
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
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                        /* postId userUid
                해당 게시물은 postid라고 한다면

                comment에서 해당 게시물 아예 삭제 >> 여기 commentid가져와서 여기서 또
                user_comment_love에서 각 사용자들 돌면서 >> 해당 commentid 삭제

                songs에서 해당 게시물 삭제
                userlike에서는 각 사용자들 돌면서 해당 postid 삭제
                user_songs에서도 해당 사용자, 해당 게시물 삭제
                 */
        //songs에서 해당 게시물 삭제
        DatabaseReference songRef = FirebaseDatabase.getInstance().getReference().child("songs").child(postId);
        songRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 삭제 성공시 동작할 내용
                        Log.d("테스트", "songs에서 데이터 삭제 성공");

                        //user_like에서 각 사용자 돌면서 해당 postid 삭제
                        DatabaseReference userLikeRef = FirebaseDatabase.getInstance().getReference().child("user_like");
                        userLikeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // 각 child(사용자 UID)를 순회합니다.
                                for (DataSnapshot users : dataSnapshot.getChildren()) {
                                    String separateuserId = users.getKey(); // 각 child의 key는 사용자의 UID입니다.
                                    DatabaseReference userpostlikeRef = userLikeRef.child(separateuserId).child(postId);
                                    userpostlikeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                userpostlikeRef.removeValue();
                                                Log.d("테스트", "user_like에서  데이터 삭제 성공");
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // 데이터를 가져오는데 실패한 경우 처리
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // 데이터를 가져오는데 실패한 경우 처리하는 코드
                                Log.e("테스트", "데이터 가져오기 실패", databaseError.toException());
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 삭제 실패시 동작할 내용
                        Toast.makeText(getContext(), "댓글 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        Log.e("테스트", "데이터 삭제 실패", e);
                    }
                });

        //user_songs에서 해당 게시물 삭제
        DatabaseReference usersongRef = FirebaseDatabase.getInstance().getReference().child("user_songs").child(userUid).child(postId);
        usersongRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 삭제 성공시 동작할 내용
                        Log.d("테스트", "user_songs에서 데이터 삭제 성공");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 삭제 실패시 동작할 내용
                        Toast.makeText(getContext(), "댓글 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        Log.e("테스트", "데이터 삭제 실패", e);
                    }
                });

        //comment에서 해당 게시물 삭제
        ArrayList<String> originalComment = new ArrayList<>(); //지우려는 포스트의 원본 댓글 id 담기
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("comment").child(postId);
        commentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String oricommentKey = snapshot.getKey(); // 지우려는 포스트의 원본 댓글 id들 가져옴
                    originalComment.add(oricommentKey);
                }

                commentRef.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // 삭제 성공시 동작할 내용
                                Log.d("테스트", "comment > postId 노드 삭제 성공");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 삭제 실패시 동작할 내용
                                Log.e("테스트", "comment > postId 노드 삭제 실패", e);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터를 가져오는데 실패한 경우 처리하는 코드
                Log.e("테스트", "데이터 가져오기 실패", databaseError.toException());
            }
        });

        //comment_child에서 해당 게시물 삭제
        ArrayList<String> childComment = new ArrayList<>(); //지우려는 포스트의 대댓글 id 담기
        for(String commentChildKey : originalComment){
            DatabaseReference commentChildRef = FirebaseDatabase.getInstance().getReference().child("comment_child").child(commentChildKey);
            commentChildRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String childcommentKey = snapshot.getKey(); // 각 원본 댓글의 대댓글 id들
                        childComment.add(childcommentKey);
                    }

                    commentChildRef.removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // 삭제 성공시 동작할 내용
                                    Log.d("테스트", "comment > postId 노드 삭제 성공");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 삭제 실패시 동작할 내용
                                    Log.e("테스트", "comment > postId 노드 삭제 실패", e);
                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 데이터를 가져오는데 실패한 경우 처리하는 코드
                    Log.e("테스트", "데이터 가져오기 실패", databaseError.toException());
                }
            });
        }





    }

}