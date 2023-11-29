package com.example.dailyband.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.dailyband.Models.CommentItem;
import com.example.dailyband.R;
import com.example.dailyband.Utils.CommentDatailClickListener;
import com.example.dailyband.Utils.CommentDetailCompletedListener;
import com.example.dailyband.Utils.CommentMainCompletedListener;
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

public class CommentDetailAdapter extends RecyclerView.Adapter<CommentDetailAdapter.CommentDetailViewHolder> {
    private Context context;
    private List<CommentItem> comments;
    private String postId;
    private CommentDetailCompletedListener completedListener;

    private boolean allDataLoaded = false;

    public CommentDetailAdapter(Context context, List<CommentItem> comments, CommentDetailCompletedListener completedListener) {
        this.context = context;
        this.comments = comments;
        this.completedListener = completedListener;
    }

    @NonNull
    @Override
    public CommentDetailAdapter.CommentDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_child, parent, false);
        CommentDetailViewHolder  holder = new CommentDetailViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentDetailAdapter.CommentDetailViewHolder holder, final int position) {
        CommentItem comment = comments.get(position);
        holder.commentcontents.setText(comment.getContents());
        postId = comment.getPost_id();

        String commentId = comment.getComment_id();
        String writeruid =  comment.getUser_id();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //날짜
        SimpleDateFormat firebaseDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.KOREA);
        Date castDate = null;
        try {
            castDate = firebaseDateFormat.parse(comment.getDate_created());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat desiredDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
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
                    Log.d("로그", "풀 하트여야 한다.");
                } else {
                    // user_comment_love 카테고리에 해당 commentId가 없음 - empty_heart 이미지 설정
                    holder.heartimg.setImageResource(R.drawable.newgreenheart_empty);
                    Log.d("로그", "빈 하트....."+commentId);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //좋아요 수
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("comment_child").child(postId).child(commentId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    CommentItem thiscomment = dataSnapshot.getValue(CommentItem.class);
                    holder.lovenum.setText(String.valueOf(thiscomment.getLove()-1));
                } else {
                    // 데이터가 존재하지 않을 때의 처리
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터를 가져오는데 실패한 경우의 처리
            }
        });

        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + writeruid + ".jpg");
        profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // 이미지가 존재하는 경우 Glide를 사용하여 이미지 로드
                Glide.with(context)
                        .load(uri)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                // 이미지 로드 실패 시 처리
                                Log.d("테스트", "이미지 로드 실패");
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                // 이미지 로드 성공 시 처리
                                Log.d("테스트", "이미지 로드 성공");
                                /*
                                if (position == comments.size() - 1) {
                                    // 마지막 아이템에 도달했을 때
                                    if (!allDataLoaded) {
                                        allDataLoaded = true; // 모든 데이터가 로드됨을 표시
                                        // 모든 데이터가 로드되었음을 알림
                                        if (completedListener != null) {
                                            completedListener.onCommentDetailCompleted();
                                        }
                                    }
                                }
                                 */
                                return false;
                            }
                        })
                        .into(holder.profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // 이미지가 존재하지 않는 경우: 기본 이미지를 설정하여 Glide로 로드
                Glide.with(context)
                        .load(R.drawable.brid_second_img) // 기본 이미지 리소스
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                // 기본 이미지 로드 실패 시 처리
                                Log.d("테스트", "기본 이미지 로드 실패");
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                // 기본 이미지 로드 성공 시 처리
                                Log.d("테스트", "기본 이미지 로드 성공");
                                /*
                                if (position == comments.size() - 1) {
                                    // 마지막 아이템에 도달했을 때
                                    if (!allDataLoaded) {
                                        allDataLoaded = true; // 모든 데이터가 로드됨을 표시
                                        // 모든 데이터가 로드되었음을 알림
                                        if (completedListener != null) {
                                            completedListener.onCommentDetailCompleted();
                                        }
                                    }
                                }
                                 */

                                return false;
                            }
                        })
                        .into(holder.profile);
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
                            DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("comment_child").child(postId).child(commentId);
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
                            DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("comment_child").child(postId).child(commentId);
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

        holder.detailclick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(writeruid.equals(userID)){
                    int currentColor = ((ColorDrawable) holder.childlayout.getBackground()).getColor();
                    int grayGreenColor = ContextCompat.getColor(context, R.color.graygreen);

                    if (currentColor == grayGreenColor) {
                        holder.childlayout.setBackgroundColor(ContextCompat.getColor(context, R.color.click_green));
                        holder.heartimg.setVisibility(View.GONE);
                        holder.lovenum.setVisibility(View.GONE);
                        holder.deletetrash.setVisibility(View.VISIBLE);
                        holder.heartlayout.setBackgroundColor(ContextCompat.getColor(context, R.color.trash_Background));
                    } else {
                        holder.childlayout.setBackgroundColor(grayGreenColor);
                        holder.heartimg.setVisibility(View.VISIBLE);
                        holder.lovenum.setVisibility(View.VISIBLE);
                        holder.deletetrash.setVisibility(View.GONE);
                        holder.heartlayout.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            }
        });
        holder.detailclick2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(writeruid.equals(userID)){
                    int currentColor = ((ColorDrawable) holder.childlayout.getBackground()).getColor();
                    int grayGreenColor = ContextCompat.getColor(context, R.color.graygreen);

                    if (currentColor == grayGreenColor) {
                        holder.childlayout.setBackgroundColor(ContextCompat.getColor(context, R.color.click_green));
                        holder.heartimg.setVisibility(View.GONE);
                        holder.lovenum.setVisibility(View.GONE);
                        holder.deletetrash.setVisibility(View.VISIBLE);
                        holder.heartlayout.setBackgroundColor(ContextCompat.getColor(context, R.color.trash_Background));
                    } else {
                        holder.childlayout.setBackgroundColor(grayGreenColor);
                        holder.heartimg.setVisibility(View.VISIBLE);
                        holder.lovenum.setVisibility(View.VISIBLE);
                        holder.deletetrash.setVisibility(View.GONE);
                        holder.heartlayout.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            }
        });

        holder.heartlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.getColor(context, R.color.click_green) == ((ColorDrawable) holder.childlayout.getBackground()).getColor()){
                    DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("comment_child").child(postId).child(commentId);
                    commentRef.removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "댓글이 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    // 데이터를 삭제하고 리사이클러뷰에서도 항목 제거
                                    int deletedPosition = holder.getAdapterPosition();
                                    if (deletedPosition != RecyclerView.NO_POSITION) {
                                        removeItem(deletedPosition);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 삭제 실패시 동작할 내용
                                    Toast.makeText(context, "댓글 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    Log.e("테스트", "데이터 삭제 실패", e);
                                }
                            });
                }
            }
        });
    }

    public void removeItem(int position) {
        if (comments != null && comments.size() > position) {
            comments.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, comments.size());
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentDetailViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname, when, commentcontents, lovenum;
        public ImageView heartimg, profile, deletetrash;
        public ConstraintLayout detailclick1, detailclick2, heartlayout, childlayout;

        public CommentDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            nickname = itemView.findViewById(R.id.nickname);
            when = itemView.findViewById(R.id.when);
            commentcontents = itemView.findViewById(R.id.commentcontents);
            lovenum = itemView.findViewById(R.id.lovenum);
            heartimg = itemView.findViewById(R.id.heartimg);
            profile = itemView.findViewById(R.id.profile);
            deletetrash = itemView.findViewById(R.id.deletetrash);
            detailclick1 = itemView.findViewById(R.id.detailclick1);
            detailclick2 = itemView.findViewById(R.id.detailclick2);
            heartlayout = itemView.findViewById(R.id.heartlayout);
            childlayout = itemView.findViewById(R.id.childlayout);


        }
    }
}
