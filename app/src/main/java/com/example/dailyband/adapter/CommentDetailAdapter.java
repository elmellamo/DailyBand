package com.example.dailyband.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyband.Models.CommentItem;
import com.example.dailyband.R;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentDetailAdapter extends RecyclerView.Adapter<CommentDetailAdapter.CommentDetailViewHolder> {
    private Context context;
    private List<CommentItem> comments;
    private String postId;

    public CommentDetailAdapter(Context context, List<CommentItem> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentDetailAdapter.CommentDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_child, parent, false);
        CommentDetailViewHolder holder = new CommentDetailViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentDetailAdapter.CommentDetailViewHolder holder, final int position) {
        CommentItem comment = comments.get(position);
        holder.commentcontents.setText(comment.getContents());
        postId = comment.getPost_id();

        String commentId = comment.getComment_id();

        SimpleDateFormat firebaseDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.KOREA);
        Date castDate = null;
        try {
            castDate = firebaseDateFormat.parse(comment.getDate_created());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 새로운 날짜 포맷을 지정
        SimpleDateFormat desiredDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
        // Date 객체를 새로운 포맷으로 변환
        String desiredDateString = desiredDateFormat.format(castDate);
        holder.when.setText(desiredDateString);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("comment").child(postId).child(commentId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    CommentItem thiscomment = dataSnapshot.getValue(CommentItem.class);
                    holder.lovenum.setText(String.valueOf(thiscomment.getLove()-1));
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터를 가져오는데 실패한 경우
            }
        });

        String writeruid =  comment.getUser_id();
        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + writeruid + ".jpg");
        profileImageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                // 파일 메타데이터를 성공적으로 가져온 경우 파일이 존재함
                // 여기에서 이미지 다운로드 및 화면에 표시하는 작업 수행
                profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context).load(uri).into(holder.profile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // 이미지 다운로드 실패 시 처리 (예: 기본 이미지 설정 또는 오류 메시지 출력)
                        holder.profile.setImageResource(R.drawable.brid_second_img);
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

        DatabaseReference userAccountRef = FirebaseDatabase.getInstance().getReference().child("UserAccount").child(writeruid);
        userAccountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userAccountDataSnapshot) {
                if (userAccountDataSnapshot.exists()) {
                    String whatnickname = userAccountDataSnapshot.child("name").getValue(String.class);
                    holder.nickname.setText(whatnickname);
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기가 실패한 경우에 대한 처리
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentDetailViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname, when, commentcontents, lovenum;
        public ImageView heartimg, profile;

        public CommentDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            nickname = itemView.findViewById(R.id.nickname);
            when = itemView.findViewById(R.id.when);
            commentcontents = itemView.findViewById(R.id.commentcontents);
            lovenum = itemView.findViewById(R.id.lovenum);
            heartimg = itemView.findViewById(R.id.heartimg);
            profile = itemView.findViewById(R.id.profile);

        }
    }
}