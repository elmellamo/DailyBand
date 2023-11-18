package com.example.dailyband.ShowMusic;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyband.Models.CommentItem;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Utils.CommentDatailClickListener;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.Utils.OnCommentSuccessListener;
import com.example.dailyband.adapter.CommentDetailAdapter;
import com.example.dailyband.adapter.CommentMainAdapter;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentDetailFragment extends Fragment{
    private View view;
    private RecyclerView re_commentrecycler;
    private EditText comment_add_edit;
    private Button registerbtn;
    private CommentDetailAdapter adapter;
    private ImageView clearimg;
    private CommentItem oricomment;
    private List<CommentItem> comments;
    private FirebaseMethods mFirebaseMethods;
    private OnCommentSuccessListener onCommentSuccessListener;
    private TextView nickname, when, commentcontents, lovenum;
    private ImageView heartimg, profile;
    public CommentDetailFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public void setCommentDetail(CommentItem oricomment){
        this.oricomment = oricomment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.comment_detail, container, false);

        nickname = view.findViewById(R.id.nickname);
        when = view.findViewById(R.id.when);
        commentcontents = view.findViewById(R.id.commentcontents);
        lovenum = view.findViewById(R.id.lovenum);
        heartimg = view.findViewById(R.id.heartimg);
        profile = view.findViewById(R.id.profile);

        re_commentrecycler = view.findViewById(R.id.re_commentrecycler);
        re_commentrecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        comment_add_edit = view.findViewById(R.id.comment_add_edit);
        registerbtn = view.findViewById(R.id.registerbtn);
        clearimg = view.findViewById(R.id.clearimg);

        mFirebaseMethods = new FirebaseMethods(getActivity());
        comments = new ArrayList<>();
        initState();
        getComment();
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String register_comment = comment_add_edit.getText().toString();

                if (!register_comment.isEmpty()) {
                    mFirebaseMethods.addToChildComment(register_comment, oricomment.getComment_id());
                    comment_add_edit.setText("");
                }
            }
        });

        return view;
    }

    private void getComment(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String myUID = mFirebaseMethods.getMyUid();
        DatabaseReference childRef = databaseReference.child("comment_child").child(oricomment.getComment_id());
        childRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    comments.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CommentItem comment = snapshot.getValue(CommentItem.class);
                        if(comment != null){
                            comments.add(comment);
                        }
                    }
                    adapter = new CommentDetailAdapter(getActivity(), comments);
                    re_commentrecycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                //callback.onDataFetchedSuccessfully();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는데 실패한 경우 처리하는 코드
                //callback.onDataFetchFailed();
            }
        });
    }

    void initState(){
        //heartimg = itemView.findViewById(R.id.heartimg);
        String writeruid=  oricomment.getUser_id();
        commentcontents.setText(oricomment.getContents());
        lovenum.setText(String.valueOf(oricomment.getLove()-1));


        DatabaseReference userAccountRef = FirebaseDatabase.getInstance().getReference().child("UserAccount").child(writeruid);
        userAccountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userAccountDataSnapshot) {
                if (userAccountDataSnapshot.exists()) {
                    String whatnickname = userAccountDataSnapshot.child("name").getValue(String.class);
                    nickname.setText(whatnickname);
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기가 실패한 경우에 대한 처리
            }
        });

        //날짜
        SimpleDateFormat firebaseDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.KOREA);
        Date castDate = null;
        try {
            castDate = firebaseDateFormat.parse(oricomment.getDate_created());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 새로운 날짜 포맷을 지정
        SimpleDateFormat desiredDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
        // Date 객체를 새로운 포맷으로 변환
        String desiredDateString = desiredDateFormat.format(castDate);
        when.setText(desiredDateString);

        //프로필 이미지
        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + writeruid + ".jpg");
        profileImageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                // 파일 메타데이터를 성공적으로 가져온 경우 파일이 존재함
                // 여기에서 이미지 다운로드 및 화면에 표시하는 작업 수행
                profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getActivity()).load(uri).into(profile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // 이미지 다운로드 실패 시 처리 (예: 기본 이미지 설정 또는 오류 메시지 출력)
                        profile.setImageResource(R.drawable.brid_second_img);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // 파일 메타데이터 가져오기 실패 시 파일이 존재하지 않음
                // 해당 경우에 대한 처리를 수행 (예: 기본 이미지 설정 또는 다른 처리)
            }
        });


    }

}