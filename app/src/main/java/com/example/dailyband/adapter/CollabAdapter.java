package com.example.dailyband.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.Models.TestSong;
import com.example.dailyband.ShowMusic.NewPickMusic;
import com.example.dailyband.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CollabAdapter extends RecyclerView.Adapter<CollabAdapter.CollabViewHolder> {
    private Context context;
    private List<ComplexName> songs;

    public CollabAdapter(Context context, List<ComplexName> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public CollabAdapter.CollabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.popular_item, parent, false);
        CollabViewHolder holder = new CollabViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CollabAdapter.CollabViewHolder holder, final int position) {
        ComplexName song = songs.get(position);
        holder.numRanking.setText(String.valueOf(position + 1));
        holder.songName.setText(song.getTitle());
    }

    @Override
    public int getItemCount() {
        return (null !=songs ? songs.size() : 0);
    }

    public class CollabViewHolder extends RecyclerView.ViewHolder {
        public TextView numRanking;
        public TextView songName;

        public CollabViewHolder(@NonNull View itemView) {
            super(itemView);
            numRanking = itemView.findViewById(R.id.numRanking);
            songName = itemView.findViewById(R.id.songname);
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
