package com.example.dailyband.adapter;

import android.content.Context;
        import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.Models.TestSong;
import com.example.dailyband.ShowMusic.NewPickMusic;
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

        import java.util.List;

public class LoveAdapter extends RecyclerView.Adapter<LoveAdapter.LoveViewHolder> {
    private Context context;
    private List<ComplexName> songs;
    private String nickname;

    public LoveAdapter(Context context, List<ComplexName> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public LoveAdapter.LoveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.love_item, parent, false);
        LoveViewHolder holder = new LoveViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LoveAdapter.LoveViewHolder holder, final int position) {
        ComplexName song = songs.get(position);
        holder.songName.setText(song.getTitle());
        String songid = song.getSongid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("songs").child(songid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    TestSong thissong = dataSnapshot.getValue(TestSong.class);
                    holder.lovenum.setText(String.valueOf(thissong.getLove()-1));
                } else {
                    // 해당 postId에 대한 데이터가 존재하지 않음
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터를 가져오는데 실패한 경우
            }
        });

        String writeruid=  song.getWriteruid();
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
                    // UserAccount 카테고리에서 name 값을 가져와서 사용하거나 처리할 수 있습니다.
                    nickname = userAccountDataSnapshot.child("name").getValue(String.class);
                    holder.writername.setText(nickname);
                    // 가져온 데이터를 사용하거나 처리할 수 있습니다.
                } else {
                    // "UserAccount" 카테고리에서 해당 데이터가 없는 경우에 대한 처리
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
        return songs.size();
    }

    public class LoveViewHolder extends RecyclerView.ViewHolder {
        public TextView songName, writername;
        public ImageView profile;
        public TextView lovenum;

        public LoveViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.songname);
            profile = itemView.findViewById(R.id.profile);
            lovenum = itemView.findViewById(R.id.lovenum);
            writername = itemView.findViewById(R.id.writername);
            //여기 click 넣어야 함 아이템 마다

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ComplexName selectedSong = songs.get(position);

                        String songid = selectedSong.getSongid();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("songs").child(songid);
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // 해당 postId에 해당하는 데이터를 가져옴
                                    TestSong picksong = dataSnapshot.getValue(TestSong.class);
                                    Intent intent = new Intent(context, NewPickMusic.class);
                                    intent.putExtra("selectedSong", picksong);
                                    intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    context.startActivity(intent);
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
                }
            });
        }
    }
}
