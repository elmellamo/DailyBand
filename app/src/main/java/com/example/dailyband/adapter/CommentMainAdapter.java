package com.example.dailyband.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.dailyband.Love.LoveActivity;
import com.example.dailyband.Models.CommentItem;
import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.Models.TestSong;
import com.example.dailyband.R;
import com.example.dailyband.ShowMusic.NewPickMusic;
import com.example.dailyband.Utils.CommentDatailClickListener;
import com.example.dailyband.Utils.CommentMainCompletedListener;
import com.example.dailyband.Utils.PopUpClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

public class CommentMainAdapter extends RecyclerView.Adapter<CommentMainAdapter.CommentMainViewHolder> {
    private Context context;
    private List<CommentItem> comments;
    private String postId;
    private CommentDatailClickListener clickListener;
    private CommentMainCompletedListener completedListener;
    private boolean allDataLoaded = false;
    public CommentMainAdapter(Context context, List<CommentItem> comments, CommentDatailClickListener clickListener, CommentMainCompletedListener completedListener) {
        this.context = context;
        this.comments = comments;
        this.clickListener = clickListener;
        this.completedListener = clickListener instanceof CommentMainCompletedListener ? (CommentMainCompletedListener) clickListener : completedListener;
    }

    @NonNull
    @Override
    public CommentMainAdapter.CommentMainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_header, parent, false);
        CommentMainViewHolder holder = new CommentMainViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentMainAdapter.CommentMainViewHolder holder, final int position) {
        CommentItem comment = comments.get(position);
        holder.commentcontents.setText(comment.getContents());
        postId = comment.getPost_id();

        String commentId = comment.getComment_id();
        String writeruid =  comment.getUser_id();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

        //좋아요 이미지
        DatabaseReference userCommentLoveRef = FirebaseDatabase.getInstance().getReference().child("user_comment_love").child(userID).child(commentId);
        userCommentLoveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // user_comment_love 카테고리에 해당 commentId가 있음 - full_heart 이미지 설정
                    holder.heartimg.setImageResource(R.drawable.newgreenheart);
                } else {
                    // user_comment_love 카테고리에 해당 commentId가 없음 - empty_heart 이미지 설정
                    holder.heartimg.setImageResource(R.drawable.newgreenheart_empty);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("comment").child(postId).child(commentId);
        databaseReference.addValueEventListener(new ValueEventListener() {
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

        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + writeruid + ".jpg");
        profileImageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                // 파일 메타데이터를 성공적으로 가져온 경우 파일이 존재함
                // 여기에서 이미지 다운로드 및 화면에 표시하는 작업 수행
                profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context)
                                .load(uri)
                                .listener(new RequestListener<Drawable>() {
                             @Override
                             public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                 return false;
                             }

                             @Override
                             public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                 if (position == comments.size() - 1) {
                                     // 마지막 아이템에 도달했을 때
                                     if (!allDataLoaded) {
                                         allDataLoaded = true; // 모든 데이터가 로드됨을 표시
                                         // 모든 데이터가 로드되었음을 알림
                                         if (completedListener != null) {
                                             completedListener.onCommentMainCompleted();
                                         }
                                     }
                                 }
                                 return false;
                             }
                         })
                        .into(holder.profile);
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

        //heartimg = itemView.findViewById(R.id.heartimg);

        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child("comment_child").child(commentId);
        childRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    long howmanychild = dataSnapshot.getChildrenCount();
                    holder.belowlayout.setVisibility(View.VISIBLE);
                    holder.child_comment.setText("답글 "+howmanychild+"개 보기");
                }else{
                    holder.belowlayout.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        holder.heartimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference userCommentLove = FirebaseDatabase.getInstance().getReference().child("user_comment_love").child(userID).child(commentId);
                userCommentLove.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // user_comment_love 카테고리에 해당 commentId가 있음 - 삭제하고 empty_heart 이미지 설정
                            userCommentLove.removeValue();
                            // 좋아요가 취소된 경우, comment_child에서 해당 commentId의 좋아요 수를 1 감소시킵니다.
                            DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("comment").child(postId).child(commentId);
                            commentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        CommentItem updatecomment = dataSnapshot.getValue(CommentItem.class);
                                        int currentLove = updatecomment.getLove();
                                        commentRef.child("love").setValue(currentLove - 1); // 좋아요 수 감소
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // 데이터를 가져오는데 실패한 경우 처리
                                }
                            });
                        } else {
                            // user_comment_love 카테고리에 해당 commentId가 없음 - 추가하고 full_heart 이미지 설정
                            userCommentLove.setValue(true);

                            // 좋아요가 추가된 경우, comment_child에서 해당 commentId의 좋아요 수를 1 증가시킵니다.
                            DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("comment").child(postId).child(commentId);
                            commentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        CommentItem updatecomment = dataSnapshot.getValue(CommentItem.class);
                                        int currentLove = updatecomment.getLove();
                                        commentRef.child("love").setValue(currentLove + 1); // 좋아요 수 증가
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
                        // 데이터를 가져오는데 실패한 경우 처리
                    }
                });
            }
        });
/*
        if (position == comments.size() - 1) {
            // 마지막 아이템에 도달했을 때
            if (!allDataLoaded) {
                allDataLoaded = true; // 모든 데이터가 로드됨을 표시

                // 모든 데이터가 로드되었음을 알림
                if (completedListener != null) {
                    completedListener.onCommentMainCompleted();
                }
            }
        }

 */
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentMainViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname, when, commentcontents, lovenum, child_comment, seecomment;
        public ConstraintLayout belowlayout;
        public ImageView heartimg, profile;

        public CommentMainViewHolder(@NonNull View itemView) {
            super(itemView);
            nickname = itemView.findViewById(R.id.nickname);
            when = itemView.findViewById(R.id.when);
            commentcontents = itemView.findViewById(R.id.commentcontents);
            lovenum = itemView.findViewById(R.id.lovenum);
            child_comment = itemView.findViewById(R.id.child_comment);
            belowlayout = itemView.findViewById(R.id.belowlayout);
            heartimg = itemView.findViewById(R.id.heartimg);
            profile = itemView.findViewById(R.id.profile);
            seecomment = itemView.findViewById(R.id.seecomment);

            belowlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        CommentItem selectedComment = comments.get(position);

                        if(clickListener !=null){
                            clickListener.onCommentDatailClicked(selectedComment);
                        }

                    }
                }
            });

            seecomment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        CommentItem selectedComment = comments.get(position);

                        if(clickListener !=null){
                            clickListener.onCommentDatailClicked(selectedComment);
                        }

                    }
                }
            });

        }
    }
}
